package com.kittys.premium.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kittys.premium.R
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Reusable image SLOTS (render real drawable if present,
//   else a clean neumorphic placeholder)
// ════════════════════════════════════════════════════════════════

@Composable
fun CharacterPlaceholder(
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    contentDescription: String? = "MIKO character",
    label: String = "Character"
) {
    val hasImage = imageExists("miko_character")

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        if (hasImage != null) {
            Image(
                painter = painterResource(hasImage),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            PlaceholderBox(
                size = size,
                cornerRadius = size / 6,
                label = label,
                subtitle = "res/drawable/miko_character.png"
            )
        }
    }
}

@Composable
fun LogoPlaceholder(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    contentDescription: String? = "MIKO logo"
) {
    val hasImage = imageExists("miko_logo")

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        if (hasImage != null) {
            Image(
                painter = painterResource(hasImage),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(MikoColors.GradientPrimary))
                    .neuGlow(
                        cornerRadius = size / 2,
                        color = MikoColors.AccentSoft,
                        alpha = 0.5f
                    )
            ) {
                Text(
                    text = "M",
                    style = MikoTypography.Display.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = (size.value * 0.55f).sp
                    )
                )
            }
        }
    }
}

@Composable
fun TextLogoPlaceholder(
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    contentDescription: String? = "MIKO wordmark"
) {
    val hasImage = imageExists("miko_text_logo")

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.height(height)
    ) {
        if (hasImage != null) {
            Image(
                painter = painterResource(hasImage),
                contentDescription = contentDescription,
                modifier = Modifier.height(height),
                contentScale = ContentScale.Fit
            )
        } else {
            Text(
                text = "MIKO",
                style = MikoTypography.Display.copy(
                    brush = Brush.linearGradient(MikoColors.GradientPrimary),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    fontSize = (height.value * 0.65f).sp
                )
            )
        }
    }
}

@Composable
private fun PlaceholderBox(
    size: Dp,
    cornerRadius: Dp,
    label: String,
    subtitle: String
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .neuRaised(
                cornerRadius = cornerRadius,
                shadowOffset = 8.dp,
                surfaceColor = MikoColors.PastelPink
            )
            .border(
                width = 1.5.dp,
                color = MikoColors.AccentLight,
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = label,
                style = MikoTypography.Title.copy(
                    brush = Brush.linearGradient(MikoColors.GradientPrimary),
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MikoTypography.Caption.copy(
                    color = MikoColors.TextMuted,
                    fontSize = 9.sp
                )
            )
        }
    }
}

@Composable
private fun imageExists(name: String): Int? {
    val context = androidx.compose.ui.platform.LocalContext.current
    return remember(name) {
        val id = context.resources.getIdentifier(name, "drawable", context.packageName)
        if (id != 0) id else null
    }
}