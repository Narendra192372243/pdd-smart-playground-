package com.example.smartplaygroundbookingequipmentrentalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartplaygroundbookingequipmentrentalapp.model.Equipment
import com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState
import com.example.smartplaygroundbookingequipmentrentalapp.model.Rental
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentDetailsScreen(
    equipment: Equipment,
    onBackClick: () -> Unit,
    onRentSuccess: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isRenting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        if (isRenting) return@Button
                        isRenting = true
                        
                        val sessionManager = com.example.smartplaygroundbookingequipmentrentalapp.SessionManager(context)
                        val userId = sessionManager.getUserId() ?: "1"
                        
                        // Create rental object
                        val rental = Rental(
                            id = UUID.randomUUID().toString(),
                            equipment = equipment,
                            date = SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(Date()),
                            duration = "1 Day",
                            amount = equipment.pricePerDay,
                            rentalId = "RN" + (100000..999999).random().toString(),
                            status = "Active",
                            paymentStatus = "Paid"
                        )
                        
                        // Save to backend and global state
                        com.example.smartplaygroundbookingequipmentrentalapp.utils.BookingRepository.rentEquipment(
                            context,
                            userId,
                            rental,
                            {
                                scope.launch {
                                    com.example.smartplaygroundbookingequipmentrentalapp.backend.FirebaseManager.saveRental(rental)
                                    GlobalState.addRental(rental) { _ -> }
                                    GlobalState.currentRentalInProgress = rental
                                    GlobalState.currentBookingInProgress = null
                                    isRenting = false
                                    onRentSuccess()
                                }
                            },
                            { error ->
                                // FALLBACK FOR DEMO: If network fails, proceed locally
                                android.util.Log.e("EquipmentDetails", "Rental failed, using fallback: $error")
                                scope.launch {
                                    com.example.smartplaygroundbookingequipmentrentalapp.backend.FirebaseManager.saveRental(rental)
                                    GlobalState.addRental(rental) { _ -> }
                                    GlobalState.currentRentalInProgress = rental
                                    GlobalState.currentBookingInProgress = null
                                    isRenting = false
                                    android.widget.Toast.makeText(context, "Demo Mode: Rental Confirmed Locally", android.widget.Toast.LENGTH_SHORT).show()
                                    onRentSuccess()
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isRenting == false
                ) {
                    if (isRenting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Rent Now", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Image Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        when (equipment.name) {
                            "Cricket Kit" -> Color(0xFFE3F2FD)
                            "Football" -> Color(0xFFE8F5E9)
                            "Badminton Racket" -> Color(0xFFFFFDE7)
                            "Camping Tent" -> Color(0xFFFFF3E0)
                            "DJ Speaker" -> Color(0xFFF3E5F5)
                            "Cooler" -> Color(0xFFE0F7FA)
                            else -> Color(0xFFF5F5F5)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.TopStart).padding(16.dp).background(Color.White.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                
                Row(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
                    IconButton(modifier = Modifier.background(Color.White.copy(alpha = 0.5f), CircleShape), onClick = { }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(modifier = Modifier.background(Color.White.copy(alpha = 0.5f), CircleShape), onClick = { }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }

                Icon(
                    imageVector = when (equipment.name) {
                        "Cricket Kit" -> Icons.Default.SportsCricket
                        "Football" -> Icons.Default.SportsSoccer
                        "Badminton Racket" -> Icons.Default.SportsTennis
                        "Camping Tent" -> Icons.Default.Cabin
                        "DJ Speaker" -> Icons.Default.Speaker
                        "Cooler" -> Icons.Default.AcUnit
                        else -> Icons.Default.Inventory
                    },
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = when (equipment.name) {
                        "Cricket Kit" -> Color(0xFF1E88E5)
                        "Football" -> Color(0xFF43A047)
                        "Badminton Racket" -> Color(0xFFFBC02D)
                        "Camping Tent" -> Color(0xFFFB8C00)
                        "DJ Speaker" -> Color(0xFF8E24AA)
                        "Cooler" -> Color(0xFF00ACC1)
                        else -> Color.Gray
                    }
                )
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Text(equipment.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    text = "₹${equipment.pricePerDay}/day",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "High quality ${equipment.name.lowercase()} for matches and practice sessions.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Includes:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                val inclusions = when(equipment.name) {
                    "Cricket Kit" -> listOf("1 Bat", "1 Helmet", "2 Balls", "1 Pair of Gloves")
                    "Football" -> listOf("1 Pro Ball", "1 Pump", "1 Pair of Shin Guards")
                    "Badminton Racket" -> listOf("2 Rackets", "3 Shuttles", "1 Bag")
                    "Camping Tent" -> listOf("1 4-Person Tent", "1 Mallet", "Pegs and Ropes")
                    "DJ Speaker" -> listOf("1 200W Speaker", "1 Wireless Mic", "Cables")
                    "Cooler" -> listOf("1 Air Cooler", "1 Remote", "Instruction Manual")
                    else -> listOf("Main Unit", "Essential Accessories")
                }

                inclusions.forEach { item ->
                    Text("• $item", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}
