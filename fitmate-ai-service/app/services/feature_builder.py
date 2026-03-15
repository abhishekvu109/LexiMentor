from datetime import date

from app.schemas.recommendation import DrillHistoryPoint


def sort_history(history: list[DrillHistoryPoint]) -> list[DrillHistoryPoint]:
    return sorted(history, key=lambda x: x.routine_date)


def days_since_last(history: list[DrillHistoryPoint], today: date) -> int | None:
    if not history:
        return None
    last_date = max(point.routine_date for point in history)
    return (today - last_date).days


def recent_sessions(history: list[DrillHistoryPoint], n: int = 5) -> list[DrillHistoryPoint]:
    ordered = sort_history(history)
    return ordered[-n:]
