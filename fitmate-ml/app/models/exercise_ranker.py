"""
Exercise Ranker model — XGBoost ranking model that scores candidate exercises
for a given user and body part, ranking them by predicted engagement/value.

Training target: implicit feedback from workout history.
  - An exercise performed after a long gap (high neglect) is treated as a
    positive signal that it was chosen for a reason → higher rank.
  - Frequency and progression rate are used as supporting signals.

Model file: {MODEL_DIR}/exercise_ranker.pkl
Scaler file: {MODEL_DIR}/ranker_scaler.pkl
"""
import os
import joblib
import mlflow
import numpy as np
import pandas as pd
from loguru import logger
from sklearn.preprocessing import StandardScaler
from xgboost import XGBRegressor

from app.config import settings

# Features used for ranking (must match feature_engineering.py output)
RANKER_FEATURES = [
    "days_since_last_done",
    "frequency_last_30_days",
    "progression_rate",
    "difficulty_level",
    "body_part_match_score",
]

MODEL_PATH = os.path.join(settings.model_dir, "exercise_ranker.pkl")
SCALER_PATH = os.path.join(settings.model_dir, "ranker_scaler.pkl")


class ExerciseRanker:
    """
    Wraps an XGBRegressor that outputs a relevance score per exercise.

    Usage:
        ranker = ExerciseRanker()
        ranker.load()                      # load from disk
        scores = ranker.predict(features_df)   # returns np.ndarray of scores
    """

    def __init__(self):
        self.model: XGBRegressor | None = None
        self.scaler: StandardScaler | None = None
        self._loaded = False

    # ── Lifecycle ─────────────────────────────────────────────────────────────

    def load(self) -> "ExerciseRanker":
        """Load a previously trained model from disk."""
        if not os.path.exists(MODEL_PATH):
            logger.warning(f"No ranker model found at {MODEL_PATH} — predictions will use rule engine")
            return self
        self.model = joblib.load(MODEL_PATH)
        self.scaler = joblib.load(SCALER_PATH)
        self._loaded = True
        logger.info("ExerciseRanker loaded from disk")
        return self

    def is_ready(self) -> bool:
        return self._loaded and self.model is not None

    # ── Training ──────────────────────────────────────────────────────────────

    def train(self, df: pd.DataFrame) -> dict:
        """
        Train on engineered exercise features.

        :param df: Output of feature_engineering.build_exercise_features()
                   Must contain RANKER_FEATURES columns plus a 'label' column
                   (e.g. 1 / (days_since_last_done + 1) as a proxy for preference).
        :returns: dict with training metrics.
        """
        # Synthetic label: exercises done more recently (lower neglect) were chosen
        # by the user → higher implied score. We invert neglect for training.
        if "label" not in df.columns:
            df = df.copy()
            df["label"] = 1.0 / (df["days_since_last_done"] + 1)

        X = df[RANKER_FEATURES].fillna(0)
        y = df["label"]

        self.scaler = StandardScaler()
        X_scaled = self.scaler.fit_transform(X)

        self.model = XGBRegressor(
            n_estimators=200,
            max_depth=4,
            learning_rate=0.05,
            subsample=0.8,
            colsample_bytree=0.8,
            random_state=42,
        )

        mlflow.set_tracking_uri(settings.mlflow_tracking_uri)
        mlflow.set_experiment(settings.mlflow_experiment_name)

        with mlflow.start_run(run_name="exercise_ranker") as run:
            self.model.fit(X_scaled, y)
            train_score = float(self.model.score(X_scaled, y))
            mlflow.log_param("n_estimators", 200)
            mlflow.log_metric("train_r2", train_score)
            logger.info(f"ExerciseRanker trained — R²={train_score:.4f}")

            os.makedirs(settings.model_dir, exist_ok=True)
            joblib.dump(self.model, MODEL_PATH)
            joblib.dump(self.scaler, SCALER_PATH)
            self._loaded = True

            return {"r2": train_score, "mlflow_run_id": run.info.run_id}

    # ── Inference ─────────────────────────────────────────────────────────────

    def predict(self, df: pd.DataFrame) -> np.ndarray:
        """Return relevance scores for each candidate exercise row."""
        if not self.is_ready():
            raise RuntimeError("ExerciseRanker is not loaded — call .load() first or trigger /train")
        X = df[RANKER_FEATURES].fillna(0)
        X_scaled = self.scaler.transform(X)
        return self.model.predict(X_scaled)


# Module-level singleton — loaded once at startup
ranker = ExerciseRanker()
