"""
Feature engineering for the fitmate ML models.

Reads directly from the MySQL database (same schema as fitmate-service) and
produces DataFrames ready for XGBoost training or inference.

Tables used:
    fitmate_routine        → workout sessions (date, username, training type)
    fitmate_routine_drill  → individual sets within a session (weight, reps)
    fitmate_exercise       → exercise catalogue (difficulty, body part, unit)
"""
import pandas as pd
import numpy as np
from datetime import date, timedelta
from sqlalchemy.orm import Session
from loguru import logger


# ── Raw data extraction ───────────────────────────────────────────────────────

DRILL_QUERY = """
    SELECT
        d.ref_id          AS drill_ref_id,
        d.measurement     AS weight,
        d.repetition      AS reps,
        d.unit            AS weight_unit,
        d.burnt_calories,
        r.ref_id          AS routine_ref_id,
        r.routine_date,
        r.username,
        e.ref_id          AS exercise_ref_id,
        e.name            AS exercise_name,
        e.difficulty_level,
        e.unit            AS exercise_unit,
        e.target_body_part AS body_part_ref_id
    FROM fitmate_routine_drill d
    JOIN fitmate_routine       r ON d.routine     = r.ref_id
    JOIN fitmate_exercise      e ON d.exercise_id = e.id
    WHERE r.routine_date >= :from_date
    ORDER BY r.username, e.ref_id, r.routine_date
"""


def load_raw_drills(db: Session, lookback_days: int = 180) -> pd.DataFrame:
    """Load all drill records within the lookback window."""
    from_date = date.today() - timedelta(days=lookback_days)
    result = db.execute(DRILL_QUERY, {"from_date": from_date})
    df = pd.DataFrame(result.fetchall(), columns=result.keys())
    df["routine_date"] = pd.to_datetime(df["routine_date"])
    logger.info(f"Loaded {len(df)} drill records (lookback={lookback_days}d)")
    return df


# ── Exercise-level features ───────────────────────────────────────────────────

def build_exercise_features(df: pd.DataFrame, reference_date: date | None = None) -> pd.DataFrame:
    """
    Compute per-(username, exercise) features for the exercise ranker model.

    Returns one row per (username, exercise_ref_id) with columns:
        days_since_last_done, frequency_last_30_days, progression_rate,
        difficulty_level, body_part_ref_id
    """
    ref = pd.Timestamp(reference_date or date.today())
    cutoff_30 = ref - pd.Timedelta(days=30)

    features = []
    for (username, ex_ref_id), group in df.groupby(["username", "exercise_ref_id"]):
        group = group.sort_values("routine_date")

        last_date = group["routine_date"].max()
        days_since = (ref - last_date).days

        freq_30 = int((group["routine_date"] >= cutoff_30).sum())

        # Progression rate: % weight change over available history
        if len(group) >= 2:
            first_w = group.iloc[0]["weight"]
            last_w = group.iloc[-1]["weight"]
            prog_rate = (last_w - first_w) / (first_w + 1e-9)
        else:
            prog_rate = 0.0

        features.append({
            "username": username,
            "exercise_ref_id": ex_ref_id,
            "exercise_name": group.iloc[-1]["exercise_name"],
            "days_since_last_done": days_since,
            "frequency_last_30_days": freq_30,
            "progression_rate": round(prog_rate, 4),
            "difficulty_level": group.iloc[-1]["difficulty_level"],
            "body_part_ref_id": group.iloc[-1]["body_part_ref_id"],
        })

    return pd.DataFrame(features)


# ── Performance features ──────────────────────────────────────────────────────

def build_performance_features(df: pd.DataFrame) -> pd.DataFrame:
    """
    Compute per-(username, exercise) features for the performance predictor.

    Returns one row per (username, exercise_ref_id) with columns needed to
    predict recommended_weight, recommended_sets, recommended_reps.
    """
    features = []
    for (username, ex_ref_id), group in df.groupby(["username", "exercise_ref_id"]):
        group = group.sort_values("routine_date")

        last_row = group.iloc[-1]
        pr = group["weight"].max()

        # Linear slope over last 5 sessions
        last5 = group.tail(5)
        if len(last5) >= 2:
            x = np.arange(len(last5))
            slope = float(np.polyfit(x, last5["weight"].values, 1)[0])
        else:
            slope = 0.0

        last_date = group["routine_date"].max()
        days_since = (pd.Timestamp(date.today()) - last_date).days

        cutoff_week = pd.Timestamp(date.today()) - pd.Timedelta(days=7)
        sessions_this_week = int((group["routine_date"] >= cutoff_week).sum())

        features.append({
            "username": username,
            "exercise_ref_id": ex_ref_id,
            "last_weight": last_row["weight"],
            "last_reps": last_row["reps"],
            "personal_record": pr,
            "days_since_last_session": days_since,
            "trend_slope": round(slope, 4),
            "sessions_this_week": sessions_this_week,
        })

    return pd.DataFrame(features)


# ── Label generation (for supervised training) ────────────────────────────────

def generate_progression_labels(df: pd.DataFrame) -> pd.DataFrame:
    """
    Generate target labels for the performance predictor by looking at what
    weight the user actually used in the NEXT session after each session.

    This turns the historical drill log into a supervised dataset:
        Features: session N stats → Label: weight used in session N+1
    """
    labelled = []
    for (username, ex_ref_id), group in df.groupby(["username", "exercise_ref_id"]):
        group = group.sort_values("routine_date").reset_index(drop=True)
        for i in range(len(group) - 1):
            curr = group.iloc[i]
            nxt = group.iloc[i + 1]
            labelled.append({
                "username": username,
                "exercise_ref_id": ex_ref_id,
                "last_weight": curr["weight"],
                "last_reps": int(curr["reps"]),
                "next_weight": nxt["weight"],    # ← regression target
                "next_reps": int(nxt["reps"]),   # ← secondary target
            })

    return pd.DataFrame(labelled)
