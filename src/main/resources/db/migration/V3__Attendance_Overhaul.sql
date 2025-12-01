-- Update attendance_sessions table
ALTER TABLE attendance_sessions ADD COLUMN IF NOT EXISTS check_in_photo_url VARCHAR(255);
ALTER TABLE attendance_sessions ADD COLUMN IF NOT EXISTS check_in_lat DOUBLE PRECISION;
ALTER TABLE attendance_sessions ADD COLUMN IF NOT EXISTS check_in_lon DOUBLE PRECISION;
ALTER TABLE attendance_sessions ADD COLUMN IF NOT EXISTS check_out_photo_url VARCHAR(255);
ALTER TABLE attendance_sessions ADD COLUMN IF NOT EXISTS check_out_lat DOUBLE PRECISION;
ALTER TABLE attendance_sessions ADD COLUMN IF NOT EXISTS check_out_lon DOUBLE PRECISION;
ALTER TABLE attendance_sessions ADD COLUMN IF NOT EXISTS duration_minutes INTEGER;

-- Create break_sessions table
CREATE TABLE IF NOT EXISTS break_sessions (
    id BIGSERIAL PRIMARY KEY,
    attendance_session_id BIGINT NOT NULL,
    break_start_time TIMESTAMP NOT NULL,
    break_start_photo_url VARCHAR(255),
    break_start_lat DOUBLE PRECISION,
    break_start_lon DOUBLE PRECISION,
    break_end_time TIMESTAMP,
    break_end_photo_url VARCHAR(255),
    break_end_lat DOUBLE PRECISION,
    break_end_lon DOUBLE PRECISION,
    break_duration_minutes INTEGER,
    CONSTRAINT fk_attendance_session FOREIGN KEY (attendance_session_id) REFERENCES attendance_sessions (id) ON DELETE CASCADE
);

-- Create attendance_day_summary table (if not exists from V2)
CREATE TABLE IF NOT EXISTS attendance_day_summary (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    first_check_in TIMESTAMP,
    last_check_out TIMESTAMP,
    total_work_minutes BIGINT,
    status VARCHAR(20),
    CONSTRAINT fk_user_attendance_summary FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uq_user_date_summary UNIQUE (user_id, attendance_date)
);
