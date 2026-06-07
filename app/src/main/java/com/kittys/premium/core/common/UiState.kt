package com.kittys.premium.core.common

// ═══════════════════════════════════════════════════════
//   GENERIC UI STATE WRAPPER
//   Use when a screen maps 1:1 to a single data type
//
//   Usage example:
//   val uiState: StateFlow<UiState<List<Product>>> = ...
//
//   In Compose:
//   when (val s = uiState) {
//       is UiState.Loading -> LoadingScreen()
//       is UiState.Success -> ProductGrid(s.data)
//       is UiState.Error   -> ErrorScreen(s.message)
//       is UiState.Empty   -> EmptyState()
//   }
// ═══════════════════════════════════════════════════════

sealed class UiState<out T> {

    /** Initial / fetching state */
    object Loading : UiState<Nothing>()

    /** Data loaded successfully */
    data class Success<T>(val data: T) : UiState<T>()

    /** Error occurred */
    data class Error(
        val message  : String,
        val retryable: Boolean = true
    ) : UiState<Nothing>()

    /** No data available (e.g. empty list) */
    data class Empty(val reason: String = "") : UiState<Nothing>()

    // ── Convenience helpers ───────────────────────────

    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError  : Boolean get() = this is Error
    val isEmpty  : Boolean get() = this is Empty

    fun dataOrNull(): T? = (this as? Success)?.data

    fun <R> map(transform: (T) -> R): UiState<R> = when (this) {
        is Loading    -> Loading
        is Success    -> Success(transform(data))
        is Error      -> Error(message, retryable)
        is Empty      -> Empty(reason)
    }
}

// ═══════════════════════════════════════════════════════
//   PAGED STATE  — for infinite-scroll lists
// ═══════════════════════════════════════════════════════

data class PagedUiState<T>(
    val items      : List<T> = emptyList(),
    val isLoading  : Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore    : Boolean = true,
    val error      : String? = null,
    val page       : Int     = 0
) {
    val isEmpty: Boolean get() = items.isEmpty() && !isLoading
}

// ═══════════════════════════════════════════════════════
//   FORM STATE  — shared pattern for forms with validation
// ═══════════════════════════════════════════════════════

data class FieldState(
    val value   : String  = "",
    val error   : String? = null,
    val touched : Boolean = false
) {
    val isValid: Boolean get() = error == null && value.isNotBlank()
    val showError: Boolean get() = touched && error != null
}

data class FormState(
    val fields     : Map<String, FieldState> = emptyMap(),
    val isSubmitting: Boolean = false,
    val submitError : String? = null,
    val isSubmitted : Boolean = false
) {
    val isValid: Boolean get() = fields.values.all { it.isValid }
    val hasErrors: Boolean get() = fields.values.any { it.error != null }
}
