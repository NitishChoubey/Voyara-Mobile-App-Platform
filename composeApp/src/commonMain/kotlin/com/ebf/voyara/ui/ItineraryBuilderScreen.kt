package com.ebf.voyara.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
                .padding(20.dp)
        ) {
            // Top Title Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Build Your Itinerary",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Itinerary list
            val sampleDays = listOf(
                DaySample(
                    title = "Day 1",
                    destination = "Paris, France",
                    activities = listOf("Eiffel Tower visit", "Seine river cruise", "Evening café stroll"),
                    timeAndBudget = "9:00 AM · Budget: €120"
                ),
                DaySample(
                    title = "Day 2",
                    destination = "Louvre & Montmartre",
                    activities = listOf("Louvre museum", "Lunch at Le Marais", "Sacré-Cœur sunset"),
                    timeAndBudget = "10:00 AM · Budget: €90"
                ),
                DaySample(
                    title = "Day 3",
                    destination = "Versailles Day Trip",
                    activities = listOf("Palace tour", "Gardens picnic"),
                    timeAndBudget = "8:30 AM · Budget: €150"
                )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sampleDays) { day ->
                    DayCard(day = day)
                }
            }
        }
    }
}

private data class DaySample(
    val title: String,
    val destination: String,
    val activities: List<String>,
    val timeAndBudget: String
)

@Composable
private fun DayCard(day: DaySample) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.04f)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = day.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = day.timeAndBudget,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = day.destination,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFF97316)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                day.activities.take(3).forEach { activity ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "•", color = Color.White.copy(alpha = 0.9f), modifier = Modifier.padding(end = 8.dp))
                        Text(text = activity, color = Color.White.copy(alpha = 0.9f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer budget/time (redundant small note)
            Text(
                text = "Estimated daily budget shown above",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}



