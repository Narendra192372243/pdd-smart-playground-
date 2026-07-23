<?php
header('Content-Type: application/json');
require 'db_config.php';

$ground_id = $_GET['ground_id'] ?? null;
$date = $_GET['date'] ?? null;

if (!$ground_id || !$date) {
    echo json_encode(["status" => "error", "message" => "Ground ID and Date required"]);
    exit;
}

// Fetch already booked slots for this ground and date
$sql = "SELECT time_slot FROM bookings WHERE ground_id = ? AND booking_date = ? AND status != 'Cancelled'";
$stmt = $conn->prepare($sql);
$stmt->bind_param("is", $ground_id, $date);
$stmt->execute();
$result = $stmt->get_result();

$booked_slots = [];
while($row = $result->fetch_assoc()) {
    $booked_slots[] = $row['time_slot'];
}

// Fetch temporarily locked slots
$sql_locked = "SELECT slot_time FROM slots WHERE ground_id = ? AND locked_until > NOW() AND is_booked = 0";
$stmt2 = $conn->prepare($sql_locked);
$stmt2->bind_param("i", $ground_id);
$stmt2->execute();
$res2 = $stmt2->get_result();

while($row = $res2->fetch_assoc()) {
    if (!in_array($row['slot_time'], $booked_slots)) {
        $booked_slots[] = $row['slot_time'];
    }
}

echo json_encode([
    "status" => "success",
    "booked_slots" => $booked_slots
]);
?>
