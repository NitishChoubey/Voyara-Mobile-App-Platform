package com.ebf.voyara.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ItineraryBuilderScreen() {
    // Animations
    val alphaAnim by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Deep slate
                        Color(0xFF1E293B), // Dark slate
                        Color(0xFF1E293B)  // Consistent dark
                    )
                )
            )
    ) {
        // Decorative background elements
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x33F97316), // Orange glow
                        Color.Transparent
                    )
                ),
                radius = 350f,
                center = center.copy(x = size.width * 0.15f, y = size.height * 0.1f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x332563EB), // Blue glow
                        Color.Transparent
                    )
                ),
                radius = 300f,
                center = center.copy(x = size.width * 0.85f, y = size.height * 0.7f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alphaAnim)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon
            Text(
                text = "🗺️",
                fontSize = 80.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Itinerary Builder",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Message
            Text(
                text = "Coming Soon!",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFF97316),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = "The Itinerary Builder feature will be available soon.\nStay tuned for updates!",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Feature preview card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.08f)
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "What to Expect:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    FeatureItem("📅", "Daily activity planning")
                    FeatureItem("📍", "Location-based suggestions")
                    FeatureItem("⏰", "Time optimization")
                    FeatureItem("💰", "Budget tracking")
                    FeatureItem("🎯", "Smart recommendations")
                }
            }
        }
    }
}

@Composable
private fun FeatureItem(icon: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 20.sp,
            modifier = Modifier.padding(end = 12.dp)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

