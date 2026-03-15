from app.schemas.recommendation import RoutineCsvRequest
from app.services.routine_intelligence import RoutineIntelligenceEngine


def test_routine_insights_returns_top_muscles() -> None:
    csv_data = """routine_date,muscle_group,measurement,repetition
2026-01-01,chest,80,8
2026-01-05,chest,82,8
2026-01-08,chest,82,7
2026-01-12,chest,82,7
2026-01-02,back,70,10
2026-01-06,back,72,10
2026-01-10,back,74,10
2026-01-14,back,75,10
2026-01-03,legs,100,6
2026-01-07,legs,100,6
2026-01-11,legs,102,6
2026-01-15,legs,102,6
"""
    engine = RoutineIntelligenceEngine()
    req = RoutineCsvRequest(username="abhi", csv_data=csv_data, top_n_muscles=2)

    result = engine.recommend(req)

    assert result.processed_rows == 12
    assert result.muscles_analyzed == 3
    assert len(result.recommendations) == 2
    assert all(0 <= rec.priority_score <= 1 for rec in result.recommendations)
    assert all(0 <= rec.recommended_weight_increase_pct <= 10 for rec in result.recommendations)


def test_routine_insights_missing_required_columns() -> None:
    csv_data = """date,muscle,weight
2026-01-01,chest,80
"""
    engine = RoutineIntelligenceEngine()
    req = RoutineCsvRequest(username="abhi", csv_data=csv_data)

    result = engine.recommend(req)

    assert result.processed_rows == 0
    assert result.recommendations == []
    assert any("Missing required CSV columns" in warning for warning in result.warnings)
