from datetime import date

from app.schemas.recommendation import DrillHistoryPoint, NextLoadRequest
from app.services.recommendation_engine import RecommendationEngine


def test_recommend_deload_after_long_break() -> None:
    req = NextLoadRequest(
        username="abhi",
        exercise_name="Bench Press",
        target_rep_min=6,
        target_rep_max=10,
        history=[
            DrillHistoryPoint(routine_date=date(2026, 1, 1), measurement=80, repetition=8),
            DrillHistoryPoint(routine_date=date(2026, 1, 5), measurement=80, repetition=9),
        ],
    )
    engine = RecommendationEngine()
    result = engine.recommend(req, today=date(2026, 1, 25))

    assert result.decision == "DELOAD"
    assert result.recommended_measurement < 80
