package com.ebf.voyara.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebf.voyara.data.SignupResponse
import com.ebf.voyara.network.AuthService
import com.ebf.voyara.sealedClasses.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignupViewModel(private val authService: AuthService) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Fields for signup
    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    // Store signup response
    private val _signupResponse = MutableStateFlow<SignupResponse?>(null)
    val signupResponse: StateFlow<SignupResponse?> = _signupResponse.asStateFlow()

    fun onFullNameChange(newValue: String) {
        _fullName.value = newValue
    }

    fun onEmailChange(newValue: String) {
        _email.value = newValue
    }

    fun onPasswordChange(newValue: String) {
        _password.value = newValue
    }

    fun onSignupClick() {
        val currentFullName = fullName.value
        val currentEmail = email.value
        val currentPassword = password.value

        // Validate all fields
        if (currentFullName.isBlank() || currentEmail.isBlank() || currentPassword.isBlank()) {
            _uiState.value = AuthUiState.Error("All fields are required")
            return
        }

        // Validate email format
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (!emailRegex.matches(currentEmail)) {
            _uiState.value = AuthUiState.Error("Invalid email format")
            return
        }

        // Validate password strength
        if (currentPassword.length < 8) {
            _uiState.value = AuthUiState.Error("Password must be at least 8 characters")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                println("SignupViewModel: Attempting signup for email: $currentEmail, fullName: $currentFullName")
                val response = authService.signup(currentFullName, currentEmail, currentPassword)
                println("SignupViewModel: Signup successful - ${response.message}, userId: ${response.user.id}")
                _signupResponse.value = response
                _uiState.value = AuthUiState.Success(response.message)
            } catch (e: Exception) {
                println("SignupViewModel: Signup failed - ${e.message}")
                _uiState.value = AuthUiState.Error(e.message ?: "Signup Failed")
            }
        }
    }

    // Reset state to idle
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}