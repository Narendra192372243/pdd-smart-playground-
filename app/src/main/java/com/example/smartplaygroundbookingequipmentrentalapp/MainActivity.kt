package com.example.smartplaygroundbookingequipmentrentalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartplaygroundbookingequipmentrentalapp.ui.navigation.Screen
import com.example.smartplaygroundbookingequipmentrentalapp.ui.navigation.SetupNavGraph
import com.example.smartplaygroundbookingequipmentrentalapp.ui.theme.SmartPlaygroundBookingEquipmentRentalAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            com.google.firebase.FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Firebase init error: ${e.message}")
        }
        
        val sessionManager = SessionManager(this)
        if (sessionManager.isLoggedIn()) {
            com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState.setUserSession(
                context = this,
                name = sessionManager.getUserName(),
                email = sessionManager.getUserEmail(),
                phone = sessionManager.getUserPhone(),
                location = sessionManager.getUserLocation()
            )
        }
        val startRoute = if (sessionManager.isLoggedIn()) {
            Screen.Home.route
        } else {
            intent?.getStringExtra("start_route") ?: Screen.Onboarding.route
        }
        
        setContent {
            SmartPlaygroundBookingEquipmentRentalAppTheme {
                MainContainer(startRoute)
            }
        }
    }
}

@Composable
fun MainContainer(startRoute: String) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.PlaygroundListing.route,
        Screen.MyBookings.route,
        Screen.EquipmentRental.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController, currentRoute)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            SetupNavGraph(navController = navController, startDestination = startRoute)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 16.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                selected = currentRoute == Screen.Home.route || currentRoute == Screen.PlaygroundListing.route,
                onClick = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } }
            )
            BottomNavItem(
                icon = Icons.Default.CalendarMonth,
                label = "Bookings",
                selected = currentRoute == Screen.MyBookings.route,
                onClick = { navController.navigate(Screen.MyBookings.route) }
            )
            
            // Central Floating Button placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }

            BottomNavItem(
                icon = Icons.Default.ShoppingBag,
                label = "Rentals",
                selected = currentRoute == Screen.EquipmentRental.route,
                onClick = { navController.navigate(Screen.EquipmentRental.route) }
            )
            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Profile",
                selected = currentRoute == Screen.Profile.route,
                onClick = { navController.navigate(Screen.Profile.route) }
            )
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}
