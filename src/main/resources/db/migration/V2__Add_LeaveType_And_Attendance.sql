-- Add leave_type to leave_requests
ALTER TABLE leave_requests ADD COLUMN leave_type VARCHAR(255) NOT NULL DEFAULT 'CASUAL';

-- Create attendance_day_summary table
CREATE TABLE IF NOT EXISTS attendance_day_summary (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    first_check_in TIMESTAMP,
    last_check_out TIMESTAMP,
    total_work_minutes BIGINT,
    status VARCHAR(255),
    CONSTRAINT fk_user_attendance_summary FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uq_user_date_summary UNIQUE (user_id, attendance_date)
);

-- Add duration_minutes to attendance_sessions
ALTER TABLE attendance_sessions ADD COLUMN duration_minutes INTEGER;
