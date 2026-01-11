package com.ebf.voyara.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.ebf.voyara.ViewModels.CreateTripUiState
import com.ebf.voyara.ViewModels.CreateTripViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTripScreen(
    viewModel: CreateTripViewModel,
    onNavigateBack: () -> Unit = {},
    onTripCreated: () -> Unit = {},
    onNavigateToViewDrafts: () -> Unit = {},
    draftToEdit: com.ebf.voyara.data.TripDraft? = null
) {
    val draftManager = com.ebf.voyara.utils.rememberDraftManager()

    // Form states - initialize with draft data if editing
    var tripName by remember { mutableStateOf(draftToEdit?.name ?: "") }
    var startDate by remember { mutableStateOf(draftToEdit?.startDate ?: "") }
    var endDate by remember { mutableStateOf(draftToEdit?.endDate ?: "") }
    var description by remember { mutableStateOf(draftToEdit?.description ?: "") }
    var budget by remember { mutableStateOf(draftToEdit?.budget ?: "") }
    var destination by remember { mutableStateOf(draftToEdit?.destination ?: "") }
    var numberOfTravelers by remember { mutableStateOf(draftToEdit?.numberOfTravelers ?: "1") }
    var selectedTripType by remember { mutableStateOf(draftToEdit?.selectedTripType ?: "Leisure") }
    var coverPhotoUrl by remember { mutableStateOf(draftToEdit?.coverPhotoUrl ?: "") }

    // UI states
    var showDatePicker by remember { mutableStateOf(false) }
    var isSelectingStartDate by remember { mutableStateOf(true) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showValidationError by remember { mutableStateOf(false) }
    var validationMessage by remember { mutableStateOf("") }
    var showLoadingDialog by remember { mutableStateOf(false) }
    var showDraftSavedDialog by remember { mutableStateOf(false) }

    val tripTypes = listOf("Leisure", "Business", "Adventure", "Family", "Solo", "Romantic")

    // Observe ViewModel state
    val uiState by viewModel.uiState.collectAsState()

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is CreateTripUiState.Loading -> {
                showLoadingDialog = true
            }
            is CreateTripUiState.Success -> {
                showLoadingDialog = false
                showSuccessDialog = true
            }
            is CreateTripUiState.Error -> {
                showLoadingDialog = false
                validationMessage = state.message
                showValidationError = true
                viewModel.resetState()
            }
            is CreateTripUiState.Idle -> {
                showLoadingDialog = false
            }
        }
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alphaAnim)
                .padding(top = 16.dp, bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            item {
                CreateTripHeader(onNavigateBack = onNavigateBack)
            }

            // Trip Name Section
            item {
                FormSection(title = "Trip Name") {
                    OutlinedTextField(
                        value = tripName,
                        onValueChange = { tripName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Give your trip a name", color = Color.White.copy(alpha = 0.6f)) },
                        placeholder = { Text("e.g., Summer Europe Adventure", color = Color.White.copy(alpha = 0.4f)) },
                        leadingIcon = { Text("✈️", fontSize = 20.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF97316),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFF97316)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                }
            }

            // Destination Section
            item {
                FormSection(title = "Destination") {
                    OutlinedTextField(
                        value = destination,
                        onValueChange = { destination = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Where are you going?", color = Color.White.copy(alpha = 0.6f)) },
                        placeholder = { Text("e.g., Paris, France", color = Color.White.copy(alpha = 0.4f)) },
                        leadingIcon = { Text("📍", fontSize = 20.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF97316),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFF97316)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                }
            }

            // Date Selection Section
            item {
                FormSection(title = "Travel Dates") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Start Date
                        DatePickerField(
                            label = "Start",
                            value = startDate,
                            icon = "📅",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                isSelectingStartDate = true
                                showDatePicker = true
                            }
                        )

                        // End Date
                        DatePickerField(
                            label = "End",
                            value = endDate,
                            icon = "📅",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                isSelectingStartDate = false
                                showDatePicker = true
                            }
                        )
                    }
                }
            }

            // Trip Type Section
            item {
                FormSection(title = "Trip Type") {
                    TripTypeSelector(
                        selectedType = selectedTripType,
                        types = tripTypes,
                        onTypeSelected = { selectedTripType = it }
                    )
                }
            }

            // Number of Travelers Section
            item {
                FormSection(title = "Number of Travellers") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = numberOfTravelers,
                            onValueChange = {
                                if (it.isEmpty() || it.toIntOrNull() != null) {
                                    numberOfTravelers = it
                                }
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("Travelers", color = Color.White.copy(alpha = 0.6f)) },
                            leadingIcon = { Text("👥", fontSize = 20.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFF97316),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFF97316)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            )
                        )

                        // Quick number buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("1", "2", "4", "6").forEach { num ->
                                SmallNumberButton(
                                    number = num,
                                    isSelected = numberOfTravelers == num,
                                    onClick = { numberOfTravelers = num }
                                )
                            }
                        }
                    }
                }
            }

            // Budget Section
            item {
                FormSection(title = "Estimated Budget") {
                    OutlinedTextField(
                        value = budget,
                        onValueChange = {
                            if (it.isEmpty() || it.toDoubleOrNull() != null) {
                                budget = it
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Total budget", color = Color.White.copy(alpha = 0.6f)) },
                        placeholder = { Text("e.g., 5000", color = Color.White.copy(alpha = 0.4f)) },
                        leadingIcon = { Text("💰", fontSize = 20.sp) },
                        suffix = { Text("INR", color = Color.White.copy(alpha = 0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF97316),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFF97316)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        )
                    )
                }
            }

            // Description Section
            item {
                FormSection(title = "Trip Description") {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        label = { Text("Describe your trip", color = Color.White.copy(alpha = 0.6f)) },
                        placeholder = {
                            Text(
                                "What are you planning? Any special activities or goals?",
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF97316),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFF97316)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                }
            }

            // Action Buttons
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Create Trip Button
                    Button(
                        onClick = {
                            // Validate form
                            when {
                                tripName.isBlank() -> {
                                    validationMessage = "Please enter a trip name"
                                    showValidationError = true
                                }
                                destination.isBlank() -> {
                                    validationMessage = "Please enter a destination"
                                    showValidationError = true
                                }
                                startDate.isBlank() -> {
                                    validationMessage = "Please select start date"
                                    showValidationError = true
                                }
                                endDate.isBlank() -> {
                                    validationMessage = "Please select end date"
                                    showValidationError = true
                                }
                                numberOfTravelers.isBlank() || numberOfTravelers.toIntOrNull() == null -> {
                                    validationMessage = "Please enter number of travelers"
                                    showValidationError = true
                                }
                                else -> {
                                    // Create trip via ViewModel
                                    viewModel.createTrip(
                                        name = tripName,
                                        description = description,
                                        startDate = startDate,
                                        endDate = endDate,
                                        coverPhoto = coverPhotoUrl.ifBlank { null },
                                        budget = budget.ifBlank { null }
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF97316)
                        ),
                        enabled = !showLoadingDialog
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✨",
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Create Trip",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    // Save as Draft Button
                    OutlinedButton(
                        onClick = {
                            // Create draft object
                            val draftId = draftToEdit?.id ?: generateUUID()
                            val draft = com.ebf.voyara.data.TripDraft(
                                id = draftId,
                                name = tripName,
                                destination = destination,
                                startDate = startDate,
                                endDate = endDate,
                                description = description,
                                budget = budget,
                                numberOfTravelers = numberOfTravelers,
                                selectedTripType = selectedTripType,
                                coverPhotoUrl = coverPhotoUrl
                            )

                            // Save draft
                            if (draftManager.saveDraft(draft)) {
                                showDraftSavedDialog = true
                            } else {
                                validationMessage = "Failed to save draft"
                                showValidationError = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📝",
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save as Draft",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Bottom spacing (for bottom nav bar)
            item {
                Spacer(modifier = Modifier.height(86.dp))
            }
        }

        // Date Picker Dialog
        if (showDatePicker) {
            DateInputDialog(
                title = if (isSelectingStartDate) "Select Start Date" else "Select End Date",
                onDateSelected = { date ->
                    if (isSelectingStartDate) {
                        startDate = date
                    } else {
                        endDate = date
                    }
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }

        // Success Dialog
        if (showSuccessDialog) {
            TripCreatedDialog(
                tripName = tripName,
                onDismiss = {
                    showSuccessDialog = false
                    onTripCreated()
                }
            )
        }

        // Validation Error Dialog
        if (showValidationError) {
            ValidationErrorDialog(
                message = validationMessage,
                onDismiss = { showValidationError = false }
            )
        }

        // Loading Dialog
        if (showLoadingDialog) {
            LoadingDialog()
        }

        // Draft Saved Dialog
        if (showDraftSavedDialog) {
            DraftSavedDialog(
                onDismiss = {
                    showDraftSavedDialog = false
                    onNavigateBack()
                },
                onViewDrafts = {
                    showDraftSavedDialog = false
                    onNavigateToViewDrafts()
                }
            )
        }
    }
}

@Composable
private fun CreateTripHeader(onNavigateBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        // Back button - aligned to start
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

        // Title - centered
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "Plan New Trip",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Create your perfect journey",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun FormSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.06f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(
    label: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = modifier.clickable(onClick = onClick),
        label = { Text(label, color = Color.White.copy(alpha = 0.6f)) },
        placeholder = { Text("Select", color = Color.White.copy(alpha = 0.4f)) },
        leadingIcon = { Text(icon, fontSize = 18.sp) },
        trailingIcon = { Text("▼", fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f)) },
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            disabledBorderColor = Color.White.copy(alpha = 0.3f),
            disabledTextColor = Color.White,
            disabledLeadingIconColor = Color.White,
            disabledTrailingIconColor = Color.White.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
private fun TripTypeSelector(
    selectedType: String,
    types: List<String>,
    onTypeSelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        types.chunked(3).forEach { rowTypes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowTypes.forEach { type ->
                    TripTypeChip(
                        type = type,
                        isSelected = selectedType == type,
                        onClick = { onTypeSelected(type) },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(3 - rowTypes.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TripTypeChip(
    type: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (type) {
        "Leisure" -> "🏖️"
        "Business" -> "💼"
        "Adventure" -> "🏔️"
        "Family" -> "👨‍👩‍👧‍👦"
        "Solo" -> "🎒"
        "Romantic" -> "💑"
        else -> "✈️"
    }

    Card(
        modifier = modifier
            .height(48.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF97316) else Color.White.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = type,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = Color.White
            )
        }
    }
}

@Composable
private fun SmallNumberButton(
    number: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) Color(0xFFF97316) else Color.White.copy(alpha = 0.08f)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateInputDialog(
    title: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis != null) {
                        // Convert millis to DD/MM/YYYY format
                        // Using simple calculation (milliseconds to days)
                        val epochDays = selectedMillis / (1000 * 60 * 60 * 24)
                        val year = 1970 + (epochDays / 365).toInt()
                        val dayOfYear = (epochDays % 365).toInt()
                        val month = ((dayOfYear / 30.44) + 1).toInt().coerceIn(1, 12)
                        val day = ((dayOfYear % 30.44) + 1).toInt().coerceIn(1, 31)

                        val formattedDate = "${day.toString().padStart(2, '0')}/${month.toString().padStart(2, '0')}/$year"
                        onDateSelected(formattedDate)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF97316)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = datePickerState.selectedDateMillis != null
            ) {
                Text("Confirm", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFFF97316))
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = Color(0xFF1E293B)
        )
    ) {
        DatePicker(
            state = datePickerState,
            title = {
                Text(
                    text = title,
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
            },
            headline = null,
            colors = DatePickerDefaults.colors(
                containerColor = Color(0xFF1E293B),
                titleContentColor = Color.White,
                headlineContentColor = Color.White,
                weekdayContentColor = Color.White.copy(alpha = 0.7f),
                subheadContentColor = Color.White.copy(alpha = 0.7f),
                yearContentColor = Color.White,
                currentYearContentColor = Color.White,
                selectedYearContentColor = Color.White,
                selectedYearContainerColor = Color(0xFFF97316),
                dayContentColor = Color.White,
                selectedDayContentColor = Color.White,
                selectedDayContainerColor = Color(0xFFF97316),
                todayContentColor = Color(0xFFF97316),
                todayDateBorderColor = Color(0xFFF97316),
                disabledDayContentColor = Color.White.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun TripCreatedDialog(
    tripName: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🎉",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Trip Created!",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981),
                    fontSize = 24.sp
                )
            }
        },
        text = {
            Text(
                text = "\"$tripName\" has been successfully created! Your adventure awaits.",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF97316)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View My Trips", fontSize = 16.sp)
            }
        },
        containerColor = Color(0xFF1E293B),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun ValidationErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "⚠️ Validation Error",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEF4444)
            )
        },
        text = {
            Text(
                text = message,
                color = Color.White.copy(alpha = 0.8f)
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF97316)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("OK")
            }
        },
        containerColor = Color(0xFF1E293B),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun LoadingDialog() {
    AlertDialog(
        onDismissRequest = { /* Prevent dismissal while loading */ },
        title = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color(0xFFF97316)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Creating Trip...",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        text = {
            Text(
                text = "Please wait while we save your trip details.",
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = { },
        containerColor = Color(0xFF1E293B),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun DraftSavedDialog(
    onDismiss: () -> Unit,
    onViewDrafts: () -> Unit
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
                    text = "📝",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Draft Saved!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
            }
        },
        text = {
            Text(
                text = "Your trip has been saved as a draft. You can continue editing it anytime from your drafts.",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onViewDrafts,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF97316)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "View Saved Drafts",
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
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Close",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}

/**
 * Simple UUID generator for multiplatform compatibility
 */
private fun generateUUID(): String {
    val chars = "0123456789abcdef"
    return buildString {
        repeat(8) { append(chars.random()) }
        append("-")
        repeat(4) { append(chars.random()) }
        append("-")
        append("4") // UUID version 4
        repeat(3) { append(chars.random()) }
        append("-")
        append(chars[8 + (0..3).random()]) // UUID variant
        repeat(3) { append(chars.random()) }
        append("-")
        repeat(12) { append(chars.random()) }
    }
}

