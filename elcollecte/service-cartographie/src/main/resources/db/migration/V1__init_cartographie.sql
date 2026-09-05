-- PHASE 5: service-cartographie - NOUVEAU avec PostGIS
-- Extension PostGIS
CREATE EXTENSION IF NOT EXISTS postgis;

-- Table zones
CREATE TABLE zones (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    projet_id BIGINT,
    type VARCHAR(50) NOT NULL,
    properties JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TYPE zone_type AS ENUM ('PROJET', 'SECTEUR', 'QUARTIER', 'REGION', 'PAYS', 'CUSTOM');
ALTER TABLE zones ALTER COLUMN type TYPE zone_type USING type::zone_type;

-- Index pour zones
CREATE INDEX idx_zones_projet ON zones(projet_id);
CREATE INDEX idx_zones_type ON zones(type);

-- Table points_interet
CREATE TABLE points_interet (
    id BIGSERIAL PRIMARY KEY,
    zone_id BIGINT,
    nom VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    altitude DECIMAL(10,2),
    type VARCHAR(50) NOT NULL,
    properties JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    geom GEOMETRY(Point, 4326)
);

CREATE TYPE point_type AS ENUM ('COLLECTE', 'ENQUETE', 'OBSERVATION', 'MARQUEUR', 'ENTREE', 'SORTIE', 'CUSTOM');
ALTER TABLE points_interet ALTER COLUMN type TYPE point_type USING type::point_type;

-- Index pour points
CREATE INDEX idx_points_zone ON points_interet(zone_id);
CREATE INDEX idx_points_geom ON points_interet USING GIST(geom);
CREATE INDEX idx_points_type ON points_interet(type);

-- Trigger pour mettre à jour geom à partir de lat/long
CREATE OR REPLACE FUNCTION update_point_geom()
RETURNS TRIGGER AS $$
BEGIN
    NEW.geom = ST_SetSRID(ST_MakePoint(NEW.longitude, NEW.latitude), 4326);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_point_geom
    BEFORE INSERT OR UPDATE ON points_interet
    FOR EACH ROW
    EXECUTE FUNCTION update_point_geom();

-- Table polygones
CREATE TABLE polygones (
    id BIGSERIAL PRIMARY KEY,
    zone_id BIGINT,
    nom VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    coordinates JSONB,
    type VARCHAR(50) NOT NULL,
    properties JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    geom GEOMETRY(Polygon, 4326)
);

CREATE TYPE polygone_type AS ENUM ('SECTEUR', 'QUARTIER', 'ZONE_COLLECTE', 'ZONE_EXCLUSION', 'CUSTOM');
ALTER TABLE polygones ALTER COLUMN type TYPE polygone_type USING type::polygone_type;

-- Index pour polygones
CREATE INDEX idx_polygones_zone ON polygones(zone_id);
CREATE INDEX idx_polygones_geom ON polygones USING GIST(geom);
CREATE INDEX idx_polygones_type ON polygones(type);
