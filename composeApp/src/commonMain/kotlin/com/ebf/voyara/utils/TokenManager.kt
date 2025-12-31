package com.ebf.voyara.utils

/**
 * TokenManager interface for multiplatform token storage
 */
interface TokenManager {
    fun saveAccessToken(token: String)
    fun saveRefreshToken(token: String)
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun clearTokens()
    fun hasValidToken(): Boolean

    // User info management
    fun saveUserInfo(userId: String, email: String, fullName: String)
    fun getUserId(): String?
    fun getUserEmail(): String?
    fun getUserFullName(): String?
    fun clearUserInfo()
}

