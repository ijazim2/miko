package com.kittys.premium.core.ui.neu

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kittys.premium.core.ui.system.MikoMotion
import com.kittys.premium.core.ui.system.NeuElevation
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   NEU BUTTONS — primary gradient + secondary outline.
// ════════════════════════════════════════════════════════════════

@Composable
fun NeuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    gradient: List<Color> = MikoColors.GradientButton
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = MikoMotion.spring(),
        label = "btn_scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .scale(scale)
            .height(54.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(
                if (enabled) Brush.linearGradient(gradient)
                else Brush.linearGradient(listOf(MikoColors.TextMuted, MikoColors.TextMuted))
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled
            ) { onClick() }
    ) {
        Text(
            text = text,
            style = MikoTypography.labelLarge.copy(
                color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun NeuOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .neuPressable(
                elevation = NeuElevation.Level2,
                cornerRadius = 50.dp,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Text(
            text = text,
            style = MikoTypography.labelLarge.copy(
                color = if (enabled) MikoColors.PrimaryEnd else MikoColors.TextDisabled,
                fontSize = 15.sp, fontWeight = FontWeight.SemiBold
            )
        )
    }
}
