<?php
header('Content-Type: application/json');
require 'db_config.php';

// Get POST data
$user_id = $_POST['user_id'] ?? null;
$ground_id = $_POST['ground_id'] ?? null;
$booking_date = $_POST['booking_date'] ?? null;
$time_slot = $_POST['time_slot'] ?? null;
$amount = $_POST['amount'] ?? null;

if (!$user_id || !$ground_id || !$booking_date || !$time_slot) {
    echo json_encode(["status" => "failed", "message" => "Missing required fields"]);
    exit;
}

// Start Database Transaction for atomicity
$conn->begin_transaction();

try {
    // 1. Check if the slot is already booked (Validation)
    // 'FOR UPDATE' prevents race conditions between simultaneous users
    $check_sql = "SELECT id FROM Bookings WHERE ground_id = ? AND booking_date = ? AND time_slot = ? AND status = 'Confirmed' FOR UPDATE";
    $stmt = $conn->prepare($check_sql);
    $stmt->bind_param("iss", $ground_id, $booking_date, $time_slot);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        // Conflict Found: Failure response
        echo json_encode([
            "status" => "failed",
            "message" => "Slot Already Booked. Please choose another time slot."
        ]);
        $conn->rollback(); // Release locks
    } else {
        // 2. No conflict: Proceed to book (Success path)
        $insert_sql = "INSERT INTO Bookings (user_id, ground_id, booking_date, time_slot, amount, status) VALUES (?, ?, ?, ?, ?, 'Confirmed')";
        $insert_stmt = $conn->prepare($insert_sql);
        $insert_stmt->bind_param("iisss", $user_id, $ground_id, $booking_date, $time_slot, $amount);

        if ($insert_stmt->execute()) {
            $conn->commit(); // Finalize booking
            echo json_encode([
                "status" => "success",
                "message" => "Booking Confirmed"
            ]);
        } else {
            throw new Exception("Execution failed: " . $conn->error);
        }
    }
} catch (Exception $e) {
    $conn->rollback();
    echo json_encode([
        "status" => "error",
        "message" => "Internal Server Error. Please try again later."
    ]);
}
?>
