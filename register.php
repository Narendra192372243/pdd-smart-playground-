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

$name = $inputData['name'] ?? $_POST['name'] ?? null;
$email = $inputData['email'] ?? $_POST['email'] ?? null;
$phone = $inputData['phone'] ?? $_POST['phone'] ?? null;
$password = $inputData['password'] ?? $_POST['password'] ?? null;
$location = $inputData['location'] ?? $_POST['location'] ?? 'Adyar, Chennai';

if (!$name || !$phone || !$password) {
    echo json_encode(["status" => "error", "message" => "Name, phone, and password are required fields"]);
    exit;
}

// Check if user already exists
$checkSql = "SELECT id FROM users WHERE phone = ? OR (email IS NOT NULL AND email = ? AND email != '')";
$checkStmt = $conn->prepare($checkSql);
$checkStmt->bind_param("ss", $phone, $email);
$checkStmt->execute();
$checkResult = $checkStmt->get_result();

if ($checkResult->num_rows > 0) {
    echo json_encode(["status" => "error", "message" => "User with this phone or email already exists"]);
    exit;
}

$hashed_password = password_hash($password, PASSWORD_DEFAULT);
$initial_points = 100; // Signup bonus points

// Ensure location column exists in users table
@$conn->query("ALTER TABLE users ADD COLUMN location VARCHAR(255) DEFAULT 'Adyar, Chennai'");

$sql = "INSERT INTO users (name, email, phone, password, location, reward_points) VALUES (?, ?, ?, ?, ?, ?)";
$stmt = $conn->prepare($sql);
if (!$stmt) {
    // Fallback if alter failed or table structure varies
    $sql = "INSERT INTO users (name, email, phone, password, reward_points) VALUES (?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ssssi", $name, $email, $phone, $hashed_password, $initial_points);
} else {
    $stmt->bind_param("sssssi", $name, $email, $phone, $hashed_password, $location, $initial_points);
}

if ($stmt->execute()) {
    $userId = $conn->insert_id;
    $userData = [
        "id" => $userId,
        "name" => $name,
        "email" => $email,
        "phone" => $phone,
        "location" => $location,
        "reward_points" => $initial_points
    ];
    echo json_encode(["status" => "success", "message" => "Account created successfully!", "user" => $userData]);
} else {
    echo json_encode(["status" => "error", "message" => "Registration failed: " . $conn->error]);
}
?>
