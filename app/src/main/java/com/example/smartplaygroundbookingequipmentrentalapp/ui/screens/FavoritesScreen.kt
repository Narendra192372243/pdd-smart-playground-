package com.example.smartplaygroundbookingequipmentrentalapp.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartplaygroundbookingequipmentrentalapp.model.Playground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(onBackClick: () -> Unit, onPlaygroundClick: (Playground) -> Unit = {}) {
    val favoritePlaygrounds = listOf(
        Playground("1", "Green Field Arena", "Adyar, Chennai", 4.8, 128, 800, 0),
        Playground("5", "Royal Kabaddi Academy", "Ambattur, Chennai", 4.6, 75, 600, 0)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Favorites", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (favoritePlaygrounds.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No favorites yet", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favoritePlaygrounds) { playground ->
                    PlaygroundItem(playground = playground, onClick = { onPlaygroundClick(playground) })
                }
            }
        }
    }
}
