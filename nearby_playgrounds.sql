-- Playgrounds table with Latitude and Longitude
USE smart_playground_v2;

CREATE TABLE playgrounds_geo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    address VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    sports VARCHAR(255),
    price_per_hour DECIMAL(10,2),
    rating FLOAT,
    status VARCHAR(50) -- 'Open Now', 'Closed', 'Fully Booked'
);

-- Sample Data for Chennai Playgrounds
INSERT INTO playgrounds_geo (name, address, latitude, longitude, sports, price_per_hour, rating, status) VALUES
('Marina View Cricket Ground', 'Marina Beach, Chennai', 13.0500, 80.2824, 'Cricket, Volleyball', 500.00, 4.5, 'Open Now'),
('Adyar Sports Arena', 'Adyar, Chennai', 13.0033, 80.2550, 'Football, Basketball', 800.00, 4.8, 'Open Now'),
('T. Nagar Badminton Hub', 'T. Nagar, Chennai', 13.0418, 80.2341, 'Badminton, Pickleball', 300.00, 4.2, 'Fully Booked'),
('Velachery Soccer Park', 'Velachery, Chennai', 12.9792, 80.2185, 'Football, Cricket', 700.00, 4.4, 'Open Now'),
('Guindy Tennis Club', 'Guindy, Chennai', 13.0067, 80.2206, 'Tennis, Basketball', 600.00, 4.6, 'Closed'),
('Mylapore Sports Center', 'Mylapore, Chennai', 13.0333, 80.2667, 'Cricket, Basketball', 450.00, 4.3, 'Open Now');
