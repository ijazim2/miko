package com.kittys.premium.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.kittys.premium.R
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography
import com.airbnb.lottie.compose.*

// ═══════════════════════════════════════════════════════
//   NEUMORPHIC MODIFIER EXTENSIONS
// ═══════════════════════════════════════════════════════

fun Modifier.neuRaised(
    cornerRadius: Dp = 22.dp,
    shadowOffset: Dp = 8.dp,
    lightColor: Color = MikoColors.NeuShadowLight,
    darkColor: Color  = MikoColors.NeuShadowDark,
    surfaceColor: Color = MikoColors.NeuSurface
): Modifier = this
    .drawBehind {
        val radiusPx  = cornerRadius.toPx()
        val offsetPx  = shadowOffset.toPx()
        drawRoundRect(
            color = darkColor,
            topLeft = Offset(offsetPx, offsetPx),
            size = size,
            cornerRadius = CornerRadius(radiusPx),
            alpha = 0.75f
        )
        drawRoundRect(
            color = lightColor,
            topLeft = Offset(-offsetPx * 0.6f, -offsetPx * 0.6f),
            size = size,
            cornerRadius = CornerRadius(radiusPx),
            alpha = 0.9f
        )
    }
    .background(surfaceColor, RoundedCornerShape(cornerRadius))

fun Modifier.neuInset(
    cornerRadius: Dp = 16.dp,
    shadowOffset: Dp = 4.dp,
    lightColor: Color = MikoColors.NeuShadowLight,
    darkColor: Color  = MikoColors.NeuShadowDark,
    bgColor: Color    = MikoColors.NeuBackground
): Modifier = this
    .drawBehind {
        val radiusPx = cornerRadius.toPx()
        val offsetPx = shadowOffset.toPx()
        drawRoundRect(
            color = darkColor,
            topLeft = Offset(-offsetPx * 0.5f, -offsetPx * 0.5f),
            size = size,
            cornerRadius = CornerRadius(radiusPx),
            alpha = 0.5f
        )
        drawRoundRect(
            color = lightColor,
            topLeft = Offset(offsetPx * 0.5f, offsetPx * 0.5f),
            size = size,
            cornerRadius = CornerRadius(radiusPx),
            alpha = 0.8f
        )
    }
    .background(bgColor, RoundedCornerShape(cornerRadius))

fun Modifier.neuGlow(
    color: Color = MikoColors.PrimaryStart,
    cornerRadius: Dp = 22.dp,
    glowRadius: Dp = 18.dp,
    alpha: Float = 0.5f
): Modifier = this.shadow(
    elevation = glowRadius,
    shape = RoundedCornerShape(cornerRadius),
    ambientColor = color.copy(alpha = alpha),
    spotColor = color.copy(alpha = alpha),
    clip = false
)

fun Modifier.neuTable(
    cornerRadius: Dp = 18.dp,
    borderColor: Color = MikoColors.BorderLight,
    surfaceColor: Color = MikoColors.Surface
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .background(surfaceColor, RoundedCornerShape(cornerRadius))
    .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))

fun gradientBrush(
    start: Color = MikoColors.BluePrimary,
    end: Color   = MikoColors.AquaAccent,
    angle: Float = 135f
): Brush = Brush.linearGradient(
    colors = listOf(start, end),
    start = Offset(0f, 0f),
    end = Offset(
        1000f * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat(),
        1000f * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
    )
)

// ═══════════════════════════════════════════════════════
//   GRADIENT BUTTON
// ═══════════════════════════════════════════════════════

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    gradient: List<Color> = MikoColors.GradientButton,
    icon: Int? = null
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "button_scale"
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
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled
            ) {
                pressed = true
                onClick()
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MikoTypography.labelLarge.copy(
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
            )
        }
    }
    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(120)
            pressed = false
        }
    }
}

// ═══════════════════════════════════════════════════════
//   OUTLINED BUTTON
// ═══════════════════════════════════════════════════════

@Composable
fun OutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(54.dp)
            .neuRaised(cornerRadius = 50.dp, shadowOffset = 5.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Text(
            text = text,
            style = MikoTypography.labelLarge.copy(
                color = MikoColors.PrimaryEnd,
                fontSize = 15.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )
        )
    }
}

// ═══════════════════════════════════════════════════════
//   NEUMORPHIC TEXT FIELD  (with Lottie eye toggle)
// ═══════════════════════════════════════════════════════

