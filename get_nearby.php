<?php
header('Content-Type: application/json');
require 'db_config.php';

// Get user location from parameters
$user_lat = $_GET['lat'] ?? null;
$user_lng = $_GET['lng'] ?? null;
$radius = 5.0; // 5 KM radius

if (!$user_lat || !$user_lng) {
    echo json_encode(["status" => "error", "message" => "Location required"]);
    exit;
}

// Haversine formula to calculate distance in KM in SQL
$sql = "SELECT *,
        (6371 * acos(cos(radians(?)) * cos(radians(latitude)) * cos(radians(longitude) - radians(?)) + sin(radians(?)) * sin(radians(latitude)))) AS distance
        FROM playgrounds_geo
        HAVING distance <= ?
        ORDER BY distance ASC";

$stmt = $conn->prepare($sql);
$stmt->bind_param("dddd", $user_lat, $user_lng, $user_lat, $radius);
$stmt->execute();
$result = $stmt->get_result();

$playgrounds = [];
while($row = $result->fetch_assoc()) {
    $row['distance'] = round($row['distance'], 2); // Round to 2 decimal places
    $playgrounds[] = $row;
}

echo json_encode([
    "status" => "success",
    "user_location" => ["lat" => $user_lat, "lng" => $user_lng],
    "count" => count($playgrounds),
    "data" => $playgrounds
]);
?>
