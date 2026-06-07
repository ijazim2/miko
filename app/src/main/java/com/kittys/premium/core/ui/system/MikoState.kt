package com.kittys.premium.core.ui.system

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ════════════════════════════════════════════════════════════════
//   MIKO — LIVING MASCOT STATE SYSTEM
//
//   Miko is not artwork. Miko is the interface.
//   The whole app drives one shared state; the nav-bar mascot
//   (and the AI surface) renders whatever state is active.
// ════════════════════════════════════════════════════════════════

sealed class MikoState(val label: String) {
    /** Resting. Slow blink, gentle breathing. The default. */
    data object Idle : MikoState("idle")

    /** A Gemini call is in flight. Eyes look up, soft glow ring. Replaces spinners. */
    data object Thinking : MikoState("thinking")

    /** Browsing. Curious, head tracks the content. */
    data object Shopping : MikoState("shopping")

    /** Item added to cart / liked. Quick joyful bounce. */
    data object Happy : MikoState("happy")

    /** Payment held in escrow. Miko raises a shield. Calm, protective. */
    data object EscrowGuardian : MikoState("escrow_guardian")

    /** Order out for delivery. Miko in motion, looking ahead. */
    data object Tracking : MikoState("tracking")

    /** Order placed / delivery confirmed. Full confetti celebration. */
    data object Celebration : MikoState("celebration")

    /** Empty states. Miko curled up napping. */
    data object Sleeping : MikoState("sleeping")
}

@HiltViewModel
class MikoViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<MikoState>(MikoState.Idle)
    val state: StateFlow<MikoState> = _state.asStateFlow()

    /** Set a persistent state (e.g. EscrowGuardian while viewing a held order). */
    fun setState(state: MikoState) {
        _state.value = state
    }

    /**
     * Fire a transient reaction, then fall back to Idle (or a given resting state).
     * Use for momentary feedback like add-to-cart Happy or order Celebration.
     */
    fun react(
        state: MikoState,
        holdMs: Long = 1400,
        fallback: MikoState = MikoState.Idle
    ) {
        _state.value = state
        viewModelScope.launch {
            delay(holdMs)
            // Only fall back if nothing else changed it in the meantime.
            if (_state.value == state) _state.value = fallback
        }
    }

    // Convenience triggers the app calls at the right moments:
    fun onAddToCart()           = react(MikoState.Happy)
    fun onAiThinking()          = setState(MikoState.Thinking)
    fun onAiDone()              = setState(MikoState.Idle)
    fun onEnterEscrow()         = setState(MikoState.EscrowGuardian)
    fun onOutForDelivery()      = setState(MikoState.Tracking)
    fun onOrderPlaced()         = react(MikoState.Celebration, holdMs = 2200)
    fun onDeliveryConfirmed()   = react(MikoState.Celebration, holdMs = 2200)
    fun onEmptyScreen()         = setState(MikoState.Sleeping)
    fun onBrowse()              = setState(MikoState.Shopping)
    fun onLeaveScreen()         = setState(MikoState.Idle)
}
