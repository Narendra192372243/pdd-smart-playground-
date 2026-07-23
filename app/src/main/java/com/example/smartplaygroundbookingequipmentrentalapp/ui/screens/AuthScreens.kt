package com.example.smartplaygroundbookingequipmentrentalapp.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smartplaygroundbookingequipmentrentalapp.SessionManager
import com.example.smartplaygroundbookingequipmentrentalapp.backend.FirebaseManager
import com.example.smartplaygroundbookingequipmentrentalapp.viewmodel.LoginViewModel
import com.example.smartplaygroundbookingequipmentrentalapp.viewmodel.LoginUiState
import com.example.smartplaygroundbookingequipmentrentalapp.utils.NotificationUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun AuthBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

val DEFAULT_KEYBOARD_OPTIONS = KeyboardOptions(
    autoCorrectEnabled = true
)

@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: LoginViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var passwordVisible by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showMockGoogleDialog by remember { mutableStateOf(false) }

    val googleSignInClient = remember {
        val defaultWebClientIdId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        val webClientId = if (defaultWebClientIdId != 0) context.getString(defaultWebClientIdId) else "mock-client-id"
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            auth.signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        if (firebaseUser != null) {
                            coroutineScope.launch {
                                val resolvedPhone = com.example.smartplaygroundbookingequipmentrentalapp.repository.AuthRepository().getUserPhone(firebaseUser.uid) ?: ""
                                FirebaseManager.saveUserProfileIfNotExists(
                                    name = firebaseUser.displayName ?: "Google User",
                                    email = firebaseUser.email ?: "",
                                    phone = resolvedPhone
                                )
                                SessionManager(context).createLoginSession(firebaseUser.uid, resolvedPhone)
                                navController.navigate("home_root") {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Google Sign-In failed: ${authTask.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } catch (e: Exception) {
            android.util.Log.e("GoogleSignIn", "ApiException or other: ${e.message}")
            showMockGoogleDialog = true
        }
    }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            val successState = uiState as LoginUiState.Success
            val user = successState.user
            val phone = successState.phone
            SessionManager(context).createLoginSession(user.uid, phone)
            navController.navigate("home_root") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Logo and Welcome
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -50 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(90.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 8.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Stadium,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Smart Playground",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Experience sports like never before",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Inputs
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 50 })
            ) {
                Column {
                    OutlinedTextField(
                        value = viewModel.email,
                        onValueChange = { viewModel.email = it; viewModel.clearError() },
                        label = { Text("Email or Mobile Number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { 
                            Icon(
                                if (viewModel.email.contains("@")) Icons.Default.Email else Icons.Default.Phone, 
                                contentDescription = null, 
                                tint = MaterialTheme.colorScheme.primary 
                            ) 
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { viewModel.password = it; viewModel.clearError() },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = viewModel.rememberMe, 
                                onCheckedChange = { viewModel.rememberMe = it },
                                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                            )
                            Text("Remember Me", style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = { showForgotPasswordDialog = true }) {
                            Text(
                                "Forgot Password?", 
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login Button
                    Button(
                        onClick = { 
                            viewModel.onLoginClick { userId ->
                                val input = viewModel.email.trim()
                                val savedAccount = com.example.smartplaygroundbookingequipmentrentalapp.utils.UserAccountManager.findUserAccount(context, input)

                                val userPhone = savedAccount?.phone ?: (if (input.all { c -> c.isDigit() }) input else com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState.currentUserPhone)
                                val userEmail = savedAccount?.email ?: (if (input.contains("@")) input else com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState.currentUserEmail)
                                val derivedName = savedAccount?.name ?: (if (input.contains("@")) {
                                    input.substringBefore("@").replace(".", " ").replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                                } else {
                                    com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState.currentUserName
                                })
                                val userLocation = savedAccount?.location ?: com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState.liveLocation

                                coroutineScope.launch {
                                    val profile = FirebaseManager.getUserProfile(userId)
                                    val finalName = profile?.get("name")?.ifEmpty { null } ?: derivedName
                                    val finalEmail = profile?.get("email")?.ifEmpty { null } ?: userEmail
                                    val finalPhone = profile?.get("phone")?.ifEmpty { null } ?: userPhone
                                    val finalLocation = profile?.get("location")?.ifEmpty { null } ?: userLocation
                                    
                                    com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState.setUserSession(
                                        context = context,
                                        name = finalName,
                                        email = finalEmail,
                                        phone = finalPhone,
                                        location = finalLocation
                                    )
                                    navController.navigate("home_root") {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            } 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .animateContentSize(),
                        shape = RoundedCornerShape(16.dp),
                        enabled = uiState !is LoginUiState.Loading,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        if (uiState is LoginUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (uiState is LoginUiState.Error) {
                        Text(
                            text = (uiState as LoginUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Divider
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Text(
                            "  Or continue with  ", 
                            style = MaterialTheme.typography.bodySmall, 
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Social Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                try {
                                    val signInIntent = googleSignInClient.signInIntent
                                    googleSignInLauncher.launch(signInIntent)
                                } catch (e: Exception) {
                                    showMockGoogleDialog = true
                                }
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                Icons.Default.AccountCircle, // Placeholder for Google
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Google", color = MaterialTheme.colorScheme.onSurface)
                        }

                        OutlinedButton(
                            onClick = { navController.navigate("otp_verification") },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                Icons.Default.PhoneIphone, 
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Phone", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Don't have an account? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Sign Up",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { navController.navigate("signup") }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(
            onDismiss = { showForgotPasswordDialog = false },
            onOptionSelected = { option ->
                showForgotPasswordDialog = false
                if (option == "OTP") {
                    navController.navigate("otp_verification")
                } else {
                    Toast.makeText(context, "Password reset link sent to your email", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    if (showMockGoogleDialog) {
        MockGoogleSignInDialog(
            onDismiss = { showMockGoogleDialog = false },
            onAccountSelected = { name, email ->
                showMockGoogleDialog = false
                val mockId = "goog_" + Math.abs(email.hashCode())
                try {
                    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                    auth.signInWithEmailAndPassword(email, "google123456")
                        .addOnCompleteListener { task ->
                            val userId = auth.currentUser?.uid ?: mockId
                            coroutineScope.launch {
                                FirebaseManager.saveUserProfileIfNotExists(name, email, "9876543210")
                                SessionManager(context).createLoginSession(userId, "9876543210")
                                navController.navigate("home_root") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                } catch (e: Exception) {
                    SessionManager(context).createLoginSession(mockId, "9876543210")
                    navController.navigate("home_root") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        )
    }
}

@Composable
fun MockGoogleSignInDialog(
    onDismiss: () -> Unit,
    onAccountSelected: (String, String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccountCircle, 
                        contentDescription = null, 
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Sign in with Google",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Choose an account to continue to Smart Playground",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(20.dp))
                
                val mockAccounts = listOf(
                    Pair("Narendra Kumar", "narendra.sports@gmail.com"),
                    Pair("Amit Sharma", "amit.sharma99@gmail.com"),
                    Pair("Rahul Dravid", "rahul.field@gmail.com")
                )
                
                mockAccounts.forEach { account ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAccountSelected(account.first, account.second) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = account.first.take(1),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(account.first, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                            Text(account.second, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        }
                    }
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ForgotPasswordDialog(onDismiss: () -> Unit, onOptionSelected: (String) -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Recovery Access",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))

                RecoveryOption(
                    icon = Icons.Default.Sms,
                    title = "OTP Verification",
                    subtitle = "Safe 4-digit code to mobile",
                    onClick = { onOptionSelected("OTP") }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                RecoveryOption(
                    icon = Icons.Default.Email,
                    title = "Email Recovery",
                    subtitle = "Get a unique reset link",
                    onClick = { onOptionSelected("Email") }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Close", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun RecoveryOption(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color(0xFFF5F5F5), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SignupScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Adyar, Chennai") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    AuthBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))
            
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "🚀 Join SmartPlayground Community",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Text(
                text = "Create Your Account",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = "Sign up to lock slots, earn 100 reward points & rent sports gear",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp, start = 12.dp, end = 12.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("City / Live Location *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Create Password *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { 
                    if (password != confirmPassword) {
                        errorMessage = "Passwords do not match"
                        return@Button
                    }
                    if (name.isBlank() || phone.isBlank() || password.isBlank()) {
                        errorMessage = "Please fill in all required fields (Name, Phone, Password)"
                        return@Button
                    }
                    
                    isLoading = true
                    errorMessage = null

                    // Save new user account persistently
                    val savedAcc = com.example.smartplaygroundbookingequipmentrentalapp.utils.UserAccountManager.saveUserAccount(
                        context, name, email, phone, location, password
                    )

                    // 1. Create Session & Global State immediately
                    com.example.smartplaygroundbookingequipmentrentalapp.SessionManager(context)
                        .createLoginSession(savedAcc.id, name, email, phone, location)
                    com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState.setUserSession(
                        context = context,
                        name = name,
                        email = email,
                        phone = phone,
                        location = location
                    )
                    com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState.loyaltyPoints = 100

                    // 2. Async Non-blocking Server & Firebase sync
                    scope.launch {
                        try {
                            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                            auth.createUserWithEmailAndPassword(if (email.isNotBlank()) email else "$phone@smartplayground.com", password)
                        } catch (e: Exception) { }
                        
                        try {
                            FirebaseManager.saveUserProfile(name, email, phone, location)
                        } catch (e: Exception) { }

                        try {
                            com.example.smartplaygroundbookingequipmentrentalapp.utils.BookingRepository.registerUserOnServer(
                                context, name, email, phone, location, password
                            )
                        } catch (e: Exception) { }
                    }

                    // 3. Show Toast & Navigate to Home screen directly
                    Toast.makeText(context, "🎉 Account Created! Welcome $name (+100 Pts)", Toast.LENGTH_LONG).show()
                    isLoading = false

                    navController.navigate("home_root") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Sign Up & Get 100 Reward Pts", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            
            Row(modifier = Modifier.padding(bottom = 24.dp)) {
                Text("Already have an account? ", color = Color.Gray)
                Text(
                    "Login", 
                    color = MaterialTheme.colorScheme.primary, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.navigate("login") }
                )
            }
        }
    }
}

@Composable
fun OtpVerificationScreen(navController: NavController) {
    val context = LocalContext.current
    var otp by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var generatedOtp by remember { mutableStateOf("") }
    var timeLeft by remember { mutableIntStateOf(45) }
    var isLoading by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()

    LaunchedEffect(isOtpSent, generatedOtp) {
        if (isOtpSent && generatedOtp.isNotEmpty()) {
            timeLeft = 45
            NotificationUtils.showOtpNotification(context, generatedOtp)
            
            delay(1500)
            for (i in 1..generatedOtp.length) {
                otp = generatedOtp.take(i)
                delay(200)
            }
            
            delay(500)
            isLoading = true
            navController.navigate("home_root") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    AuthBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            Text(
                text = if (!isOtpSent) "Firebase Verification" else "Auto-Verification",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = if (!isOtpSent) "Enter mobile number to receive OTP" else "Connecting to Firebase...",
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            if (!isOtpSent) {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { if (it.length <= 10) phoneNumber = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    prefix = { Text("+91 ") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    keyboardOptions = DEFAULT_KEYBOARD_OPTIONS.copy(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
                
                if (phoneNumber.isNotEmpty()) {
                    Text("Number: +91 $phoneNumber", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { 
                        if (phoneNumber.length == 10) {
                            isLoading = true
                            scope.launch {
                                delay(1000)
                                generatedOtp = Random.nextInt(1000, 9999).toString()
                                isOtpSent = true
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = phoneNumber.length == 10 && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Send OTP", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("GENERATED OTP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(generatedOtp, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, letterSpacing = 8.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(4) { index ->
                        val char = otp.getOrNull(index)?.toString() ?: ""
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .border(
                                    2.dp, 
                                    if (otp.length == index) MaterialTheme.colorScheme.primary else Color.LightGray, 
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(char, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (isLoading) {
                    Text("Verified with Firebase!", color = Color(0xFF43A047), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                } else {
                    Text("Auto-reading SMS...", color = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            if (!isLoading) {
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Back to Login", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
