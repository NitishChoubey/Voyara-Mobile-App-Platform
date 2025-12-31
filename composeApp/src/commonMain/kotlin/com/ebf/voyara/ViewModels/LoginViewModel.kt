package com.ebf.voyara.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebf.voyara.data.LoginResponse
import com.ebf.voyara.network.AuthService
import com.ebf.voyara.sealedClasses.AuthUiState
import com.ebf.voyara.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authService: AuthService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Fields
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    // Store login response for token management
    private val _loginResponse = MutableStateFlow<LoginResponse?>(null)
    val loginResponse: StateFlow<LoginResponse?> = _loginResponse.asStateFlow()

    // This function will be used to update the email
    fun onEmailChange(newValue: String) {
        _email.value = newValue
    }

    // This function will be used to update the password
    fun onPasswordChange(newValue: String) {
        _password.value = newValue
    }

    fun onLoginClick() {
        val currentEmail = email.value
        val currentPassword = password.value

        // Validate fields
        if (currentEmail.isBlank() || currentPassword.isBlank()) {
            _uiState.value = AuthUiState.Error("Fields cannot be empty")
            return
        }

        // Simple email validation using regex
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (!emailRegex.matches(currentEmail)) {
            _uiState.value = AuthUiState.Error("Invalid email format")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                println("LoginViewModel: Attempting login for email: $currentEmail")
                val response = authService.login(currentEmail, currentPassword)
                println("LoginViewModel: Login successful - ${response.message}")

                // Save tokens to SharedPreferences (fast operation)
                tokenManager.saveAccessToken(response.accessToken)
                tokenManager.saveRefreshToken(response.refreshToken)

                // Save user information
                tokenManager.saveUserInfo(
                    userId = response.user.id,
                    email = response.user.email,
                    fullName = response.user.fullName
                )

                println("LoginViewModel: Tokens and user info saved successfully")

                _loginResponse.value = response
                _uiState.value = AuthUiState.Success(response.message)
            } catch (e: Exception) {
                println("LoginViewModel: Login failed - ${e.message}")
                _uiState.value = AuthUiState.Error(e.message ?: "Login Failed")
            }
        }
    }

    // Reset state to idle
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

}