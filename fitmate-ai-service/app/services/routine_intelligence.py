from __future__ import annotations

import csv
from dataclasses import dataclass
from datetime import date
from io import StringIO
from math import log, sqrt

import numpy as np
from sklearn.ensemble import RandomForestRegressor
from sklearn.neural_network import MLPRegressor
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler

from app.schemas.recommendation import (
    MusclePriorityRecommendation,
    RoutineCsvRequest,
    RoutineInsightsResponse,
)

_DATE_COLUMNS = ("routine_date", "date", "session_date", "workout_date")
_MUSCLE_COLUMNS = ("muscle_group", "muscle", "primary_muscle", "target_muscle")
_WEIGHT_COLUMNS = ("measurement", "weight", "load", "kg", "lbs")
_REP_COLUMNS = ("repetition", "repetitions", "reps", "rep")


@dataclass
class _RoutineRow:
    routine_date: date
    muscle_group: str
    measurement: float
    repetition: int

    @property
    def volume(self) -> float:
        return self.measurement * self.repetition


class RoutineIntelligenceEngine:
    """Hybrid recommender: trend ML + neural sequence model + bandit policy."""

    _BANDIT_ACTIONS = (0.0, 1.25, 2.5, 5.0)

    def recommend(self, request: RoutineCsvRequest) -> RoutineInsightsResponse:
        parsed_rows, warnings = self._parse_csv_rows(request.csv_data)
        if not parsed_rows:
            warnings.append("No valid rows found in CSV after parsing")
            return RoutineInsightsResponse(
                username=request.username,
                processed_rows=0,
                muscles_analyzed=0,
                recommendations=[],
                warnings=warnings,
            )

        grouped: dict[str, list[_RoutineRow]] = {}
        for row in parsed_rows:
            grouped.setdefault(row.muscle_group, []).append(row)
        for rows in grouped.values():
            rows.sort(key=lambda item: item.routine_date)

        max_frequency = max(len(rows) for rows in grouped.values())
        recommendations: list[MusclePriorityRecommendation] = []

        for muscle, rows in grouped.items():
            if len(rows) < 3:
                recommendations.append(
                    MusclePriorityRecommendation(
                        muscle_name=muscle,
                        priority_score=0.55,
                        recommended_weight_increase_pct=0.0,
                        confidence=0.35,
                        reasons=[
                            "Not enough history for robust modeling",
                            "Hold load until more sessions are logged",
                        ],
                    )
                )
                continue

            features, targets, recent_growth = self._build_supervised_dataset(rows)
            ml_pred = self._predict_with_ml(features, targets)
            dl_pred = self._predict_with_dl(features, targets)
            trend_pred = recent_growth

            blended_growth = (0.4 * ml_pred) + (0.35 * dl_pred) + (0.25 * trend_pred)
            recommended_increase, bandit_conf = self._bandit_recommendation(
                rows,
                request.target_rep_min,
                request.target_rep_max,
                blended_growth,
            )

            stagnation_score = self._stagnation_score(recent_growth)
            frequency_gap = 1.0 - (len(rows) / max_frequency)
            priority = float(np.clip((0.6 * stagnation_score) + (0.4 * frequency_gap), 0.0, 1.0))
            confidence = float(np.clip(0.45 + (0.45 * bandit_conf), 0.2, 0.95))

            reasons = [
                f"Recent growth estimate: {recent_growth * 100:.2f}%",
                f"Model-predicted next growth: {blended_growth * 100:.2f}%",
                f"Training frequency gap score: {frequency_gap:.2f}",
            ]
            if recommended_increase == 0.0:
                reasons.append("Progress signal is weak; hold weight for this muscle")
            else:
                reasons.append(
                    f"Bandit policy selected +{recommended_increase:.2f}% weight progression"
                )

            recommendations.append(
                MusclePriorityRecommendation(
                    muscle_name=muscle,
                    priority_score=round(priority, 3),
                    recommended_weight_increase_pct=round(recommended_increase, 2),
                    confidence=round(confidence, 3),
                    reasons=reasons,
                )
            )

        recommendations.sort(key=lambda item: item.priority_score, reverse=True)
        return RoutineInsightsResponse(
            username=request.username,
            processed_rows=len(parsed_rows),
            muscles_analyzed=len(grouped),
            recommendations=recommendations[: request.top_n_muscles],
            warnings=warnings,
        )

    def _parse_csv_rows(self, csv_data: str) -> tuple[list[_RoutineRow], list[str]]:
        warnings: list[str] = []
        reader = csv.DictReader(StringIO(csv_data.strip()))
        if not reader.fieldnames:
            return [], ["CSV does not have a header row"]

        mapped = {field.strip().lower(): field for field in reader.fieldnames if field}
        date_key = self._resolve_column(mapped, _DATE_COLUMNS)
        muscle_key = self._resolve_column(mapped, _MUSCLE_COLUMNS)
        weight_key = self._resolve_column(mapped, _WEIGHT_COLUMNS)
        reps_key = self._resolve_column(mapped, _REP_COLUMNS)

        missing = []
        if not date_key:
            missing.append("date")
        if not muscle_key:
            missing.append("muscle_group")
        if not weight_key:
            missing.append("measurement")
        if not reps_key:
            missing.append("repetition")
        if missing:
            return [], [f"Missing required CSV columns: {', '.join(missing)}"]

        rows: list[_RoutineRow] = []
        for index, row in enumerate(reader, start=2):
            try:
                parsed_date = date.fromisoformat(str(row[date_key]).strip())
                muscle = str(row[muscle_key]).strip().lower()
                measurement = float(str(row[weight_key]).strip())
                repetition = int(float(str(row[reps_key]).strip()))
                if not muscle or measurement < 0 or repetition < 0:
                    raise ValueError("Invalid measurement/repetition/muscle values")
                rows.append(
                    _RoutineRow(
                        routine_date=parsed_date,
                        muscle_group=muscle,
                        measurement=measurement,
                        repetition=repetition,
                    )
                )
            except Exception:
                warnings.append(f"Skipped invalid row at line {index}")
        return rows, warnings

    def _resolve_column(self, mapped: dict[str, str], aliases: tuple[str, ...]) -> str | None:
        for alias in aliases:
            if alias in mapped:
                return mapped[alias]
        return None

    def _build_supervised_dataset(self, rows: list[_RoutineRow]) -> tuple[np.ndarray, np.ndarray, float]:
        features: list[list[float]] = []
        targets: list[float] = []
        growth_changes: list[float] = []

        for idx in range(1, len(rows)):
            prev = rows[idx - 1]
            current = rows[idx]
            gap_days = (current.routine_date - prev.routine_date).days
            prev_volume = max(prev.volume, 1.0)
            current_volume = current.volume
            growth = (current_volume - prev_volume) / prev_volume
            growth_changes.append(growth)

            rolling_window = rows[max(0, idx - 3) : idx]
            rolling_volume = float(np.mean([item.volume for item in rolling_window]))
            features.append(
                [
                    prev.measurement,
                    prev.repetition,
                    gap_days,
                    prev_volume,
                    rolling_volume,
                    idx / len(rows),
                ]
            )
            targets.append(growth)

        recent_growth = float(np.mean(growth_changes[-3:])) if growth_changes else 0.0
        return np.array(features, dtype=float), np.array(targets, dtype=float), recent_growth

    def _predict_with_ml(self, features: np.ndarray, targets: np.ndarray) -> float:
        if len(features) < 3:
            return float(targets[-1]) if len(targets) > 0 else 0.0
        model = RandomForestRegressor(n_estimators=120, random_state=42, min_samples_leaf=2)
        model.fit(features, targets)
        prediction = model.predict(features[-1].reshape(1, -1))[0]
        return float(np.clip(prediction, -0.25, 0.25))

    def _predict_with_dl(self, features: np.ndarray, targets: np.ndarray) -> float:
        if len(features) < 4:
            return float(targets[-1]) if len(targets) > 0 else 0.0
        model = Pipeline(
            [
                ("scale", StandardScaler()),
                (
                    "mlp",
                    MLPRegressor(
                        hidden_layer_sizes=(24, 12),
                        activation="relu",
                        max_iter=500,
                        random_state=42,
                        learning_rate_init=0.01,
                    ),
                ),
            ]
        )
        model.fit(features, targets)
        prediction = model.predict(features[-1].reshape(1, -1))[0]
        return float(np.clip(prediction, -0.25, 0.25))

    def _bandit_recommendation(
        self,
        rows: list[_RoutineRow],
        target_rep_min: int,
        target_rep_max: int,
        blended_growth: float,
    ) -> tuple[float, float]:
        counts = np.ones(len(self._BANDIT_ACTIONS), dtype=float)
        rewards = np.zeros(len(self._BANDIT_ACTIONS), dtype=float)

        for idx in range(1, len(rows)):
            prev = rows[idx - 1]
            current = rows[idx]
            applied_pct = ((current.measurement - prev.measurement) / max(prev.measurement, 1.0)) * 100
            action_idx = self._closest_action_index(applied_pct)
            prev_volume = max(prev.volume, 1.0)
            reward = (current.volume - prev_volume) / prev_volume

            if current.repetition < target_rep_min:
                reward -= 0.06
            if current.repetition > target_rep_max + 2:
                reward -= 0.03

            counts[action_idx] += 1
            rewards[action_idx] += reward

        means = rewards / counts
        total = float(np.sum(counts))
        exploration = np.sqrt((2.0 * log(total + 1.0)) / counts)
        ucb = means + (0.65 * exploration)
        best_idx = int(np.argmax(ucb))
        chosen_action = self._BANDIT_ACTIONS[best_idx]

        if blended_growth < -0.03:
            chosen_action = max(0.0, chosen_action - 1.25)
        elif blended_growth > 0.08:
            chosen_action = min(5.0, chosen_action + 1.25)

        confidence = float(np.clip(1.0 - (np.std(means) + 0.1), 0.25, 0.9))
        return chosen_action, confidence

    def _closest_action_index(self, applied_pct: float) -> int:
        distances = [abs(applied_pct - action) for action in self._BANDIT_ACTIONS]
        return int(np.argmin(distances))

    def _stagnation_score(self, recent_growth: float) -> float:
        if recent_growth < 0:
            return float(np.clip(0.7 + abs(recent_growth), 0.0, 1.0))
        if recent_growth < 0.02:
            return 0.65
        if recent_growth < 0.05:
            return 0.45
        return 0.25
