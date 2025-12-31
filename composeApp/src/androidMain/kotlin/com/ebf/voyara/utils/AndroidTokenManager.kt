package com.ebf.voyara.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Android implementation of TokenManager using SharedPreferences
 */
class AndroidTokenManager(context: Context) : TokenManager {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREF_NAME = "GlobeTrotterPrefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_FULL_NAME = "user_full_name"
    }

    override fun saveAccessToken(token: String) {
        println("AndroidTokenManager: Saving access token...")
        println("AndroidTokenManager: Token length: ${token.length} chars")
        println("AndroidTokenManager: Token preview: ${token.take(50)}...")
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
        println("AndroidTokenManager: ✅ Access token saved successfully")
    }

    override fun saveRefreshToken(token: String) {
        println("AndroidTokenManager: Saving refresh token...")
        prefs.edit().putString(KEY_REFRESH_TOKEN, token).apply()
        println("AndroidTokenManager: ✅ Refresh token saved successfully")
    }

    override fun getAccessToken(): String? {
        val token = prefs.getString(KEY_ACCESS_TOKEN, null)
        println("AndroidTokenManager: Getting access token...")
        if (token != null) {
            println("AndroidTokenManager: ✅ Token found")
            println("AndroidTokenManager: Token length: ${token.length} chars")
            println("AndroidTokenManager: Token (first 50): ${token.take(50)}...")
            println("AndroidTokenManager: Full token: $token")
        } else {
            println("AndroidTokenManager: ⚠️  No token found in SharedPreferences")
        }
        return token
    }

    override fun getRefreshToken(): String? {
        val token = prefs.getString(KEY_REFRESH_TOKEN, null)
        println("AndroidTokenManager: Getting refresh token...")
        if (token != null) {
            println("AndroidTokenManager: ✅ Refresh token found")
        } else {
            println("AndroidTokenManager: ⚠️  No refresh token found")
        }
        return token
    }

    override fun clearTokens() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
    }

    override fun hasValidToken(): Boolean {
        return !getAccessToken().isNullOrEmpty()
    }

    // User info management
    override fun saveUserInfo(userId: String, email: String, fullName: String) {
        prefs.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_FULL_NAME, fullName)
            .apply()
    }

    override fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    override fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    override fun getUserFullName(): String? {
        return prefs.getString(KEY_USER_FULL_NAME, null)
    }

    override fun clearUserInfo() {
        prefs.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_FULL_NAME)
            .apply()
    }
}

