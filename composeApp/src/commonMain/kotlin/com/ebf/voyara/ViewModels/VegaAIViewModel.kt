package com.ebf.voyara.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebf.voyara.data.VegaAIRequest
import com.ebf.voyara.data.VegaAIResponse
import com.ebf.voyara.network.VegaAIService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class VegaAIUiState {
    object Idle : VegaAIUiState()
    object Loading : VegaAIUiState()
    data class Success(val response: VegaAIResponse) : VegaAIUiState()
    data class Error(val message: String) : VegaAIUiState()
}

class VegaAIViewModel(
    private val vegaAIService: VegaAIService
) : ViewModel() {

    private val _uiState = MutableStateFlow<VegaAIUiState>(VegaAIUiState.Idle)
    val uiState: StateFlow<VegaAIUiState> = _uiState.asStateFlow()

    fun getSuggestions(
        tripId: String,
        city: String,
        country: String,
        day: Int,
        timeSlot: String,
        totalBudget: Double,
        remainingBudget: Double,
        preferences: List<String>,
        adults: Int = 1,
        children: Int = 0
    ) {
        viewModelScope.launch {
            _uiState.value = VegaAIUiState.Loading
            try {
                println("========================================")
                println("VegaAI: START AI SUGGESTION PROCESS")
                println("========================================")
                println("VegaAI: Trip ID: $tripId")
                println("VegaAI: Destination: $city, $country")
                println("VegaAI: Day: $day, Time Slot: $timeSlot")
                println("VegaAI: Budget: Total=₹$totalBudget, Remaining=₹$remainingBudget")
                println("VegaAI: Preferences: ${preferences.joinToString()}")
                println("VegaAI: Travelers: $adults adult(s), $children child(ren)")

                val request = VegaAIRequest(
                    tripId = tripId,
                    city = city,
                    country = country,
                    day = day,
                    timeSlot = timeSlot.lowercase(),
                    totalBudget = totalBudget,
                    remainingBudget = remainingBudget,
                    preferences = preferences,
                    adults = adults,
                    children = children
                )

                println("VegaAI: Making API request...")
                val response = vegaAIService.getSuggestions(request)

                println("VegaAI: ✅ SUCCESS! Received ${response.suggestions.size} suggestions")
                println("VegaAI: Message: ${response.message}")
                println("========================================")

                _uiState.value = VegaAIUiState.Success(response)

            } catch (e: Exception) {
                println("VegaAI: ❌ ERROR: ${e.message}")
                println("========================================")
                _uiState.value = VegaAIUiState.Error(
                    e.message ?: "Failed to get AI suggestions"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = VegaAIUiState.Idle
    }
}

