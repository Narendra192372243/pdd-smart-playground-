<?php
header('Content-Type: application/json');
require 'db_config.php';

$ground_id = $_POST['ground_id'] ?? null;
$slot_time = $_POST['slot_time'] ?? null;
$user_id = $_POST['user_id'] ?? null;

if (!$ground_id || !$slot_time || !$user_id) {
    echo json_encode(["status" => "error", "message" => "Missing parameters"]);
    exit;
}

// Check if already booked or locked by someone else
$check_sql = "SELECT * FROM slots WHERE ground_id = ? AND slot_time = ? AND (is_booked = 1 OR (locked_until > NOW() AND locked_by_user != ?))";
$stmt = $conn->prepare($check_sql);
$stmt->bind_param("isi", $ground_id, $slot_time, $user_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    echo json_encode(["status" => "error", "message" => "Slot temporarily reserved or already booked"]);
} else {
    // Lock the slot for 2 minutes
    $lock_sql = "INSERT INTO slots (ground_id, slot_time, locked_by_user, locked_until)
                 VALUES (?, ?, ?, DATE_ADD(NOW(), INTERVAL 2 MINUTE))
                 ON DUPLICATE KEY UPDATE locked_by_user = ?, locked_until = DATE_ADD(NOW(), INTERVAL 2 MINUTE)";
    $stmt = $conn->prepare($lock_sql);
    $stmt->bind_param("isii", $ground_id, $slot_time, $user_id, $user_id);

    if ($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Slot locked for 2 minutes"]);
    } else {
        echo json_encode(["status" => "error", "message" => "Failed to lock slot"]);
    }
}
?>
