package com.example.smartplaygroundbookingequipmentrentalapp.utils

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.smartplaygroundbookingequipmentrentalapp.model.Booking
import com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState
import com.example.smartplaygroundbookingequipmentrentalapp.model.Playground
import org.json.JSONObject

object BookingRepository {
    private const val BASE_URL = "http://10.0.2.2/smart_playground/"

    fun bookGround(
        context: Context,
        userId: String,
        booking: Booking,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "${BASE_URL}book_slot.php"
        val queue = Volley.newRequestQueue(context)

        val request = object : StringRequest(Request.Method.POST, url,
            { response ->
                android.util.Log.d("BookingRepo", "Response: $response")
                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.has("status") && jsonResponse.getString("status") == "success") {
                        val bookingId = jsonResponse.getString("booking_id")
                        onSuccess(bookingId)
                    } else {
                        onError(jsonResponse.optString("message", "Unknown error from server"))
                    }
                } catch (e: Exception) {
                    onError("Failed to parse server response")
                }
            },
            { error -> 
                val errorMsg = if (error.networkResponse != null) {
                    "Server Error: ${error.networkResponse.statusCode}"
                } else {
                    "Check your internet connection"
                }
                onError(errorMsg)
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = userId
                params["ground_id"] = booking.playground.id
                params["date"] = booking.date
                params["slot"] = booking.timeSlot
                params["amount"] = booking.amount.toString()
                return params
            }
        }
        queue.add(request)
    }

    fun fetchBookingHistory(
        context: Context,
        userId: String,
        onSuccess: (List<Booking>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "${BASE_URL}get_history.php?user_id=$userId"
        val queue = Volley.newRequestQueue(context)

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    if (response.getString("status") == "success") {
                        val data = response.getJSONArray("data")
                        val list = mutableListOf<Booking>()
                        for (i in 0 until data.length()) {
                            val obj = data.getJSONObject(i)
                            list.add(Booking(
                                id = obj.getString("id"),
                                playground = Playground(
                                    id = obj.getString("ground_id"),
                                    name = obj.getString("playground_name"),
                                    location = obj.getString("address"),
                                    rating = 4.5, // Placeholder or fetch from DB
                                    reviewsCount = 100,
                                    pricePerHour = obj.getInt("amount"),
                                    imageResId = 0
                                ),
                                date = obj.getString("booking_date"),
                                timeSlot = obj.getString("time_slot"),
                                amount = obj.getInt("amount"),
                                bookingId = obj.getString("booking_id_str"),
                                status = obj.getString("status"),
                                paymentStatus = obj.getString("payment_status")
                            ))
                        }
                        onSuccess(list)
                    } else {
                        onError(response.getString("message"))
                    }
                } catch (e: Exception) {
                    onError("Parse error: ${e.message}")
                }
            },
            { error -> onError("Network error: ${error.message}") }
        )
        queue.add(request)
    }

    fun fetchBookedSlots(
        context: Context,
        groundId: String,
        date: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "${BASE_URL}get_slots.php?ground_id=$groundId&date=$date"
        val queue = Volley.newRequestQueue(context)

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    if (response.getString("status") == "success") {
                        val data = response.getJSONArray("booked_slots")
                        val list = mutableListOf<String>()
                        for (i in 0 until data.length()) {
                            list.add(data.getString(i))
                        }
                        onSuccess(list)
                    } else {
                        onError(response.getString("message"))
                    }
                } catch (e: Exception) {
                    onError("Parse error")
                }
            },
            { error -> onError("Network error") }
        )
        queue.add(request)
    }

    fun rentEquipment(
        context: Context,
        userId: String,
        rental: com.example.smartplaygroundbookingequipmentrentalapp.model.Rental,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "${BASE_URL}rent_equipment.php"
        val queue = Volley.newRequestQueue(context)

        val request = object : StringRequest(Request.Method.POST, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getString("status") == "success") {
                        onSuccess()
                    } else {
                        onError(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {
                    onError("Failed to parse response: ${e.message}")
                }
            },
            { error -> onError("Network error: ${error.message}") }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = userId
                params["equipment_id"] = rental.equipment.id
                params["duration"] = rental.duration
                params["amount"] = rental.amount.toString()
                return params
            }
        }
        queue.add(request)
    }

    fun fetchRentalHistory(
        context: Context,
        userId: String,
        onSuccess: (List<com.example.smartplaygroundbookingequipmentrentalapp.model.Rental>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "${BASE_URL}get_rental_history.php?user_id=$userId"
        val queue = Volley.newRequestQueue(context)

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    if (response.getString("status") == "success") {
                        val data = response.getJSONArray("data")
                        val list = mutableListOf<com.example.smartplaygroundbookingequipmentrentalapp.model.Rental>()
                        for (i in 0 until data.length()) {
                            val obj = data.getJSONObject(i)
                            list.add(com.example.smartplaygroundbookingequipmentrentalapp.model.Rental(
                                id = obj.getString("id"),
                                equipment = com.example.smartplaygroundbookingequipmentrentalapp.model.Equipment(
                                    id = obj.getString("equipment_id"),
                                    name = obj.getString("equipment_name"),
                                    pricePerDay = obj.getInt("amount"),
                                    imageResId = 0,
                                    category = obj.getString("category")
                                ),
                                date = obj.getString("rental_date"),
                                duration = obj.getString("duration"),
                                amount = obj.getInt("amount"),
                                rentalId = "RN" + obj.getString("id"),
                                status = obj.getString("status"),
                                paymentStatus = "Paid"
                            ))
                        }
                        onSuccess(list)
                    } else {
                        onError(response.getString("message"))
                    }
                } catch (e: Exception) {
                    onError("Parse error: ${e.message}")
                }
            },
            { error -> onError("Network error: ${error.message}") }
        )
        queue.add(request)
    }

    fun registerUserOnServer(
        context: Context,
        name: String,
        email: String,
        phone: String,
        location: String,
        password: String
    ) {
        val url = "${BASE_URL}register.php"
        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.POST, url,
            { response -> android.util.Log.d("RegisterServer", "Server response: $response") },
            { error -> android.util.Log.e("RegisterServer", "Server error: ${error.message}") }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["name"] = name
                params["email"] = email
                params["phone"] = phone
                params["location"] = location
                params["password"] = password
                return params
            }
        }
        queue.add(request)
    }
}
