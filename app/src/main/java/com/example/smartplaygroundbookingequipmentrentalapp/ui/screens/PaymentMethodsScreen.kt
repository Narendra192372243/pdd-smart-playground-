package com.example.smartplaygroundbookingequipmentrentalapp.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(
    onBackClick: () -> Unit,
    onAddCardClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedMethodForDialog by remember { mutableStateOf<SavedMethod?>(null) }

    val methods = remember {
        mutableStateListOf(
            SavedMethod("HDFC Bank Debit Card", "**** 4242", Icons.Default.CreditCard, Color(0xFF1E88E5), true),
            SavedMethod("Google Pay UPI", "narendra@okaxis", Icons.Default.AccountBalance, Color(0xFF43A047), false),
            SavedMethod("PhonePe UPI", "9876543210@ybl", Icons.Default.AccountBalanceWallet, Color(0xFF8E24AA), false)
        )
    }

    if (selectedMethodForDialog != null) {
        val target = selectedMethodForDialog!!
        Dialog(onDismissRequest = { selectedMethodForDialog = null }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).background(target.color.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(target.icon, contentDescription = null, tint = target.color)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(target.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(target.detail, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    TextButton(
                        onClick = {
                            val index = methods.indexOfFirst { it.name == target.name }
                            if (index != -1) {
                                methods.indices.forEach { i -> methods[i] = methods[i].copy(isDefault = i == index) }
                                Toast.makeText(context, "${target.name} set as default payment method", Toast.LENGTH_SHORT).show()
                            }
                            selectedMethodForDialog = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Set as Default Payment Method", fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = {
                            methods.removeIf { it.name == target.name }
                            Toast.makeText(context, "${target.name} removed", Toast.LENGTH_SHORT).show()
                            selectedMethodForDialog = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Remove Payment Method", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { selectedMethodForDialog = null }, modifier = Modifier.align(Alignment.End)) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Payment Methods", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp) {
                Button(
                    onClick = onAddCardClick,
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add New Card / Method", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Your Saved Methods", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Gray)
            }
            items(methods) { method ->
                SavedMethodItem(method = method, onClick = { selectedMethodForDialog = method })
            }
        }
    }
}

@Composable
fun SavedMethodItem(method: SavedMethod, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).background(method.color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(method.icon, contentDescription = null, tint = method.color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(method.name, fontWeight = FontWeight.Bold)
                    if (method.isDefault) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                "Default",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(method.detail, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onClick) {
                Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.Gray)
            }
        }
    }
}

data class SavedMethod(
    val name: String,
    val detail: String,
    val icon: ImageVector,
    val color: Color,
    val isDefault: Boolean = false
)
