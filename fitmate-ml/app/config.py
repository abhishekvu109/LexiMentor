from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

    # MySQL
    db_host: str = "localhost"
    db_port: int = 3306
    db_name: str = "fitmate"
    db_user: str = "root"
    db_password: str = ""

    # MLflow
    mlflow_tracking_uri: str = "http://localhost:5000"
    mlflow_experiment_name: str = "fitmate-recommendation"

    # Model artefacts directory
    model_dir: str = "./artefacts"

    # App
    app_env: str = "development"
    log_level: str = "INFO"

    @property
    def db_url(self) -> str:
        return (
            f"mysql+pymysql://{self.db_user}:{self.db_password}"
            f"@{self.db_host}:{self.db_port}/{self.db_name}"
        )


settings = Settings()
