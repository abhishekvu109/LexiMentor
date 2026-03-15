"""
/predict endpoints — called by fitmate-service (Spring Boot) when the ML
models are ready. The rule engine in Spring Boot acts as a fallback when
these endpoints are unavailable or when the models are not yet trained.
"""
import pandas as pd
from fastapi import APIRouter, HTTPException

from app.models.exercise_ranker import ranker
from app.models.performance_predictor import predictor
from app.schemas.predict import (
    ExerciseRankerRequest,
    ExerciseRankerResponse,
    RankedExercise,
    PerformanceFeatures,
    PerformancePrediction,
    PerformancePredictorResponse,
)

router = APIRouter(prefix="/predict", tags=["predict"])

_RANKER_VERSION = "exercise_ranker_v1"
_PREDICTOR_VERSION = "performance_predictor_v1"


@router.post("/exercises", response_model=ExerciseRankerResponse)
def rank_exercises(request: ExerciseRankerRequest):
    """
    Rank candidate exercises by predicted relevance for this user.

    Called by fitmate-service with a pre-filtered list of exercises
    (already filtered by body part / training type). The ML model
    re-ranks them based on the user's personal history features.
    """
    if not ranker.is_ready():
        raise HTTPException(
            status_code=503,
            detail="Exercise ranker model not trained yet. POST /train to trigger training."
        )

    candidates_df = pd.DataFrame([c.model_dump() for c in request.candidates])
    scores = ranker.predict(candidates_df)

    ranked = sorted(
        zip(request.candidates, scores),
        key=lambda x: x[1],
        reverse=True,
    )

    return ExerciseRankerResponse(
        username=request.username,
        ranked_exercises=[
            RankedExercise(
                exercise_ref_id=ex.exercise_ref_id,
                exercise_name=ex.exercise_name,
                rank=rank + 1,
                score=round(float(score), 4),
            )
            for rank, (ex, score) in enumerate(ranked)
        ],
        model_version=_RANKER_VERSION,
    )


@router.post("/performance", response_model=PerformancePredictorResponse)
def predict_performance(features: list[PerformanceFeatures]):
    """
    Predict recommended weight + reps for each exercise.

    Accepts a list so that one call can cover all exercises in a planned
    session, minimising round-trips from fitmate-service.
    """
    if not predictor.is_ready():
        raise HTTPException(
            status_code=503,
            detail="Performance predictor not trained yet. POST /train to trigger training."
        )

    df = pd.DataFrame([f.model_dump() for f in features])
    weights, reps_arr = predictor.predict(df)

    predictions = []
    for i, feat in enumerate(features):
        w = round(float(weights[i]), 2)
        r = int(reps_arr[i])
        delta = w - feat.last_weight
        note = (
            f"Increase to {w} kg (+{delta:.1f})" if delta > 0 else
            f"Deload to {w} kg ({delta:.1f})" if delta < -1 else
            f"Maintain {w} kg"
        )
        predictions.append(PerformancePrediction(
            exercise_ref_id=feat.exercise_ref_id,
            recommended_weight=w,
            recommended_sets=3,
            recommended_reps=r,
            confidence=0.75,   # TODO: derive from model uncertainty
            progression_note=note,
        ))

    return PerformancePredictorResponse(
        predictions=predictions,
        model_version=_PREDICTOR_VERSION,
    )
