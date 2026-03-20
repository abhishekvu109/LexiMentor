"""
Data cleaning for fitmate-ml raw drill records.

Standardises the raw DataFrame produced by load_raw_drills() before
feature engineering runs. Steps applied in order:

  1. Filter to weight-based exercises only  (unit = kg / lbs)
  2. Normalise lbs → kg
  3. Drop zero / null weight rows
  4. Cap extreme outliers                   (1 – 500 kg)
  5. Deduplicate: keep best set per         (user, exercise, date)
  6. Require minimum session history per    (user, exercise)
  7. Encode training_type → ordinal int
"""
import pandas as pd
from loguru import logger


# ── Constants ─────────────────────────────────────────────────────────────────

# Only these unit values represent a liftable weight
WEIGHT_UNITS = {"kg", "lbs"}

# Ordinal encoding: heavier/more-intense training types get higher values
TRAINING_TYPE_MAP = {
    "STRENGTH":    2,
    "HYPERTROPHY": 1,
    "ENDURANCE":   0,
}
DEFAULT_TRAINING_TYPE_ENCODED = 1   # HYPERTROPHY — sensible middle-ground default

# Physiological bounds (kg) — anything outside this range is a data error
MIN_WEIGHT_KG = 1.0
MAX_WEIGHT_KG = 500.0

# Minimum number of distinct sessions an (user, exercise) pair must have
# to be included in training.  Fewer sessions → can't compute labels / slopes.
MIN_SESSIONS = 2


# ── Public API ────────────────────────────────────────────────────────────────

def clean_raw_drills(df: pd.DataFrame) -> pd.DataFrame:
    """
    Full cleaning pipeline applied to the raw drill DataFrame.

    Returns a cleaned copy; the input is never mutated.
    Logs a summary of how many rows each step removes.
    """
    if df.empty:
        logger.warning("clean_raw_drills received an empty DataFrame — nothing to clean")
        return df

    original_len = len(df)
    df = df.copy()

    df = _filter_weight_units(df)
    df = _convert_lbs_to_kg(df)
    df = _drop_invalid_weights(df)
    df = _cap_outliers(df)
    df = _deduplicate(df)
    df = _filter_min_history(df)
    df = _encode_training_type(df)

    removed = original_len - len(df)
    logger.info(
        f"Data cleaning complete: {original_len} → {len(df)} rows "
        f"({removed} removed, {len(df['exercise_ref_id'].unique())} unique exercises)"
    )
    return df


# ── Private helpers ───────────────────────────────────────────────────────────

def _filter_weight_units(df: pd.DataFrame) -> pd.DataFrame:
    """Keep only rows where the drill unit is a recognised weight unit."""
    before = len(df)
    mask = df["weight_unit"].str.strip().str.lower().isin(WEIGHT_UNITS)
    df = df[mask].copy()
    logger.debug(f"  [1] Unit filter      : {before:>5} → {len(df):>5} rows  "
                 f"(dropped {before - len(df)} non-weight rows)")
    return df


def _convert_lbs_to_kg(df: pd.DataFrame) -> pd.DataFrame:
    """Convert lbs measurements to kg and normalise the unit column."""
    lbs_mask = df["weight_unit"].str.strip().str.lower() == "lbs"
    n_lbs = lbs_mask.sum()
    if n_lbs:
        df.loc[lbs_mask, "weight"] = (df.loc[lbs_mask, "weight"] * 0.453592).round(2)
        df.loc[lbs_mask, "weight_unit"] = "kg"
        logger.debug(f"  [2] lbs → kg        : converted {n_lbs} entries")
    else:
        logger.debug("  [2] lbs → kg        : no lbs entries found")
    return df


def _drop_invalid_weights(df: pd.DataFrame) -> pd.DataFrame:
    """Drop rows with null, NaN, or non-positive weight."""
    before = len(df)
    df = df[df["weight"].notna() & (df["weight"] > 0)].copy()
    logger.debug(f"  [3] Invalid weights  : {before:>5} → {len(df):>5} rows  "
                 f"(dropped {before - len(df)} null/zero rows)")
    return df


def _cap_outliers(df: pd.DataFrame) -> pd.DataFrame:
    """Clip weights to a physiologically sane range [MIN_WEIGHT_KG, MAX_WEIGHT_KG]."""
    n_low  = (df["weight"] < MIN_WEIGHT_KG).sum()
    n_high = (df["weight"] > MAX_WEIGHT_KG).sum()
    df["weight"] = df["weight"].clip(MIN_WEIGHT_KG, MAX_WEIGHT_KG)
    if n_low or n_high:
        logger.warning(f"  [4] Outlier cap      : clamped {n_low} below {MIN_WEIGHT_KG} kg "
                       f"and {n_high} above {MAX_WEIGHT_KG} kg")
    else:
        logger.debug(f"  [4] Outlier cap      : no outliers found")
    return df


def _deduplicate(df: pd.DataFrame) -> pd.DataFrame:
    """
    Within the same (username, exercise_ref_id, date), keep only the
    row with the highest weight — i.e. the user's best set that day.
    Multiple sets per session collapse to a single representative row.
    """
    before = len(df)
    df["_date"] = df["routine_date"].dt.date
    df = (
        df.sort_values("weight", ascending=False)
          .drop_duplicates(subset=["username", "exercise_ref_id", "_date"])
          .drop(columns=["_date"])
          .sort_values(["username", "exercise_ref_id", "routine_date"])
          .reset_index(drop=True)
    )
    logger.debug(f"  [5] Deduplication    : {before:>5} → {len(df):>5} rows  "
                 f"(collapsed {before - len(df)} duplicate sets)")
    return df


def _filter_min_history(df: pd.DataFrame) -> pd.DataFrame:
    """
    Drop (username, exercise_ref_id) pairs that appear in fewer than
    MIN_SESSIONS distinct sessions.  These pairs cannot produce training
    labels (need at least 2 sessions to form a current→next pair) and
    cannot produce a meaningful trend slope.
    """
    before = len(df)
    session_counts = df.groupby(["username", "exercise_ref_id"])["routine_date"].transform("nunique")
    df = df[session_counts >= MIN_SESSIONS].copy()
    logger.debug(f"  [6] Min history      : {before:>5} → {len(df):>5} rows  "
                 f"(need {MIN_SESSIONS}+ sessions per exercise)")
    return df


def _encode_training_type(df: pd.DataFrame) -> pd.DataFrame:
    """
    Map training_type string column to an ordinal integer column
    training_type_encoded.  Unknown / null values fall back to the default.
    """
    if "training_type" not in df.columns:
        logger.warning("  [7] training_type    : column missing — defaulting all to HYPERTROPHY (1)")
        df["training_type_encoded"] = DEFAULT_TRAINING_TYPE_ENCODED
        return df

    df["training_type_encoded"] = (
        df["training_type"]
          .str.strip()
          .str.upper()
          .map(TRAINING_TYPE_MAP)
          .fillna(DEFAULT_TRAINING_TYPE_ENCODED)
          .astype(int)
    )
    counts = df["training_type_encoded"].value_counts().to_dict()
    logger.debug(f"  [7] training_type    : encoded → {counts}")
    return df
