package com.kittys.premium.core.ui.system

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kittys.premium.core.ui.theme.MikoColors

// ════════════════════════════════════════════════════════════════
//   MIKO — REAL NEUMORPHISM ENGINE
//
//   Mental model: ONE continuous surface. Light (fixed top-left)
//   carves depth into it. Shadows are TINTS of the background,
//   never black/white. Soft + large blur = luxury silicone.
//
//   Replaces the old neuRaised / neuInset. Use this everywhere.
// ════════════════════════════════════════════════════════════════

// Fixed light source: top-left. Highlight is a warm near-white,
// ambient shadow is a soft lavender tint of the white background.
private val NeuHighlight = MikoColors.NeuShadowLight          // ~#FFFFFF warm
private val NeuShadow    = MikoColors.NeuShadowDark           // ~#E8DFFF lavender

/**
 * RAISED neumorphic surface — element swells out of the background.
 * Soft dual shadow obeying a fixed top-left light source.
 */
fun Modifier.neuRaised(
    elevation: NeuElevation = NeuElevation.Level2,
    cornerRadius: Dp = elevation.cornerRadius,
    surfaceColor: Color = MikoColors.NeuSurface
): Modifier = this
    .drawBehind {
        val radius = CornerRadius(cornerRadius.toPx())
        val off    = elevation.offset.toPx()
        val grow   = elevation.blur.toPx() * 0.5f  // simulate blur by spreading + fading

        // Ambient shadow — bottom-right, soft, low opacity, lavender tint.
        // Layered passes fake a gaussian blur (Compose draw has no blur primitive here).
        val passes = 4
        for (i in 1..passes) {
            val t = i / passes.toFloat()
            drawRoundRect(
                color = NeuShadow.copy(alpha = 0.10f * (1f - t) + 0.04f),
                topLeft = Offset(off * t + grow * t, off * t + grow * t),
                size = Size(size.width, size.height),
                cornerRadius = radius
            )
        }
        // Highlight — top-left, warm near-white.
        for (i in 1..passes) {
            val t = i / passes.toFloat()
            drawRoundRect(
                color = NeuHighlight.copy(alpha = 0.55f * (1f - t) + 0.10f),
                topLeft = Offset(-(off * t + grow * t), -(off * t + grow * t)),
                size = Size(size.width, size.height),
                cornerRadius = radius
            )
        }
    }
    .background(surfaceColor, RoundedCornerShape(cornerRadius))

/**
 * INSET neumorphic surface — element pressed INTO the background.
 * Used for input fields, search bars, and the pressed state of raised elements.
 */
fun Modifier.neuInset(
    cornerRadius: Dp = NeuElevation.Level1.cornerRadius,
    surfaceColor: Color = MikoColors.NeuBackground
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .background(surfaceColor, RoundedCornerShape(cornerRadius))
    .drawBehind {
        val radius = CornerRadius(cornerRadius.toPx())
        val off = 4.dp.toPx()
        val passes = 4
        // Inner shadow top-left (dark) + inner highlight bottom-right (light)
        for (i in 1..passes) {
            val t = i / passes.toFloat()
            drawRoundRect(
                color = NeuShadow.copy(alpha = 0.12f * (1f - t)),
                topLeft = Offset(-off * t, -off * t),
                size = Size(size.width + off * 2 * t, size.height + off * 2 * t),
                cornerRadius = radius
            )
        }
    }

/**
 * THE touch language: a raised element that presses INTO the surface when
 * touched and settles back on release. Drop-in clickable replacement.
 *
 * Usage:
 *   Box(Modifier.neuPressable(NeuElevation.Level2) { onClick() }) { ... }
 */
@Composable
fun Modifier.neuPressable(
    elevation: NeuElevation = NeuElevation.Level2,
    cornerRadius: Dp = elevation.cornerRadius,
    surfaceColor: Color = MikoColors.NeuSurface,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    // When pressed, collapse the elevation toward 0 → it sinks in.
    val animOffset by animateDpAsState(
        targetValue = if (pressed) 0.dp else elevation.offset,
        animationSpec = MikoMotion.spring(),
        label = "neu_press_offset"
    )

    val dynamicElevation = when {
        animOffset <= 1.dp -> null              // fully pressed → render as inset
        else -> elevation
    }

    return this
        .then(
            if (dynamicElevation == null)
                Modifier.neuInset(cornerRadius, surfaceColor)
            else
                Modifier.neuRaised(elevation, cornerRadius, surfaceColor)
        )
        .clip(RoundedCornerShape(cornerRadius))
        .indication(interaction, null)
        .then(
            Modifier.pressClickable(interaction, enabled, onClick)
        )
}

// Small helper so we can attach press tracking + click in one place.
private fun Modifier.pressClickable(
    interaction: MutableInteractionSource,
    enabled: Boolean,
    onClick: () -> Unit
): Modifier = this.then(
    androidx.compose.foundation.clickable(
        interactionSource = interaction,
        indication = null,
        enabled = enabled
    ) { onClick() }
)
