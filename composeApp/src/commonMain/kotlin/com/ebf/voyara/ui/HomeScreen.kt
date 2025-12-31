package com.ebf.voyara.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ebf.voyara.data.Trip

// Sample data classes for UI
data class Destination(
    val name: String,
    val country: String,
    val emoji: String,
    val description: String
)

data class QuickStat(
    val label: String,
    val value: String,
    val icon: String,
    val color: Color
)

/**
 * Premium Home Screen with Modern Design
 */
@Composable
fun HomeScreen(
    tokenManager: com.ebf.voyara.utils.TokenManager? = null,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToCreateTrip: () -> Unit = {},
    onNavigateToAskVegaAI: () -> Unit = {},
    onNavigateToViewDrafts: () -> Unit = {},
    showLoginPrompt: Boolean = false,
    onDismissLoginPrompt: () -> Unit = {}
) {
    // Check if user is logged in
    val isLoggedIn = remember(tokenManager) {
        tokenManager?.hasValidToken() ?: false
    }

    // State for showing login prompt from Plan New Trip button
    var showLocalLoginPrompt by remember { mutableStateOf(false) }

    // Get user's name from TokenManager (saved during login)
    val userName = remember(isLoggedIn, tokenManager) {
        if (isLoggedIn) {
            tokenManager?.getUserFullName()?.split(" ")?.firstOrNull() ?: "User"
        } else {
            "Guest"
        }
    }

    // Sample data - Replace with real data from ViewModel
    val upcomingTrips = remember {
        listOf(
            Trip(
                id = "1",
                name = "Tokyo Adventure",
                startDate = "2024-03-15",
                endDate = "2024-03-22",
                description = "Exploring the vibrant streets of Tokyo"
            ),
            Trip(
                id = "2",
                name = "Paris Getaway",
                startDate = "2024-04-10",
                endDate = "2024-04-17",
                description = "Romantic week in the City of Light"
            )
        )
    }

    val recommendedDestinations = remember {
        listOf(
            Destination("Bali", "Indonesia", "🏝️", "Tropical paradise with stunning beaches"),
            Destination("New York", "USA", "🗽", "The city that never sleeps"),
            Destination("Rome", "Italy", "🏛️", "Ancient history and amazing cuisine"),
            Destination("Dubai", "UAE", "🏙️", "Modern luxury and desert adventures")
        )
    }

    val quickStats = remember {
        listOf(
            QuickStat("Total Trips", "12", "✈️", Color(0xFFF97316)),
            QuickStat("Countries", "8", "🌍", Color(0xFF3B82F6)),
            QuickStat("Budget Used", "$4.2K", "💰", Color(0xFF10B981)),
            QuickStat("Upcoming", "2", "📅", Color(0xFFEC4899))
        )
    }

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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alphaAnim)
                .padding(top = 16.dp, bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Welcome Header (without profile icon)
            item {
                WelcomeHeaderSimple(
                    userName = userName,
                    isLoggedIn = isLoggedIn
                )
            }

            // Sign-in prompt for guests
            if (!isLoggedIn) {
                item {
                    SignInPromptText(onNavigateToLogin = onNavigateToLogin)
                }
            }

            // Plan New Trip Button (Only this button, removed View My Trips)
            item {
                QuickActionsSection(
                    isLoggedIn = isLoggedIn,
                    onPlanNewTrip = {
                        if (isLoggedIn) {
                            onNavigateToCreateTrip()
                        } else {
                            showLocalLoginPrompt = true
                        }
                    },
                    onViewDrafts = onNavigateToViewDrafts
                )
            }

            // Upcoming Trips (Always show)
            item {
                UpcomingTripsSection(
                    trips = upcomingTrips,
                    onTripClick = { }
                )
            }

            // Recommended Destinations
            item {
                RecommendedDestinationsSection(destinations = recommendedDestinations)
            }

            // Bottom spacing (extra padding for bottom nav bar - 70dp nav + 16dp extra)
            item {
                Spacer(modifier = Modifier.height(86.dp))
            }
        }

        // Login Prompt Dialog (from bottom nav)
        if (showLoginPrompt) {
            LoginPromptDialog(
                onDismiss = onDismissLoginPrompt,
                onLoginClick = {
                    onDismissLoginPrompt()
                    onNavigateToLogin()
                }
            )
        }

        // Login Prompt Dialog (from Plan New Trip button)
        if (showLocalLoginPrompt) {
            LoginPromptDialog(
                onDismiss = { showLocalLoginPrompt = false },
                onLoginClick = {
                    showLocalLoginPrompt = false
                    onNavigateToLogin()
                }
            )
        }

        // Floating "Ask Vega AI" Button
        FloatingActionButton(
            onClick = onNavigateToAskVegaAI,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 90.dp),
            containerColor = Color(0xFF6366F1),
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⭐",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ask Vega AI",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun WelcomeHeaderSimple(userName: String, isLoggedIn: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = if (isLoggedIn) "Welcome back," else "Welcome,",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.6f),
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = userName,
                fontSize = 32.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = " 👋",
                fontSize = 32.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isLoggedIn) "Ready for your next adventure?" else "Explore destinations and plan your trips!",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun SignInPromptText(onNavigateToLogin: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable(onClick = onNavigateToLogin)
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🔐",
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Click here to Sign in or Sign up to access more features",
            fontSize = 14.sp,
            color = Color(0xFFF97316),
            fontWeight = FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun PlanNewTripButtonSection(onPlanNewTrip: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // Plan New Trip Button (Primary)
        Button(
            onClick = onPlanNewTrip,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF97316)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "✨",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Plan New Trip",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun QuickActionsSection(
    isLoggedIn: Boolean,
    onPlanNewTrip: () -> Unit,
    onViewDrafts: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Plan New Trip Button (Primary)
        Button(
            onClick = onPlanNewTrip,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF97316)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "✨",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Plan New Trip",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        // View Drafts Button (Secondary) - Only show when logged in
        if (isLoggedIn) {
            OutlinedButton(
                onClick = onViewDrafts,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = BorderStroke(1.5.dp, Color(0xFFF97316).copy(alpha = 0.5f))
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📝",
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "View Saved Drafts",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeHeader(userName: String, isLoggedIn: Boolean, onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (isLoggedIn) "Welcome back," else "Welcome,",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = userName,
                    fontSize = 32.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " 👋",
                    fontSize = 32.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isLoggedIn) "Ready for your next adventure?" else "Sign in to start planning your trips!",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.5f),
                fontWeight = FontWeight.Normal
            )
        }

        // Profile Icon Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .clickable(onClick = onProfileClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "👤",
                fontSize = 24.sp
            )
        }
    }
}

