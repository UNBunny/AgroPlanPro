-- Создание всех таблиц для проекта

-- Таблица типов культур
CREATE TABLE IF NOT EXISTS crop_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    growing_season_days INTEGER,
    optimal_temperature_min NUMERIC(5,2),
    optimal_temperature_max NUMERIC(5,2),
    water_requirements_mm NUMERIC(8,2),
    notes TEXT
);

-- Таблица сортов культур
CREATE TABLE IF NOT EXISTS crop_varieties (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    crop_type_id BIGINT REFERENCES crop_types(id),
    seed_producer VARCHAR(255),
    maturation_days INTEGER,
    drought_tolerance VARCHAR(20),
    frost_tolerance VARCHAR(20),
    recommended_seeding_rate_kg_per_ha NUMERIC(10,2),
    seed_cost_per_kg NUMERIC(10,2),
    is_hybrid BOOLEAN DEFAULT false,
    notes TEXT
);

-- Таблица болезней
CREATE TABLE IF NOT EXISTS diseases (
    id BIGSERIAL PRIMARY KEY,
    scientific_name VARCHAR(200) NOT NULL,
    common_name VARCHAR(200) NOT NULL,
    disease_type VARCHAR(30) NOT NULL,
    symptoms TEXT,
    prevention_measures TEXT,
    treatment_methods TEXT,
    risk_level VARCHAR(20) NOT NULL,
    active_season VARCHAR(100),
    favorable_conditions VARCHAR(500),
    image_url VARCHAR(500),
    is_active BOOLEAN DEFAULT true
);

-- Связующая таблица болезней и культур (Many-to-Many)
CREATE TABLE IF NOT EXISTS disease_affected_crops (
    disease_id BIGINT NOT NULL REFERENCES diseases(id) ON DELETE CASCADE,
    crop_type_id BIGINT NOT NULL REFERENCES crop_types(id) ON DELETE CASCADE,
    PRIMARY KEY (disease_id, crop_type_id)
);

-- Таблица устойчивости сортов к болезням
CREATE TABLE IF NOT EXISTS disease_resistances (
    id BIGSERIAL PRIMARY KEY,
    disease_id BIGINT REFERENCES diseases(id),
    crop_variety_id BIGINT REFERENCES crop_varieties(id),
    resistance_level VARCHAR(20) NOT NULL
);

-- Таблица истории посевов
CREATE TABLE IF NOT EXISTS crop_history (
    id BIGSERIAL PRIMARY KEY,
    field_id BIGINT REFERENCES agricultural_fields(id),
    crop_type_id BIGINT REFERENCES crop_types(id),
    crop_variety_id BIGINT REFERENCES crop_varieties(id),
    planting_date TIMESTAMP,
    actual_harvest_date TIMESTAMP,
    expected_harvest_date TIMESTAMP,
    seed_amount_kg_per_ha NUMERIC(10,2),
    seed_depth_cm NUMERIC(5,2),
    plant_spacing_cm NUMERIC(5,2),
    actual_yield_kg NUMERIC(12,2),
    expected_yield_kg NUMERIC(12,2),
    planting_status VARCHAR(20),
    notes TEXT,
    weather_conditions TEXT
);
