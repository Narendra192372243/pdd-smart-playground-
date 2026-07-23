package com.example.smartplaygroundbookingequipmentrentalapp.model

data class Playground(
    val id: String,
    val name: String,
    val location: String,
    val rating: Double,
    val reviewsCount: Int,
    val pricePerHour: Int,
    val imageResId: Int,
    val categories: List<String> = listOf("Cricket", "Football"),
    val occupancyStatus: String = "Free" // Free, Busy, Full
)

data class Equipment(
    val id: String,
    val name: String,
    val pricePerDay: Int,
    val imageResId: Int,
    val category: String,
    val predictedDemand: String = "Low" // Low, Medium, High
)

data class Booking(
    val id: String,
    val playground: Playground,
    val date: String,
    val timeSlot: String,
    val amount: Int,
    val bookingId: String,
    val status: String = "Confirmed",
    val paymentStatus: String = "Paid",
    val qrCodeData: String = "SP-CHECKIN-${bookingId}",
    val pointsEarned: Int = amount / 10
)

data class Rental(
    val id: String,
    val equipment: Equipment,
    val date: String,
    val duration: String,
    val amount: Int,
    val rentalId: String,
    val status: String = "Confirmed",
    val paymentStatus: String = "Paid"
)

data class Team(
    val id: String,
    val name: String,
    val sport: String,
    val playersNeeded: Int,
    val location: String,
    val creatorName: String
)
