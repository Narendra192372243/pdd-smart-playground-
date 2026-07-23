<?php
header('Content-Type: application/json');
require 'db_config.php';

$user_id = $_GET['user_id'];

$sql = "SELECT Bookings.*, Grounds.name as ground_name, Grounds.location
        FROM Bookings
        JOIN Grounds ON Bookings.ground_id = Grounds.id
        WHERE user_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$bookings = [];
while($row = $result->fetch_assoc()) {
    $bookings[] = $row;
}

echo json_encode(['status' => 'success', 'data' => $bookings]);
?>