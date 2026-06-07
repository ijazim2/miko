package com.kittys.premium.features.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kittys.premium.core.ui.mascot.MikoHost
import com.kittys.premium.core.ui.neu.neuRaised
import com.kittys.premium.core.ui.system.MikoMotion
import com.kittys.premium.core.ui.system.MikoViewModel
import com.kittys.premium.core.ui.system.NeuElevation
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography
import kotlin.math.cos
import kotlin.math.sin

// ════════════════════════════════════════════════════════════════
//   AI STYLIST OVERLAY — flagship. Miko unfolds, cards orbit out.
//   Render at the TOP of your scaffold so it covers everything.
// ════════════════════════════════════════════════════════════════

data class OutfitCard(
    val id: String,
    val title: String,
    val priceLkr: Int,
    val emoji: String
)

@Composable
fun AIStylistOverlay(
    visible: Boolean,
    greeting: String,
    cards: List<OutfitCard>,
    mikoViewModel: MikoViewModel,
    onCardClick: (OutfitCard) -> Unit,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {

        val scrim = remember { Animatable(0f) }
        val mikoScale = remember { Animatable(1f) }
        var showBubble by remember { mutableStateOf(false) }
        var showCards by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            mikoViewModel.onAiThinking()
            scrim.animateTo(0.6f, tween(MikoMotion.SLOW))
        }
        LaunchedEffect(Unit) {
            mikoScale.animateTo(2.4f, MikoMotion.springSoft())
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(300); showBubble = true
            kotlinx.coroutines.delay(100); showCards = true
            kotlinx.coroutines.delay(400); mikoViewModel.onAiDone()
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(MikoColors.Overlay.copy(alpha = scrim.value))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() }
        ) {
            val anchorX = constraints.maxWidth * 0.5f
            val anchorY = constraints.maxHeight * 0.32f

            // Orbiting outfit cards
            if (showCards) {
                cards.take(4).forEachIndexed { index, card ->
                    OrbitingCard(
                        card = card,
                        index = index,
                        total = cards.size.coerceAtMost(4),
                        onClick = { onCardClick(card) }
                    )
                }
            }

            // Miko, grown and floating upper-center
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 90.dp)
            ) {
                MikoHost(
                    viewModel = mikoViewModel,
                    size = 72.dp,
                    modifier = Modifier.scale(mikoScale.value / 1f)
                )
            }

            // Greeting bubble
            AnimatedVisibility(
                visible = showBubble,
                enter = scaleIn(MikoMotion.springBouncy()) + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 200.dp)
            ) {
                Box(
                    modifier = Modifier
                        .neuRaised(elevation = NeuElevation.Level2, cornerRadius = 20.dp)
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    Text(
                        greeting,
                        style = MikoTypography.titleSmall.copy(
                            color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun OrbitingCard(
    card: OutfitCard,
    index: Int,
    total: Int,
    onClick: () -> Unit
) {
    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 60L)
        appear.animateTo(1f, MikoMotion.springBouncy())
    }

    // Fan the cards across a downward arc beneath Miko.
    val spread = 60f
    val baseAngle = 90f + (index - (total - 1) / 2f) * spread
    val rad = Math.toRadians(baseAngle.toDouble())
    val radius = 150f * appear.value
    val dx = (cos(rad) * radius).toInt()
    val dy = (sin(rad) * radius).toInt()

    Box(
        modifier = Modifier
            .padding(top = 230.dp)
            .offsetPx(dx, dy)
            .scale(appear.value)
            .width(130.dp)
            .height(180.dp)
            .neuRaised(elevation = NeuElevation.Level2, cornerRadius = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MikoColors.PastelPink)
            ) { Text(card.emoji, fontSize = 36.sp) }
            Text(
                card.title,
                style = MikoTypography.labelMedium.copy(color = MikoColors.TextPrimary),
                maxLines = 2,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                "LKR ${card.priceLkr}",
                style = MikoTypography.labelMedium.copy(
                    color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

// Pixel offset helper.
private fun Modifier.offsetPx(x: Int, y: Int): Modifier =
    this.then(Modifier.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(x, y)
        }
    })
