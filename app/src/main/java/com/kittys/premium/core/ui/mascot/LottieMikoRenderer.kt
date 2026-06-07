package com.kittys.premium.core.ui.mascot

// ════════════════════════════════════════════════════════════════
//   LOTTIE RENDERER  (future drop-in)
//
//   When you have Miko .json animations in res/raw, uncomment this
//   class, then in MikoHost.rememberMikoRenderer() return
//   LottieMikoRenderer(MikoLottieSet(...)) instead of the static one.
//
//   Nothing else in the app changes — MikoMascot applies the same
//   motion/effects over it. Kept commented so the project compiles
//   today without the .json files present.
// ════════════════════════════════════════════════════════════════

/*
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kittys.premium.core.ui.system.MikoMotion
import com.kittys.premium.core.ui.system.MikoState

class LottieMikoRenderer(
    private val lottie: MikoLottieSet
) : MikoRenderer {

    @Composable
    override fun Render(state: MikoState, modifier: Modifier) {
        Crossfade(
            targetState = lottie.rawFor(state),
            animationSpec = MikoMotion.fade(MikoMotion.NORMAL),
            label = "miko_lottie_crossfade"
        ) { rawRes ->
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(rawRes)
            )
            // Transient states (happy/celebration) play once; loops otherwise.
            val iterations = when (state) {
                MikoState.Happy, MikoState.Celebration -> 1
                else -> LottieConstants.IterateForever
            }
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = iterations
            )
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = modifier.fillMaxSize()
            )
        }
    }
}
*/
