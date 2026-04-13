from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

    fitmate_base_url: str = "http://localhost:8080"
    fitmate_timeout_seconds: int = 10

    llm_enabled: bool = False
    llm_provider: str = "ollama"
    llm_model: str = "qwen2.5:7b"
    ollama_url: str = "http://localhost:11434/api/generate"


settings = Settings()
