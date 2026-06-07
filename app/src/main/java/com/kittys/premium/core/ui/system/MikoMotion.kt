package com.kittys.premium.core.ui.system

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ════════════════════════════════════════════════════════════════
//   MIKO — MOTION LANGUAGE
//   ONE spring used everywhere so the whole app feels consistent.
//   Soft, weighted, slightly springy — "marshmallow gravity".
// ════════════════════════════════════════════════════════════════

object MikoMotion {

    /** The signature spring. Use this for cards, buttons, nav, dialogs, Miko. */
    fun <T> spring(): AnimationSpec<T> = spring(
        dampingRatio = 0.75f,   // gentle settle, tiny overshoot
        stiffness    = 300f
    )

    /** A softer, slower spring for big surfaces (AI unfold, sheets). */
    fun <T> springSoft(): AnimationSpec<T> = spring(
        dampingRatio = 0.80f,
        stiffness    = 180f
    )

    /** A bouncier spring for celebratory/playful moments (add-to-cart, success). */
    fun <T> springBouncy(): AnimationSpec<T> = spring(
        dampingRatio = 0.55f,
        stiffness    = 320f
    )

    // ── Durations (for fades / blurs that aren't springs) ──
    const val DURATION_FAST   = 150   // press / release
    const val DURATION_NORMAL = 320   // standard transitions
    const val DURATION_SLOW   = 420   // AI unfold, scrim blur

    fun <T> fade(durationMs: Int = DURATION_NORMAL): AnimationSpec<T> =
        tween(durationMillis = durationMs)
}

// ════════════════════════════════════════════════════════════════
//   ELEVATION SYSTEM
//   Three levels. Higher level = larger, softer shadow = more important.
//   This IS the visual hierarchy.
// ════════════════════════════════════════════════════════════════

enum class NeuElevation(
    val offset: Dp,       // how far the shadows sit
    val blur: Dp,         // softness — bigger = more "silicone"
    val cornerRadius: Dp  // default radius for this level
) {
    /** Chips, list rows — barely raised. */
    Level1(offset = 4.dp,  blur = 8.dp,  cornerRadius = 14.dp),

    /** Cards, product tiles — medium. */
    Level2(offset = 7.dp,  blur = 16.dp, cornerRadius = 20.dp),

    /** Bottom nav, FABs, active dialogs — floating, prominent. */
    Level3(offset = 11.dp, blur = 26.dp, cornerRadius = 28.dp)
}

/** Whether a surface pops out (Raised) or is pressed in (Inset). */
enum class NeuSurface { Raised, Inset }
