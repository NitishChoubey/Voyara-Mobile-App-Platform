package com.ebf.voyara

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.ebf.voyara.navigation.AppNavigation
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        AppNavigation()
    }
}