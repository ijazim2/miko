package com.kittys.premium.core.ui.theme
import androidx.compose.ui.graphics.Color

// ════════════════════════════════════════════════════════════════
//   MIKO — Final Brand Color System
//   Source: client design spec (FINAL)
//
//   Primary gradient : #FA5BD2  →  #BE0A88
//   Button gradient  : #6C0853  →  #FF6BCE
//   Background       : #FFFFFF
//   Neu shadow light : #FFFFFF
//   Neu shadow dark  : #E8DFFF
// ════════════════════════════════════════════════════════════════

object MikoColors {

    // ─── PRIMARY GRADIENT (FA5BD2 → BE0A88) ───
    val PrimaryStart   = Color(0xFFFA5BD2)   // Bright pink
    val PrimaryEnd     = Color(0xFFBE0A88)   // Deep magenta

    /** Alias for code paths that just need "the brand color" */
    val Primary        = PrimaryStart
    val PrimaryDark    = PrimaryEnd

    // ─── ACCENT COLORS ───
    val AccentLight    = Color(0xFFF8A2E8)   // Light pink accent
    val AccentBright   = Color(0xFFC532EA)   // Bright violet accent
    val AccentSoft     = Color(0xFFFF6BCE)   // Soft pink accent (also button-end)

    // ─── BUTTON GRADIENT (6C0853 → FF6BCE) ───
    val ButtonStart    = Color(0xFF6C0853)   // Deep plum
    val ButtonEnd      = Color(0xFFFF6BCE)   // Bright pink

    // ─── BACKGROUNDS ───
    val Background     = Color(0xFFFFFFFF)
    val Surface        = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFFBF5FF)   // Very faint lavender wash for inputs/chips
    val Overlay        = Color(0xCC1A0E2E)   // Modal scrim — dark plum

    // ─── NEUMORPHISM SHADOWS ───
    val NeuShadowLight = Color(0xFFFFFFFF)   // top-left highlight
    val NeuShadowDark  = Color(0xFFE8DFFF)   // bottom-right shadow (lavender tint)
    val NeuSurface     = Color(0xFFFFFFFF)
    val NeuBackground  = Color(0xFFFFFFFF)
    val NeuInsetLight  = Color(0xFFF6F1FF)   // for inset/pressed states

    // ─── TEXT ───
    val TextPrimary    = Color(0xFF1A0E2E)   // Deep plum-black (never pure #000)
    val TextSecondary  = Color(0xFF5A4870)   // Muted plum
    val TextMuted      = Color(0xFF9A8FAA)
    val TextDisabled   = Color(0xFFD4CCE0)
    val TextOnPrimary  = Color(0xFFFFFFFF)
    val TextOnGradient = Color(0xFFFFFFFF)

    // ─── STATUS COLORS ───
    val Success        = Color(0xFF22C58A)
    val SuccessSoft    = Color(0xFFD9F7E8)
    val Warning        = Color(0xFFFFB020)
    val WarningSoft    = Color(0xFFFFF1D6)
    val Error          = Color(0xFFFF4D6D)
    val ErrorSoft      = Color(0xFFFFE1E7)
    val Info           = Color(0xFF7B6BFF)
    val InfoSoft       = Color(0xFFE5E1FF)

    // ─── PASTELS (category tiles, banners, soft highlights) ───
    val PastelPink     = Color(0xFFFFE5F4)
    val PastelLavender = Color(0xFFEFE5FF)
    val PastelBlush    = Color(0xFFFFEEF7)
    val PastelLilac    = Color(0xFFF3E8FF)
    val PastelSky      = Color(0xFFE5EBFF)
    val PastelMint     = Color(0xFFDFF7E8)
    val PastelButter   = Color(0xFFFFF6D6)
    val PastelCoral    = Color(0xFFFFD9E4)
    val PastelPeach    = Color(0xFFFFE5D6)
    val PastelOrange   = Color(0xFFFFE5D6)
    val PastelYellow   = Color(0xFFFFF6D6)
    val PastelPurple   = Color(0xFFEFE5FF)
    val PastelBlue     = Color(0xFFE5EBFF)

    // ─── UI ELEMENTS ───
    val Divider        = Color(0xFFF1E8FF)
    val BorderLight    = Color(0xFFF1E8FF)
    val BorderMedium   = Color(0xFFD4C5EC)
    val BorderDark     = Color(0xFF9A8FAA)
    val StarYellow     = Color(0xFFFFB020)
    val SaleColor      = PrimaryEnd          // Use deep magenta for sale tags
    val PriceColor     = TextPrimary
    val NewBadge       = Success
    val LiveBadge      = Error
    val VerifiedBadge  = Info
    val Premium        = Color(0xFFFFB020)
    val Trust          = Success
    val Discount       = PrimaryEnd
    val FreeShipping   = Success
    val FastDelivery   = Info

    // ─── DARK MODE ───
    val DarkBackground = Color(0xFF120822)
    val DarkSurface    = Color(0xFF1F1230)
    val DarkShadow     = Color(0xFF0A0418)
    val DarkHighlight  = Color(0xFF2A1A40)

    // ─── GRADIENT LISTS (for Brush.linearGradient(...)) ───
    val GradientPrimary  = listOf(PrimaryStart, PrimaryEnd)        // FA5BD2 → BE0A88
    val GradientButton   = listOf(ButtonStart, ButtonEnd)          // 6C0853 → FF6BCE
    val GradientAccent   = listOf(AccentBright, AccentSoft)        // C532EA → FF6BCE
    val GradientAi       = listOf(ButtonStart, ButtonEnd)          // AI center button uses same as button
    val GradientGlowSoft = listOf(AccentLight, AccentSoft)         // for soft glow halos

    // ════════════════════════════════════════════════════════
    //   BACKWARD-COMPAT ALIASES — so existing Kitty code compiles
    //   These map old names → new MIKO values.
    // ════════════════════════════════════════════════════════
    val PinkPrimary      = PrimaryStart
    val PinkSecondary    = AccentSoft
    val PinkSoft         = PastelPink
    val PinkBlush        = PastelBlush
    val PinkCream        = PastelPink
    val OrangePrimary    = PrimaryStart
    val OrangeLight      = AccentSoft
    val OrangeDark       = PrimaryEnd
    val OrangeSoft       = PastelPink
    val BluePrimary      = PrimaryStart
    val NavyPrimary      = ButtonStart
    val NavySecondary    = TextPrimary
    val NavyLight        = TextSecondary
    val Coral            = AccentSoft
    val Rose             = PrimaryStart
    val Mauve            = PrimaryEnd
    val AquaAccent       = AccentLight
    val VioletAccent     = AccentBright
    val SkyBlue          = PastelSky
    val GradientPink     = GradientPrimary
    val GradientBlue     = GradientPrimary
    val GradientViolet   = listOf(AccentBright, PrimaryEnd)
    val GradientMagic    = GradientPrimary
    val GradientSunset   = GradientButton
    val GradientPremium  = listOf(ButtonStart, PrimaryEnd)
    val GradientSoft     = listOf(PastelPink, Surface)
    val SoftBlush        = PastelBlush
    val SoftPink         = PastelPink
    val SoftSky          = PastelSky
    val SoftButter       = PastelButter
    val SoftMint         = PastelMint
    val Pink             = PrimaryStart
    val White            = Color(0xFFFFFFFF)
    val PrimaryLight     = AccentLight
    val BadgeRed         = Error
}


