<?php
header('Content-Type: application/json');
require 'db_config.php';

$sql = "SELECT * FROM Grounds";
$result = $conn->query($sql);

$grounds = [];
while($row = $result->fetch_assoc()) {
    $grounds[] = $row;
}

echo json_encode(['status' => 'success', 'data' => $grounds]);
?>