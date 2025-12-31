package com.ebf.voyara.sealedClasses

sealed class AuthUiState {

    object Idle : AuthUiState()
    object Loading: AuthUiState()
    data class Success(val message: String) : AuthUiState()
    data class Error(val errorMessage: String):  AuthUiState()


}