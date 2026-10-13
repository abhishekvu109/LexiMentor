import os
import json
import logging
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import Optional
from ollama import Client

logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
logger = logging.getLogger(__name__)

app = FastAPI(title="Cloud LLM Service", version="1.0.0")

OLLAMA_API_KEY = os.environ.get("OLLAMA_API_KEY", "")
OLLAMA_HOST = os.environ.get("OLLAMA_HOST", "https://ollama.com")
DEFAULT_MODEL = os.environ.get("MODEL_NAME", "gpt-oss:120b")

client = Client(
    host=OLLAMA_HOST,
    headers={"Authorization": "Bearer " + OLLAMA_API_KEY},
)


class PromptRequest(BaseModel):
    prompt: str
    model: Optional[str] = None
    format: Optional[str] = None
    options: Optional[dict] = None


@app.post("/api/v2/llm/text")
def generate_text_v2(request: PromptRequest) -> str:
    return _generate(request)


@app.post("/api/v1/llm/text")
def generate_text_v1(request: PromptRequest) -> str:
    return _generate(request)


def _generate(request: PromptRequest) -> str:
    model = request.model or DEFAULT_MODEL
    messages = [{"role": "user", "content": request.prompt}]

    kwargs = {"stream": False}
    if request.format:
        try:
            kwargs["format"] = json.loads(request.format)
        except (json.JSONDecodeError, TypeError):
            logger.warning("format is not valid JSON, ignoring: %s", request.format)

    logger.info("Calling model=%s prompt_len=%d has_format=%s", model, len(request.prompt), "format" in kwargs)
    try:
        response = client.chat(model=model, messages=messages, **kwargs)
        if response.message is None:
            logger.error("Cloud LLM returned a response with no message field: %s", response)
            raise HTTPException(status_code=502, detail="LLM returned empty message")
        content = response.message.content
        logger.info("Response received len=%d", len(content))
        return content
    except HTTPException:
        raise
    except Exception as e:
        logger.error("Cloud LLM call failed: %s", str(e))
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/health")
def health():
    return {"status": "UP", "model": DEFAULT_MODEL, "host": OLLAMA_HOST}
