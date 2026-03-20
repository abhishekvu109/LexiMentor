"""
/train endpoint — triggers model retraining from the MySQL database.
Called by the async-job-spring-boot-starter scheduler in fitmate-service
(weekly cron), or manually by an admin.
"""
from datetime import datetime
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from loguru import logger

from app.db.session import get_db
from app.features.feature_engineering import (
    load_raw_drills,
    build_exercise_features,
    build_performance_features,
    generate_progression_labels,
)
from app.models.exercise_ranker import ranker
from app.models.performance_predictor import predictor
from app.schemas.train import TrainRequest, TrainResponse

router = APIRouter(prefix="/train", tags=["train"])


@router.post("", response_model=TrainResponse)
def trigger_training(request: TrainRequest = TrainRequest(), db: Session = Depends(get_db)):
    """
    Retrain one or both ML models using the latest workout data from MySQL.

    Steps:
      1. Extract drill history from the DB.
      2. Engineer features.
      3. Train the requested model(s).
      4. Persist artefacts and log to MLflow.
    """
    logger.info(f"Training triggered: model={request.model} lookback={request.lookback_days}d")

    raw = load_raw_drills(db, lookback_days=request.lookback_days)
    if raw.empty:
        return TrainResponse(
            status="skipped — no data",
            models_trained=[],
            triggered_at=datetime.utcnow(),
            mlflow_run_ids={},
        )

    trained = []
    run_ids = {}

    if request.model in ("ranker", "all"):
        ex_features = build_exercise_features(raw)
        result = ranker.train(ex_features)
        trained.append("exercise_ranker")
        run_ids["exercise_ranker"] = result.get("mlflow_run_id", "")

    if request.model in ("predictor", "all"):
        labels = generate_progression_labels(raw)
        perf_features = build_performance_features(raw)
        # Merge labels into features for supervised training.
        # perf_features and labels both contain last_weight / last_reps; drop
        # them from perf_features first so pandas doesn't rename them to
        # last_weight_x / last_weight_y — the per-pair versions in labels are
        # the correct training context.
        perf_extra = perf_features.drop(
            columns=["last_weight", "last_reps"], errors="ignore"
        )
        merged = labels.merge(perf_extra, on=["username", "exercise_ref_id"], how="inner")
        if not merged.empty:
            result = predictor.train(merged)
            trained.append("performance_predictor")
            run_ids.update(result.get("run_ids", {}))
        else:
            logger.warning("Not enough labelled data to train performance predictor — need 2+ sessions per exercise")

    return TrainResponse(
        status="success",
        models_trained=trained,
        triggered_at=datetime.utcnow(),
        mlflow_run_ids=run_ids,
    )
