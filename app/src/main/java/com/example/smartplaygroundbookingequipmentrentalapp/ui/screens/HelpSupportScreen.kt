package com.example.smartplaygroundbookingequipmentrentalapp.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

data class SupportChatMessage(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var showAiChatDialog by remember { mutableStateOf(false) }
    var showTicketDialog by remember { mutableStateOf(false) }

    var expandedCategory by remember { mutableStateOf<String?>(null) }

    // Ticket Form States
    var ticketSubject by remember { mutableStateOf("") }
    var ticketMessage by remember { mutableStateOf("") }

    // AI Chat Bot States
    val chatMessages = remember {
        mutableStateListOf(
            SupportChatMessage("Hello! I am your Smart Playground AI assistant. How can I help you today?", isUser = false)
        )
    }
    var chatInput by remember { mutableStateOf("") }

    if (showAiChatDialog) {
        Dialog(onDismissRequest = { showAiChatDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().height(520.dp).padding(8.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SmartToy, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("AI Support Bot", fontWeight = FontWeight.Bold)
                            Text("Online 24/7", color = Color(0xFF43A047), fontSize = 11.sp)
                        }
                        IconButton(onClick = { showAiChatDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))

                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        chatMessages.forEach { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(if (msg.isUser) Alignment.End else Alignment.Start)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (msg.isUser) MaterialTheme.colorScheme.primary else Color(0xFFF0F0F0)
                                ) {
                                    Text(
                                        text = msg.text,
                                        color = if (msg.isUser) Color.White else Color.Black,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            placeholder = { Text("Ask anything about booking, refund...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (chatInput.isNotBlank()) {
                                    val userText = chatInput
                                    chatMessages.add(SupportChatMessage(userText, isUser = true))
                                    chatInput = ""
                                    
                                    val aiReply = when {
                                        userText.contains("cancel", ignoreCase = true) || userText.contains("refund", ignoreCase = true) ->
                                            "You can cancel your booking up to 2 hours before the slot start time from your Booking History screen for a full refund."
                                        userText.contains("book", ignoreCase = true) ->
                                            "To book a slot, select any playground from the Home screen, pick your date/time, and complete payment via UPI or Card."
                                        userText.contains("equipment", ignoreCase = true) || userText.contains("rent", ignoreCase = true) ->
                                            "Equipment can be rented directly alongside your playground slot or separately from the Rent Equipment section."
                                        else ->
                                            "Thank you for contacting support! Our team has logged your query and will assist you shortly."
                                    }
                                    chatMessages.add(SupportChatMessage(aiReply, isUser = false))
                                }
                            },
                            modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }
        }
    }

    if (showTicketDialog) {
        Dialog(onDismissRequest = { showTicketDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Submit Support Ticket", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = ticketSubject,
                        onValueChange = { ticketSubject = it },
                        label = { Text("Subject / Issue Title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = ticketMessage,
                        onValueChange = { ticketMessage = it },
                        label = { Text("Detailed Description") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (ticketSubject.isBlank() || ticketMessage.isBlank()) {
                                Toast.makeText(context, "Please fill in both subject and description", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val ticketId = "TK-" + (1000..9999).random()
                            Toast.makeText(context, "Ticket #$ticketId submitted successfully! We will contact you soon.", Toast.LENGTH_LONG).show()
                            showTicketDialog = false
                            ticketSubject = ""
                            ticketMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Submit Ticket", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { showTicketDialog = false }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Help & Support", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("How can we help you?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            HelpCategoryCard(
                title = "Booking & Reservations",
                subtitle = "How to book, reschedule or cancel slots",
                answer = "You can view and manage all active bookings under 'Booking History'. Cancellations made 2+ hours in advance are eligible for instant refunds.",
                icon = Icons.Default.CalendarMonth,
                isExpanded = expandedCategory == "Booking",
                onClick = { expandedCategory = if (expandedCategory == "Booking") null else "Booking" }
            )

            HelpCategoryCard(
                title = "Payments & Refunds",
                subtitle = "UPI, Card failures & refund timeline",
                answer = "Refunds are automatically processed back to your original payment method (UPI / Card) within 24 to 48 business hours.",
                icon = Icons.Default.Payments,
                isExpanded = expandedCategory == "Payment",
                onClick = { expandedCategory = if (expandedCategory == "Payment") null else "Payment" }
            )

            HelpCategoryCard(
                title = "Account & Profile",
                subtitle = "Phone number, email & security settings",
                answer = "Go to Settings -> Account Profile Info to update your display name, registered phone number, or login credentials.",
                icon = Icons.Default.AccountCircle,
                isExpanded = expandedCategory == "Account",
                onClick = { expandedCategory = if (expandedCategory == "Account") null else "Account" }
            )

            HelpCategoryCard(
                title = "Equipment Quality & Rental",
                subtitle = "Damaged gear, return policy & extensions",
                answer = "Inspect rented equipment upon pickup. If any item is damaged, report it immediately via the app to receive a free replacement.",
                icon = Icons.Default.SportsCricket,
                isExpanded = expandedCategory == "Equipment",
                onClick = { expandedCategory = if (expandedCategory == "Equipment") null else "Equipment" }
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text("Contact Us Directly", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            ContactMethodItem(
                title = "Chat with AI Support Bot",
                detail = "Instant answers 24/7",
                icon = Icons.Default.SmartToy,
                color = Color(0xFF2874F0),
                onClick = { showAiChatDialog = true }
            )

            ContactMethodItem(
                title = "Email Support Team",
                detail = "support@smartplayground.com",
                icon = Icons.Default.Email,
                color = Color(0xFF43A047),
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:support@smartplayground.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Customer Inquiry - Smart Playground App")
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Email support: support@smartplayground.com", Toast.LENGTH_LONG).show()
                    }
                }
            )

            ContactMethodItem(
                title = "Call Support Desk",
                detail = "+91 1800-PLAY-NOW (1800 752 9669)",
                icon = Icons.Default.Phone,
                color = Color(0xFFE53935),
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:18007529669")
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Call Support: 1800-752-9669", Toast.LENGTH_LONG).show()
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Create Support Ticket Button
            Button(
                onClick = { showTicketDialog = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.ConfirmationNumber, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit a Support Ticket", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun HelpCategoryCard(
    title: String,
    subtitle: String,
    answer: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold)
                    Text(subtitle, color = Color.Gray, fontSize = 12.sp)
                }
                Icon(
                    if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = answer,
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ContactMethodItem(title: String, detail: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(44.dp).background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = color)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(detail, color = Color.Gray, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
    }
}
