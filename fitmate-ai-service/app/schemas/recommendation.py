from datetime import date

from pydantic import BaseModel, Field


class DrillHistoryPoint(BaseModel):
    routine_date: date
    measurement: float = Field(ge=0)
    repetition: int = Field(ge=0)


class NextLoadRequest(BaseModel):
    username: str
    exercise_name: str
    target_rep_min: int = Field(default=6, ge=1)
    target_rep_max: int = Field(default=12, ge=1)
    increment_pct: float = Field(default=2.5, ge=0.0, le=20.0)
    history: list[DrillHistoryPoint]


class NextLoadResponse(BaseModel):
    username: str
    exercise_name: str
    recommended_measurement: float
    decision: str
    confidence: float
    reasons: list[str]
    llm_explanation: str | None = None


class RoutineCsvRequest(BaseModel):
    username: str
    csv_data: str = Field(min_length=1)
    target_rep_min: int = Field(default=6, ge=1)
    target_rep_max: int = Field(default=12, ge=1)
    top_n_muscles: int = Field(default=3, ge=1, le=12)


class MusclePriorityRecommendation(BaseModel):
    muscle_name: str
    priority_score: float = Field(ge=0.0, le=1.0)
    recommended_weight_increase_pct: float = Field(ge=0.0, le=10.0)
    confidence: float = Field(ge=0.0, le=1.0)
    reasons: list[str]


class RoutineInsightsResponse(BaseModel):
    username: str
    processed_rows: int
    muscles_analyzed: int
    recommendations: list[MusclePriorityRecommendation]
    warnings: list[str] = Field(default_factory=list)
