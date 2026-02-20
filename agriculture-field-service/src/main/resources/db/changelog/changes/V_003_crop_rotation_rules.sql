CREATE TABLE crop_rotation_rules (
    id                  BIGSERIAL PRIMARY KEY,
    predecessor_crop_id BIGINT NOT NULL REFERENCES crop_types(id),
    successor_crop_id   BIGINT NOT NULL REFERENCES crop_types(id),
    allowed             BOOLEAN NOT NULL DEFAULT true,
    min_gap_years       INTEGER DEFAULT 0,
    reason              VARCHAR(500),
    UNIQUE (predecessor_crop_id, successor_crop_id)
);
