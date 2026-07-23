package com.example.smartplaygroundbookingequipmentrentalapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onEditProfileClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onBillingClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var showLanguageDialog by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English (UK)") }

    if (showLanguageDialog) {
        Dialog(onDismissRequest = { showLanguageDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Select Language", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    val languages = listOf("English (UK)", "Hindi (हिंदी)", "Tamil (தமிழ்)", "Telugu (తెలుగు)", "Kannada (ಕನ್ನಡ)")
                    languages.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLanguage = lang
                                    showLanguageDialog = false
                                    Toast.makeText(context, "Language set to $lang", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedLanguage == lang,
                                onClick = {
                                    selectedLanguage = lang
                                    showLanguageDialog = false
                                    Toast.makeText(context, "Language set to $lang", Toast.LENGTH_SHORT).show()
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(lang, fontWeight = if (selectedLanguage == lang) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { showLanguageDialog = false }, modifier = Modifier.align(Alignment.End)) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SettingsSection(title = "Account & Preferences") {
                SettingsItem(
                    icon = Icons.Default.Person, 
                    title = "Account Profile Info", 
                    subtitle = "Manage name, email & phone",
                    onClick = onEditProfileClick
                )
                SettingsItem(
                    icon = Icons.Default.Translate, 
                    title = "Language Preference", 
                    subtitle = selectedLanguage,
                    onClick = { showLanguageDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.Security, 
                    title = "Security & Privacy", 
                    subtitle = "App permissions & data privacy",
                    onClick = onPrivacyClick
                )
                SettingsItem(
                    icon = Icons.Default.Payments, 
                    title = "Payment Methods & Billing", 
                    subtitle = "Manage saved cards & UPI",
                    onClick = onBillingClick
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(title = "AI Smart Settings") {
                SettingsItem(
                    icon = Icons.Default.AutoAwesome, 
                    title = "AI Smart Suggestions", 
                    subtitle = "Personalized playground recommendations",
                    hasSwitch = true
                )
                SettingsItem(
                    icon = Icons.Default.SmartToy, 
                    title = "Auto-Booking Assistant", 
                    subtitle = "Auto-reserve slot recommendations",
                    hasSwitch = true
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SettingsSection(title = "App Information") {
                SettingsItem(
                    icon = Icons.Default.Info, 
                    title = "About App", 
                    subtitle = "Terms, Privacy & App Version",
                    onClick = onAboutClick
                )
                SettingsItem(
                    icon = Icons.Default.Logout, 
                    title = "Sign Out", 
                    subtitle = "Securely log out of your account",
                    onClick = onLogoutClick
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "App Version 2.1.0 (Smart Enabled)", 
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector, 
    title: String, 
    subtitle: String, 
    hasSwitch: Boolean = false,
    onClick: () -> Unit = {}
) {
    var checked by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                if (hasSwitch) {
                    checked = !checked
                    val status = if (checked) "Enabled" else "Disabled"
                    Toast.makeText(context, "$title $status", Toast.LENGTH_SHORT).show()
                } else {
                    onClick()
                }
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        
        if (hasSwitch) {
            Switch(
                checked = checked,
                onCheckedChange = { 
                    checked = it
                    val status = if (checked) "Enabled" else "Disabled"
                    Toast.makeText(context, "$title $status", Toast.LENGTH_SHORT).show()
                },
                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
            )
        } else {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}
