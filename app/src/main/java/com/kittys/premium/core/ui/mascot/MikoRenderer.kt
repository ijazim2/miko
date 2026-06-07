package com.kittys.premium.core.ui.mascot

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kittys.premium.core.ui.system.MikoState

// ════════════════════════════════════════════════════════════════
//   MIKO — RENDERER ABSTRACTION
//
//   The rest of the app only ever talks to `MikoMascot`. HOW Miko is
//   drawn (static PNG today, Lottie later, Rive someday) is hidden
//   behind MikoRenderer. Swapping renderers changes ZERO screen code.
// ════════════════════════════════════════════════════════════════

/**
 * A renderer draws the *base artwork* for a given MikoState.
 * State-driven motion (bounce, breathe, glow, etc.) is applied OUTSIDE
 * the renderer by MikoMascot, so it works identically for every backend.
 *
 * Implementations:
 *   - StaticImageMikoRenderer  (now)
 *   - LottieMikoRenderer       (drop-in later)
 *   - RiveMikoRenderer         (future)
 */
interface MikoRenderer {
    @Composable
    fun Render(state: MikoState, modifier: Modifier)
}

/**
 * Maps each MikoState to its art asset. For the static renderer these are
 * drawables; for Lottie they'd be raw .json res ids. One mapping object
 * keeps "which file is which state" in a single place.
 */
data class MikoArtSet(
    @DrawableRes val idle: Int,
    @DrawableRes val thinking: Int = idle,
    @DrawableRes val shopping: Int = idle,
    @DrawableRes val happy: Int = idle,
    @DrawableRes val escrowGuardian: Int = idle,
    @DrawableRes val tracking: Int = idle,
    @DrawableRes val celebration: Int = idle,
    @DrawableRes val sleeping: Int = idle
) {
    @DrawableRes
    fun drawableFor(state: MikoState): Int = when (state) {
        MikoState.Idle           -> idle
        MikoState.Thinking       -> thinking
        MikoState.Shopping       -> shopping
        MikoState.Happy          -> happy
        MikoState.EscrowGuardian -> escrowGuardian
        MikoState.Tracking       -> tracking
        MikoState.Celebration    -> celebration
        MikoState.Sleeping       -> sleeping
    }
}

/**
 * Same idea for Lottie raw resources — used when you switch renderer later.
 * Unknown states fall back to idle so nothing ever crashes mid-migration.
 */
data class MikoLottieSet(
    @RawRes val idle: Int,
    @RawRes val thinking: Int = idle,
    @RawRes val shopping: Int = idle,
    @RawRes val happy: Int = idle,
    @RawRes val escrowGuardian: Int = idle,
    @RawRes val tracking: Int = idle,
    @RawRes val celebration: Int = idle,
    @RawRes val sleeping: Int = idle
) {
    @RawRes
    fun rawFor(state: MikoState): Int = when (state) {
        MikoState.Idle           -> idle
        MikoState.Thinking       -> thinking
        MikoState.Shopping       -> shopping
        MikoState.Happy          -> happy
        MikoState.EscrowGuardian -> escrowGuardian
        MikoState.Tracking       -> tracking
        MikoState.Celebration    -> celebration
        MikoState.Sleeping       -> sleeping
    }
}
