from fastapi import FastAPI

from app.api.routes import router as recommendation_router

app = FastAPI(title="Fitmate AI Service", version="0.1.0")


@app.get("/health")
def health() -> dict:
    return {"status": "ok"}


app.include_router(recommendation_router)
