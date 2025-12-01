CREATE TABLE attendance_day_summary (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    first_check_in TIMESTAMP,
    last_check_out TIMESTAMP,
    total_work_minutes BIGINT,
    status VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_summary_user_date ON attendance_day_summary(user_id, attendance_date);
