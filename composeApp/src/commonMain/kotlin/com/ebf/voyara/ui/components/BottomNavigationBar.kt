package com.ebf.voyara.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Bottom Navigation Items
 */
sealed class BottomNavItem(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
) {
    data object Home : BottomNavItem(
        route = "home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        label = "Home"
    )
    data object MyTrips : BottomNavItem(
        route = "my_trips",
        selectedIcon = Icons.Filled.List,
        unselectedIcon = Icons.Outlined.List,
        label = "My Trips"
    )
    data object ItineraryBuilder : BottomNavItem(
        route = "itinerary_builder",
        selectedIcon = Icons.Filled.Map,
        unselectedIcon = Icons.Outlined.Map,
        label = "Itinerary"
    )
    data object CreateTrip : BottomNavItem(
        route = "create_trip",
        selectedIcon = Icons.Filled.AddCircle,
        unselectedIcon = Icons.Outlined.AddCircle,
        label = "New Trip"
    )
    data object Profile : BottomNavItem(
        route = "profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        label = "Profile"
    )
}

/**
 * Professional Bottom Navigation Bar
 */
@Composable
fun BottomNavigationBar(
    selectedRoute: String,
    isLoggedIn: Boolean,
    onNavigate: (String) -> Unit,
    onShowLoginPrompt: () -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.MyTrips,
        BottomNavItem.ItineraryBuilder,
        BottomNavItem.CreateTrip,
        BottomNavItem.Profile
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E293B).copy(alpha = 0.98f),
                            Color(0xFF0F172A).copy(alpha = 0.98f)
                        )
                    )
                )
        ) {
            // Subtle top border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.1f))
                    .align(Alignment.TopCenter)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    BottomNavItemView(
                        item = item,
                        isSelected = selectedRoute == item.route,
                        isLoggedIn = isLoggedIn,
                        onClick = {
                            // Home is always accessible
                            if (item.route == "home") {
                                onNavigate(item.route)
                            } else {
                                // Other screens require login
                                if (isLoggedIn) {
                                    onNavigate(item.route)
                                } else {
                                    onShowLoginPrompt()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    isLoggedIn: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Simplified animations
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) {
            Color(0xFFF97316)
        } else {
            Color.White.copy(alpha = 0.7f)
        },
        animationSpec = tween(durationMillis = 200)
    )

    val labelColor by animateColorAsState(
        targetValue = if (isSelected) {
            Color(0xFFF97316)
        } else {
            Color.White.copy(alpha = 0.8f)
        },
        animationSpec = tween(durationMillis = 200)
    )

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .scale(scale)
            .width(70.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.label,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Label
        Text(
            text = item.label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = labelColor,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

