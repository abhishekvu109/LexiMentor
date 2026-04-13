"""
fitmate-ml — FastAPI ML microservice for workout recommendations.

Run locally:
    uvicorn main:app --reload --port 8001

Swagger UI: http://localhost:8001/docs
"""
from contextlib import asynccontextmanager
from loguru import logger
from fastapi import FastAPI

from app.config import settings
from app.models.exercise_ranker import ranker
from app.models.performance_predictor import predictor
from app.routers import predict, train


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Load model artefacts on startup; log on shutdown."""
    logger.info(f"Starting fitmate-ml [{settings.app_env}]")
    ranker.load()
    predictor.load()
    if not ranker.is_ready():
        logger.warning("Exercise ranker not ready — POST /train to initialise")
    if not predictor.is_ready():
        logger.warning("Performance predictor not ready — POST /train to initialise")
    yield
    logger.info("fitmate-ml shutting down")


app = FastAPI(
    title="fitmate-ml",
    description=(
        "ML microservice for fitmate. Provides exercise ranking and "
        "performance prediction to augment the Spring Boot rule engine."
    ),
    version="1.0.0",
    lifespan=lifespan,
)

app.include_router(predict.router)
app.include_router(train.router)


@app.get("/health", tags=["ops"])
def health():
    return {
        "status": "ok",
        "ranker_ready": ranker.is_ready(),
        "predictor_ready": predictor.is_ready(),
    }
