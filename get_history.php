<?php
header('Content-Type: application/json');
require 'db_config.php';

$user_id = $_GET['user_id'] ?? null;

if (!$user_id) {
    echo json_encode(["status" => "error", "message" => "User ID required"]);
    exit;
}

$sql = "SELECT b.*, p.name as playground_name, p.address
        FROM bookings b
        JOIN playgrounds p ON b.ground_id = p.id
        WHERE b.user_id = ?
        ORDER BY b.booking_date DESC, b.created_at DESC";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$history = [];
while($row = $result->fetch_assoc()) {
    $history[] = $row;
}

echo json_encode(["status" => "success", "data" => $history]);
?>
