package com.ebf.voyara.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ebf.voyara.ViewModels.VegaAIUiState
import com.ebf.voyara.ViewModels.VegaAIViewModel
import com.ebf.voyara.data.Suggestion
import com.ebf.voyara.network.VegaAIService

@Composable
fun AskVegaAIScreen(
    onNavigateBack: () -> Unit,
    viewModel: VegaAIViewModel = viewModel { VegaAIViewModel(VegaAIService()) }
) {
    var tripId by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    var timeSlot by remember { mutableStateOf("Morning") }
    var totalBudget by remember { mutableStateOf("") }
    var remainingBudget by remember { mutableStateOf("") }
    var adults by remember { mutableStateOf("1") }
    var children by remember { mutableStateOf("0") }
    var selectedPreferences by remember { mutableStateOf(setOf<String>()) }

    val uiState by viewModel.uiState.collectAsState()
    var showSuggestions by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val timeSlots = listOf("Morning", "Afternoon", "Evening", "Night")
    val preferences = listOf("History", "Quiet Places", "Nature", "Adventure", "Shopping", "Food", "Culture", "Beach")

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is VegaAIUiState.Success -> {
                showSuggestions = true
                errorMessage = null
            }
            is VegaAIUiState.Error -> {
                errorMessage = state.message
                showSuggestions = false
            }
            else -> {}
        }
    }

    // Show error dialog
    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("Error", color = Color.White) },
            text = { Text(message, color = Color.White.copy(alpha = 0.8f)) },
            confirmButton = {
                TextButton(onClick = { errorMessage = null }) {
                    Text("OK", color = Color(0xFF818CF8))
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }

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
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✨",
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ask Vega AI",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            if (!showSuggestions) {
                // Input Form
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Get personalized place recommendations",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Trip Details Section
                    item {
                        SectionHeader(title = "Trip Details", icon = Icons.Default.Info)
                    }

                    item {
                        ModernTextField(
                            value = tripId,
                            onValueChange = { tripId = it },
                            label = "Trip ID",
                            placeholder = "e.g., test_003"
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                ModernTextField(
                                    value = city,
                                    onValueChange = { city = it },
                                    label = "City",
                                    placeholder = "e.g., Mumbai"
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                ModernTextField(
                                    value = country,
                                    onValueChange = { country = it },
                                    label = "Country",
                                    placeholder = "e.g., India"
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                ModernTextField(
                                    value = day,
                                    onValueChange = { day = it },
                                    label = "Day",
                                    placeholder = "e.g., 2"
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                TimeSlotSelector(
                                    selectedSlot = timeSlot,
                                    slots = timeSlots,
                                    onSlotSelected = { timeSlot = it }
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                ModernTextField(
                                    value = adults,
                                    onValueChange = { adults = it },
                                    label = "Adults",
                                    placeholder = "e.g., 2"
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                ModernTextField(
                                    value = children,
                                    onValueChange = { children = it },
                                    label = "Children",
                                    placeholder = "e.g., 1"
                                )
                            }
                        }
                    }

                    // Budget Section
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(title = "Budget", icon = Icons.Default.AccountBalanceWallet)
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                ModernTextField(
                                    value = totalBudget,
                                    onValueChange = { totalBudget = it },
                                    label = "Total Budget (₹)",
                                    placeholder = "e.g., 3000"
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                ModernTextField(
                                    value = remainingBudget,
                                    onValueChange = { remainingBudget = it },
                                    label = "Remaining (₹)",
                                    placeholder = "e.g., 2500"
                                )
                            }
                        }
                    }

                    // Preferences Section
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(title = "Preferences", icon = Icons.Default.Favorite)
                    }

                    item {
                        PreferenceChipGroup(
                            preferences = preferences,
                            selectedPreferences = selectedPreferences,
                            onPreferenceToggle = { preference ->
                                selectedPreferences = if (selectedPreferences.contains(preference)) {
                                    selectedPreferences - preference
                                } else {
                                    selectedPreferences + preference
                                }
                            }
                        )
                    }

                    // Generate Button
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                // Validate required fields
                                if (tripId.isBlank() || city.isBlank() || country.isBlank() ||
                                    day.isBlank() || totalBudget.isBlank() || remainingBudget.isBlank()) {
                                    errorMessage = "Please fill all required fields"
                                    return@Button
                                }

                                // Parse and validate numeric fields
                                val dayInt = day.toIntOrNull()
                                val totalBudgetDouble = totalBudget.toDoubleOrNull()
                                val remainingBudgetDouble = remainingBudget.toDoubleOrNull()
                                val adultsInt = adults.toIntOrNull() ?: 1
                                val childrenInt = children.toIntOrNull() ?: 0

                                if (dayInt == null || dayInt <= 0) {
                                    errorMessage = "Day must be a positive number"
                                    return@Button
                                }
                                if (totalBudgetDouble == null || totalBudgetDouble <= 0) {
                                    errorMessage = "Total budget must be a positive number"
                                    return@Button
                                }
                                if (remainingBudgetDouble == null || remainingBudgetDouble < 0) {
                                    errorMessage = "Remaining budget must be a non-negative number"
                                    return@Button
                                }
                                if (adultsInt <= 0) {
                                    errorMessage = "Number of adults must be at least 1"
                                    return@Button
                                }

                                // Make real API call through ViewModel
                                viewModel.getSuggestions(
                                    tripId = tripId,
                                    city = city,
                                    country = country,
                                    day = dayInt,
                                    timeSlot = timeSlot,
                                    totalBudget = totalBudgetDouble,
                                    remainingBudget = remainingBudgetDouble,
                                    preferences = selectedPreferences.toList(),
                                    adults = adultsInt,
                                    children = childrenInt
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6366F1)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            enabled = city.isNotBlank() && uiState !is VegaAIUiState.Loading
                        ) {
                            if (uiState is VegaAIUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Get AI Suggestions",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Loading message
                    if (uiState is VegaAIUiState.Loading) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF1E293B)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "🤖 AI is thinking...",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "First request may take 60-90 seconds",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.7f),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Text(
                                        text = "(Service is warming up)",
                                        fontSize = 11.sp,
                                        color = Color(0xFF818CF8),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            } else {
                // Suggestions Display
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "AI Suggestions",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = when (val state = uiState) {
                                        is VegaAIUiState.Success -> state.response.message
                                        else -> "Powered by Gemini"
                                    },
                                    fontSize = 12.sp,
                                    color = Color(0xFF818CF8)
                                )
                            }
                            TextButton(onClick = {
                                showSuggestions = false
                                viewModel.resetState()
                            }) {
                                Text(
                                    text = "New Search",
                                    color = Color(0xFF818CF8)
                                )
                            }
                        }
                    }

                    when (val state = uiState) {
                        is VegaAIUiState.Success -> {
                            items(state.response.suggestions) { suggestion ->
                                SuggestionCard(suggestion = suggestion)
                            }
                        }
                        else -> {
                            // Should not happen, but handle gracefully
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF818CF8),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.4f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF334155),
                unfocusedContainerColor = Color(0xFF1E293B),
                focusedIndicatorColor = Color(0xFF818CF8),
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
    }
}

