package com.don.focustimer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// Guardian color palette — light mode
private val GuardianLightColors = lightColorScheme(
    primary          = Color(0xFF3949AB),   // indigo-600
    onPrimary        = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE8EAF6),
    onPrimaryContainer = Color(0xFF1A237E),
    secondary        = Color(0xFF00897B),   // teal-600
    onSecondary      = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE0F2F1),
    onSecondaryContainer = Color(0xFF004D40),
    tertiary         = Color(0xFF6A1B9A),   // purple
    onTertiary       = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF3E5F5),
    onTertiaryContainer = Color(0xFF4A148C),
    error            = Color(0xFFC62828),
    onError          = Color(0xFFFFFFFF),
    errorContainer   = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C),
    background       = Color(0xFFF5F6FA),
    onBackground     = Color(0xFF1A1F3C),
    surface          = Color(0xFFFFFFFF),
    onSurface        = Color(0xFF1A1F3C),
    surfaceVariant   = Color(0xFFE8EAF6),
    onSurfaceVariant = Color(0xFF3949AB),
    outline          = Color(0xFFB0BEC5),
    outlineVariant   = Color(0xFFE0E4EF),
    inverseSurface   = Color(0xFF1A1F3C),
    inverseOnSurface = Color(0xFFE8EAF6),
    inversePrimary   = Color(0xFF9FA8DA),
    surfaceTint      = Color(0xFF3949AB),
)

// Guardian color palette — dark mode
private val GuardianDarkColors = darkColorScheme(
    primary          = Color(0xFF9FA8DA),   // indigo-300
    onPrimary        = Color(0xFF1A237E),
    primaryContainer = Color(0xFF283593),
    onPrimaryContainer = Color(0xFFE8EAF6),
    secondary        = Color(0xFF80CBC4),   // teal-200
    onSecondary      = Color(0xFF00352F),
    secondaryContainer = Color(0xFF00574F),
    onSecondaryContainer = Color(0xFFE0F2F1),
    tertiary         = Color(0xFFCE93D8),   // purple-200
    onTertiary       = Color(0xFF4A148C),
    tertiaryContainer = Color(0xFF6A1B9A),
    onTertiaryContainer = Color(0xFFF3E5F5),
    error            = Color(0xFFEF9A9A),
    onError          = Color(0xFFB71C1C),
    errorContainer   = Color(0xFFC62828),
    onErrorContainer = Color(0xFFFFEBEE),
    background       = Color(0xFF0F1228),
    onBackground     = Color(0xFFE8EAF6),
    surface          = Color(0xFF1A1F3C),
    onSurface        = Color(0xFFE8EAF6),
    surfaceVariant   = Color(0xFF283593),
    onSurfaceVariant = Color(0xFF9FA8DA),
    outline          = Color(0xFF37474F),
    outlineVariant   = Color(0xFF283593),
    inverseSurface   = Color(0xFFE8EAF6),
    inverseOnSurface = Color(0xFF1A1F3C),
    inversePrimary   = Color(0xFF3949AB),
    surfaceTint      = Color(0xFF9FA8DA),
)

val GuardianTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 45.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 1.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp
    ),
)

val GuardianShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
)

@Composable
fun FocusTimerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) GuardianDarkColors else GuardianLightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GuardianTypography,
        shapes = GuardianShapes,
        content = content
    )
}
