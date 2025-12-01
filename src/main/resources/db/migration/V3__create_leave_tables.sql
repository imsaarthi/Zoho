CREATE TABLE leave_balances (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    total_leaves DOUBLE PRECISION NOT NULL DEFAULT 0,
    CONSTRAINT fk_leave_balances_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE leave_requests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    reason TEXT,
    CONSTRAINT fk_leave_requests_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_leave_requests_user ON leave_requests(user_id);
