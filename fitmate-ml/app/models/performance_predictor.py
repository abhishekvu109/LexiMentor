"""
Performance Predictor — two XGBRegressor models that predict:
  1. recommended_weight  (regression on next-session weight)
  2. recommended_reps    (regression on next-session reps)

Both models are trained on the labelled dataset produced by
feature_engineering.generate_progression_labels().

Model files:
    {MODEL_DIR}/weight_predictor.pkl
    {MODEL_DIR}/reps_predictor.pkl
    {MODEL_DIR}/perf_scaler.pkl
"""
import os
import joblib
import mlflow
import numpy as np
import pandas as pd
from loguru import logger
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_absolute_error
from xgboost import XGBRegressor

from app.config import settings

PERF_FEATURES = [
    "last_weight",
    "last_reps",
    "personal_record",
    "days_since_last_session",
    "trend_slope",
    "sessions_this_week",
]

WEIGHT_MODEL_PATH = os.path.join(settings.model_dir, "weight_predictor.pkl")
REPS_MODEL_PATH = os.path.join(settings.model_dir, "reps_predictor.pkl")
SCALER_PATH = os.path.join(settings.model_dir, "perf_scaler.pkl")


class PerformancePredictor:
    """
    Predicts recommended_weight and recommended_reps for the next session.

    Usage:
        predictor = PerformancePredictor()
        predictor.load()
        weight, reps = predictor.predict_single(features_dict)
    """

    def __init__(self):
        self.weight_model: XGBRegressor | None = None
        self.reps_model: XGBRegressor | None = None
        self.scaler: StandardScaler | None = None
        self._loaded = False

    # ── Lifecycle ─────────────────────────────────────────────────────────────

    def load(self) -> "PerformancePredictor":
        if not os.path.exists(WEIGHT_MODEL_PATH):
            logger.warning("No performance predictor found — will use rule engine fallback")
            return self
        self.weight_model = joblib.load(WEIGHT_MODEL_PATH)
        self.reps_model = joblib.load(REPS_MODEL_PATH)
        self.scaler = joblib.load(SCALER_PATH)
        self._loaded = True
        logger.info("PerformancePredictor loaded from disk")
        return self

    def is_ready(self) -> bool:
        return self._loaded and self.weight_model is not None

    # ── Training ──────────────────────────────────────────────────────────────

    def train(self, df: pd.DataFrame) -> dict:
        """
        Train weight and reps prediction models.

        :param df: Output of feature_engineering.generate_progression_labels().
        """
        if len(df) < 50:
            logger.warning(f"Only {len(df)} labelled samples — model may underfit. Needs 50+.")

        X = df[PERF_FEATURES].fillna(0)
        y_weight = df["next_weight"]
        y_reps = df["next_reps"]

        X_train, X_val, yw_train, yw_val, yr_train, yr_val = train_test_split(
            X, y_weight, y_reps, test_size=0.15, random_state=42
        )

        self.scaler = StandardScaler()
        X_train_s = self.scaler.fit_transform(X_train)
        X_val_s = self.scaler.transform(X_val)

        xgb_params = dict(
            n_estimators=300, max_depth=4, learning_rate=0.04,
            subsample=0.8, colsample_bytree=0.8, random_state=42
        )
        self.weight_model = XGBRegressor(**xgb_params)
        self.reps_model = XGBRegressor(**xgb_params)

        mlflow.set_tracking_uri(settings.mlflow_tracking_uri)
        mlflow.set_experiment(settings.mlflow_experiment_name)

        run_ids = {}
        with mlflow.start_run(run_name="weight_predictor") as run:
            self.weight_model.fit(X_train_s, yw_train)
            mae_w = mean_absolute_error(yw_val, self.weight_model.predict(X_val_s))
            mlflow.log_metric("val_mae_weight", mae_w)
            logger.info(f"Weight predictor MAE={mae_w:.2f} kg")
            run_ids["weight_predictor"] = run.info.run_id

        with mlflow.start_run(run_name="reps_predictor") as run:
            self.reps_model.fit(X_train_s, yr_train)
            mae_r = mean_absolute_error(yr_val, self.reps_model.predict(X_val_s))
            mlflow.log_metric("val_mae_reps", mae_r)
            logger.info(f"Reps predictor MAE={mae_r:.2f} reps")
            run_ids["reps_predictor"] = run.info.run_id

        os.makedirs(settings.model_dir, exist_ok=True)
        joblib.dump(self.weight_model, WEIGHT_MODEL_PATH)
        joblib.dump(self.reps_model, REPS_MODEL_PATH)
        joblib.dump(self.scaler, SCALER_PATH)
        self._loaded = True

        return {"mae_weight": mae_w, "mae_reps": mae_r, "run_ids": run_ids}

    # ── Inference ─────────────────────────────────────────────────────────────

    def predict(self, df: pd.DataFrame) -> tuple[np.ndarray, np.ndarray]:
        """Returns (predicted_weights, predicted_reps) arrays."""
        if not self.is_ready():
            raise RuntimeError("PerformancePredictor not loaded — call /train first")
        X = df[PERF_FEATURES].fillna(0)
        X_scaled = self.scaler.transform(X)
        weights = self.weight_model.predict(X_scaled)
        reps = self.reps_model.predict(X_scaled).clip(1, 30).round().astype(int)
        return weights, reps

    def predict_single(self, features: dict) -> tuple[float, int]:
        """Convenience wrapper for one exercise at a time."""
        df = pd.DataFrame([features])
        w, r = self.predict(df)
        return float(round(w[0], 2)), int(r[0])


# Module-level singleton
predictor = PerformancePredictor()