@Composable
private fun QuickStatsSection(stats: List<QuickStat>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(stats) { stat ->
                QuickStatCard(stat = stat)
            }
        }
    }
}

@Composable
private fun QuickStatCard(stat: QuickStat) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(stat.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stat.icon,
                    fontSize = 18.sp
                )
            }
            Column {
                Text(
                    text = stat.value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = stat.label,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun QuickActionButtonsSection(
    onPlanNewTrip: () -> Unit,
    onViewMyTrips: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Plan New Trip Button (Primary)
        Button(
            onClick = onPlanNewTrip,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF97316)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "✨",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Plan New Trip",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        // View My Trips Button (Secondary)
        OutlinedButton(
            onClick = onViewMyTrips,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            ),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFF97316))
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "📋",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "View My Trips",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun PlanNewTripButton(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF97316)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "✨",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Plan New Trip",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun UpcomingTripsSection(
    trips: List<Trip>,
    onTripClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Upcoming Trips",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "See all",
                fontSize = 14.sp,
                color = Color(0xFFF97316),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { /* Navigate to all trips */ }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (trips.isEmpty()) {
            NoTripsCard()
        } else {
            trips.forEach { trip ->
                TripCard(trip = trip, onClick = { onTripClick(trip.id ?: "") })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun TripCard(trip: Trip, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Decorative gradient accent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFF97316),
                                Color(0xFFEC4899)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = trip.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = trip.description,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.6f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF97316).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✈️", fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DateBadge(label = "Start", date = trip.startDate)
                    DateBadge(label = "End", date = trip.endDate)
                }
            }
        }
    }
}

@Composable
private fun DateBadge(label: String, date: String) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = formatDate(date),
                fontSize = 13.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun NoTripsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🗺️",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No trips planned yet",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Start planning your next adventure!",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun RecommendedDestinationsSection(destinations: List<Destination>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp)
    ) {
        Text(
            text = "✨ Recommended Destinations",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(end = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(end = 24.dp)
        ) {
            items(destinations) { destination ->
                DestinationCard(destination = destination)
            }
        }
    }
}

@Composable
private fun DestinationCard(destination: Destination) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(180.dp)
            .clickable { /* Handle destination click */ },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF0F172A).copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = destination.emoji,
                        fontSize = 48.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF97316).copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Popular",
                            fontSize = 11.sp,
                            color = Color(0xFFF97316),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Column {
                    Text(
                        text = destination.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = destination.country,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = destination.description,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// Helper function to format date
private fun formatDate(dateString: String): String {
    // Simple formatting - you can enhance this with proper date parsing
    val parts = dateString.split("-")
    if (parts.size == 3) {
        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                           "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val month = parts[1].toIntOrNull()?.let {
            if (it in 1..12) months[it - 1] else parts[1]
        } ?: parts[1]
        return "$month ${parts[2]}"
    }
    return dateString
}

/**
 * Login Prompt Dialog shown when guest user tries to access protected features
 */
@Composable
private fun LoginPromptDialog(
    onDismiss: () -> Unit,
    onLoginClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E293B),
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🔐",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Sign In Required",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        text = {
            Text(
                text = "You need to sign in or create an account to access this feature and start planning your amazing trips!",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF97316)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Sign In / Sign Up",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Maybe Later",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}
