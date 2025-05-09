CREATE TABLE IF NOT EXISTS patient (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS doctor (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    specialty VARCHAR(255) NOT NULL,
    centre_name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS appointment (
    id SERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    patient_name VARCHAR(255) NOT NULL,
    doctor_id BIGINT NOT NULL,
    doctor_name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    reason TEXT
);