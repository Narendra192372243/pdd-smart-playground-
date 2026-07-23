package com.example.smartplaygroundbookingequipmentrentalapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.smartplaygroundbookingequipmentrentalapp.ui.screens.*
import com.example.smartplaygroundbookingequipmentrentalapp.model.Playground
import com.example.smartplaygroundbookingequipmentrentalapp.model.Booking
import com.example.smartplaygroundbookingequipmentrentalapp.model.Equipment
import com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Home : Screen("home_root")
    object PlaygroundListing : Screen("playground_listing")
    object BookingDetails : Screen("booking_details")
    object BookingSummary : Screen("booking_summary")
    object Confirmation : Screen("confirmation")
    object Payment : Screen("payment")
    object EquipmentRental : Screen("equipment_rental")
    object EquipmentDetails : Screen("equipment_details")
    object Profile : Screen("profile")
    object MyBookings : Screen("my_bookings")
    object Favorites : Screen("favorites")
    object Notifications : Screen("notifications")
    object Search : Screen("search")
    object EditProfile : Screen("edit_profile")
    object Settings : Screen("settings")
    object PaymentMethods : Screen("payment_methods")
    object AddCard : Screen("add_card")
    object HelpCenter : Screen("help_center")
    object AboutUs : Screen("about_us")
    object Terms : Screen("terms")
    object Privacy : Screen("privacy")
    object RentalHistory : Screen("rental_history")
    object ReviewSubmission : Screen("review_submission")
    object OtpVerification : Screen("otp_verification")
    object AdminDashboard : Screen("admin_dashboard")
    object TeamFinder : Screen("team_finder")
}

