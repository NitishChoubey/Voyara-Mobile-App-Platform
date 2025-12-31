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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ebf.voyara.data.TripDraft
import com.ebf.voyara.utils.DraftManager

/**
 * Screen to display all saved drafts
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewDraftsScreen(
    draftManager: DraftManager,
    onNavigateBack: () -> Unit = {},
    onDraftClick: (TripDraft) -> Unit = {},
    onDeleteDraft: (String) -> Unit = {}
) {
    // Load drafts
    var drafts by remember { mutableStateOf(draftManager.getAllDrafts()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var draftToDelete by remember { mutableStateOf<TripDraft?>(null) }

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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alphaAnim)
                .padding(top = 16.dp, bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                DraftsHeader(
                    draftCount = drafts.size,
                    onNavigateBack = onNavigateBack
                )
            }

            if (drafts.isEmpty()) {
                // Empty state
                item {
                    EmptyDraftsCard()
                }
            } else {
                // Draft items
                items(drafts) { draft ->
                    DraftCard(
                        draft = draft,
                        onClick = { onDraftClick(draft) },
                        onDelete = {
                            draftToDelete = draft
                            showDeleteDialog = true
                        }
                    )
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(86.dp))
            }
        }

        // Delete confirmation dialog
        if (showDeleteDialog && draftToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    draftToDelete = null
                },
                containerColor = Color(0xFF1E293B),
                shape = RoundedCornerShape(24.dp),
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🗑️",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Delete Draft?",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                text = {
                    Text(
                        text = "Are you sure you want to delete this draft? This action cannot be undone.",
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            draftToDelete?.let { draft ->
                                if (draftManager.deleteDraft(draft.id)) {
                                    drafts = draftManager.getAllDrafts()
                                    onDeleteDraft(draft.id)
                                }
                            }
                            showDeleteDialog = false
                            draftToDelete = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Delete",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = {
                            showDeleteDialog = false
                            draftToDelete = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun DraftsHeader(
    draftCount: Int,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        // Back button
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "Saved Drafts",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "$draftCount draft${if (draftCount != 1) "s" else ""} saved",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun DraftCard(
    draft: TripDraft,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Gradient accent
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
                        // Draft badge
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFFF97316).copy(alpha = 0.2f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "📝 DRAFT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF97316)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Trip name
                        Text(
                            text = draft.name.ifBlank { "Untitled Trip" },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        if (draft.destination.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📍", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = draft.destination,
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }

                        if (draft.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = draft.description,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.6f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Delete button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Text(
                            text = "🗑️",
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dates and details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (draft.startDate.isNotBlank()) {
                        DetailBadge(
                            icon = "📅",
                            text = formatDraftDate(draft.startDate),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (draft.endDate.isNotBlank()) {
                        DetailBadge(
                            icon = "📅",
                            text = formatDraftDate(draft.endDate),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailBadge(
                        icon = draft.getTripTypeIcon(),
                        text = draft.selectedTripType
                    )
                    DetailBadge(
                        icon = "👥",
                        text = "${draft.numberOfTravelers} travelers"
                    )
                    if (draft.budget.isNotBlank()) {
                        DetailBadge(
                            icon = "💰",
                            text = "₹${draft.budget}"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Last saved
                Text(
                    text = "Last saved: ${formatTimestamp(draft.updatedAt)}",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
private fun DetailBadge(
    icon: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EmptyDraftsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📝",
                fontSize = 64.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Drafts Saved",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your saved trip drafts will appear here",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.5f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// Helper functions
private fun formatDraftDate(date: String): String {
    return try {
        if (date.contains("/")) {
            val parts = date.split("/")
            if (parts.size == 3) {
                val day = parts[0].padStart(2, '0')
                val month = parts[1].padStart(2, '0')
                val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                val monthName = months.getOrNull(month.toInt() - 1) ?: month
                "$monthName $day"
            } else {
                date
            }
        } else {
            date
        }
    } catch (e: Exception) {
        date
    }
}

private fun formatTimestamp(timestamp: Long): String {
    return try {
        val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        val diff = now - timestamp

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "$minutes min ago"
            hours < 24 -> "$hours hour${if (hours != 1L) "s" else ""} ago"
            days < 7 -> "$days day${if (days != 1L) "s" else ""} ago"
            else -> "Recently"
        }
    } catch (_: Exception) {
        "Recently"
    }
}

private fun TripDraft.getTripTypeIcon(): String {
    return when (selectedTripType) {
        "Leisure" -> "🏖️"
        "Business" -> "💼"
        "Adventure" -> "🏔️"
        "Family" -> "👨‍👩‍👧‍👦"
        "Solo" -> "🎒"
        "Romantic" -> "💑"
        else -> "✈️"
    }
}

