CREATE DATABASE IF NOT EXISTS jakloc
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE jakloc;

CREATE TABLE IF NOT EXISTS users (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    full_name     VARCHAR(80)  NOT NULL,
    email         VARCHAR(254) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    phone         VARCHAR(20)  NOT NULL DEFAULT '',
    role          VARCHAR(10)  NOT NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS vehicles (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    brand        VARCHAR(60)   NOT NULL,
    model        VARCHAR(80)   NOT NULL,
    category     VARCHAR(20)   NOT NULL,
    transmission VARCHAR(20)   NOT NULL,
    fuel_type    VARCHAR(20)   NOT NULL,
    model_year   INT           NOT NULL,
    seats        INT           NOT NULL,
    doors        INT           NOT NULL,
    daily_rate   DECIMAL(10,2) NOT NULL,
    mileage      INT           NOT NULL DEFAULT 0,
    location     VARCHAR(120)  NOT NULL DEFAULT '',
    image_url    VARCHAR(500)  NULL,
    description  VARCHAR(500)  NULL,
    status       VARCHAR(20)   NOT NULL DEFAULT 'AVAILABLE',
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_vehicles_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO users (id, full_name, email, password_hash, phone, role, created_at) VALUES
(1, 'JakLoc Administrator', 'admin@jakloc.tn', '$2a$12$xF7pip2MsIPn97aROSYeUOtPHPKSmP0NbUixACDysD5n8qjCI/aXa', '+216 000 000 000', 'ADMIN', NOW()),
(2, 'Demo Client', 'client@jakloc.tn', '$2a$12$0cG41oAePmW8MQEreLnF0Ojzyk.QyoHHqga/RITfjPiWJWDx/BAgC', '+216 200 000 000', 'CLIENT', NOW());

INSERT IGNORE INTO vehicles (id, brand, model, category, transmission, fuel_type, model_year, seats, doors, daily_rate, mileage, location, image_url, description, status, created_at) VALUES
(1, 'Porsche', '911 Carrera S', 'SPORTS', 'AUTOMATIC', 'PETROL', 2023, 4, 2, 520.00, 18400, 'Tunis - La Marsa', NULL, 'A precise, theatre-like drive for coastal escapes and weekend getaways.', 'AVAILABLE', NOW()),
(2, 'Mercedes-Benz', 'GLE 450', 'SUV', 'AUTOMATIC', 'HYBRID', 2024, 5, 5, 390.00, 9200, 'Tunis - Centre Ville', NULL, 'Quiet, spacious and composed, with room for the whole crew.', 'AVAILABLE', NOW()),
(3, 'BMW', 'i4 eDrive40', 'ELECTRIC', 'AUTOMATIC', 'ELECTRIC', 2024, 5, 4, 340.00, 7600, 'Aeroport Tunis-Carthage', NULL, 'Silent acceleration and a cabin built for long, easy drives.', 'AVAILABLE', NOW()),
(4, 'Range Rover', 'Velar', 'SUV', 'AUTOMATIC', 'DIESEL', 2023, 5, 5, 450.00, 21300, 'Hammamet', NULL, 'A calm, elevated ride with enough presence for any occasion.', 'AVAILABLE', NOW()),
(5, 'Audi', 'A6 55 TFSI', 'LUXURY', 'AUTOMATIC', 'PETROL', 2024, 5, 4, 360.00, 12400, 'Tunis - Les Berges du Lac', NULL, 'Understated comfort with a sharp, modern edge.', 'AVAILABLE', NOW()),
(6, 'Volkswagen', 'Golf 8', 'HATCHBACK', 'MANUAL', 'PETROL', 2023, 5, 5, 180.00, 24800, 'Sousse', NULL, 'Compact, efficient and genuinely fun on city streets.', 'AVAILABLE', NOW()),
(7, 'Mercedes-Benz', 'V-Class', 'VAN', 'AUTOMATIC', 'DIESEL', 2023, 7, 5, 380.00, 30100, 'Aeroport Tunis-Carthage', NULL, 'Seven seats, generous luggage space and effortless cruising.', 'RENTED', NOW()),
(8, 'Dacia', 'Duster', 'SUV', 'MANUAL', 'DIESEL', 2022, 5, 5, 140.00, 46500, 'Tozeur', NULL, 'A dependable companion for the road less travelled.', 'MAINTENANCE', NOW());
