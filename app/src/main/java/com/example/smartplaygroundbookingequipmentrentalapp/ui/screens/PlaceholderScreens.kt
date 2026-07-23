package com.example.smartplaygroundbookingequipmentrentalapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartplaygroundbookingequipmentrentalapp.model.Equipment
import com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState
import com.example.smartplaygroundbookingequipmentrentalapp.model.Playground
import com.example.smartplaygroundbookingequipmentrentalapp.model.Rental

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onBackClick: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val recentSearches = remember { listOf("Cricket Ground Adyar", "Football Turf", "Badminton Court", "Night Slot") }
    
    val allItems = remember {
        listOf(
            Playground("1", "Green Field Arena", "Adyar, Chennai", 4.8, 128, 800, 0, listOf("Cricket", "Football")),
            Playground("2", "PlayMax Ground", "T. Nagar, Chennai", 4.4, 96, 700, 0, listOf("Cricket", "Badminton")),
            Playground("3", "Victory Sports Club", "Velachery, Chennai", 4.5, 110, 900, 0, listOf("Football", "Tennis")),
            Playground("4", "Sun Volleyball Court", "Marina, Chennai", 4.2, 50, 500, 0, listOf("Volleyball")),
            Playground("5", "Royal Kabaddi Academy", "Ambattur, Chennai", 4.6, 75, 600, 0, listOf("Kabaddi"))
        )
    }

    val filteredResults = remember(query, selectedCategory) {
        allItems.filter { pg ->
            val matchesQuery = query.isEmpty() || pg.name.contains(query, ignoreCase = true) || pg.location.contains(query, ignoreCase = true)
            val matchesCat = selectedCategory == "All" || pg.categories.contains(selectedCategory)
            matchesQuery && matchesCat
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Search Grounds", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search location, playground or sport...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Chips
            val categories = listOf("All", "Cricket", "Football", "Badminton", "Tennis", "Volleyball")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val selected = cat == selectedCategory
                    FilterChip(
                        selected = selected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (query.isEmpty() && selectedCategory == "All") {
                Spacer(modifier = Modifier.height(20.dp))
                Text("Recent Searches", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    recentSearches.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.clickable { query = tag }
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(tag, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("SearchResults (${filteredResults.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            if (filteredResults.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No grounds found matching your search", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(filteredResults) { pg ->
                        PlaygroundItem(playground = pg, onClick = { })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var nameOnCard by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add New Card", fontWeight = FontWeight.Bold) },
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
                    onClick = {
                        if (cardNumber.length < 16 || expiry.length < 5 || cvv.length < 3 || nameOnCard.isEmpty()) {
                            Toast.makeText(context, "Please fill in all card details", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Card added successfully!", Toast.LENGTH_SHORT).show()
                            onBackClick()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Card", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Live Card Preview
            Card(
                modifier = Modifier.fillMaxWidth().height(160.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("CREDIT / DEBIT", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        val brand = if (cardNumber.startsWith("4")) "Visa" else if (cardNumber.startsWith("5")) "Mastercard" else "Card"
                        Text(brand, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                    Text(
                        text = if (cardNumber.isEmpty()) "•••• •••• •••• ••••" else cardNumber.chunked(4).joinToString(" "),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("CARDHOLDER", color = Color.White.copy(alpha = 0.5f), fontSize = 8.sp)
                            Text(text = if (nameOnCard.isEmpty()) "YOUR NAME" else nameOnCard.uppercase(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("EXPIRES", color = Color.White.copy(alpha = 0.5f), fontSize = 8.sp)
                            Text(text = if (expiry.isEmpty()) "MM/YY" else expiry, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = nameOnCard,
                onValueChange = { nameOnCard = it },
                label = { Text("Name on Card") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = cardNumber,
                onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 16) cardNumber = it },
                label = { Text("Card Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = expiry,
                    onValueChange = {
                        var input = it.replace("/", "")
                        if (input.length > 2) input = input.substring(0, 2) + "/" + input.substring(2)
                        if (input.length <= 5) expiry = input
                    },
                    label = { Text("Expiry (MM/YY)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = cvv,
                    onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 3) cvv = it },
                    label = { Text("CVV") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentalHistoryScreen(onBackClick: () -> Unit) {
    val rentals = remember {
        listOf(
            Rental("1", Equipment("1", "Cricket Kit", 300, 0, "Sports"), "2026-07-20", "1 Day", 300, "RN849201", "Active", "Paid"),
            Rental("2", Equipment("2", "Football", 150, 0, "Sports"), "2026-07-15", "2 Days", 300, "RN739102", "Completed", "Paid"),
            Rental("3", Equipment("3", "Badminton Racket", 120, 0, "Sports"), "2026-07-10", "1 Day", 120, "RN520193", "Completed", "Paid")
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Rental History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(rentals) { rental ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Rental #${rental.rentalId}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (rental.status == "Active") Color(0xFFE8F5E9) else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = rental.status,
                                    color = if (rental.status == "Active") Color(0xFF2E7D32) else Color.Gray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(rental.equipment.name, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.bodyLarge)
                        Text("Duration: ${rental.duration}  •  Date: ${rental.date}", color = Color.Gray, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Paid", color = Color.Gray, fontSize = 13.sp)
                            Text("₹${rental.amount}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewSubmissionScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var rating by remember { mutableIntStateOf(5) }
    var groundQualityRating by remember { mutableFloatStateOf(4.5f) }
    var cleanlinessRating by remember { mutableFloatStateOf(5.0f) }
    var reviewText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Write a Review", fontWeight = FontWeight.Bold) },
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
                    onClick = {
                        if (reviewText.isEmpty()) {
                            Toast.makeText(context, "Please write your review feedback", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Thank you for your feedback!", Toast.LENGTH_SHORT).show()
                            onBackClick()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Submit Review", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
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
            Spacer(modifier = Modifier.height(8.dp))
            Text("Overall Experience", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            // Star Selector
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "$i Star",
                        tint = if (i <= rating) Color(0xFFFFB300) else Color.Gray,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { rating = i }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Sub-ratings
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Detailed Ratings", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Ground & Turf Quality (${String.format("%.1f", groundQualityRating)})", fontSize = 13.sp)
                    Slider(
                        value = groundQualityRating,
                        onValueChange = { groundQualityRating = it },
                        valueRange = 1f..5f,
                        steps = 7
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Cleanliness & Amenities (${String.format("%.1f", cleanlinessRating)})", fontSize = 13.sp)
                    Slider(
                        value = cleanlinessRating,
                        onValueChange = { cleanlinessRating = it },
                        valueRange = 1f..5f,
                        steps = 7
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                label = { Text("Share your experience...") },
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = RoundedCornerShape(16.dp),
                maxLines = 5
            )
        }
    }
}
