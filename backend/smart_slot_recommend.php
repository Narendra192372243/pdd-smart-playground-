<?php
header('Content-Type: application/json');
require 'db_config.php';

$ground_id = $_GET['ground_id'];
$date = $_GET['date'];

// Logic: Find slots with fewer bookings or lower historical occupancy
$sql = "SELECT time_slot, COUNT(*) as booking_count
        FROM Bookings
        WHERE ground_id = ? AND booking_date = ?
        GROUP BY time_slot";
$stmt = $conn->prepare($sql);
$stmt->bind_param("is", $ground_id, $date);
$stmt->execute();
$result = $stmt->get_result();

$booked_slots = [];
while($row = $result->fetch_assoc()) {
    $booked_slots[$row['time_slot']] = $row['booking_count'];
}

$all_slots = ["6 AM - 8 AM", "8 AM - 10 AM", "10 AM - 12 PM", "12 PM - 2 PM", "2 PM - 4 PM", "4 AM - 6 PM"];
$recommendations = [];

foreach($all_slots as $slot) {
    $count = $booked_slots[$slot] ?? 0;
    if($count == 0) {
        $recommendations[] = ["slot" => $slot, "status" => "Highly Recommended (Least Crowded)", "discount" => "10% Off"];
    } else if($count < 3) {
        $recommendations[] = ["slot" => $slot, "status" => "Recommended", "discount" => "None"];
    }
}

echo json_encode(["status" => "success", "recommendations" => $recommendations]);
?>
