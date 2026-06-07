package com.kittys.premium.core.common

// ═══════════════════════════════════════════════════════
//   RESULT WRAPPER  — used across all use cases
// ═══════════════════════════════════════════════════════

sealed class Result<out T> {
    data class Success<T>(val data: T)     : Result<T>()
    data class Error(val message: String?) : Result<Nothing>()
    object Loading                         : Result<Nothing>()
}
