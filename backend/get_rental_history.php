<?php
header('Content-Type: application/json');
require 'db_config.php';

$user_id = $_GET['user_id'] ?? null;

if (!$user_id) {
    echo json_encode(["status" => "error", "message" => "User ID required"]);
    exit;
}

$sql = "SELECT r.*, e.name as equipment_name, e.category
        FROM rentals r
        JOIN equipment e ON r.equipment_id = e.id
        WHERE r.user_id = ?
        ORDER BY r.rental_date DESC";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$rentals = [];
while($row = $result->fetch_assoc()) {
    $rentals[] = $row;
}

echo json_encode(["status" => "success", "data" => $rentals]);
?>
