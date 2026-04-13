CREATE TABLE IF NOT EXISTS async_jobs (
    job_id VARCHAR(100) PRIMARY KEY,
    job_type VARCHAR(180) NOT NULL,
    status VARCHAR(40) NOT NULL,
    attempt INT NOT NULL,
    payload_json TEXT NULL,
    result_json TEXT NULL,
    metadata_json TEXT NULL,
    error_type VARCHAR(255) NULL,
    error_message TEXT NULL,
    error_stack TEXT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL
);
