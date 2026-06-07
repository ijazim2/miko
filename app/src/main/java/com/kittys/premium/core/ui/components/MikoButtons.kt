package com.kittys.premium.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Buttons
//   Spec: gradient + soft glow + premium shadow + neumorphic depth
// ════════════════════════════════════════════════════════════════

@Composable
fun MikoPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: String? = null,
    trailingIcon: String? = "→",
    height: Dp = 56.dp,
    cornerRadius: Dp = 16.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(),
        label = "press_scale"
    )

    Box(modifier = modifier.scale(scale)) {

        if (enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(top = 4.dp)
                    .neuGlow(
                        cornerRadius = cornerRadius,
                        color = MikoColors.AccentSoft,
                        alpha = 0.5f
                    )
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    if (enabled)
                        Brush.linearGradient(MikoColors.GradientButton)
                    else
                        Brush.linearGradient(
                            listOf(MikoColors.BorderMedium, MikoColors.BorderMedium)
                        )
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled
                ) { onClick() }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (leadingIcon != null) {
                    Text(leadingIcon, color = Color.White, fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MikoTypography.Button.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                if (trailingIcon != null) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        trailingIcon,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MikoSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 56.dp,
    cornerRadius: Dp = 16.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(),
        label = "press_scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .scale(scale)
            .neuRaised(
                cornerRadius = cornerRadius,
                shadowOffset = 6.dp,
                surfaceColor = MikoColors.Surface
            )
            .height(height)
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(MikoColors.GradientPrimary),
                shape = RoundedCornerShape(cornerRadius)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
    ) {
        Text(
            text = text,
            style = MikoTypography.Button.copy(
                brush = Brush.linearGradient(MikoColors.GradientPrimary),
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun MikoTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MikoColors.PrimaryStart
) {
    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { onClick() }
    ) {
        Text(
            text = text,
            style = MikoTypography.Button.copy(color = color)
        )
    }
}

@Composable
fun MikoGradientFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    gradient: List<Color> = MikoColors.GradientAi,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.size(size)) {

        Box(
            modifier = Modifier
                .matchParentSize()
                .neuGlow(
                    cornerRadius = size / 2,
                    color = gradient.last(),
                    alpha = 0.7f
                )
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Brush.linearGradient(gradient))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClick() }
        ) {
            content()
        }
    }
}

@Composable
fun MikoIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    content: @Composable () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .neuRaised(
                cornerRadius = size / 2,
                shadowOffset = 4.dp,
                surfaceColor = MikoColors.Surface
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        content()
    }
}

@Composable
fun MikoChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .clip(RoundedCornerShape(50.dp))
                .background(Brush.linearGradient(MikoColors.GradientPrimary))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClick() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = text,
                style = MikoTypography.Label.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .neuRaised(
                    cornerRadius = 50.dp,
                    shadowOffset = 4.dp,
                    surfaceColor = MikoColors.Surface
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClick() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = text,
                style = MikoTypography.Label.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}