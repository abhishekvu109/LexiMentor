from pydantic import BaseModel
from datetime import datetime


class TrainRequest(BaseModel):
    """Optional parameters for triggering a manual retrain."""
    model: str = "all"           # "ranker" | "predictor" | "all"
    lookback_days: int = 180     # how many days of history to use
    force: bool = False          # retrain even if recent model exists


class TrainResponse(BaseModel):
    status: str
    models_trained: list[str]
    triggered_at: datetime
    mlflow_run_ids: dict[str, str]
