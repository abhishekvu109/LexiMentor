from fastapi import APIRouter

from app.schemas.recommendation import (
    NextLoadRequest,
    NextLoadResponse,
    RoutineCsvRequest,
    RoutineInsightsResponse,
)
from app.services.llm_explainer import LlmExplainer
from app.services.recommendation_engine import RecommendationEngine
from app.services.routine_intelligence import RoutineIntelligenceEngine

router = APIRouter(prefix="/v1/recommendations", tags=["recommendations"])
engine = RecommendationEngine()
explainer = LlmExplainer()
routine_engine = RoutineIntelligenceEngine()


@router.post("/next-load", response_model=NextLoadResponse)
async def recommend_next_load(payload: NextLoadRequest) -> NextLoadResponse:
    recommendation = engine.recommend(payload)
    explanation = await explainer.explain(payload, recommendation)
    if explanation:
        recommendation.llm_explanation = explanation.strip()
    return recommendation


@router.post("/routine-insights", response_model=RoutineInsightsResponse)
def recommend_routine_focus(payload: RoutineCsvRequest) -> RoutineInsightsResponse:
    return routine_engine.recommend(payload)
