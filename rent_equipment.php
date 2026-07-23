<?php
header('Content-Type: application/json');
require 'db_config.php';

$user_id = $_POST['user_id'] ?? null;
$equipment_id = $_POST['equipment_id'] ?? null;
$duration = $_POST['duration'] ?? null;
$amount = $_POST['amount'] ?? null;

if (!$user_id || !$equipment_id || !$duration) {
    echo json_encode(["status" => "error", "message" => "Missing rental details"]);
    exit;
}

$conn->begin_transaction();

try {
    // 1. Check stock
    $stock_check = $conn->prepare("SELECT stock FROM equipment WHERE id = ?");
    $stock_check->bind_param("i", $equipment_id);
    $stock_check->execute();
    $res = $stock_check->get_result();
    $eq = $res->fetch_assoc();

    if ($eq['stock'] <= 0) {
        throw new Exception("Equipment out of stock");
    }

    // 2. Insert rental
    $date = date('Y-m-d');
    $sql = "INSERT INTO rentals (user_id, equipment_id, rental_date, duration, amount, status) VALUES (?, ?, ?, ?, ?, 'Active')";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("iissd", $user_id, $equipment_id, $date, $duration, $amount);
    $stmt->execute();

    // 3. Update stock
    $update_stock = $conn->prepare("UPDATE equipment SET stock = stock - 1 WHERE id = ?");
    $update_stock->bind_param("i", $equipment_id);
    $update_stock->execute();

    $conn->commit();
    echo json_encode(["status" => "success", "message" => "Equipment rented successfully"]);

} catch (Exception $e) {
    $conn->rollback();
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
?>
