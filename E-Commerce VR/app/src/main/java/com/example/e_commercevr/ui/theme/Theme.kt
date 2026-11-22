package com.example.e_commercevr.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * AURA E-Commerce Design System Tokens
 * Strict Rules: NO GRADIENTS allowed. Solid colors and precise opacity only.
 */
object AuraColors {
    val Primary = Color(0xFF2B3467)         // Deep Navy (Teks utama, stroke, ikon)
    val Secondary = Color(0xFFBAD7E9)       // Soft Blue (Elemen dekoratif & shapes)
    val Background = Color(0xFFFCFFE7)      // Cream Off-White (Latar canvas utama)
    val Accent = Color(0xFFEB455F)          // Vibrant Crimson (Tombol utama, badge AR, diskon)

    // Glassmorphism Token Specifications
    val GlassFill = Color.White.copy(alpha = 0.15f)
    val GlassFillElevated = Color.White.copy(alpha = 0.20f)
    val GlassFillHeavy = Color.White.copy(alpha = 0.85f)
    val GlassStroke = Color.White.copy(alpha = 0.40f)
    val GlassStrokeFocused = Color.White.copy(alpha = 0.60f)
    val GlassShadowColor = Color(0xFF2B3467).copy(alpha = 0.08f)
}

object AuraShapes {
    val Small = RoundedCornerShape(8.dp)
    val Medium = RoundedCornerShape(16.dp)     // Tombol, chip kecil, input field
    val Large = RoundedCornerShape(24.dp)      // Kartu besar, bottom sheet, bottom bar
    val ExtraLarge = RoundedCornerShape(32.dp) // Modal utama, preview container
    val Pill = RoundedCornerShape(999.dp)      // Search bar, badge, tombol capsule
}

/**
 * Tipografi Plus Jakarta Sans
 */
val AuraTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        color = AuraColors.Primary
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        color = AuraColors.Primary
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = AuraColors.Primary.copy(alpha = 0.80f)
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = AuraColors.Primary.copy(alpha = 0.70f)
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = AuraColors.Primary.copy(alpha = 0.60f)
    )
)

private val LightColorScheme = lightColorScheme(
    primary = AuraColors.Primary,
    onPrimary = Color.White,
    primaryContainer = AuraColors.Secondary,
    onPrimaryContainer = AuraColors.Primary,
    secondary = AuraColors.Secondary,
    onSecondary = AuraColors.Primary,
    background = AuraColors.Background,
    onBackground = AuraColors.Primary,
    surface = AuraColors.Background,
    onSurface = AuraColors.Primary,
    error = AuraColors.Accent,
    onError = Color.White
)

@Composable
fun AuraTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AuraTypography,
        content = content
    )
}
