<?php
header('Content-Type: application/json');
require 'db_config.php';

$sql = "SELECT * FROM equipment";
$result = $conn->query($sql);

$equipment = [];
while($row = $result->fetch_assoc()) {
    $equipment[] = $row;
}

echo json_encode(["status" => "success", "data" => $equipment]);
?>
