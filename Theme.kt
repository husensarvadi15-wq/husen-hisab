package com.sarvadiverse.husenhisab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryYellow,
    onPrimary = PureBlack,
    secondary = SecondaryYellow,
    onSecondary = PureBlack,
    background = PureBlack,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextWhite,
    error = AlertRed
)

@Composable
fun HusenHisabTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
