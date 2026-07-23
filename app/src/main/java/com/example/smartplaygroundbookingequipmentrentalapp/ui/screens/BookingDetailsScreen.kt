package com.example.smartplaygroundbookingequipmentrentalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartplaygroundbookingequipmentrentalapp.model.Booking
import com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState
import com.example.smartplaygroundbookingequipmentrentalapp.model.Playground
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookingDetailsScreen(
    playground: Playground,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Generate next 4 days starting from today (Reduced from 14 as requested)
    val dates = remember {
        val calendarList = mutableListOf<Date>()
        val calendar = Calendar.getInstance()
        for (i in 0 until 4) {
            calendarList.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        calendarList
    }
    
    val dayFormatter = remember { SimpleDateFormat("EEE", Locale.UK) }
    val dateNumFormatter = remember { SimpleDateFormat("dd MMM", Locale.UK) }
    val fullDateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.UK) }
    
    var selectedDate by remember { mutableStateOf(dates[0]) }
    val slots = listOf("6 AM - 8 AM", "8 AM - 10 AM", "10 AM - 12 PM", "12 PM - 2 PM", "2 PM - 4 PM", "4 PM - 6 PM")
    var selectedSlot by remember { mutableStateOf("") }
    var bookedSlots by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoadingSlots by remember { mutableStateOf(false) }

    // Fetch booked slots when date changes
    LaunchedEffect(selectedDate) {
        isLoadingSlots = true
        com.example.smartplaygroundbookingequipmentrentalapp.utils.BookingRepository.fetchBookedSlots(
            context,
            playground.id,
            fullDateFormatter.format(selectedDate),
            { list ->
                bookedSlots = list
                isLoadingSlots = false
                selectedSlot = "" // Reset selection
            },
            { error ->
                isLoadingSlots = false
                android.widget.Toast.makeText(context, "Error loading slots", android.widget.Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        if (selectedSlot.isEmpty()) {
                            android.widget.Toast.makeText(context, "Please select a time slot", android.widget.Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        // Capture selected date/time and ground to GlobalState
                        GlobalState.currentBookingInProgress = Booking(
                            id = UUID.randomUUID().toString(),
                            playground = playground,
                            date = fullDateFormatter.format(selectedDate),
                            timeSlot = selectedSlot,
                            amount = playground.pricePerHour, // Use ground rate
                            bookingId = "PENDING",
                            status = "Upcoming",
                            paymentStatus = "Unpaid"
                        )
                        GlobalState.currentRentalInProgress = null // Reset rental if booking starts
                        onContinueClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = selectedSlot.isNotEmpty()
                ) {
                    Text("Continue to Book", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())) {
            Box(
                modifier = Modifier.fillMaxWidth().height(250.dp).background(Color.LightGray)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.padding(16.dp).background(Color.White.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(playground.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(playground.location, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                
                Spacer(modifier = Modifier.height(24.dp))

                Text("Select Date", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(dates) { date ->
                        val isSelected = selectedDate == date
                        Box(
                            modifier = Modifier
                                .width(75.dp)
                                .height(85.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF5F5F5))
                                .clickable { selectedDate = date }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    dayFormatter.format(date),
                                    textAlign = TextAlign.Center,
                                    color = if (isSelected) Color.White else Color.Gray,
                                    fontSize = 12.sp
                                )
                                Text(
                                    dateNumFormatter.format(date),
                                    textAlign = TextAlign.Center,
                                    color = if (isSelected) Color.White else Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Select Time Slot", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                if (isLoadingSlots) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 3
                    ) {
                        slots.forEach { slot ->
                            val isBooked = bookedSlots.contains(slot)
                            val isSelected = selectedSlot == slot
                            
                            Box(
                                modifier = Modifier
                                    .width(105.dp) // Fixed width for stability
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        1.dp, 
                                        when {
                                            isBooked -> Color.Transparent
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            else -> Color.LightGray
                                        }, 
                                        RoundedCornerShape(8.dp)
                                    )
                                    .background(
                                        when {
                                            isBooked -> Color.LightGray.copy(alpha = 0.3f)
                                            isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable(enabled = !isBooked) { selectedSlot = slot }
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    slot,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        isBooked -> Color.Gray
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        else -> Color.Black
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
