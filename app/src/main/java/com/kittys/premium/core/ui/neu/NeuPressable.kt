package com.kittys.premium.core.ui.neu

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kittys.premium.core.ui.system.MikoMotion
import com.kittys.premium.core.ui.system.NeuElevation
import com.kittys.premium.core.ui.theme.MikoColors

// ════════════════════════════════════════════════════════════════
//   NEU PRESSABLE — raised surface that sinks INTO the bg on touch
//   and springs back on release. The touch language.
// ════════════════════════════════════════════════════════════════

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

    val animOffset by animateDpAsState(
        targetValue = if (pressed) 0.dp else elevation.offset,
        animationSpec = MikoMotion.spring(),
        label = "neu_press"
    )

    return this
        .then(
            if (animOffset <= 1.dp) Modifier.neuInset(cornerRadius, surfaceColor)
            else Modifier.neuRaised(elevation, cornerRadius, surfaceColor)
        )
        .clip(RoundedCornerShape(cornerRadius))
        .clickable(
            interactionSource = interaction,
            indication = null,
            enabled = enabled
        ) { onClick() }
}
