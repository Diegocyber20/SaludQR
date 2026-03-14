package com.diegocanaquiri.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MedicalPrimary,
    secondary = MedicalSecondary,
    tertiary = MedicalTertiary,
    background = MedicalBackground,
    surface = MedicalSurface,
    onPrimary = MedicalOnPrimary,
    error = MedicalError,
    onSecondary = Color.White
)

@Composable
fun SaludQRTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
