package com.ebf.voyara.utils

import androidx.compose.runtime.Composable

/**
 * Expect declaration for platform-specific TokenManager
 */
@Composable
expect fun rememberTokenManager(): TokenManager

