CREATE TABLE holidays (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    recurring BOOLEAN NOT NULL DEFAULT FALSE,
    description TEXT,
    organization_id BIGINT NOT NULL,
    CONSTRAINT fk_holidays_organization FOREIGN KEY (organization_id) REFERENCES organizations(id)
);

CREATE INDEX idx_holidays_organization_date ON holidays(organization_id, date);
