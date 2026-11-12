package com.example.win11calculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class Win11CalculatorColors(
    val background: Color,
    val surface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val digitButton: Color,
    val digitButtonBorder: Color,
    val functionButton: Color,
    val functionButtonBorder: Color,
    val accentButton: Color,
    val accentButtonBorder: Color,
    val accentButtonText: Color,
    val memoryDisabled: Color,
    val isDark: Boolean
)

val LocalWin11Colors = staticCompositionLocalOf {
    Win11CalculatorColors(
        background = Win11LightBackground,
        surface = Win11LightSurface,
        textPrimary = Win11LightTextPrimary,
        textSecondary = Win11LightTextSecondary,
        digitButton = Win11LightDigitButton,
        digitButtonBorder = Win11LightButtonBorder,
        functionButton = Win11LightFunctionButton,
        functionButtonBorder = Win11LightButtonBorder,
        accentButton = Win11LightAccentBlue,
        accentButtonBorder = Win11LightAccentBlue,
        accentButtonText = Win11LightAccentBlueText,
        memoryDisabled = Win11LightMemoryDisabled,
        isDark = false
    )
}

private val LightColorScheme = lightColorScheme(
    primary = Win11LightAccentBlue,
    onPrimary = Win11LightAccentBlueText,
    background = Win11LightBackground,
    surface = Win11LightSurface,
    onBackground = Win11LightTextPrimary,
    onSurface = Win11LightTextPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = Win11DarkAccentBlue,
    onPrimary = Win11DarkAccentBlueText,
    background = Win11DarkBackground,
    surface = Win11DarkSurface,
    onBackground = Win11DarkTextPrimary,
    onSurface = Win11DarkTextPrimary
)

@Composable
fun Win11CalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val win11Colors = if (darkTheme) {
        Win11CalculatorColors(
            background = Win11DarkBackground,
            surface = Win11DarkSurface,
            textPrimary = Win11DarkTextPrimary,
            textSecondary = Win11DarkTextSecondary,
            digitButton = Win11DarkDigitButton,
            digitButtonBorder = Win11DarkButtonBorder,
            functionButton = Win11DarkFunctionButton,
            functionButtonBorder = Win11DarkButtonBorder,
            accentButton = Win11DarkAccentBlue,
            accentButtonBorder = Win11DarkAccentBlue,
            accentButtonText = Win11DarkAccentBlueText,
            memoryDisabled = Win11DarkMemoryDisabled,
            isDark = true
        )
    } else {
        Win11CalculatorColors(
            background = Win11LightBackground,
            surface = Win11LightSurface,
            textPrimary = Win11LightTextPrimary,
            textSecondary = Win11LightTextSecondary,
            digitButton = Win11LightDigitButton,
            digitButtonBorder = Win11LightButtonBorder,
            functionButton = Win11LightFunctionButton,
            functionButtonBorder = Win11LightButtonBorder,
            accentButton = Win11LightAccentBlue,
            accentButtonBorder = Win11LightAccentBlue,
            accentButtonText = Win11LightAccentBlueText,
            memoryDisabled = Win11LightMemoryDisabled,
            isDark = false
        )
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalWin11Colors provides win11Colors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
