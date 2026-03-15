from datetime import date

from app.schemas.recommendation import NextLoadRequest, NextLoadResponse
from app.services.feature_builder import days_since_last, recent_sessions


class RecommendationEngine:
    def recommend(self, request: NextLoadRequest, today: date | None = None) -> NextLoadResponse:
        today = today or date.today()
        history = recent_sessions(request.history, n=5)

        if not history:
            return NextLoadResponse(
                username=request.username,
                exercise_name=request.exercise_name,
                recommended_measurement=0.0,
                decision="HOLD",
                confidence=0.25,
                reasons=["No history found for this exercise"],
            )

        last = history[-1]
        last_weight = last.measurement
        avg_reps = sum(x.repetition for x in history) / len(history)
        max_reps = max(x.repetition for x in history)
        gap_days = days_since_last(history, today) or 0

        reasons: list[str] = []
        decision = "HOLD"
        recommendation = last_weight
        confidence = 0.75

        if gap_days >= 14:
            recommendation = round(last_weight * 0.9, 2)
            decision = "DELOAD"
            confidence = 0.9
            reasons.append(f"Long break detected ({gap_days} days)")
        elif avg_reps >= request.target_rep_max:
            recommendation = round(last_weight * (1 + request.increment_pct / 100.0), 2)
            decision = "INCREASE"
            reasons.append(
                f"Recent average reps ({avg_reps:.1f}) reached target top ({request.target_rep_max})"
            )
        elif max_reps < request.target_rep_min:
            recommendation = round(last_weight * 0.97, 2)
            decision = "DECREASE"
            confidence = 0.7
            reasons.append(
                f"Recent reps are below target minimum ({request.target_rep_min})"
            )
        else:
            reasons.append("Performance is in target range; keep load stable")

        if gap_days >= 7 and gap_days < 14:
            confidence = min(confidence, 0.65)
            reasons.append(f"Mild consistency gap ({gap_days} days)")

        return NextLoadResponse(
            username=request.username,
            exercise_name=request.exercise_name,
            recommended_measurement=max(recommendation, 0.0),
            decision=decision,
            confidence=confidence,
            reasons=reasons,
        )
