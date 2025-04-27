-- Ensure the UUID extension is enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 01-create-users.sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    keycloak_id UUID NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    user_type VARCHAR(20) NOT NULL,
    phone_number VARCHAR(20),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- 02-create-cars.sql
CREATE TABLE cars (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL,
    power INTEGER NOT NULL,
    seats INTEGER NOT NULL,
    transmission VARCHAR(20) NOT NULL,
    fuel_type VARCHAR(20) NOT NULL,
    price DECIMAL NOT NULL,
    location VARCHAR(255),
    description TEXT,
    features TEXT[],
    images TEXT[],
    status VARCHAR(20),
    rating DECIMAL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- 03-create-bookings.sql
CREATE TABLE bookings (
    id UUID PRIMARY KEY,
    car_id UUID NOT NULL,
    client_id UUID NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_price DECIMAL NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- 04-create-reviews.sql
CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL,
    car_id UUID NOT NULL,
    client_id UUID NOT NULL,
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP
);
