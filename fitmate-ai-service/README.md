# Fitmate AI Service (Python)

Python microservice for advanced training recommendations (next weight, deload detection, consistency-aware progression) using a hybrid approach:

- Deterministic safety/progression logic (always on)
- ML model layer (optional, pluggable)
- LLM explanation layer (optional)

## Quick Start

```bash
cd D:\Coding\LexiMentor\fitmate-ai-service
python -m venv .venv
.venv\Scripts\activate
pip install -e .
uvicorn app.main:app --reload --port 8091
```

Health check:

```bash
curl http://localhost:8091/health
```

## Core Endpoint

`POST /v1/recommendations/next-load`

Input includes user history for one exercise (date, weight/measurement, reps), plus target rep range.

Response includes:

- `recommended_measurement`
- `decision` (`INCREASE`, `HOLD`, `DELOAD`, `DECREASE`)
- `confidence`
- `reasons`
- `llm_explanation` (optional)

## CSV Routine Intelligence Endpoint

`POST /v1/recommendations/routine-insights`

This endpoint accepts your full workout CSV and returns:

- which muscles to prioritize (`priority_score`)
- suggested weight progression per muscle (`recommended_weight_increase_pct`)
- confidence + reasoning

### Request Body

```json
{
  "username": "abhi",
  "csv_data": "routine_date,muscle_group,measurement,repetition\n2026-01-01,chest,80,8\n2026-01-05,chest,82,8",
  "target_rep_min": 6,
  "target_rep_max": 12,
  "top_n_muscles": 3
}
```

### Supported CSV Header Aliases

- Date: `routine_date`, `date`, `session_date`, `workout_date`
- Muscle: `muscle_group`, `muscle`, `primary_muscle`, `target_muscle`
- Weight: `measurement`, `weight`, `load`, `kg`, `lbs`
- Reps: `repetition`, `repetitions`, `reps`, `rep`

## Integration With Java Service

1. Java service prepares drill history for a username + exercise.
2. Java calls this service endpoint.
3. Java persists/returns recommendation to UI.

## Next Build Steps

1. Add Fitmate Java API client auth and pull history directly.
2. Add model training pipeline (`train/`) with LightGBM/XGBoost.
3. Add LLM explanation endpoint via Ollama/OpenAI.
4. Add evaluation harness for recommendation quality.
