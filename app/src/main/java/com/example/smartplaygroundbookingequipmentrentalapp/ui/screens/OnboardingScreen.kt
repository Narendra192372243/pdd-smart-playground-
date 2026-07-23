package com.example.smartplaygroundbookingequipmentrentalapp.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit = {},
    onSignupClick: () -> Unit = onFinished,
    onLoginClick: () -> Unit = onFinished
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.8f), primaryColor)
                )
            )
    ) {
        // Decorative Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                radius = 400f,
                center = Offset(size.width * 0.8f, 200f),
                style = Stroke(width = 80f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = 600f,
                center = Offset(size.width * 0.2f, size.height * 0.4f),
                style = Stroke(width = 120f)
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Section with Icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.1f),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 12.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SportsSoccer,
                            contentDescription = "Sports",
                            tint = primaryColor,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }
            }

            // Bottom Content Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Book Turf & Gear\nIn Seconds",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 38.sp,
                            color = Color(0xFF1A1A1A)
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Discover top playgrounds and rent high-quality sports gear in just a few taps.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        lineHeight = 24.sp
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Page indicator
                    Row(
                        modifier = Modifier.padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.size(28.dp, 6.dp).background(primaryColor, RoundedCornerShape(10.dp)))
                        Box(modifier = Modifier.size(6.dp).background(Color.LightGray, CircleShape))
                        Box(modifier = Modifier.size(6.dp).background(Color.LightGray, CircleShape))
                    }

                    // CREATE ACCOUNT / SIGNUP BUTTON
                    Button(
                        onClick = onSignupClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("SIGN UP", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = onLoginClick) {
                        Text(
                            "Already have an account? Sign In",
                            color = primaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
