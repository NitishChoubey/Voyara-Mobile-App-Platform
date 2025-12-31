package com.ebf.voyara.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.ebf.voyara.ViewModels.TripsListUiState
import com.ebf.voyara.ViewModels.TripsListViewModel
import com.ebf.voyara.data.Trip


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTripsScreen(
    viewModel: TripsListViewModel,
    onNavigateBack: () -> Unit = {},
    onTripClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()

    // Fetch trips on first composition
    LaunchedEffect(Unit) {
        viewModel.fetchTrips("planning")
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
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF1E293B)
                    )
                )
            )
    ) {
        // Decorative background elements
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x33F97316),
                        Color.Transparent
                    )
                ),
                radius = 350f,
                center = center.copy(x = size.width * 0.85f, y = size.height * 0.15f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x332563EB),
                        Color.Transparent
                    )
                ),
                radius = 300f,
                center = center.copy(x = size.width * 0.15f, y = size.height * 0.8f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alphaAnim)
        ) {
            // Header
            MyTripsHeader(onNavigateBack = onNavigateBack)

            // Status Filter Tabs
            StatusFilterTabs(
                selectedStatus = selectedStatus,
                onStatusSelected = { status ->
                    viewModel.changeStatusFilter(status)
                }
            )

            // Content based on state
            when (val state = uiState) {
                is TripsListUiState.Idle -> {
                    // Show nothing or initial state
                }
                is TripsListUiState.Loading -> {
                    LoadingContent()
                }
                is TripsListUiState.Success -> {
                    if (state.trips.isEmpty()) {
                        EmptyTripsContent(status = selectedStatus)
                    } else {
                        TripsListContent(
                            trips = state.trips,
                            onTripClick = onTripClick
                        )
                    }
                }
                is TripsListUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { viewModel.fetchTrips(selectedStatus) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MyTripsHeader(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Title
        Column {
            Text(
                text = "My Trips",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Manage your travel plans",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun StatusFilterTabs(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit
) {
    val statuses = listOf(
        "planning" to "📋 Planning",
        "ongoing" to "✈️ Ongoing",
        "completed" to "✅ Completed",
        "cancelled" to "❌ Cancelled"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        statuses.forEach { (status, label) ->
            StatusChip(
                label = label,
                isSelected = selectedStatus == status,
                onClick = { onStatusSelected(status) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    Color(0xFFF97316)
                } else {
                    Color.White.copy(alpha = 0.1f)
                }
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = Color(0xFFF97316),
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Loading your trips...",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun EmptyTripsContent(status: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text(
                text = "🌍",
                fontSize = 64.sp
            )
            Text(
                text = "No ${status} trips yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Start planning your next adventure!",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text(
                text = "⚠️",
                fontSize = 64.sp
            )
            Text(
                text = "Oops! Something went wrong",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF97316)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun TripsListContent(
    trips: List<Trip>,
    onTripClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(trips) { trip ->
            TripCard(
                trip = trip,
                onClick = { trip.id?.let { onTripClick(it) } }
            )
        }

        item {
            Spacer(modifier = Modifier.height(86.dp))
        }
    }
}

@Composable
private fun TripCard(
    trip: Trip,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Trip Name and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trip.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                // Status Badge
                trip.status?.let { status ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (status) {
                                    "planning" -> Color(0xFF3B82F6)
                                    "ongoing" -> Color(0xFFF97316)
                                    "completed" -> Color(0xFF10B981)
                                    "cancelled" -> Color(0xFFEF4444)
                                    else -> Color.Gray
                                }
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = status.capitalize(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = trip.description,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Start Date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "📅", fontSize = 16.sp)
                    Column {
                        Text(
                            text = "Start",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                        Text(
                            text = formatDate(trip.startDate),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                // End Date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "🏁", fontSize = 16.sp)
                    Column {
                        Text(
                            text = "End",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                        Text(
                            text = formatDate(trip.endDate),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                // Budget (if available)
                trip.budget?.let { budget ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "💰", fontSize = 16.sp)
                        Column {
                            Text(
                                text = "Budget",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "₹${budget.toInt()}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFF97316)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Format date from YYYY-MM-DD to readable format
 */
private fun formatDate(date: String): String {
    return try {
        val parts = date.split("-")
        if (parts.size == 3) {
            val year = parts[0]
            val month = parts[1]
            val day = parts[2]

            val monthName = when (month) {
                "01" -> "Jan"
                "02" -> "Feb"
                "03" -> "Mar"
                "04" -> "Apr"
                "05" -> "May"
                "06" -> "Jun"
                "07" -> "Jul"
                "08" -> "Aug"
                "09" -> "Sep"
                "10" -> "Oct"
                "11" -> "Nov"
                "12" -> "Dec"
                else -> month
            }

            "$day $monthName $year"
        } else {
            date
        }
    } catch (e: Exception) {
        date
    }
}

private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

