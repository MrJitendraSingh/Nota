package com.nota.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SoftChampagneGold,
    onPrimary = DarkNavyBlue,
    primaryContainer = DarkNavyBlue,
    onPrimaryContainer = SoftChampagneGold,
    secondary = MutedGold,
    onSecondary = DeepNavy,
    background = DeepNavy,
    onBackground = TextOnDark,
    surface = DarkNavyBlue,
    onSurface = TextOnDark,
    surfaceVariant = DarkNavyBlue,
    onSurfaceVariant = SecondaryText,
    outline = SoftChampagneGold,
    outlineVariant = SoftSilver
)

@Composable
fun NotaTheme(
    content: @Composable () -> Unit
) {
    // The visual identity is specifically designed with a deep navy background.
    // We prioritize the DarkColorScheme for the premium NOTA feel.
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
