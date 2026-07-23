-- Innovative Smart Playground Database Schema
CREATE DATABASE smart_playground_v2;
USE smart_playground_v2;

-- 1. Users with Loyalty Points
CREATE TABLE Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    loyalty_points INT DEFAULT 0,
    preferred_sport VARCHAR(50),
    is_verified TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Grounds with Occupancy Tracking
CREATE TABLE Grounds (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(255) NOT NULL,
    base_price INT NOT NULL,
    peak_multiplier DECIMAL(3,2) DEFAULT 1.5,
    rating DECIMAL(2,1),
    occupancy_status ENUM('Free', 'Busy', 'Full') DEFAULT 'Free',
    image_url VARCHAR(255)
);

-- 3. Real-Time Slot Locking (2-minute temporary lock)
CREATE TABLE Slot_Locks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ground_id INT,
    slot_time VARCHAR(50),
    locked_by_user INT,
    lock_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ground_id) REFERENCES Grounds(id),
    FOREIGN KEY (locked_by_user) REFERENCES Users(id)
);

-- 4. Dynamic Bookings with QR Codes
CREATE TABLE Bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    ground_id INT,
    booking_date DATE NOT NULL,
    time_slot VARCHAR(50) NOT NULL,
    final_amount INT NOT NULL,
    points_earned INT DEFAULT 0,
    qr_code_data VARCHAR(255),
    status ENUM('Confirmed', 'Cancelled', 'Completed', 'Waiting') DEFAULT 'Confirmed',
    is_weather_alerted TINYINT(1) DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (ground_id) REFERENCES Grounds(id)
);

-- 5. Waiting List for Smart Cancellation
CREATE TABLE Waiting_List (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ground_id INT,
    booking_date DATE,
    time_slot VARCHAR(50),
    user_id INT,
    priority_score INT DEFAULT 0,
    FOREIGN KEY (ground_id) REFERENCES Grounds(id),
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

-- 6. Team Match Finder
CREATE TABLE Teams (
    id INT AUTO_INCREMENT PRIMARY KEY,
    team_name VARCHAR(100),
    sport_type VARCHAR(50),
    creator_id INT,
    players_needed INT DEFAULT 0,
    location VARCHAR(100),
    FOREIGN KEY (creator_id) REFERENCES Users(id)
);

-- 7. Equipment with AI Prediction Data
CREATE TABLE Equipment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    total_stock INT,
    rented_count INT DEFAULT 0,
    predicted_demand ENUM('Low', 'Medium', 'High') DEFAULT 'Low',
    price_per_day INT NOT NULL
);

-- Sample Data for Innovation
INSERT INTO Grounds (name, location, base_price, peak_multiplier, rating, occupancy_status) VALUES
('Elite Cricket Stadium', 'Chennai South', 1000, 1.8, 4.9, 'Busy'),
('Kabaddi Pro Arena', 'Chennai North', 600, 1.2, 4.7, 'Free'),
('Sky-High Volleyball', 'Marina Beach', 500, 2.0, 4.5, 'Full');

INSERT INTO Equipment (name, total_stock, rented_count, predicted_demand, price_per_day) VALUES
('Premium Cricket Kit', 10, 8, 'High', 400),
('Standard Football', 20, 2, 'Low', 150);
