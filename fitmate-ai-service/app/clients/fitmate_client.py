import httpx

from app.config import settings


class FitmateClient:
    def __init__(self) -> None:
        self._client = httpx.Client(
            base_url=settings.fitmate_base_url,
            timeout=settings.fitmate_timeout_seconds,
        )

    def health(self) -> dict:
        return {"fitmate_base_url": settings.fitmate_base_url, "status": "configured"}
