package com.example.smartplaygroundbookingequipmentrentalapp.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState
import com.example.smartplaygroundbookingequipmentrentalapp.model.Playground
import com.google.android.gms.location.LocationServices

@Composable
fun HomeScreen(
    onBookPlaygroundClick: () -> Unit,
    onRentEquipmentClick: () -> Unit,
    onMyBookingsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onPopularClick: (Playground) -> Unit,
    onTeamFinderClick: () -> Unit = {},
    onSearchClick: (String) -> Unit = {},
    onNearbyMapClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val fetchRealGpsLocation = {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    GlobalState.userLatitude = loc.latitude
                    GlobalState.userLongitude = loc.longitude
                    GlobalState.isGpsActive = true
                    GlobalState.liveLocation = "GPS (${String.format("%.3f", loc.latitude)}, ${String.format("%.3f", loc.longitude)})"
                    Toast.makeText(context, "GPS Location Updated!", Toast.LENGTH_SHORT).show()
                } else {
                    GlobalState.userLatitude = 13.0827
                    GlobalState.userLongitude = 80.2707
                    GlobalState.liveLocation = "Adyar, Chennai (GPS Fixed)"
                }
            }
        } catch (e: SecurityException) {
            GlobalState.liveLocation = "Adyar, Chennai"
        }
    }

    val locationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            fetchRealGpsLocation()
        } else {
            Toast.makeText(context, "Location permission recommended for nearby grounds", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        val fineCheck = androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
        if (fineCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            fetchRealGpsLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val nearbyPlaygrounds = remember(GlobalState.userLatitude, GlobalState.userLongitude, GlobalState.searchRadiusKm) {
        GlobalState.getNearbyPlaygrounds()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        // Live Location Tracking Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .clickable {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Live Location: ${GlobalState.liveLocation}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .background(Color.Red, RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text("LIVE GPS", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Smart Playground", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text("Find & Book Sports Grounds", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Row {
                IconButton(onClick = onNotificationsClick) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                if (it.isNotEmpty()) {
                    onSearchClick(it)
                }
            },
            placeholder = { Text("Search playgrounds, sports or location...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { onSearchClick(searchQuery) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.LightGray
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Actions Row (Map View & Team Finder)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNearbyMapClick() },
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Map, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Interactive Map", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTeamFinderClick() },
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFE8F5E9)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Group, contentDescription = null, tint = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Team Finder", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Action Cards
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MainActionCard(
                title = "Book\nPlayground",
                subtitle = "Turfs, courts &\narenas nearby",
                color = Color(0xFFE3F2FD),
                icon = Icons.Default.Stadium,
                modifier = Modifier.weight(1f),
                onClick = onBookPlaygroundClick,
                iconColor = MaterialTheme.colorScheme.primary
            )
            MainActionCard(
                title = "Rent\nEquipment",
                subtitle = "Sports gear, party\nitems & more",
                color = Color(0xFFF3E5F5), 
                icon = Icons.Default.SportsCricket,
                modifier = Modifier.weight(1f),
                onClick = onRentEquipmentClick,
                iconColor = Color(0xFF8E24AA)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Nearby Playgrounds Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Nearby Playgrounds", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text("${GlobalState.searchRadiusKm.toInt()} km GPS radius", fontSize = 11.sp, color = Color.Gray)
            }
            TextButton(onClick = onBookPlaygroundClick) {
                Text("View all", color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (nearbyPlaygrounds.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.LocationOff, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No Playgrounds Near Your GPS Location",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "There are currently no registered grounds within ${GlobalState.searchRadiusKm.toInt()} km of your location.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { GlobalState.searchRadiusKm = 2500.0 }) {
                            Text("Show All Cities", fontSize = 12.sp)
                        }
                        Button(onClick = onBookPlaygroundClick) {
                            Text("Browse All", fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(nearbyPlaygrounds) { pg ->
                    PopularCard(pg, onClick = { onPopularClick(pg) })
                }
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp)) 
    }
}

@Composable
fun MainActionCard(
    title: String,
    subtitle: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    iconColor: Color = Color.DarkGray
) {
    Card(
        modifier = modifier
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 22.sp)
            Text(subtitle, fontSize = 11.sp, color = Color.Gray, lineHeight = 15.sp)
        }
    }
}

@Composable
fun PopularCard(pg: Playground, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(240.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(getGroundBg(pg.name)),
                contentAlignment = Alignment.Center
            ) {
                Icon(getGroundIcon(pg.name), contentDescription = null, tint = getGroundColor(pg.name), modifier = Modifier.size(60.dp))
                
                // Distance badge on top-left of image
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.NearMe, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${pg.distanceKm} km", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${pg.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(pg.name, fontWeight = FontWeight.Bold, maxLines = 1, style = MaterialTheme.typography.titleMedium)
                Text(pg.location, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("₹${pg.pricePerHour}/hr", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    Text(pg.occupancyStatus, fontSize = 11.sp, color = if (pg.occupancyStatus == "Free") Color(0xFF43A047) else Color(0xFFE53935))
                }
            }
        }
    }
}
