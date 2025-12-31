package com.ebf.voyara.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebf.voyara.data.CreateTripResponse
import com.ebf.voyara.network.TripService
import com.ebf.voyara.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CreateTripUiState {
    object Idle : CreateTripUiState()
    object Loading : CreateTripUiState()
    data class Success(val message: String, val response: CreateTripResponse) : CreateTripUiState()
    data class Error(val message: String) : CreateTripUiState()
}

class CreateTripViewModel(
    private val tripService: TripService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateTripUiState>(CreateTripUiState.Idle)
    val uiState: StateFlow<CreateTripUiState> = _uiState.asStateFlow()

    fun createTrip(
        name: String,
        description: String,
        startDate: String,
        endDate: String,
        coverPhoto: String?,
        budget: String?
    ) {
        viewModelScope.launch {
            _uiState.value = CreateTripUiState.Loading
            try {
                println("========================================")
                println("CreateTripViewModel: START CREATE TRIP PROCESS")
                println("========================================")

                // Get auth token
                println("CreateTripViewModel: Retrieving auth token from TokenManager...")
                val authToken = tokenManager.getAccessToken()

                if (authToken.isNullOrBlank()) {
                    println("CreateTripViewModel: ERROR - No auth token found!")
                    _uiState.value = CreateTripUiState.Error("Please login to create a trip")
                    return@launch
                }

                println("CreateTripViewModel: ✅ Auth token retrieved successfully")
                println("CreateTripViewModel: TOKEN: $authToken")
                println("CreateTripViewModel: Token length: ${authToken.length} characters")
                println("CreateTripViewModel: Token preview: ${authToken.take(50)}...")

                // Convert date format from DD/MM/YYYY to YYYY-MM-DD
                println("\nCreateTripViewModel: Converting dates...")
                println("CreateTripViewModel: Original start date: $startDate")
                println("CreateTripViewModel: Original end date: $endDate")

                val formattedStartDate = formatDateForApi(startDate)
                val formattedEndDate = formatDateForApi(endDate)

                println("CreateTripViewModel: Formatted start date: $formattedStartDate")
                println("CreateTripViewModel: Formatted end date: $formattedEndDate")

                // Parse budget
                val budgetValue = budget?.toDoubleOrNull()

                println("\nCreateTripViewModel: REQUEST DATA:")
                println("CreateTripViewModel: - Name: $name")
                println("CreateTripViewModel: - Description: $description")
                println("CreateTripViewModel: - Start Date: $formattedStartDate")
                println("CreateTripViewModel: - End Date: $formattedEndDate")
                println("CreateTripViewModel: - Cover Photo: ${coverPhoto ?: "null"}")
                println("CreateTripViewModel: - Budget: $budgetValue")

                println("\nCreateTripViewModel: Calling TripService.createTrip()...")

                val response = tripService.createTrip(
                    name = name,
                    description = description,
                    startDate = formattedStartDate,
                    endDate = formattedEndDate,
                    coverPhoto = coverPhoto,
                    budget = budgetValue,
                    authToken = authToken
                )

                // Store both trip ID and user ID
                println("\n========================================")
                println("CreateTripViewModel: ✅ TRIP CREATED SUCCESSFULLY!")
                println("========================================")
                println("CreateTripViewModel: Trip ID: ${response.id}")
                println("CreateTripViewModel: User ID: ${response.userId}")
                println("CreateTripViewModel: Trip Name: ${response.name}")
                println("CreateTripViewModel: Status: ${response.status}")
                println("CreateTripViewModel: Created At: ${response.createdAt}")
                println("========================================")

                // You can store the trip ID in SharedPreferences if needed for later use
                // For now, we'll just pass it in the success state
                val successMessage = "Trip '${response.name}' created successfully!"
                _uiState.value = CreateTripUiState.Success(successMessage, response)
            } catch (e: Exception) {
                println("\n========================================")
                println("CreateTripViewModel: ❌ FAILED TO CREATE TRIP")
                println("========================================")
                println("CreateTripViewModel: Error Type: ${e::class.simpleName}")
                println("CreateTripViewModel: Error Message: ${e.message}")
                println("CreateTripViewModel: Stack Trace:")
                e.printStackTrace()
                println("========================================")
                _uiState.value = CreateTripUiState.Error(e.message ?: "Failed to create trip")
            }
        }
    }

    /**
     * Convert date format from DD/MM/YYYY to YYYY-MM-DD
     */
    private fun formatDateForApi(date: String): String {
        return try {
            val parts = date.split("/")
            if (parts.size == 3) {
                val day = parts[0].padStart(2, '0')
                val month = parts[1].padStart(2, '0')
                val year = parts[2]
                "$year-$month-$day"
            } else {
                date
            }
        } catch (e: Exception) {
            date
        }
    }

    fun resetState() {
        _uiState.value = CreateTripUiState.Idle
    }
}

