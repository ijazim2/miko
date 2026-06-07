package com.kittys.premium.core.common

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════
//   SHARED ANIMATION SPECS
//   Import and reuse across all screens for consistency
// ═══════════════════════════════════════════════════════

object Animations {

    // ── Spring specs ─────────────────────────────────
    val springBouncy: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness    = Spring.StiffnessMedium
    )

    val springSmooth: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness    = Spring.StiffnessLow
    )

    val springSnap: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness    = Spring.StiffnessMediumLow
    )

    // ── Tween specs ──────────────────────────────────
    val tweenFast  : TweenSpec<Float> = tween(durationMillis = 200)
    val tweenNormal: TweenSpec<Float> = tween(durationMillis = 320)
    val tweenSlow  : TweenSpec<Float> = tween(durationMillis = 500)

    // ── Enter/Exit transitions ───────────────────────

    /** Fade + slide up — used for screen entrances */
    val screenEnterTransition: EnterTransition =
        fadeIn(animationSpec = tween(320)) +
                slideInVertically(
                    initialOffsetY   = { it / 10 },
                    animationSpec    = tween(380)
                )

    val screenExitTransition: ExitTransition =
        fadeOut(animationSpec = tween(250))

    /** Slide in from right — for forward navigation */
    val slideInFromRight: EnterTransition =
        slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(320)) +
                fadeIn(animationSpec = tween(320))

    val slideOutToLeft: ExitTransition =
        slideOutHorizontally(targetOffsetX = { -it / 3 }, animationSpec = tween(280)) +
                fadeOut(animationSpec = tween(280))

    /** Slide in from bottom — for bottom sheets */
    val slideInFromBottom: EnterTransition =
        slideInVertically(initialOffsetY = { it }, animationSpec = tween(380))

    val slideOutToBottom: ExitTransition =
        slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300))

    /** Scale + fade — for dialogs and cards */
    val scaleIn: EnterTransition =
        scaleIn(initialScale = 0.85f, animationSpec = tween(320)) +
                fadeIn(animationSpec = tween(280))

    val scaleOut: ExitTransition =
        scaleOut(targetScale = 0.9f, animationSpec = tween(240)) +
                fadeOut(animationSpec = tween(240))
}

// ═══════════════════════════════════════════════════════
//   ANIMATED COMPOSABLES
//   Ready-to-use animated wrappers
// ═══════════════════════════════════════════════════════

/**
 * Animate an item entrance when it first enters composition.
 * Slides up + fades in.
 */
@Composable
fun AnimatedEntrance(
    modifier   : Modifier = Modifier,
    delayMs    : Int      = 0,
    content    : @Composable () -> Unit
) {
    val alpha  = remember { Animatable(0f) }
    val offset = remember { Animatable(30f) }

    LaunchedEffect(Unit) {
        if (delayMs > 0) delay(delayMs.toLong())
        launch { alpha.animateTo(1f, tween(380)) }
        launch { offset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
    }

    Box(
        modifier = modifier.graphicsLayer {
            this.alpha        = alpha.value
            this.translationY = offset.value
        }
    ) {
        content()
    }
}

/**
 * Pulsing animation — for loading badges, live indicators
 */
@Composable
fun rememberPulseScale(): State<Float> {
    val transition = rememberInfiniteTransition(label = "pulse")
    return transition.animateFloat(
        initialValue    = 1f,
        targetValue     = 1.12f,
        animationSpec   = infiniteRepeatable(
            animation  = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label           = "pulseScale"
    )
}

/**
 * Shimmer loading effect alpha
 */
@Composable
fun rememberShimmerAlpha(): State<Float> {
    val transition = rememberInfiniteTransition(label = "shimmer")
    return transition.animateFloat(
        initialValue  = 0.3f,
        targetValue   = 0.9f,
        animationSpec = infiniteRepeatable(
            animation  = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
}

/**
 * Bounce counter animation — for cart badge, notification count
 */
@Composable
fun rememberBounceScale(trigger: Int): State<Float> {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(trigger) {
        if (trigger > 0) {
            scale.animateTo(1.3f, spring(dampingRatio = Spring.DampingRatioLowBouncy))
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }
    return scale.asState()
}