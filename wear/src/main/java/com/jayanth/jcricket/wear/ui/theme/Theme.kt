package com.jayanth.jcricket.wear.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

// Wear Theme Colors
val BgDark = Color(0xFF111827)
val CardBgDark = Color(0xFF1F2937)
val TextPrimaryDark = Color(0xFFF9FAFB)
val TextSecondaryDark = Color(0xFF9CA3AF)

// Status & Format Colors
val LiveColor = Color(0xFFF87171)
val UpcomingColor = Color(0xFF60A5FA)
val ResultColor = Color(0xFF4ADE80)

val FormatTestColor = Color(0xFFA78BFA)
val FormatOdiColor = Color(0xFF38BDF8)
val FormatT20Color = Color(0xFFFB923C)

private val WearColorScheme = Colors(
    primary = UpcomingColor,
    secondary = FormatTestColor,
    background = BgDark,
    surface = CardBgDark,
    onPrimary = BgDark,
    onSecondary = BgDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark
)

@Composable
fun WearJcricketTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = WearColorScheme,
        content = content
    )
}