@Composable
fun TimeSlotSelector(
    selectedSlot: String,
    slots: List<String>,
    onSlotSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Time Slot",
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E293B))
                .border(
                    width = 1.dp,
                    color = if (expanded) Color(0xFF818CF8) else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { expanded = true }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedSlot,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color(0xFF334155))
            ) {
                slots.forEach { slot ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = slot,
                                color = Color.White
                            )
                        },
                        onClick = {
                            onSlotSelected(slot)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PreferenceChipGroup(
    preferences: List<String>,
    selectedPreferences: Set<String>,
    onPreferenceToggle: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        preferences.chunked(3).forEach { rowPreferences ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowPreferences.forEach { preference ->
                    PreferenceChip(
                        text = preference,
                        isSelected = selectedPreferences.contains(preference),
                        onToggle = { onPreferenceToggle(preference) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill empty spaces
                repeat(3 - rowPreferences.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun PreferenceChip(
    text: String,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(21.dp))
            .background(
                if (isSelected) Color(0xFF6366F1) else Color(0xFF334155)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFF818CF8) else Color.Transparent,
                shape = RoundedCornerShape(21.dp)
            )
            .clickable { onToggle() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun SuggestionCard(suggestion: Suggestion) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Place Name
                    Text(
                        text = suggestion.displayName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Subtitle if available
                    suggestion.subtitle?.let { subtitle ->
                        if (subtitle.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = subtitle,
                                fontSize = 13.sp,
                                color = Color(0xFF818CF8),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Info Badges Row (Cost, Duration, Category)
            if (suggestion.displayCost != null || suggestion.displayDuration != null || suggestion.category != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cost Badge
                    suggestion.displayCost?.let { cost ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "₹${cost.toInt()}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }

                    // Duration Badge
                    suggestion.displayDuration?.let { duration ->
                        if (duration.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF818CF8).copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = Color(0xFF818CF8),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = duration,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF818CF8)
                                    )
                                }
                            }
                        }
                    }

                    // Category Badge
                    suggestion.category?.let { category ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF59E0B).copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Category,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = category,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFF59E0B)
                                )
                            }
                        }
                    }
                }
            }

            // Recommendation Reason (if available)
            suggestion.displayReason?.let { reason ->
                if (reason.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFBBF24).copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = reason,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFFBBF24),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Description
            if (suggestion.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = suggestion.description,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Highlights Section
            if (suggestion.highlights.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "✨ Highlights",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF818CF8),
                        modifier = Modifier.fillMaxWidth()
                    )

                    suggestion.highlights.forEach { highlight ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF818CF8))
                            )
                            Text(
                                text = highlight,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = Color.White.copy(alpha = 0.75f),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}




