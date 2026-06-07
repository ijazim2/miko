package com.kittys.premium.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ════════════════════════════════════════════════════════════════
//   MIKO — Typography System
//   Using system font for now. Add Poppins font files later and
//   swap `FontFamily.Default` for the real families.
// ════════════════════════════════════════════════════════════════

// When you add fonts later, define families here. For now, system default:
val PoppinsRounded = FontFamily.Default
val Poppins        = FontFamily.Default
val DancingScript  = FontFamily.Default

object MikoTypography {

    // ── Custom MIKO names ──
    val Display = TextStyle(
        fontFamily = PoppinsRounded,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    )

    val Headline = TextStyle(
        fontFamily = PoppinsRounded,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    )

    val Title = TextStyle(
        fontFamily = PoppinsRounded,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    )

    val Subtitle = TextStyle(
        fontFamily = PoppinsRounded,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    )

    val Body = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )

    val Caption = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )

    val Button = TextStyle(
        fontFamily = PoppinsRounded,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp
    )

    val Tagline = TextStyle(
        fontFamily = DancingScript,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp
    )

    // ── Material3 aliases (so screens can use MikoTypography.titleSmall etc.) ──
    val displayLarge   = Display
    val displayMedium  = Display.copy(fontSize = 28.sp)
    val displaySmall   = Display.copy(fontSize = 24.sp)
    val headlineLarge  = Headline.copy(fontSize = 28.sp)
    val headlineMedium = Headline
    val headlineSmall  = Headline.copy(fontSize = 20.sp)
    val titleLarge     = Title.copy(fontSize = 20.sp)
    val titleMedium    = Title
    val titleSmall     = Subtitle
    val bodyLarge      = Body.copy(fontSize = 16.sp)
    val bodyMedium     = Body
    val bodySmall      = Caption
    val labelLarge     = Button
    val labelMedium    = Caption
    val labelSmall     = Caption.copy(fontSize = 11.sp)
    val Label          = Button  // some screens use MikoTypography.Label
}

// Material3 Typography (for MaterialTheme.typography)
val MikoMaterialTypography = Typography(
    displayLarge   = MikoTypography.displayLarge,
    displayMedium  = MikoTypography.displayMedium,
    displaySmall   = MikoTypography.displaySmall,
    headlineLarge  = MikoTypography.headlineLarge,
    headlineMedium = MikoTypography.headlineMedium,
    headlineSmall  = MikoTypography.headlineSmall,
    titleLarge     = MikoTypography.titleLarge,
    titleMedium    = MikoTypography.titleMedium,
    titleSmall     = MikoTypography.titleSmall,
    bodyLarge      = MikoTypography.bodyLarge,
    bodyMedium     = MikoTypography.bodyMedium,
    bodySmall      = MikoTypography.bodySmall,
    labelLarge     = MikoTypography.labelLarge,
    labelMedium    = MikoTypography.labelMedium,
    labelSmall     = MikoTypography.labelSmall
)
