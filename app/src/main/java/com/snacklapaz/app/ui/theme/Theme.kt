package com.snacklapaz.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Definimos apenas o esquema claro por enquanto (identidade do app é clara/creme).
// Se quiser modo escuro no futuro, é só me pedir que eu adiciono darkColorScheme().
private val LightColors = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = OnOrange,
    primaryContainer = OrangeLight,
    onPrimaryContainer = OrangeDark,

    secondary = SuccessGreen,
    onSecondary = White,

    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRedLight,
    onErrorContainer = ErrorRed,

    background = CreamBackground,
    onBackground = GrayDark,

    surface = White,
    onSurface = GrayDark,
    surfaceVariant = GrayLight,
    onSurfaceVariant = GrayMedium,

    outline = GrayBorder
)

@Composable
fun SnackLaPazTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // reservado para o futuro
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}

// Cores utilitárias de status de pedido, usadas fora do MaterialTheme
object OrderStatusColors {
    val completed: Color = SuccessGreen
    val current: Color = OrangePrimary
    val pending: Color = GrayMedium
}