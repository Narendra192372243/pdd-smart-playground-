package com.example.smartplaygroundbookingequipmentrentalapp.model

import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.graphics.Color
import com.example.smartplaygroundbookingequipmentrentalapp.ui.screens.NotificationData
import kotlinx.coroutines.launch

object GlobalState {
    var selectedPlayground by mutableStateOf<Playground?>(null)
    var currentBookingInProgress by mutableStateOf<Booking?>(null)
    var currentRentalInProgress by mutableStateOf<Rental?>(null)
    var selectedPaymentMethod by mutableStateOf("PhonePe")
    var liveLocation by mutableStateOf("Adyar, Chennai")
    var currentUserName by mutableStateOf("Narendra Reddy")
    var currentUserEmail by mutableStateOf("narendrareddyk742@gmail.com")
    var currentUserPhone by mutableStateOf("+91 9876543210")
    var profileImageUri by mutableStateOf<String?>(null)
    var loyaltyPoints by mutableIntStateOf(150)

    fun setUserSession(context: android.content.Context, name: String, email: String, phone: String, location: String = "Adyar, Chennai") {
        if (name.isNotBlank()) currentUserName = name
        if (email.isNotBlank()) currentUserEmail = email
        if (phone.isNotBlank()) currentUserPhone = phone
        if (location.isNotBlank()) liveLocation = location

        val session = com.example.smartplaygroundbookingequipmentrentalapp.SessionManager(context)
        session.createLoginSession(session.getUserId() ?: "1", name, email, phone, location)
        if (profileImageUri == null) {
            profileImageUri = session.getUserProfileImage()
        }
    }

    fun updateProfileImage(context: android.content.Context, uriString: String) {
        profileImageUri = uriString
        val session = com.example.smartplaygroundbookingequipmentrentalapp.SessionManager(context)
        session.updateUserProfileImage(uriString)
    }

    fun updatePhone(context: android.content.Context, newPhone: String) {
        currentUserPhone = newPhone
        val session = com.example.smartplaygroundbookingequipmentrentalapp.SessionManager(context)
        session.updateUserPhone(newPhone)
        kotlinx.coroutines.GlobalScope.launch {
            com.example.smartplaygroundbookingequipmentrentalapp.backend.FirebaseManager.saveUserProfile(currentUserName, currentUserEmail, newPhone, liveLocation)
        }
    }

    fun updateLocation(context: android.content.Context, newLocation: String) {
        liveLocation = newLocation
        val session = com.example.smartplaygroundbookingequipmentrentalapp.SessionManager(context)
        session.updateUserLocation(newLocation)
        kotlinx.coroutines.GlobalScope.launch {
            com.example.smartplaygroundbookingequipmentrentalapp.backend.FirebaseManager.saveUserProfile(currentUserName, currentUserEmail, currentUserPhone, newLocation)
        }
    }

    var userLatitude by mutableDoubleStateOf(13.0827) // Default Chennai Lat
    var userLongitude by mutableDoubleStateOf(80.2707) // Default Chennai Lng
    var isGpsActive by mutableStateOf(false)
    var searchRadiusKm by mutableDoubleStateOf(20.0) // 20 km default search radius

    val bookings = mutableStateListOf<Booking>()
    val rentals = mutableStateListOf<Rental>()
    val notifications = mutableStateListOf<NotificationData>()

