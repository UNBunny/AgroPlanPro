CREATE TABLE agricultural_fields (
    id BIGSERIAL PRIMARY KEY,

    -- Основная инфа
    field_name VARCHAR(255) NOT NULL,
--     farm_name VARCHAR(255) NOT NULL
    crop_type VARCHAR(100),
    status VARCHAR(50) default 'active', -- Статус (запланировано к посеву, посеяно и тд)

    -- Геометрия поля (PostGIS)
    geom GEOMETRY(Polygon, 4326) NOT NULL,
    holes GEOMETRY(MultiPolygon, 4326),

    area_hectares NUMERIC(12, 2)
)