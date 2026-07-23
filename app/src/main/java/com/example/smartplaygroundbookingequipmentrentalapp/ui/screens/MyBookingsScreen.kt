package com.example.smartplaygroundbookingequipmentrentalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.smartplaygroundbookingequipmentrentalapp.model.Booking
import com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(onBackClick: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Playgrounds", "Equipment")
    
    LaunchedEffect(Unit) {
        GlobalState.refreshHistory(context)
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("My History", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
                SecondaryTabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8F9FA))) {
            when (selectedTab) {
                0 -> { // Playgrounds
                    if (GlobalState.bookings.isEmpty()) {
                        EmptyState("No playground bookings found")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(GlobalState.bookings) { booking ->
                                BookingHistoryItem(booking)
                            }
                        }
                    }
                }
                1 -> { // Equipment
                    if (GlobalState.rentals.isEmpty()) {
                        EmptyState("No equipment rentals found")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(GlobalState.rentals) { rental ->
                                RentalHistoryItem(rental)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, color = Color.Gray)
        }
    }
}

@Composable
fun RentalHistoryItem(rental: com.example.smartplaygroundbookingequipmentrentalapp.model.Rental) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Inventory, contentDescription = null, tint = Color(0xFF1E88E5))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(rental.equipment.name, fontWeight = FontWeight.Bold)
                    Text(rental.equipment.category, fontSize = 12.sp, color = Color.Gray)
                }
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = rental.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${rental.date} | ${rental.duration}", fontSize = 13.sp)
                Text("₹${rental.amount}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun BookingHistoryItem(booking: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(getGroundBg(booking.playground.name)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(getGroundIcon(booking.playground.name), contentDescription = null, tint = getGroundColor(booking.playground.name))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(booking.playground.name, fontWeight = FontWeight.Bold)
                    Text(booking.playground.location, fontSize = 12.sp, color = Color.Gray)
                }
                Surface(
                    color = if (booking.status == "Confirmed") Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = booking.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (booking.status == "Confirmed") Color(0xFF2E7D32) else Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${booking.date} | ${booking.timeSlot}", fontSize = 13.sp)
                Text("₹${booking.amount}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

fun getGroundBg(name: String) = when {
    name.contains("Green Field") -> Color(0xFFE8F5E9)
    name.contains("PlayMax") -> Color(0xFFF3E5F5)
    name.contains("Victory") -> Color(0xFFE3F2FD)
    name.contains("Volleyball") -> Color(0xFFFFF3E0)
    name.contains("Kabaddi") -> Color(0xFFFFEBEE)
    else -> Color(0xFFF5F5F5)
}
fun getGroundIcon(name: String) = when {
    name.contains("Green Field") -> Icons.Default.Stadium
    name.contains("PlayMax") -> Icons.Default.SportsCricket
    name.contains("Victory") -> Icons.Default.SportsSoccer
    name.contains("Volleyball") -> Icons.Default.SportsVolleyball
    name.contains("Kabaddi") -> Icons.Default.SportsKabaddi
    else -> Icons.Default.Image
}
fun getGroundColor(name: String) = when {
    name.contains("Green Field") -> Color(0xFF43A047)
    name.contains("PlayMax") -> Color(0xFF8E24AA)
    name.contains("Victory") -> Color(0xFF1E88E5)
    name.contains("Volleyball") -> Color(0xFFFB8C00)
    name.contains("Kabaddi") -> Color(0xFFE53935)
    else -> Color.Gray
}
