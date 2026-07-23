package com.example.smartplaygroundbookingequipmentrentalapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartplaygroundbookingequipmentrentalapp.model.Booking
import com.example.smartplaygroundbookingequipmentrentalapp.model.Rental
import com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState
import kotlinx.coroutines.delay

@Composable
fun BookingConfirmationScreen(
    booking: Booking?,
    rental: Rental?,
    onViewBookingClick: () -> Unit,
    onBackToHomeClick: () -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showDetails by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        GlobalState.refreshHistory(context)
        delay(500)
        showDetails = true
    }

    val title = if (booking != null) "Booking Confirmed!" else "Rental Confirmed!"
    val idStr = if (booking != null) "Booking ID: #${booking.bookingId}" else if (rental != null) "Rental ID: #${rental.rentalId}" else "Success"
    val name = booking?.playground?.name ?: rental?.equipment?.name ?: "Item"
    val subInfo = if (booking != null) "${booking.date} | ${booking.timeSlot}" else "${rental?.date} | ${rental?.duration}"
    val icon = if (booking != null) Icons.Default.Stadium else Icons.Default.Inventory

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary) 
    ) {
        // Success Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check, 
                        contentDescription = null, 
                        tint = Color(0xFF43A047), 
                        modifier = Modifier.size(60.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    title, 
                    color = Color.White, 
                    fontSize = 26.sp, 
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    idStr, 
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }
        }

        // Details White Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            AnimatedVisibility(
                visible = showDetails,
                enter = slideInVertically { it / 2 } + fadeIn()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(50.dp).background(Color.White, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(name, fontWeight = FontWeight.Bold)
                                Text(subInfo, fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "Success! Item added to your history.", 
                        color = Color(0xFF43A047), 
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("Check your 'Bookings' tab for more details.", fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = onViewBookingClick,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("VIEW IN HISTORY", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onBackToHomeClick,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("BACK TO HOME", fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
