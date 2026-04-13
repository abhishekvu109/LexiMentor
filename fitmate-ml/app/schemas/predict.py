from pydantic import BaseModel, Field


# ── Exercise ranker ───────────────────────────────────────────────────────────

class ExerciseFeatures(BaseModel):
    """Features for a single candidate exercise."""
    exercise_ref_id: int
    exercise_name: str
    days_since_last_done: int = Field(ge=0, description="999 means never done")
    frequency_last_30_days: int = Field(ge=0)
    progression_rate: float = Field(description="% weight change over last 30 days")
    difficulty_level: int = Field(ge=1, le=5)
    body_part_match_score: float = Field(ge=0.0, le=1.0)


class ExerciseRankerRequest(BaseModel):
    username: str
    target_body_part_ref_id: int
    training_ref_id: int | None = None
    candidates: list[ExerciseFeatures]


class RankedExercise(BaseModel):
    exercise_ref_id: int
    exercise_name: str
    rank: int
    score: float


class ExerciseRankerResponse(BaseModel):
    username: str
    ranked_exercises: list[RankedExercise]
    model_version: str


# ── Performance predictor ─────────────────────────────────────────────────────

class PerformanceFeatures(BaseModel):
    """Features for predicting weight / reps for one exercise."""
    exercise_ref_id: int
    username: str
    last_weight: float
    last_reps: int
    last_sets: int
    personal_record: float
    days_since_last_session: int
    trend_slope: float = Field(description="Linear slope of weight over last 5 sessions")
    sessions_this_week: int
    training_type: str = Field(description="e.g. STRENGTH, HYPERTROPHY, ENDURANCE")


class PerformancePrediction(BaseModel):
    exercise_ref_id: int
    recommended_weight: float
    recommended_sets: int
    recommended_reps: int
    confidence: float = Field(ge=0.0, le=1.0)
    progression_note: str


class PerformancePredictorResponse(BaseModel):
    predictions: list[PerformancePrediction]
    model_version: str
