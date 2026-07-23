package com.example.smartplaygroundbookingequipmentrentalapp.ui.screens

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.intl.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.smartplaygroundbookingequipmentrentalapp.model.Equipment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentRentalScreen(
    onBackClick: () -> Unit,
    onEquipmentClick: (Equipment) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val allEquipment = remember {
        listOf(
            Equipment("1", "Cricket Kit", 300, 0, "Sports"),
            Equipment("2", "Football", 150, 0, "Sports"),
            Equipment("3", "Badminton Racket", 120, 0, "Sports"),
            Equipment("4", "Camping Tent", 500, 0, "Others"),
            Equipment("5", "DJ Speaker", 800, 0, "Party"),
            Equipment("6", "Cooler", 400, 0, "Party")
        )
    }

    val filteredEquipment = remember(searchQuery, selectedCategory) {
        allEquipment.filter { item ->
            val matchesSearch = item.name.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == "All" || item.category == selectedCategory
            matchesSearch && matchesCategory
        }
    }

    val standardKeyboardOptions = KeyboardOptions(autoCorrectEnabled = true)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Rent Equipment", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search equipment...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                keyboardOptions = standardKeyboardOptions,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Categories
            val categories = listOf("All", "Sports", "Party", "Fitness", "Others")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        shape = CircleShape
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredEquipment.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Inventory,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Not Available",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "We couldn't find any kit matching '$searchQuery'",
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredEquipment) { equipment ->
                        EquipmentItem(equipment = equipment, onClick = { onEquipmentClick(equipment) })
                    }
                }
            }
        }
    }
}

@Composable
fun EquipmentItem(equipment: Equipment, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
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
                    tint = when (equipment.name) {
                        "Cricket Kit" -> Color(0xFF1E88E5)
                        "Football" -> Color(0xFF43A047)
                        "Badminton Racket" -> Color(0xFFFBC02D)
                        "Camping Tent" -> Color(0xFFFB8C00)
                        "DJ Speaker" -> Color(0xFF8E24AA)
                        "Cooler" -> Color(0xFF00ACC1)
                        else -> Color.Gray
                    },
                    modifier = Modifier.size(48.dp)
                )
                IconButton(
                    onClick = { },
                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(24.dp)
                ) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(equipment.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("₹${equipment.pricePerDay}/day", style = MaterialTheme.typography.titleSmall, color = Color.Black)
                // INNOVATIVE: PREDICTED DEMAND
                Surface(
                    color = if(equipment.name == "Cricket Kit") Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        if(equipment.name == "Cricket Kit") "High Demand" else "Available",
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        fontSize = 8.sp,
                        color = if(equipment.name == "Cricket Kit") Color.Red else Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
