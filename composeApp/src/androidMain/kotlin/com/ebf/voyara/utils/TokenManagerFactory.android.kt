package com.ebf.voyara.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Android implementation of rememberTokenManager
 */
@Composable
actual fun rememberTokenManager(): TokenManager {
    val context = LocalContext.current
    return remember { AndroidTokenManager(context) }
}

