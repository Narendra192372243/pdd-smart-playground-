<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Headers: Content-Type');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

require 'db_config.php';

// Accept both JSON and POST Form Data
$inputData = json_decode(file_get_contents('php://input'), true);

$phone = $inputData['phone'] ?? $inputData['email'] ?? $_POST['phone'] ?? $_POST['email'] ?? null;
$password = $inputData['password'] ?? $_POST['password'] ?? null;

if (!$phone || !$password) {
    echo json_encode(["status" => "error", "message" => "Phone/Email and password required"]);
    exit;
}

// Ensure location column exists
@$conn->query("ALTER TABLE users ADD COLUMN location VARCHAR(255) DEFAULT 'Adyar, Chennai'");

$sql = "SELECT id, name, email, phone, password, profile_pic, location, reward_points FROM users WHERE phone = ? OR email = ?";
$stmt = $conn->prepare($sql);
if (!$stmt) {
    $sql = "SELECT id, name, email, phone, password, profile_pic, reward_points FROM users WHERE phone = ? OR email = ?";
    $stmt = $conn->prepare($sql);
}
$stmt->bind_param("ss", $phone, $phone);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $user = $result->fetch_assoc();
    if (password_verify($password, $user['password'])) {
        unset($user['password']); // Remove password from response
        if (empty($user['location'])) {
            $user['location'] = 'Adyar, Chennai';
        }
        echo json_encode(["status" => "success", "message" => "Login successful", "user" => $user]);
    } else {
        echo json_encode(["status" => "error", "message" => "Invalid password"]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "User account not found"]);
}
?>
