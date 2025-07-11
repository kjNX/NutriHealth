package com.unmsm.nutrihealth.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF66BB6A),              // Verde medio
    onPrimary = Color.White,
    secondary = Color(0xFFA8D8B9),            // Verde pastel
    secondaryContainer = Color(0xFFE3F2FD),   // Azul pastel claro
    background = Color(0xFFF8FFF8),           // Fondo general
    surface = Color(0xFFF1F8E9),              // Fondo de paneles
    onSurface = Color(0xFF2E7D32)             // Texto sobre surface
)


@Composable
fun NutriHealthTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Evitamos que lo sobreescriba
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme // Puedes hacer otro esquema pastel oscuro si deseas
    } else {
        LightColorScheme // Este ya es el pastel
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
