<?php
header('Content-Type: application/json');
require 'db_config.php';

$user_id = $_POST['user_id'] ?? null;
$ground_id = $_POST['ground_id'] ?? null;
$date = $_POST['date'] ?? null;
$slot = $_POST['slot'] ?? null;
$amount = $_POST['amount'] ?? null;

if (!$user_id || !$ground_id || !$date || !$slot) {
    echo json_encode(["status" => "error", "message" => "Missing booking details"]);
    exit;
}

// Generate a random Booking ID
$booking_id_str = "BK" . rand(100000, 999999);

// Start transaction
$conn->begin_transaction();

try {
    // 1. Insert into bookings
    $sql_book = "INSERT INTO bookings (user_id, ground_id, booking_date, time_slot, amount, booking_id_str) VALUES (?, ?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($sql_book);
    $stmt->bind_param("iissss", $user_id, $ground_id, $date, $slot, $amount, $booking_id_str);
    $stmt->execute();

    // 2. Mark slot as booked and clear lock
    $sql_slot = "UPDATE slots SET is_booked = 1, locked_until = NULL WHERE ground_id = ? AND slot_time = ?";
    $stmt2 = $conn->prepare($sql_slot);
    $stmt2->bind_param("is", $ground_id, $slot);
    $stmt2->execute();

    // 3. Update user reward points (10 points per booking)
    $sql_points = "UPDATE users SET reward_points = reward_points + 10 WHERE id = ?";
    $stmt3 = $conn->prepare($sql_points);
    $stmt3->bind_param("i", $user_id);
    $stmt3->execute();

    $conn->commit();
    echo json_encode(["status" => "success", "message" => "Booking confirmed", "booking_id" => $booking_id_str]);

} catch (Exception $e) {
    $conn->rollback();
    echo json_encode(["status" => "error", "message" => "Booking failed: " . $e->getMessage()]);
}
?>
