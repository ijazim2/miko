package com.kittys.premium.core.ui.mascot

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.kittys.premium.core.ui.system.MikoMotion
import com.kittys.premium.core.ui.system.MikoState

// ════════════════════════════════════════════════════════════════
//   STATIC IMAGE RENDERER  (use now)
//
//   Draws Miko from a single illustration (or per-state stills if you
//   have them). All life/motion comes from MikoMascot's Compose
//   animations layered on top — so even ONE image already feels alive.
// ════════════════════════════════════════════════════════════════

class StaticImageMikoRenderer(
    private val art: MikoArtSet
) : MikoRenderer {

    @Composable
    override fun Render(state: MikoState, modifier: Modifier) {
        // Crossfade between state artwork. If every state points to the same
        // drawable (one illustration), this is a no-op and costs nothing.
        Crossfade(
            targetState = art.drawableFor(state),
            animationSpec = MikoMotion.fade(MikoMotion.NORMAL),
            label = "miko_art_crossfade"
        ) { drawableRes ->
            Image(
                painter = painterResource(drawableRes),
                contentDescription = "Miko — ${state.label}",
                contentScale = ContentScale.Fit,
                modifier = modifier.fillMaxSize()
            )
        }
    }
}
