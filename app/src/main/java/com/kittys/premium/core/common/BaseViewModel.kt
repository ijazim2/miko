package com.kittys.premium.core.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════
//   MIKO — Base ViewModel
//   Extend for: loading state, error state, one-time events
// ═══════════════════════════════════════════════════════

abstract class BaseViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>(extraBufferCapacity = 8)
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    protected fun setLoading(loading: Boolean) { _isLoading.value = loading }
    protected fun setError(message: String?) { _error.value = message }
    protected fun clearError() { _error.value = null }

    protected fun emitEvent(event: UiEvent) {
        viewModelScope.launch { _events.emit(event) }
    }

    protected fun showMessage(message: String) = emitEvent(UiEvent.ShowMessage(message))
    protected fun navigateTo(route: String) = emitEvent(UiEvent.Navigate(route))

    protected suspend fun <T> execute(block: suspend () -> Result<T>): Result<T> {
        setLoading(true)
        clearError()
        return try {
            val result = block()
            if (result is Result.Error) setError(result.message)
            result
        } catch (e: Exception) {
            val msg = e.message ?: "Something went wrong"
            setError(msg)
            Result.Error(msg)
        } finally {
            setLoading(false)
        }
    }
}

sealed class UiEvent {
    data class ShowMessage(val message: String) : UiEvent()
    data class ShowError(val message: String) : UiEvent()
    data class Navigate(val route: String) : UiEvent()
    data class NavigateBack(val result: Boolean = true) : UiEvent()
    object ShowAuthRequired : UiEvent()
    object ScrollToTop : UiEvent()
}