    val allPlaygrounds = listOf(
        // CHENNAI PLAYGROUNDS
        Playground("1", "Green Field Turf Arena", "Adyar, Chennai", 4.8, 128, 800, 0, listOf("Cricket", "Football"), "Free", 13.0067, 80.2571),
        Playground("2", "PlayMax Sports Ground", "T. Nagar, Chennai", 4.6, 95, 600, 0, listOf("Badminton", "Tennis"), "High Occupancy", 13.0418, 80.2341),
        Playground("3", "Victory Turf Club", "Velachery, Chennai", 4.9, 210, 1200, 0, listOf("Football", "Cricket"), "Free", 12.9815, 80.2180),
        Playground("4", "Marina Volleyball Court", "Marina, Chennai", 4.5, 78, 400, 0, listOf("Volleyball"), "Free", 13.0500, 80.2824),
        Playground("5", "Royal Kabaddi & Sports Turf", "Ambattur, Chennai", 4.7, 112, 500, 0, listOf("Kabaddi", "Cricket"), "Free", 13.1143, 80.1548),
        
        // BANGALORE PLAYGROUNDS
        Playground("6", "Kanteerava Sports Turf", "Koramangala, Bangalore", 4.8, 180, 950, 0, listOf("Football", "Cricket"), "Free", 12.9352, 77.6245),
        Playground("7", "Indiranagar Smash Arena", "Indiranagar, Bangalore", 4.7, 140, 700, 0, listOf("Badminton", "Tennis"), "Free", 12.9784, 77.6408),
        Playground("8", "Whitefield Football Ground", "Whitefield, Bangalore", 4.6, 90, 850, 0, listOf("Football"), "Free", 12.9698, 77.7499),

        // HYDERABAD PLAYGROUNDS
        Playground("9", "Gachibowli Sports Hub", "Gachibowli, Hyderabad", 4.9, 230, 1100, 0, listOf("Cricket", "Football"), "Free", 17.4401, 78.3489),
        Playground("10", "Hitec City Turf Arena", "Hitec City, Hyderabad", 4.7, 160, 900, 0, listOf("Badminton", "Cricket"), "Free", 17.4435, 78.3772),

        // MUMBAI PLAYGROUNDS
        Playground("11", "Juhu Beach Sports Complex", "Juhu, Mumbai", 4.8, 205, 1300, 0, listOf("Volleyball", "Football"), "Free", 19.1075, 72.8263),
        Playground("12", "Bandra Football Ground", "Bandra West, Mumbai", 4.6, 115, 1000, 0, listOf("Football", "Cricket"), "Free", 19.0596, 72.8295),

        // DELHI PLAYGROUNDS
        Playground("13", "Siri Fort Sports Complex", "Siri Fort, New Delhi", 4.9, 290, 1400, 0, listOf("Tennis", "Badminton"), "Free", 28.5492, 77.2184),
        Playground("14", "Dwarka Cricket Academy", "Dwarka, New Delhi", 4.5, 85, 750, 0, listOf("Cricket"), "Free", 28.5921, 77.0460)
    )

    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return Math.round(r * c * 10.0) / 10.0
    }

    fun getNearbyPlaygrounds(): List<Playground> {
        return allPlaygrounds.map { pg ->
            val dist = calculateDistanceKm(userLatitude, userLongitude, pg.latitude, pg.longitude)
            pg.copy(distanceKm = dist)
        }.filter { it.distanceKm <= searchRadiusKm }.sortedBy { it.distanceKm }
    }

    fun addBooking(booking: Booking) {
        android.util.Log.d("GlobalState", "Adding booking to UI: ${booking.bookingId}")
        val existing = bookings.find { it.bookingId == booking.bookingId }
        if (existing == null) {
            bookings.add(0, booking)
            loyaltyPoints += 50
            
            notifications.add(0, NotificationData(
                "Booking Confirmed!",
                "Your booking for ${booking.playground.name} is successful.",
                "Just now",
                Icons.Default.CheckCircle,
                Color(0xFF43A047)
            ))
        }
    }

    fun refreshHistory(context: android.content.Context) {
        val sessionManager = com.example.smartplaygroundbookingequipmentrentalapp.SessionManager(context)
        val userId = sessionManager.getUserId() ?: "1"
        
        com.example.smartplaygroundbookingequipmentrentalapp.utils.BookingRepository.fetchBookingHistory(
            context,
            userId,
            { list ->
                bookings.clear()
                bookings.addAll(list)
            },
            { error -> android.util.Log.e("GlobalState", "Failed to load booking history: $error") }
        )

        com.example.smartplaygroundbookingequipmentrentalapp.utils.BookingRepository.fetchRentalHistory(
            context,
            userId,
            { list ->
                rentals.clear()
                rentals.addAll(list)
            },
            { error -> android.util.Log.e("GlobalState", "Failed to load rental history: $error") }
        )
    }

    fun addRental(rental: Rental, onComplete: (Boolean) -> Unit) {
        if (rentals.none { it.rentalId == rental.rentalId }) {
            rentals.add(0, rental)
        }
        
        notifications.add(0, NotificationData(
            "Rental Confirmed!",
            "Your rental for ${rental.equipment.name} is successful.",
            "Just now",
            Icons.Default.CheckCircle,
            Color(0xFF43A047)
        ))
        onComplete(true)
    }
}
