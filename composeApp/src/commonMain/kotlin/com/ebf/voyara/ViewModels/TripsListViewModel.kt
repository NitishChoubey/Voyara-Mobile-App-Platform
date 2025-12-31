package com.ebf.voyara.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebf.voyara.data.Trip
import com.ebf.voyara.network.TripService
import com.ebf.voyara.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class TripsListUiState {
    object Idle : TripsListUiState()
    object Loading : TripsListUiState()
    data class Success(val trips: List<Trip>) : TripsListUiState()
    data class Error(val message: String) : TripsListUiState()
}

class TripsListViewModel(
    private val tripService: TripService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<TripsListUiState>(TripsListUiState.Idle)
    val uiState: StateFlow<TripsListUiState> = _uiState.asStateFlow()

    private val _selectedStatus = MutableStateFlow("planning")
    val selectedStatus: StateFlow<String> = _selectedStatus.asStateFlow()

    fun fetchTrips(status: String = "planning") {
        viewModelScope.launch {
            _uiState.value = TripsListUiState.Loading
            _selectedStatus.value = status

            try {
                println("\n========================================")
                println("TripsListViewModel: START FETCH TRIPS")
                println("========================================")
                println("TripsListViewModel: Status filter: $status")

                // Get auth token
                val authToken = tokenManager.getAccessToken()

                if (authToken.isNullOrBlank()) {
                    println("TripsListViewModel: ERROR - No auth token found!")
                    _uiState.value = TripsListUiState.Error("Please login to view trips")
                    return@launch
                }

                println("TripsListViewModel: ✅ Token retrieved")
                println("TripsListViewModel: Calling TripService.fetchTrips()...")

                val trips = tripService.fetchTrips(status, authToken)

                println("\n========================================")
                println("TripsListViewModel: ✅ TRIPS FETCHED SUCCESSFULLY")
                println("========================================")
                println("TripsListViewModel: Total trips: ${trips.size}")
                trips.forEach { trip ->
                    println("  - ${trip.name} (${trip.status})")
                }
                println("========================================\n")

                _uiState.value = TripsListUiState.Success(trips)

            } catch (e: Exception) {
                println("\n========================================")
                println("TripsListViewModel: ❌ FAILED TO FETCH TRIPS")
                println("========================================")
                println("TripsListViewModel: Error: ${e.message}")
                e.printStackTrace()
                println("========================================\n")

                _uiState.value = TripsListUiState.Error(e.message ?: "Failed to fetch trips")
            }
        }
    }

    fun changeStatusFilter(status: String) {
        if (status != _selectedStatus.value) {
            fetchTrips(status)
        }
    }

    fun resetState() {
        _uiState.value = TripsListUiState.Idle
    }
}

