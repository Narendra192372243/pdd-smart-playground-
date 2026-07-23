package com.example.smartplaygroundbookingequipmentrentalapp.backend

import com.example.smartplaygroundbookingequipmentrentalapp.model.Booking
import com.example.smartplaygroundbookingequipmentrentalapp.model.Equipment
import com.example.smartplaygroundbookingequipmentrentalapp.model.Playground
import com.example.smartplaygroundbookingequipmentrentalapp.model.Rental
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

object FirebaseManager {
    private val auth: FirebaseAuth? get() = try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
    private val db: FirebaseFirestore? get() = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }

    // --- Authentication ---
    fun getCurrentUserId(): String? = auth?.currentUser?.uid

    fun logout() {
        auth?.signOut()
    }

    // --- Ground Bookings ---
    suspend fun saveBooking(booking: Booking): Boolean {
        return try {
            val database = db ?: return false
            val userId = getCurrentUserId() ?: return false
            val bookingData = hashMapOf(
                "userId" to userId,
                "groundId" to booking.playground.id,
                "groundName" to booking.playground.name,
                "date" to booking.date,
                "timeSlot" to booking.timeSlot,
                "amount" to booking.amount,
                "status" to booking.status,
                "paymentStatus" to booking.paymentStatus,
                "timestamp" to System.currentTimeMillis()
            )
            database.collection("bookings").document(booking.bookingId).set(bookingData).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserBookings(): List<Booking> {
        return try {
            val database = db ?: return emptyList()
            val userId = getCurrentUserId() ?: return emptyList()
            val snapshot = database.collection("bookings")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            
            snapshot.documents.map { doc ->
                Booking(
                    id = doc.id,
                    playground = Playground(doc.getString("groundId") ?: "", doc.getString("groundName") ?: "", "", 0.0, 0, 0, 0),
                    date = doc.getString("date") ?: "",
                    timeSlot = doc.getString("timeSlot") ?: "",
                    amount = doc.getLong("amount")?.toInt() ?: 0,
                    bookingId = doc.id,
                    status = doc.getString("status") ?: "Confirmed",
                    paymentStatus = doc.getString("paymentStatus") ?: "Paid"
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- Equipment Rentals ---
    suspend fun saveRental(rental: Rental): Boolean {
        return try {
            val database = db ?: return false
            val userId = getCurrentUserId() ?: return false
            val rentalData = hashMapOf(
                "userId" to userId,
                "equipmentId" to rental.equipment.id,
                "equipmentName" to rental.equipment.name,
                "date" to rental.date,
                "duration" to rental.duration,
                "amount" to rental.amount,
                "status" to rental.status,
                "paymentStatus" to rental.paymentStatus,
                "timestamp" to System.currentTimeMillis()
            )
            database.collection("rentals").document(rental.rentalId).set(rentalData).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // --- User Profile ---
    suspend fun saveUserProfile(name: String, email: String, phone: String, location: String = "Adyar, Chennai"): Boolean {
        return try {
            val database = db ?: return false
            val userId = getCurrentUserId() ?: return false
            val userData = hashMapOf(
                "name" to name,
                "email" to email,
                "phone" to phone,
                "location" to location,
                "role" to "User"
            )
            database.collection("users").document(userId).set(userData).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun saveUserProfileIfNotExists(name: String, email: String, phone: String, location: String = "Adyar, Chennai"): Boolean {
        return try {
            val database = db ?: return false
            val userId = getCurrentUserId() ?: return false
            val docRef = database.collection("users").document(userId)
            val documentSnapshot = docRef.get().await()
            if (!documentSnapshot.exists()) {
                val userData = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "phone" to phone,
                    "location" to location,
                    "role" to "User"
                )
                docRef.set(userData).await()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserProfile(userId: String): Map<String, String>? {
        return try {
            val database = db ?: return null
            val docRef = database.collection("users").document(userId).get().await()
            if (docRef.exists()) {
                mapOf(
                    "name" to (docRef.getString("name") ?: ""),
                    "email" to (docRef.getString("email") ?: ""),
                    "phone" to (docRef.getString("phone") ?: ""),
                    "location" to (docRef.getString("location") ?: "Adyar, Chennai")
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
