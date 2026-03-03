package com.example.gymapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Configuração do TEMA ESCURO (Dark Mode)
private val DarkColorScheme = darkColorScheme(
    primary = OrangePastel,        // Laranja mais suave para não ofuscar
    onPrimary = GreyDark,          // Texto em cima do botão laranja
    primaryContainer = OrangeMatte, // Container de destaque
    onPrimaryContainer = TextWhite,

    secondary = TextWhite,         // Ícones e textos secundários
    onSecondary = GreyDark,

    background = GreyDark,         // Fundo geral (Cinza escuro, não preto)
    surface = GreyMedium,          // Cards (um pouco mais claros que o fundo)
    onSurface = TextWhite,         // Texto principal nos cards

    surfaceVariant = Color(0xFF303030), // Variação para cards secundários
    onSurfaceVariant = Color(0xFFBDBDBD), // Texto cinza claro

    error = Color(0xFFCF6679)
)

// Configuração do TEMA CLARO (Light Mode)
private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,       // Laranja vibrante
    onPrimary = TextWhite,         // Texto branco no botão laranja
    primaryContainer = Color(0xFFFFE0B2), // Laranja bem clarinho para fundos de destaque
    onPrimaryContainer = Color(0xFFE65100),

    secondary = GreyDark,          // Elementos secundários em cinza chumbo
    onSecondary = TextWhite,

    background = GreyLight,        // Fundo Off-white
    surface = TextWhite,           // Cards brancos
    onSurface = TextBlack,         // Texto preto nos cards

    surfaceVariant = GreySurface,  // Cards cinza clarinho
    onSurfaceVariant = TextGrey,   // Texto cinza

    error = Color(0xFFB00020)
)

@Composable
fun GymAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // DESATIVAR CORES DINÂMICAS PARA MANTER SUA IDENTIDADE
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Barra de status com a cor do fundo para parecer "infinita"
            window.statusBarColor = colorScheme.background.toArgb()

            // Ícones da barra de status (escuros no tema claro, claros no escuro)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}