@Composable
fun SetupNavGraph(navController: NavHostController, startDestination: String = Screen.Onboarding.route) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val mapLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == android.app.Activity.RESULT_OK) {
                    val action = result.data?.getStringExtra("action")
                    if (action == "open_booking") {
                        navController.navigate(Screen.BookingDetails.route)
                    }
                }
            }
            
            HomeScreen(
                onBookPlaygroundClick = { navController.navigate(Screen.PlaygroundListing.route) },
                onRentEquipmentClick = { navController.navigate(Screen.EquipmentRental.route) },
                onMyBookingsClick = { navController.navigate(Screen.MyBookings.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onNotificationsClick = { navController.navigate(Screen.Notifications.route) },
                onPopularClick = { pg ->
                    GlobalState.selectedPlayground = pg
                    navController.navigate(Screen.BookingDetails.route)
                },
                onTeamFinderClick = { navController.navigate(Screen.TeamFinder.route) },
                onSearchClick = { query -> 
                    navController.navigate("${Screen.PlaygroundListing.route}?query=$query") 
                },
                onNearbyMapClick = {
                    val intent = android.content.Intent(context, com.example.smartplaygroundbookingequipmentrentalapp.NearbyMapActivity::class.java)
                    mapLauncher.launch(intent)
                }
            )
        }
        composable(
            route = "${Screen.PlaygroundListing.route}?query={query}",
            arguments = listOf(navArgument("query") { 
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            PlaygroundListingScreen(
                initialQuery = query,
                onPlaygroundClick = { playground -> 
                    GlobalState.selectedPlayground = playground
                    navController.navigate(Screen.BookingDetails.route)
                },
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }
        
        composable(Screen.PlaygroundListing.route) {
            PlaygroundListingScreen(
                onPlaygroundClick = { playground -> 
                    GlobalState.selectedPlayground = playground
                    navController.navigate(Screen.BookingDetails.route)
                },
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.BookingDetails.route) {
            val pg = GlobalState.selectedPlayground ?: Playground("1", "Green Field Arena", "Adyar, Chennai", 4.8, 128, 800, 0)
            BookingDetailsScreen(
                playground = pg,
                onBackClick = { navController.popBackStack() },
                onContinueClick = { 
                    navController.navigate(Screen.BookingSummary.route)
                }
            )
        }
        composable(Screen.BookingSummary.route) {
            val booking = GlobalState.currentBookingInProgress ?: Booking(
                "1", Playground("1", "Green Field Arena", "Adyar, Chennai", 4.8, 128, 800, 0),
                "Tue, 21 May 2024", "8:00 AM - 10:00 AM", 840, "GF123456"
            )
            BookingSummaryScreen(
                booking = booking,
                onBackClick = { navController.popBackStack() },
                onConfirmClick = { 
                    navController.navigate(Screen.Payment.route)
                }
            )
        }
        composable(Screen.Payment.route) {
            PaymentScreen(
                amount = GlobalState.currentBookingInProgress?.amount ?: 840,
                onBackClick = { navController.popBackStack() },
                onPaymentSuccess = { 
                    navController.navigate(Screen.Confirmation.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }
        composable(Screen.Confirmation.route) {
            BookingConfirmationScreen(
                booking = GlobalState.currentBookingInProgress,
                rental = GlobalState.currentRentalInProgress,
                onViewBookingClick = { 
                    navController.navigate(Screen.MyBookings.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onBackToHomeClick = { 
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.EquipmentRental.route) {
            EquipmentRentalScreen(
                onBackClick = { navController.popBackStack() },
                onEquipmentClick = { equipment -> 
                    GlobalState.currentRentalInProgress = null // Reset
                    navController.navigate(Screen.EquipmentDetails.route) 
                }
            )
        }
        composable(Screen.EquipmentDetails.route) {
            val eq = GlobalState.currentRentalInProgress?.equipment ?: Equipment("1", "Cricket Kit", 300, 0, "Sports")
            EquipmentDetailsScreen(
                equipment = eq,
                onBackClick = { navController.popBackStack() },
                onRentSuccess = { 
                    navController.navigate(Screen.Confirmation.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }
        composable(Screen.Profile.route) { 
            val context = androidx.compose.ui.platform.LocalContext.current
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onEditProfileClick = { navController.navigate(Screen.EditProfile.route) },
                onBookingHistoryClick = { navController.navigate(Screen.MyBookings.route) },
                onPaymentMethodsClick = { navController.navigate(Screen.PaymentMethods.route) },
                onFavoritesClick = { navController.navigate(Screen.Favorites.route) },
                onNotificationsClick = { navController.navigate(Screen.Notifications.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onAdminClick = { 
                    val intent = android.content.Intent(context, com.example.smartplaygroundbookingequipmentrentalapp.AdminDashboardActivity::class.java)
                    context.startActivity(intent)
                },
                onHelpClick = { navController.navigate(Screen.HelpCenter.route) },
                onLogoutClick = {
                    com.example.smartplaygroundbookingequipmentrentalapp.SessionManager(context).logoutUser()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            ) 
        }
        composable(Screen.MyBookings.route) { MyBookingsScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Favorites.route) { 
            FavoritesScreen(
                onBackClick = { navController.popBackStack() },
                onPlaygroundClick = { pg ->
                    GlobalState.selectedPlayground = pg
                    navController.navigate(Screen.BookingDetails.route)
                }
            ) 
        }
        composable(Screen.Notifications.route) { NotificationsScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.PaymentMethods.route) { 
            PaymentMethodsScreen(
                onBackClick = { navController.popBackStack() },
                onAddCardClick = { navController.navigate(Screen.AddCard.route) }
            ) 
        }
        composable(Screen.HelpCenter.route) { HelpSupportScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Search.route) { SearchScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.EditProfile.route) { EditProfileScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Settings.route) { 
            val context = androidx.compose.ui.platform.LocalContext.current
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onEditProfileClick = { navController.navigate(Screen.EditProfile.route) },
                onPrivacyClick = { navController.navigate(Screen.Privacy.route) },
                onBillingClick = { navController.navigate(Screen.PaymentMethods.route) },
                onAboutClick = { navController.navigate(Screen.AboutUs.route) },
                onLogoutClick = {
                    com.example.smartplaygroundbookingequipmentrentalapp.SessionManager(context).logoutUser()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            ) 
        }
        composable(Screen.AddCard.route) { AddCardScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.AboutUs.route) { StaticContentScreen("About Us", "We are a smart playground platform.", onBackClick = { navController.popBackStack() }) }
        composable(Screen.Terms.route) { StaticContentScreen("Terms & Conditions", "By using this app...", onBackClick = { navController.popBackStack() }) }
        composable(Screen.Privacy.route) { StaticContentScreen("Privacy Policy", "We value your privacy...", onBackClick = { navController.popBackStack() }) }
        composable(Screen.RentalHistory.route) { RentalHistoryScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.ReviewSubmission.route) { ReviewSubmissionScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Onboarding.route) { 
            OnboardingScreen(
                onSignupClick = { navController.navigate(Screen.Signup.route) },
                onLoginClick = { navController.navigate(Screen.Login.route) }
            ) 
        }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Signup.route) { SignupScreen(navController) }
        composable(Screen.OtpVerification.route) { OtpVerificationScreen(navController) }
        composable(Screen.AdminDashboard.route) { AdminDashboardScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.TeamFinder.route) { TeamFinderScreen(onBackClick = { navController.popBackStack() }) }
    }
}

private fun UUID(): String = java.util.UUID.randomUUID().toString()
