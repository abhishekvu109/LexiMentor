import json

import httpx

from app.config import settings
from app.schemas.recommendation import NextLoadRequest, NextLoadResponse


class LlmExplainer:
    async def explain(self, req: NextLoadRequest, rec: NextLoadResponse) -> str | None:
        if not settings.llm_enabled:
            return None

        prompt = self._build_prompt(req, rec)
        payload = {
            "model": settings.llm_model,
            "stream": False,
            "prompt": prompt,
        }
        async with httpx.AsyncClient(timeout=20) as client:
            response = await client.post(settings.ollama_url, json=payload)
            response.raise_for_status()
            data = response.json()
        return data.get("response")

    def _build_prompt(self, req: NextLoadRequest, rec: NextLoadResponse) -> str:
        return (
            "You are a strength coach. Explain this recommendation briefly and safely.\n"
            "Avoid medical claims. Mention break/consistency effects if present.\n"
            f"Input: {json.dumps(req.model_dump(mode='json'))}\n"
            f"Recommendation: {json.dumps(rec.model_dump(mode='json'))}\n"
        )
