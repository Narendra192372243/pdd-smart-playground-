package com.example.smartplaygroundbookingequipmentrentalapp.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    amount: Int,
    onBackClick: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedMethod by remember { mutableStateOf("PhonePe") }
    var isProcessing by remember { mutableStateOf(false) }
    var processingStatus by remember { mutableStateOf("") }

    // Input States
    var upiId by remember { mutableStateOf("") }
    var showQrCode by remember { mutableStateOf(false) }
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }
    var selectedCardBank by remember { mutableStateOf("SBI") }

    if (isProcessing) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = processingStatus,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Payment", fontWeight = FontWeight.Bold) },
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
                    onClick = {
                        if (isProcessing) return@Button
                        
                        // Validate selected payment details first
                        if (selectedMethod in listOf("PhonePe", "Google Pay", "Paytm") && upiId.isEmpty() && !showQrCode) {
                            Toast.makeText(context, "Please enter a valid UPI ID or scan the QR Code", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (selectedMethod == "Credit/Debit Card" && (cardNumber.length < 16 || cardExpiry.length < 5 || cardCvv.length < 3)) {
                            Toast.makeText(context, "Please enter valid card details", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        scope.launch {
                            isProcessing = true
                            processingStatus = "Connecting to Secure Gateway..."
                            delay(1200)
                            processingStatus = "Verifying payment details..."
                            delay(1200)
                            processingStatus = "Authorizing transaction with bank..."
                            delay(1200)
                            processingStatus = "Securing booking slot..."

                            val sessionManager = com.example.smartplaygroundbookingequipmentrentalapp.SessionManager(context)
                            val userId = sessionManager.getUserId() ?: "1"

                            val currentBooking = GlobalState.currentBookingInProgress
                            if (currentBooking != null) {
                                com.example.smartplaygroundbookingequipmentrentalapp.utils.BookingRepository.bookGround(
                                    context,
                                    userId,
                                    currentBooking,
                                    { bookingId ->
                                        val finalBooking = currentBooking.copy(bookingId = bookingId, status = "Confirmed", paymentStatus = "Paid")
                                        scope.launch {
                                            processingStatus = "Saving booking to Firebase Database..."
                                            val fbSuccess = com.example.smartplaygroundbookingequipmentrentalapp.backend.FirebaseManager.saveBooking(finalBooking)
                                            if (fbSuccess) {
                                                processingStatus = "Payment Successful!"
                                                delay(1000)
                                                GlobalState.addBooking(finalBooking)
                                                GlobalState.currentBookingInProgress = finalBooking
                                                isProcessing = false
                                                onPaymentSuccess()
                                            } else {
                                                // Local confirmation fallback
                                                processingStatus = "Confirmed! Syncing details..."
                                                delay(1000)
                                                GlobalState.addBooking(finalBooking)
                                                GlobalState.currentBookingInProgress = finalBooking
                                                isProcessing = false
                                                onPaymentSuccess()
                                            }
                                        }
                                    },
                                    { error ->
                                        // Demo mock fallback if API fails
                                        android.util.Log.e("PaymentScreen", "Booking failed, using fallback: $error")
                                        val mockId = "BK" + (100000..999999).random().toString()
                                        val finalBooking = currentBooking.copy(bookingId = mockId, status = "Confirmed", paymentStatus = "Paid")
                                        scope.launch {
                                            processingStatus = "Saving booking to Firebase Database..."
                                            com.example.smartplaygroundbookingequipmentrentalapp.backend.FirebaseManager.saveBooking(finalBooking)
                                            processingStatus = "Payment Successful!"
                                            delay(1000)
                                            GlobalState.addBooking(finalBooking)
                                            GlobalState.currentBookingInProgress = finalBooking
                                            isProcessing = false
                                            onPaymentSuccess()
                                        }
                                    }
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isProcessing
                ) {
                    Text("Pay ₹$amount", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            Text("Select Payment Method", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            val methods = listOf("PhonePe", "Google Pay", "Paytm", "Credit/Debit Card", "Net Banking")
            methods.forEach { method ->
                PaymentMethodItem(
                    title = method,
                    selected = selectedMethod == method,
                    onSelect = { selectedMethod = it }
                )

                if (selectedMethod == method) {
                    Spacer(modifier = Modifier.height(8.dp))
                    when (method) {
                        "PhonePe", "Google Pay", "Paytm" -> {
                            UpiMethodContent(
                                upiId = upiId,
                                onUpiIdChange = { upiId = it },
                                showQrCode = showQrCode,
                                onToggleQrCode = { showQrCode = !showQrCode },
                                amount = amount,
                                context = context
                            )
                        }
                        "Credit/Debit Card" -> {
                            CardMethodContent(
                                cardNumber = cardNumber,
                                onCardNumberChange = { if (it.all { char -> char.isDigit() } && it.length <= 16) cardNumber = it },
                                cardExpiry = cardExpiry,
                                onCardExpiryChange = { cardExpiry = it },
                                cardCvv = cardCvv,
                                onCardCvvChange = { if (it.all { char -> char.isDigit() } && it.length <= 3) cardCvv = it }
                            )
                        }
                        "Net Banking" -> {
                            NetBankingMethodContent(
                                selectedBank = selectedCardBank,
                                onBankSelect = { selectedCardBank = it }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun PaymentMethodItem(title: String, selected: Boolean, onSelect: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(title) },
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else Color.LightGray
        ),
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = { onSelect(title) }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Composable
fun UpiMethodContent(
    upiId: String,
    onUpiIdChange: (String) -> Unit,
    showQrCode: Boolean,
    onToggleQrCode: () -> Unit,
    amount: Int,
    context: Context
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = upiId,
                onValueChange = onUpiIdChange,
                label = { Text("Enter UPI ID (e.g. name@upi)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                trailingIcon = {
                    if (upiId.contains("@")) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "ValidFormat", tint = Color(0xFF43A047))
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Or Pay via QR Code", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                TextButton(onClick = onToggleQrCode) {
                    Text(if (showQrCode) "Hide QR Code" else "Show QR Code", fontWeight = FontWeight.Bold)
                }
            }
            if (showQrCode) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.QrCode,
                            contentDescription = "UPI QR Code",
                            tint = Color.Black,
                            modifier = Modifier.size(140.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Scan this QR using any UPI app to pay ₹$amount", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun CardMethodContent(
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    cardExpiry: String,
    onCardExpiryChange: (String) -> Unit,
    cardCvv: String,
    onCardCvvChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("DEBIT/CREDIT CARD", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        val cardBrand = if (cardNumber.startsWith("4")) "Visa" else if (cardNumber.startsWith("5")) "Mastercard" else "Card"
                        Text(cardBrand, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    Text(
                        text = if (cardNumber.isEmpty()) "•••• •••• •••• ••••" else cardNumber.chunked(4).joinToString(" "),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("VALID THRU", color = Color.White.copy(alpha = 0.5f), fontSize = 8.sp)
                            Text(text = if (cardExpiry.isEmpty()) "MM/YY" else cardExpiry, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("CVV", color = Color.White.copy(alpha = 0.5f), fontSize = 8.sp)
                            Text(text = if (cardCvv.isEmpty()) "•••" else "•".repeat(cardCvv.length), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = cardNumber,
                onValueChange = onCardNumberChange,
                label = { Text("Card Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = cardExpiry,
                    onValueChange = {
                        var expiryInput = it.replace("/", "")
                        if (expiryInput.length > 2) {
                            expiryInput = expiryInput.substring(0, 2) + "/" + expiryInput.substring(2)
                        }
                        if (expiryInput.length <= 5) {
                            onCardExpiryChange(expiryInput)
                        }
                    },
                    label = { Text("Expiry (MM/YY)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = cardCvv,
                    onValueChange = onCardCvvChange,
                    label = { Text("CVV") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    singleLine = true
                )
            }
        }
    }
}

@Composable
fun NetBankingMethodContent(
    selectedBank: String,
    onBankSelect: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Select Bank", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            val banks = listOf("SBI", "HDFC", "ICICI", "Axis", "KOTAK")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                banks.forEach { bank ->
                    val selected = selectedBank == bank
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary else Color.White,
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (selected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onBankSelect(bank) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = bank,
                            color = if (selected) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
