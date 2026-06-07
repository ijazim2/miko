package com.kittys.premium.core.ui.neu

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kittys.premium.core.ui.system.NeuElevation
import com.kittys.premium.core.ui.theme.MikoColors

// ════════════════════════════════════════════════════════════════
//   NEUMORPHISM ENGINE — real dual soft shadows, fixed top-left light.
//   Shadows are tints of the background, never black/white.
// ════════════════════════════════════════════════════════════════

private val Highlight = MikoColors.NeuShadowLight
private val Ambient = MikoColors.NeuShadowDark

fun Modifier.neuRaised(
    elevation: NeuElevation = NeuElevation.Level2,
    cornerRadius: Dp = elevation.cornerRadius,
    surfaceColor: Color = MikoColors.NeuSurface
): Modifier = this
    .drawBehind {
        val radius = CornerRadius(cornerRadius.toPx())
        val off = elevation.offset.toPx()
        val grow = elevation.blur.toPx() * 0.5f
        val passes = 4
        for (i in 1..passes) {
            val t = i / passes.toFloat()
            drawRoundRect(
                color = Ambient.copy(alpha = 0.10f * (1f - t) + 0.04f),
                topLeft = Offset(off * t + grow * t, off * t + grow * t),
                size = Size(size.width, size.height),
                cornerRadius = radius
            )
        }
        for (i in 1..passes) {
            val t = i / passes.toFloat()
            drawRoundRect(
                color = Highlight.copy(alpha = 0.55f * (1f - t) + 0.10f),
                topLeft = Offset(-(off * t + grow * t), -(off * t + grow * t)),
                size = Size(size.width, size.height),
                cornerRadius = radius
            )
        }
    }
    .background(surfaceColor, RoundedCornerShape(cornerRadius))

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
        for (i in 1..passes) {
            val t = i / passes.toFloat()
            drawRoundRect(
                color = Ambient.copy(alpha = 0.12f * (1f - t)),
                topLeft = Offset(-off * t, -off * t),
                size = Size(size.width + off * 2 * t, size.height + off * 2 * t),
                cornerRadius = radius
            )
        }
    }
