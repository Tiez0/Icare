package com.example.icare.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta de Cores Padrão (Tema Claro)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF88e788),      // Verde claro
    onPrimary = Color.Black,          // Texto preto sobre o verde
    background = Color.White,
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    error = Color.Red,                // Vermelho para erros
    onError = Color.White             // Branco sobre o vermelho
)

// Paleta de Cores de Alto Contraste (Sua visão)
private val HighContrastColorScheme = darkColorScheme(
    primary = Color(0xFFFFC107),      // Amarelo no lugar do verde
    onPrimary = Color.Black,          // Texto preto sobre amarelo para contraste máximo
    background = Color.Black,
    surface = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color(0xFF9C27B0),        // Roxo no lugar do vermelho
    onError = Color.White             // Branco sobre o roxo
)

@Composable
fun IcareTheme(
    useHighContrast: Boolean = false, // Parâmetro para controlar o tema
    content: @Composable () -> Unit
) {
    val colorScheme = if (useHighContrast) {
        HighContrastColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
