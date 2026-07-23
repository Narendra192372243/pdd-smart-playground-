-- Complete Database for Smart Playground App
CREATE DATABASE smart_playground_final;
USE smart_playground_v2;

-- Users table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(15) UNIQUE NOT NULL,
    password VARCHAR(255),
    profile_pic VARCHAR(255),
    location VARCHAR(255) DEFAULT 'Adyar, Chennai',
    reward_points INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Playgrounds table
CREATE TABLE playgrounds (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    sports VARCHAR(255),
    price_per_hour DECIMAL(10,2),
    rating FLOAT DEFAULT 0,
    reviews_count INT DEFAULT 0,
    status VARCHAR(50) DEFAULT 'Open Now'
);

-- Slots and Real-time Locking
CREATE TABLE slots (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ground_id INT,
    slot_time VARCHAR(50),
    is_booked TINYINT(1) DEFAULT 0,
    locked_until DATETIME,
    locked_by_user INT,
    FOREIGN KEY (ground_id) REFERENCES playgrounds(id)
);

-- Bookings table
CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    ground_id INT,
    booking_date DATE,
    time_slot VARCHAR(50),
    amount DECIMAL(10,2),
    booking_id_str VARCHAR(20) UNIQUE,
    qr_code VARCHAR(255),
    status ENUM('Upcoming', 'Completed', 'Cancelled') DEFAULT 'Upcoming',
    payment_status VARCHAR(20) DEFAULT 'Paid',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (ground_id) REFERENCES playgrounds(id)
);

-- Equipment table
CREATE TABLE equipment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    price_per_day DECIMAL(10,2),
    stock INT DEFAULT 0,
    image_url VARCHAR(255)
);

-- Equipment Rentals
CREATE TABLE rentals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    equipment_id INT,
    rental_date DATE,
    duration VARCHAR(20),
    amount DECIMAL(10,2),
    status VARCHAR(20) DEFAULT 'Active',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (equipment_id) REFERENCES equipment(id)
);

-- Teams and Community
CREATE TABLE teams (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    sport VARCHAR(50),
    creator_id INT,
    players_needed INT,
    location VARCHAR(100),
    FOREIGN KEY (creator_id) REFERENCES users(id)
);

-- Admins table
CREATE TABLE admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(255)
);

-- Sample Chennai Data
INSERT INTO playgrounds (name, address, latitude, longitude, sports, price_per_hour, rating, status) VALUES
('Marina Cricket Ground', 'Marina Beach Road', 13.0500, 80.2824, 'Cricket', 500.00, 4.5, 'Open Now'),
('Adyar Football Turf', 'Besant Nagar', 13.0033, 80.2550, 'Football', 800.00, 4.8, 'Open Now'),
('T. Nagar Badminton Club', 'South Boag Road', 13.0418, 80.2341, 'Badminton', 300.00, 4.2, 'Fully Booked'),
('Velachery Basketball court', 'Phoenix MarketCity', 12.9792, 80.2185, 'Basketball', 400.00, 4.4, 'Open Now');
