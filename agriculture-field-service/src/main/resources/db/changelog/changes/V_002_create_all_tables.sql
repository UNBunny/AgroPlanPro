CREATE TABLE IF NOT EXISTS crop_types (
    id                      BIGSERIAL PRIMARY KEY,
    name                    VARCHAR(255),
    category                VARCHAR(100),
    growing_season_days     INTEGER,
    optimal_temperature_min NUMERIC(5, 2),
    optimal_temperature_max NUMERIC(5, 2),
    water_requirements_mm   NUMERIC(8, 2),
    notes                   TEXT
);

CREATE TABLE IF NOT EXISTS crop_varieties (
    id                              BIGSERIAL PRIMARY KEY,
    name                            VARCHAR(255),
    crop_type_id                    BIGINT REFERENCES crop_types(id),
    seed_producer                   VARCHAR(255),
    maturation_days                 INTEGER,
    drought_tolerance               VARCHAR(50),
    frost_tolerance                 VARCHAR(50),
    recommended_seeding_rate_kg_per_ha NUMERIC(8, 2),
    seed_cost_per_kg                NUMERIC(10, 2),
    is_hybrid                       BOOLEAN DEFAULT false,
    notes                           TEXT
);

CREATE TABLE IF NOT EXISTS diseases (
    id                   BIGSERIAL PRIMARY KEY,
    scientific_name      VARCHAR(255),
    common_name          VARCHAR(255),
    disease_type         VARCHAR(50),
    symptoms             TEXT,
    prevention_measures  TEXT,
    treatment_methods    TEXT,
    risk_level           VARCHAR(50),
    active_season        VARCHAR(100),
    favorable_conditions TEXT,
    image_url            VARCHAR(500),
    is_active            BOOLEAN DEFAULT true
);

CREATE TABLE IF NOT EXISTS disease_affected_crops (
    disease_id   BIGINT NOT NULL REFERENCES diseases(id),
    crop_type_id BIGINT NOT NULL REFERENCES crop_types(id),
    PRIMARY KEY (disease_id, crop_type_id)
);

CREATE TABLE IF NOT EXISTS disease_resistances (
    id               BIGSERIAL PRIMARY KEY,
    disease_id       BIGINT REFERENCES diseases(id),
    crop_variety_id  BIGINT REFERENCES crop_varieties(id),
    resistance_level VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS crop_history (
    id                    BIGSERIAL PRIMARY KEY,
    field_id              BIGINT REFERENCES agricultural_fields(id),
    crop_type_id          BIGINT REFERENCES crop_types(id),
    crop_variety_id       BIGINT REFERENCES crop_varieties(id),
    planting_date         DATE,
    actual_harvest_date   DATE,
    expected_harvest_date DATE,
    seed_amount_kg_per_ha NUMERIC(8, 2),
    seed_depth_cm         NUMERIC(5, 2),
    plant_spacing_cm      NUMERIC(5, 2),
    actual_yield_kg       NUMERIC(12, 2),
    expected_yield_kg     NUMERIC(12, 2),
    planting_status       VARCHAR(50),
    notes                 TEXT,
    weather_conditions    TEXT
);
