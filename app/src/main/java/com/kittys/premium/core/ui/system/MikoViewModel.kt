package com.kittys.premium.core.ui.system

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ════════════════════════════════════════════════════════════════
//   MIKO — VIEWMODEL
//   Hoist ONE instance at the nav-host so the nav mascot and every
//   screen reaction share the same on-screen Miko.
// ════════════════════════════════════════════════════════════════

data class MikoUiState(
    val state: MikoState = MikoState.Idle,
    val hasNotification: Boolean = false,
    val pendingSuggestion: Boolean = false
)

@HiltViewModel
class MikoViewModel @Inject constructor() : ViewModel() {

    private val _ui = MutableStateFlow(MikoUiState())
    val ui: StateFlow<MikoUiState> = _ui.asStateFlow()

    // Guards transient reactions: a newer set cancels an older fallback.
    private var reactionToken = 0

    fun setState(state: MikoState) {
        reactionToken++ // any persistent set invalidates pending fallbacks
        _ui.update { it.copy(state = state) }
    }

    fun react(state: MikoState, holdMs: Long = 1400, fallback: MikoState = MikoState.Idle) {
        val token = ++reactionToken
        _ui.update { it.copy(state = state) }
        viewModelScope.launch {
            delay(holdMs)
            if (token == reactionToken) {
                _ui.update { it.copy(state = fallback) }
            }
        }
    }

    fun setNotification(active: Boolean) = _ui.update { it.copy(hasNotification = active) }
    fun setPendingSuggestion(active: Boolean) = _ui.update { it.copy(pendingSuggestion = active) }

    // Convenience triggers
    fun onAddToCart() = react(MikoState.Happy)
    fun onLike() = react(MikoState.Happy, holdMs = 900)
    fun onAiThinking() = setState(MikoState.Thinking)
    fun onAiDone() = setState(MikoState.Idle)
    fun onEnterEscrow() = setState(MikoState.EscrowGuardian)
    fun onOutForDelivery() = setState(MikoState.Tracking)
    fun onOrderPlaced() = react(MikoState.Celebration, holdMs = 2200)
    fun onDeliveryConfirmed() = react(MikoState.Celebration, holdMs = 2200)
    fun onEmptyScreen() = setState(MikoState.Sleeping)
    fun onBrowse() = setState(MikoState.Shopping)
    fun onLeaveScreen() = setState(MikoState.Idle)
}
