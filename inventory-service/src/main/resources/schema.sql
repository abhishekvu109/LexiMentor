CREATE TABLE IF NOT EXISTS job
(
    job_id    int AUTO_INCREMENT PRIMARY KEY,
    ref_id    VARCHAR(200) NOT NULL,
    crtn_date timestamp
);

CREATE TABLE IF NOT EXISTS word_record
(
    id        int AUTO_INCREMENT PRIMARY KEY,
    ref_id    VARCHAR(200) NOT NULL,
    word      varchar(200),
    load_date timestamp,
    job_id    int,
    status    int
);