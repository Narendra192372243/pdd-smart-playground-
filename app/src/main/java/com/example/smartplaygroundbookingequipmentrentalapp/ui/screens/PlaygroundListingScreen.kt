package com.example.smartplaygroundbookingequipmentrentalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState
import com.example.smartplaygroundbookingequipmentrentalapp.model.Playground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundListingScreen(
    initialQuery: String = "",
    onPlaygroundClick: (Playground) -> Unit,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf(initialQuery) }
    var selectedCategory by remember { mutableStateOf("All") }
    var currentRadius by remember { mutableStateOf(GlobalState.searchRadiusKm) }

    val nearbyGrounds = remember(GlobalState.userLatitude, GlobalState.userLongitude, currentRadius) {
        GlobalState.allPlaygrounds.map { pg ->
            val dist = GlobalState.calculateDistanceKm(GlobalState.userLatitude, GlobalState.userLongitude, pg.latitude, pg.longitude)
            pg.copy(distanceKm = dist)
        }.filter { it.distanceKm <= currentRadius }.sortedBy { it.distanceKm }
    }

    val filteredPlaygrounds = remember(searchQuery, selectedCategory, nearbyGrounds) {
        nearbyGrounds.filter { pg ->
            val matchesQuery = searchQuery.isEmpty() ||
                    pg.name.contains(searchQuery, ignoreCase = true) ||
                    pg.location.contains(searchQuery, ignoreCase = true) ||
                    pg.categories.any { cat -> cat.contains(searchQuery, ignoreCase = true) }
            val matchesCategory = selectedCategory == "All" || pg.categories.any { it.equals(selectedCategory, ignoreCase = true) }
            matchesQuery && matchesCategory
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Playgrounds Near You", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search location, playground or sport...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category & Radius Filter Row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val categories = listOf("All", "Cricket", "Football", "Badminton", "Tennis", "Volleyball", "Kabaddi")
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) },
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${filteredPlaygrounds.size} grounds within ${currentRadius.toInt()} km",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                TextButton(onClick = { currentRadius = if (currentRadius < 100.0) 2500.0 else 20.0 }) {
                    Text(if (currentRadius > 100.0) "Filter Nearby" else "Show All Cities", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredPlaygrounds.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.LocationOff, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No Playgrounds Near This Location",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "There are currently no registered playgrounds within ${currentRadius.toInt()} km of your GPS location.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { currentRadius = 2500.0 },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("View All Grounds Across India")
                        }
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(filteredPlaygrounds) { playground ->
                        PlaygroundItem(playground = playground, onClick = { onPlaygroundClick(playground) })
                    }
                }
            }
        }
    }
}

@Composable
fun PlaygroundItem(playground: Playground, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(12.dp)).background(getGroundBg(playground.name)),
                contentAlignment = Alignment.Center
            ) {
                Icon(getGroundIcon(playground.name), contentDescription = null, tint = getGroundColor(playground.name), modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(playground.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(playground.location, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                    Text("${playground.rating}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("₹${playground.pricePerHour}/hr", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.NearMe, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${playground.distanceKm} km away",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
