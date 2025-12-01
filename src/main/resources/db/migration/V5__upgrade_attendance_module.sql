-- Add new columns to attendance_sessions
ALTER TABLE attendance_sessions
ADD COLUMN check_in_photo_url VARCHAR(500),
ADD COLUMN check_in_lat DOUBLE PRECISION,
ADD COLUMN check_in_lon DOUBLE PRECISION,
ADD COLUMN check_out_photo_url VARCHAR(500),
ADD COLUMN check_out_lat DOUBLE PRECISION,
ADD COLUMN check_out_lon DOUBLE PRECISION;

-- Create break_sessions table
CREATE TABLE break_sessions (
    id BIGSERIAL PRIMARY KEY,
    attendance_session_id BIGINT NOT NULL,
    break_start_time TIMESTAMP NOT NULL,
    break_start_photo_url VARCHAR(500),
    break_start_lat DOUBLE PRECISION,
    break_start_lon DOUBLE PRECISION,
    break_end_time TIMESTAMP,
    break_end_photo_url VARCHAR(500),
    break_end_lat DOUBLE PRECISION,
    break_end_lon DOUBLE PRECISION,
    FOREIGN KEY (attendance_session_id) REFERENCES attendance_sessions(id)
);

CREATE INDEX idx_break_session_attendance_id ON break_sessions(attendance_session_id);
