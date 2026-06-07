package com.kittys.premium.core.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.kittys.premium.R
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO BRAND COMPONENTS
//   Reusable building blocks featuring the MIKO character
// ════════════════════════════════════════════════════════════════

/**
 * Brand header with MIKO character + name + tagline
 * Used on splash, login, signup, and onboarding screens
 */
@Composable
fun MikoBrandHeader(
    showCharacter: Boolean = true,
    showTagline: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        // Brand name
        Text(
            text = "MIKO",
            style = MikoTypography.Display.copy(
                color = MikoColors.Primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        )

        // Paw print indicator
        Text(
            text = "🐾",
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 2.dp)
        )

        if (showTagline) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.app_tagline),  // "Be Cute, Be You."
                style = MikoTypography.Tagline.copy(
                    color = MikoColors.TextSecondary
                )
            )
        }

        if (showCharacter) {
            Spacer(Modifier.height(20.dp))
            Image(
                painter = painterResource(R.drawable.miko_splash_logo),
                contentDescription = "MIKO character",
                modifier = Modifier
                    .size(220.dp)
            )
        }
    }
}

/**
 * Empty state with MIKO character
 * Use for empty cart, empty wishlist, no results
 */
@Composable
fun MikoEmptyState(
    title: String,
    subtitle: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.miko_character),
            contentDescription = null,
            modifier = Modifier.size(180.dp)
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = title,
            style = MikoTypography.Headline.copy(
                color = MikoColors.TextPrimary
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MikoTypography.Body.copy(
                color = MikoColors.TextSecondary,
                lineHeight = 22.sp
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(28.dp))
            MikoPrimaryButton(
                text = actionLabel,
                onClick = onAction,
                modifier = Modifier.width(200.dp)
            )
        }
    }
}

/**
 * Neumorphic card with MIKO styling — soft shadows, rounded
 */
@Composable
fun MikoCard(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MikoColors.Surface)
            .padding(padding)
    ) {
        content()
    }
}

/**
 * Pastel banner card — for promos, badges, callouts
 */
@Composable
fun MikoPastelBanner(
    emoji: String,
    title: String,
    subtitle: String,
    backgroundColor: Color = MikoColors.PastelLavender,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .let { m -> if (onClick != null) m.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() } else m }
            .padding(16.dp)
    ) {
        Text(emoji, fontSize = 32.sp)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = subtitle,
                style = MikoTypography.Caption.copy(
                    color = MikoColors.TextSecondary,
                    lineHeight = 18.sp
                )
            )
        }
    }
}

/**
 * MIKO logo + name horizontal (for top bars)
 */
@Composable
fun MikoLogoMark(
    modifier: Modifier = Modifier,
    showPaw: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = "MIKO",
            style = MikoTypography.Title.copy(
                color = MikoColors.Primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        )
        if (showPaw) {
            Spacer(Modifier.width(4.dp))
            Text("🐾", fontSize = 14.sp)
        }
    }
}