package com.example.smartplaygroundbookingequipmentrentalapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onEditProfileClick: () -> Unit = {},
    onBookingHistoryClick: () -> Unit = {},
    onPaymentMethodsClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onAdminClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var showRedeemDialog by remember { mutableStateOf(false) }
    var showPhoneDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var editPhoneInput by remember { mutableStateOf(GlobalState.currentUserPhone) }
    var editLocationInput by remember { mutableStateOf(GlobalState.liveLocation) }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) { }
            GlobalState.updateProfileImage(context, uri.toString())
            Toast.makeText(context, "📸 Profile Picture Updated!", Toast.LENGTH_SHORT).show()
        }
    }

    if (showLocationDialog) {
        Dialog(onDismissRequest = { showLocationDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Update Live Location", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Enter your location / city to discover nearby grounds & live server updates:", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = editLocationInput,
                        onValueChange = { editLocationInput = it },
                        label = { Text("Location / City") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Quick Select City:", fontSize = 11.sp, color = Color.Gray)
                    val locPresets = listOf("Adyar, Chennai", "T. Nagar, Chennai", "Koramangala, Bangalore", "Gachibowli, Hyderabad")
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                        locPresets.take(2).forEach { pre ->
                            FilterChip(
                                selected = editLocationInput == pre,
                                onClick = { editLocationInput = pre },
                                label = { Text(pre, fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if (editLocationInput.isNotBlank()) {
                                GlobalState.updateLocation(context, editLocationInput)
                                Toast.makeText(context, "Location Saved: $editLocationInput", Toast.LENGTH_SHORT).show()
                                showLocationDialog = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Location", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { showLocationDialog = false }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            }
        }
    }

    if (showPhoneDialog) {
        Dialog(onDismissRequest = { showPhoneDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Update Phone Number", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Enter or select a phone number to save directly across profile & admin settings:", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = editPhoneInput,
                        onValueChange = { editPhoneInput = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Quick Select Sample Phone:", fontSize = 11.sp, color = Color.Gray)
                    val presets = listOf("+91 9876543210", "+91 9123456789", "+91 9988776655")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                        presets.forEach { pre ->
                            FilterChip(
                                selected = editPhoneInput == pre,
                                onClick = { editPhoneInput = pre },
                                label = { Text(pre, fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if (editPhoneInput.isNotBlank()) {
                                GlobalState.updatePhone(context, editPhoneInput)
                                Toast.makeText(context, "Phone Number Saved: $editPhoneInput", Toast.LENGTH_SHORT).show()
                                showPhoneDialog = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Phone Number", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { showPhoneDialog = false }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            }
        }
    }

    if (showRedeemDialog) {
        Dialog(onDismissRequest = { showRedeemDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(60.dp).background(Color(0xFFFFD700), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Redeem Reward Points", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("You have ${GlobalState.loyaltyPoints} points available. Redeem 500 points to get a ₹100 discount voucher on your next playground booking!", textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = Color.Gray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            if (GlobalState.loyaltyPoints >= 500) {
                                GlobalState.loyaltyPoints -= 500
                                Toast.makeText(context, "Redeemed ₹100 Discount Voucher!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Voucher Applied! Keep playing to earn more points.", Toast.LENGTH_SHORT).show()
                            }
                            showRedeemDialog = false
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Claim ₹100 Off Voucher", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { showRedeemDialog = false }) {
                        Text("Close", color = Color.Gray)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditProfileClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .clickable { photoLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (GlobalState.profileImageUri != null) {
                    AsyncImage(
                        model = GlobalState.profileImageUri,
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary)
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(30.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Change Photo", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(GlobalState.currentUserName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(GlobalState.currentUserEmail, color = Color.Gray)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Clickable Phone Number Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.clickable { 
                        editPhoneInput = GlobalState.currentUserPhone
                        showPhoneDialog = true 
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = GlobalState.currentUserPhone,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(10.dp))
                    }
                }

                // Clickable Location Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.clickable { 
                        editLocationInput = GlobalState.liveLocation
                        showLocationDialog = true 
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = GlobalState.liveLocation,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(10.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // REWARD POINTS CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(44.dp).background(Color(0xFFFFD700), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Loyalty Reward Points", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Text("${GlobalState.loyaltyPoints} Points", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = { showRedeemDialog = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("REDEEM", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProfileMenuItem(icon = Icons.Default.History, label = "Booking History", onClick = onBookingHistoryClick)
            ProfileMenuItem(icon = Icons.Default.Payment, label = "Payment Methods", onClick = onPaymentMethodsClick)
            ProfileMenuItem(icon = Icons.Default.Favorite, label = "My Favorites", onClick = onFavoritesClick)
            ProfileMenuItem(icon = Icons.Default.Notifications, label = "Notifications", onClick = onNotificationsClick)
            ProfileMenuItem(icon = Icons.Default.Settings, label = "Settings", onClick = onSettingsClick)
            ProfileMenuItem(icon = Icons.Default.AdminPanelSettings, label = "Admin Dashboard", onClick = onAdminClick)
            ProfileMenuItem(icon = Icons.Default.Help, label = "Help & Support", onClick = onHelpClick)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLogoutClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Red)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout Account", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}
