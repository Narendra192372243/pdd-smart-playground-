<?php
header('Content-Type: application/json');
require 'db_config.php';

$lat = $_GET['lat'] ?? null;
$lng = $_GET['lng'] ?? null;
$sport = $_GET['sport'] ?? null;
$radius = 5.0; // 5 KM

$query = "SELECT *,
          (6371 * acos(cos(radians(?)) * cos(radians(latitude)) * cos(radians(longitude) - radians(?)) + sin(radians(?)) * sin(radians(latitude)))) AS distance
          FROM playgrounds";

$params = [$lat, $lng, $lat];
$types = "ddd";

if ($sport) {
    $query .= " WHERE sports LIKE ?";
    $params[] = "%$sport%";
    $types .= "s";
}

$query .= " HAVING distance <= ? ORDER BY distance ASC";
$params[] = $radius;
$types .= "d";

$stmt = $conn->prepare($query);
$stmt->bind_param($types, ...$params);
$stmt->execute();
$result = $stmt->get_result();

$playgrounds = [];
while($row = $result->fetch_assoc()) {
    $row['distance'] = round($row['distance'], 2);
    $playgrounds[] = $row;
}

echo json_encode(["status" => "success", "data" => $playgrounds]);
?>
