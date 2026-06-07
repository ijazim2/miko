package com.kittys.premium.core.ui.mascot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kittys.premium.core.ui.system.MikoMotion
import com.kittys.premium.core.ui.system.MikoState
import com.kittys.premium.core.ui.theme.MikoColors

// ════════════════════════════════════════════════════════════════
//   MIKO MASCOT — the living composable.
//
//   ONE entry point for the whole app. Give it a state + a renderer;
//   it applies the right Compose-driven motion (breathe, bounce, glow,
//   spin, shield, confetti, zzz) ON TOP of the renderer's artwork.
//
//   Because motion lives here (not in the renderer), switching to
//   Lottie/Rive later keeps every animation behavior identical.
// ════════════════════════════════════════════════════════════════

@Composable
fun MikoMascot(
    state: MikoState,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    renderer: MikoRenderer
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // ── Behind-art effects (glow ring, shield) ──
        StateBackdrop(state, size)

        // ── The artwork, transformed by per-state motion ──
        val motion = rememberMikoMotion(state)
        renderer.Render(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .scale(motion.scale)
                .rotate(motion.rotation)
        )

        // ── In-front effects (sparkles, confetti, zzz) ──
        StateForeground(state, size)
    }
}

// Holds the live transform values for the current state.
private data class MikoMotionValues(val scale: Float, val rotation: Float)

@Composable
private fun rememberMikoMotion(state: MikoState): MikoMotionValues {
    val infinite = rememberInfiniteTransition(label = "miko_infinite")

    // Idle / Shopping / Guardian / Tracking / Sleeping → gentle continuous breathe.
    val breathe by infinite.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600), repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    // Thinking → slightly faster pulse.
    val pulse by infinite.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(700), repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Celebration → continuous wobble rotation.
    val wobble by infinite.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(420), repeatMode = RepeatMode.Reverse
        ),
        label = "wobble"
    )

    // Happy → a one-shot springy pop (re-keyed each time state becomes Happy).
    val happyPop by animateFloatAsState(
        targetValue = if (state == MikoState.Happy) 1.18f else 1f,
        animationSpec = MikoMotion.springBouncy(),
        label = "happy_pop"
    )

    return when (state) {
        MikoState.Idle,
        MikoState.Shopping,
        MikoState.EscrowGuardian,
        MikoState.Tracking      -> MikoMotionValues(scale = breathe, rotation = 0f)

        MikoState.Sleeping      -> MikoMotionValues(scale = breathe, rotation = 0f) // slow breathe = sleeping rise/fall

        MikoState.Thinking      -> MikoMotionValues(scale = pulse, rotation = 0f)

        MikoState.Happy         -> MikoMotionValues(scale = happyPop, rotation = 0f)

        MikoState.Celebration   -> MikoMotionValues(scale = 1.1f, rotation = wobble)
    }
}

// ── Effects rendered BEHIND the artwork ──
@Composable
private fun StateBackdrop(state: MikoState, size: Dp) {
    // Thinking: soft pulsing glow ring.
    AnimatedVisibility(
        visible = state == MikoState.Thinking,
        enter = fadeIn(), exit = fadeOut()
    ) {
        val infinite = rememberInfiniteTransition(label = "glow")
        val glow by infinite.animateFloat(
            initialValue = 0.25f, targetValue = 0.6f,
            animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
            label = "glow_alpha"
        )
        Box(
            Modifier
                .size(size)
                .clip(CircleShape)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                MikoColors.PrimaryStart.copy(alpha = glow),
                                MikoColors.PrimaryStart.copy(alpha = 0f)
                            )
                        )
                    )
                }
        )
    }

    // EscrowGuardian: a soft shield disc that slow-pulses behind Miko.
    AnimatedVisibility(
        visible = state == MikoState.EscrowGuardian,
        enter = scaleIn(MikoMotion.springSoft()) + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        val infinite = rememberInfiniteTransition(label = "shield")
        val s by infinite.animateFloat(
            initialValue = 1.0f, targetValue = 1.08f,
            animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
            label = "shield_pulse"
        )
        Box(
            Modifier
                .size(size)
                .scale(s)
                .clip(CircleShape)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                MikoColors.Success.copy(alpha = 0.22f),
                                MikoColors.Success.copy(alpha = 0.04f)
                            )
                        )
                    )
                }
        )
    }
}

// ── Effects rendered IN FRONT of the artwork ──
@Composable
private fun StateForeground(state: MikoState, size: Dp) {
    // Celebration: lightweight confetti burst (cheap — a few drawn dots).
    AnimatedVisibility(
        visible = state == MikoState.Celebration,
        enter = fadeIn(), exit = fadeOut()
    ) {
        val infinite = rememberInfiniteTransition(label = "confetti")
        val t by infinite.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(900), RepeatMode.Restart),
            label = "confetti_t"
        )
        val colors = remember {
            listOf(
                MikoColors.PrimaryStart, MikoColors.AccentBright,
                MikoColors.Success, MikoColors.Warning, MikoColors.AccentSoft
            )
        }
        Box(
            Modifier
                .size(size)
                .drawBehind {
                    val n = 10
                    for (i in 0 until n) {
                        val angle = (i / n.toFloat()) * 6.2832f
                        val dist = (size.toPx() * 0.55f) * t
                        val cx = center.x + dist * kotlin.math.cos(angle)
                        val cy = center.y + dist * kotlin.math.sin(angle)
                        drawCircle(
                            color = colors[i % colors.size].copy(alpha = (1f - t)),
                            radius = 4f,
                            center = Offset(cx, cy)
                        )
                    }
                }
        )
    }

    // Sleeping: a tiny floating "z" cue (drawn as small fading dots rising).
    AnimatedVisibility(
        visible = state == MikoState.Sleeping,
        enter = fadeIn(), exit = fadeOut()
    ) {
        val infinite = rememberInfiniteTransition(label = "zzz")
        val rise by infinite.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Restart),
            label = "zzz_rise"
        )
        Box(
            Modifier
                .size(size)
                .drawBehind {
                    val x = center.x + size.toPx() * 0.32f
                    val baseY = center.y - size.toPx() * 0.30f
                    drawCircle(
                        color = MikoColors.TextMuted.copy(alpha = (1f - rise) * 0.7f),
                        radius = 3f + rise * 3f,
                        center = Offset(x, baseY - rise * size.toPx() * 0.4f)
                    )
                }
        )
    }
}
