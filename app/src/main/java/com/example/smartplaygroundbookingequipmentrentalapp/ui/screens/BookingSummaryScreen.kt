package com.example.smartplaygroundbookingequipmentrentalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartplaygroundbookingequipmentrentalapp.model.Booking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingSummaryScreen(
    booking: Booking,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Booking Summary", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = onConfirmClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirm Booking", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(booking.playground.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(booking.playground.location, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Date & Time", fontWeight = FontWeight.Bold)
            Text("${booking.date} | ${booking.timeSlot}", color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            Text("Price Details", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            PriceRowItem("Ground Rental", "₹${booking.amount}")
            PriceRowItem("Service Fee", "₹0")
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            PriceRowItem("Total Amount", "₹${booking.amount}", isTotal = true)
        }
    }
}

@Composable
private fun PriceRowItem(label: String, value: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = if (isTotal) Color.Black else Color.Gray, fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal)
        Text(value, fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal, color = if (isTotal) MaterialTheme.colorScheme.primary else Color.Black)
    }
}
