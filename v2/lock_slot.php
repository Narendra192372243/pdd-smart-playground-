<?php
header('Content-Type: application/json');
require '../db_config.php';

$ground_id = $_POST['ground_id'];
$slot_time = $_POST['slot_time'];
$user_id = $_POST['user_id'];

$conn->begin_transaction();

// 1. Check if booked
$check = $conn->prepare("SELECT id FROM slots WHERE ground_id = ? AND slot_time = ? AND is_booked = 1");
$check->bind_param("is", $ground_id, $slot_time);
$check->execute();
if($check->get_result()->num_rows > 0) {
    echo json_encode(["status" => "failed", "message" => "Slot already booked"]);
    $conn->rollback();
    exit;
}

// 2. Check if locked by others
$now = date('Y-m-d H:i:s');
$lock_check = $conn->prepare("SELECT id FROM slots WHERE ground_id = ? AND slot_time = ? AND locked_until > ? AND locked_by_user != ?");
$lock_check->bind_param("issi", $ground_id, $slot_time, $now, $user_id);
$lock_check->execute();
if($lock_check->get_result()->num_rows > 0) {
    echo json_encode(["status" => "failed", "message" => "Slot Temporarily Reserved"]);
    $conn->rollback();
    exit;
}

// 3. Apply/Renew Lock
$locked_until = date('Y-m-d H:i:s', strtotime('+2 minutes'));
$upsert = $conn->prepare("INSERT INTO slots (ground_id, slot_time, locked_until, locked_by_user)
    VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE locked_until = ?, locked_by_user = ?");
$upsert->bind_param("issisi", $ground_id, $slot_time, $locked_until, $user_id, $locked_until, $user_id);

if($upsert->execute()) {
    $conn->commit();
    echo json_encode(["status" => "success", "message" => "Slot locked for 2 minutes"]);
} else {
    $conn->rollback();
    echo json_encode(["status" => "error", "message" => "Lock failed"]);
}
?>