@Composable
fun MikoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(56.dp)
            .neuInset(cornerRadius = 16.dp, shadowOffset = 4.dp)
            .padding(horizontal = 16.dp)
    ) {
        if (leadingIcon != null) {
            Icon(
                painter = painterResource(leadingIcon),
                contentDescription = null,
                tint = MikoColors.TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MikoTypography.bodyLarge.copy(color = MikoColors.TextPrimary),
            visualTransformation = when {
                isPassword && !passwordVisible -> PasswordVisualTransformation()
                else -> VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            keyboardActions = KeyboardActions(onAny = { onImeAction() }),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(hint, style = MikoTypography.bodyLarge.copy(color = MikoColors.TextMuted))
                }
                inner()
            }
        )

        // Password toggle with Lottie ic_eye animation
        if (isPassword) {
            val eyeComposition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.ic_eye)
            )
            val eyeProgress by animateFloatAsState(
                targetValue = if (passwordVisible) 0.5f else 0f,
                label = "eye_toggle"
            )
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                LottieAnimation(
                    composition = eyeComposition,
                    progress = { eyeProgress },
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════
//   NEUMORPHIC CARD
// ═══════════════════════════════════════════════════════

@Composable
fun NeuCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    shadowOffset: Dp = 8.dp,
    padding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .neuRaised(cornerRadius = cornerRadius, shadowOffset = shadowOffset)
            .padding(padding),
        content = content
    )
}

// ═══════════════════════════════════════════════════════
//   TOP APP BAR  (with Lottie ic_arrow_left back button)
// ═══════════════════════════════════════════════════════

@Composable
fun MikoTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MikoColors.NeuBackground)
            .padding(horizontal = 20.dp)
    ) {
        if (onBackClick != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .neuRaised(cornerRadius = 14.dp, shadowOffset = 5.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onBackClick() }
            ) {
                val backComposition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.ic_arrowleft)
                )
                LottieAnimation(
                    composition = backComposition,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
        }
        Text(
            text = title,
            style = MikoTypography.headlineSmall,
            color = MikoColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Row(content = actions)
    }
}

// ═══════════════════════════════════════════════════════
//   SECTION HEADER
// ═══════════════════════════════════════════════════════

@Composable
fun SectionHeader(
    title: String,
    actionText: String = "See All",
    onActionClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
    ) {
        Text(title, style = MikoTypography.titleLarge, color = MikoColors.TextPrimary)
        Text(
            actionText,
            style = MikoTypography.labelMedium.copy(color = MikoColors.PrimaryEnd),
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onActionClick() }
        )
    }
}

// ═══════════════════════════════════════════════════════
//   BADGE BOX
// ═══════════════════════════════════════════════════════

@Composable
fun BadgeBox(
    count: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()
        if (count > 0) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp)
                    .background(MikoColors.BadgeRed, CircleShape)
                    .offset(x = 4.dp, y = (-4).dp)
            ) {
                Text(
                    if (count > 99) "99+" else count.toString(),
                    style = MikoTypography.labelSmall.copy(color = Color.White, fontSize = 9.sp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════
//   STAR RATING
// ═══════════════════════════════════════════════════════

@Composable
fun StarRating(
    rating: Float,
    modifier: Modifier = Modifier,
    starSize: Dp = 14.dp
) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            val filled = index < rating.toInt()
            Text(
                if (filled) "★" else "☆",
                color = if (filled) MikoColors.StarYellow else MikoColors.TextMuted,
                fontSize = starSize.value.sp
            )
        }
    }
}

// ═══════════════════════════════════════════════════════
//   PRICE TAG
// ═══════════════════════════════════════════════════════

@Composable
fun PriceTag(
    price: Int,
    originalPrice: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.Bottom, modifier = modifier) {
        Text(
            "LKR $price",
            style = MikoTypography.titleMedium.copy(
                brush = Brush.linearGradient(MikoColors.GradientPrimary),
                fontSize = 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        )
        if (originalPrice != null) {
            Spacer(Modifier.width(6.dp))
            Text(
                "LKR $originalPrice",
                style = MikoTypography.bodySmall.copy(
                    color = MikoColors.TextMuted,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                )
            )
        }
    }
}

// ═══════════════════════════════════════════════════════
//   CATEGORY CHIP
// ═══════════════════════════════════════════════════════

@Composable
fun CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(50.dp))
            .then(
                if (selected) Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                else Modifier.neuRaised(cornerRadius = 50.dp, shadowOffset = 4.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 18.dp)
    ) {
        Text(
            text,
            style = MikoTypography.labelMedium.copy(
                color = if (selected) Color.White else MikoColors.TextSecondary,
                fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.SemiBold
                else androidx.compose.ui.text.font.FontWeight.Normal
            )
        )
    }
}

// ═══════════════════════════════════════════════════════
//   SHIMMER BOX
// ═══════════════════════════════════════════════════════

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(MikoColors.NeuShadowDark.copy(alpha = shimmerAlpha))
    )
}