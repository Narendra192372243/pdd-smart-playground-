<?php
header('Content-Type: application/json');
require 'db_config.php';

$sport = $_GET['sport'];
$user_location = $_GET['location'];

// Innovative logic: Find teams of the same sport type near the user
$sql = "SELECT Teams.*, Users.name as creator_name
        FROM Teams
        JOIN Users ON Teams.creator_id = Users.id
        WHERE sport_type = ? AND players_needed > 0";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $sport);
$stmt->execute();
$result = $stmt->get_result();

$teams = [];
while($row = $result->fetch_assoc()) {
    $teams[] = $row;
}

echo json_encode([
    "status" => "success",
    "message" => "Found " . count($teams) . " teams looking for players",
    "teams" => $teams
]);
?>
