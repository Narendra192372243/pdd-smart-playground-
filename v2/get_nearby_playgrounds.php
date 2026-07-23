<?php
header('Content-Type: application/json');
require '../db_config.php';

$lat = $_GET['lat'];
$lng = $_GET['lng'];
$radius = 5.0; // 5 KM

// Haversine formula to find playgrounds within 5KM
$query = "SELECT *,
    (6371 * acos(cos(radians(?)) * cos(radians(latitude)) * cos(radians(longitude) - radians(?)) + sin(radians(?)) * sin(radians(latitude)))) AS distance
    FROM playgrounds
    HAVING distance <= ?
    ORDER BY distance ASC";

$stmt = $conn->prepare($query);
$stmt->bind_param("dddd", $lat, $lng, $lat, $radius);
$stmt->execute();
$result = $stmt->get_result();

$data = [];
while ($row = $result->fetch_assoc()) {
    $row['distance'] = round($row['distance'], 2);
    $data[] = $row;
}

echo json_encode(["status" => "success", "data" => $data]);
?>
