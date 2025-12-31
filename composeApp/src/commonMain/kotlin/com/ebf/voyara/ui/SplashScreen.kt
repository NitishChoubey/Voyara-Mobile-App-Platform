package com.ebf.voyara.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import globetrotter.composeapp.generated.resources.Res

import globetrotter.composeapp.generated.resources.VoyaraSplashScreen

/**
 * Enhanced Clean & Modern Splash Screen
 */
@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit = {}
) {
    // Navigate after delay - always go to home screen
    LaunchedEffect(Unit) {
        delay(2000L) // 2 seconds

        // Always navigate to home screen (guest or logged in)
        onNavigateToHome()
    }

    // Animation: Scale up and Fade in
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow
        )
    )

    val alphaAnim by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000)
    )

    // Main Layout Container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8FAFC), // Very Light Gray/White
                        Color(0xFFE2E8F0), // Soft Blue-ish Gray
                        Color(0xFFCBD5E1)  // Muted Sky
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alphaAnim)
        ) {
            // Logo Container with a subtle "Card" look for visibility
            Surface(
                modifier = Modifier
                    .size(220.dp)
                    .scale(scale)
                    .padding(12.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.7f), // Semi-transparent glass effect
                shadowElevation = 8.dp
            ) {
                Image(
                    painter = painterResource(Res.drawable.VoyaraSplashScreen),
                    contentDescription = "Globe Trotter Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name with high contrast dark text
            Text(
                text = "Voyara",
                color = Color(0xFF0F172A), // Slate Dark Blue (Highly visible)
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Plan Smarter, Travel Better",
                color = Color(0xFF475569), // Muted Slate
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 1.sp
            )
        }

        // Footer
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Crafted for Travelers",
                    color = Color(0xFF64748B),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}
