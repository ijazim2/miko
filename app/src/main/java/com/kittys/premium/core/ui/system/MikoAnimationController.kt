package com.kittys.premium.core.ui.mascot

import androidx.compose.runtime.Immutable
import com.kittys.premium.core.ui.system.MikoState

// ════════════════════════════════════════════════════════════════
//   MIKO ANIMATION CONTROLLER
//
//   ONE source of truth for the numbers behind every state:
//   timing, scale range, rotation, glow, loop vs one-shot, and the
//   spring family to use. MikoMascot reads these so the spec and the
//   code never drift apart. Tuning Miko = editing this table only.
// ════════════════════════════════════════════════════════════════

enum class MikoSpring { Signature, Soft, Bouncy }

@Immutable
data class MikoAnimSpec(
    val scaleFrom: Float,
    val scaleTo: Float,
    val rotationFrom: Float = 0f,
    val rotationTo: Float = 0f,
    val periodMs: Int,            // one half-cycle duration for loops / pop duration for one-shots
    val loop: Boolean,            // true = infinite reverse; false = one-shot then hold/exit
    val glow: Boolean = false,    // pulsing primary glow ring (Thinking)
    val shield: Boolean = false,  // protective disc (EscrowGuardian)
    val confetti: Boolean = false,// burst (Celebration)
    val zzz: Boolean = false,     // floating sleep cue (Sleeping)
    val spring: MikoSpring = MikoSpring.Signature
)

object MikoAnimationController {

    fun specFor(state: MikoState): MikoAnimSpec = when (state) {

        // Calm continuous breathe. Barely-there life. The resting baseline.
        MikoState.Idle -> MikoAnimSpec(
            scaleFrom = 1.00f, scaleTo = 1.02f,
            periodMs = 1600, loop = true, spring = MikoSpring.Soft
        )

        // Curious — same breathe but the host may add head-tracking to scroll.
        MikoState.Shopping -> MikoAnimSpec(
            scaleFrom = 1.00f, scaleTo = 1.03f,
            periodMs = 1400, loop = true, spring = MikoSpring.Soft
        )

        // Faster pulse + glow ring. Used for every Gemini call (replaces spinners).
        MikoState.Thinking -> MikoAnimSpec(
            scaleFrom = 1.00f, scaleTo = 1.05f,
            periodMs = 700, loop = true, glow = true, spring = MikoSpring.Signature
        )

        // One-shot springy pop on add-to-cart / like. Falls back to Idle via react().
        MikoState.Happy -> MikoAnimSpec(
            scaleFrom = 1.00f, scaleTo = 1.18f,
            periodMs = 1, loop = false, spring = MikoSpring.Bouncy
        )

        // Protective. Steady scale + slow-pulsing shield disc behind Miko.
        MikoState.EscrowGuardian -> MikoAnimSpec(
            scaleFrom = 1.00f, scaleTo = 1.02f,
            periodMs = 1400, loop = true, shield = true, spring = MikoSpring.Soft
        )

        // In motion, leaning forward. Slightly quicker breathe = "on the way".
        MikoState.Tracking -> MikoAnimSpec(
            scaleFrom = 1.00f, scaleTo = 1.04f,
            periodMs = 1100, loop = true, spring = MikoSpring.Signature
        )

        // Full joy: wobble rotation + confetti burst. Transient (~2.2s) via react().
        MikoState.Celebration -> MikoAnimSpec(
            scaleFrom = 1.10f, scaleTo = 1.10f,
            rotationFrom = -8f, rotationTo = 8f,
            periodMs = 420, loop = true, confetti = true, spring = MikoSpring.Bouncy
        )

        // Slow rise/fall breathe + floating "z". Persists while a screen is empty.
        MikoState.Sleeping -> MikoAnimSpec(
            scaleFrom = 0.98f, scaleTo = 1.01f,
            periodMs = 2200, loop = true, zzz = true, spring = MikoSpring.Soft
        )
    }
}
