package com.kittys.premium.features.auth

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
//   MIKO — Auth ViewModel (self-contained)
//   Handles login, signup, Google sign-in, password reset.
//   Swap the simulated calls for Supabase Auth later.
// ════════════════════════════════════════════════════════════════

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val resetEmailSent: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    // private val authRepository: AuthRepository  // wire Supabase later
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ─── Email / password login ───
    fun login(email: String, password: String) {
        if (!isValidEmail(email)) {
            _uiState.update { it.copy(error = "Please enter a valid email") }
            return
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(error = "Please enter your password") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            delay(900) // simulate Supabase auth
            // TODO: authRepository.signIn(email, password)
            _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
        }
    }

    // ─── Signup ───
    fun signup(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        agreedToTerms: Boolean
    ) {
        when {
            fullName.trim().length < 2 -> { setError("Please enter your full name"); return }
            !isValidEmail(email) -> { setError("Please enter a valid email"); return }
            !isValidSriLankanPhone(phone) -> { setError("Please enter a valid Sri Lankan phone number"); return }
            !passwordRules(password).all { it.met } -> { setError("Password doesn't meet all requirements"); return }
            password != confirmPassword -> { setError("Passwords don't match"); return }
            !agreedToTerms -> { setError("Please accept the Terms & Privacy Policy"); return }
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            delay(1000)
            // TODO: authRepository.signUp(...)
            _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
        }
    }

    // ─── Google sign-in ───
    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            delay(900)
            // TODO: trigger Google OAuth via Supabase
            _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
        }
    }

    // ─── Forgot password ───
    fun sendPasswordReset(email: String) {
        if (!isValidEmail(email)) {
            _uiState.update { it.copy(error = "Please enter a valid email") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            delay(900)
            // TODO: authRepository.sendPasswordReset(email)
            _uiState.update { it.copy(isLoading = false, resetEmailSent = true) }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
    fun resetState() = _uiState.update { AuthUiState() }

    private fun setError(msg: String) = _uiState.update { it.copy(error = msg) }

    // ─── Validation helpers ───
    private fun isValidEmail(email: String): Boolean =
        email.trim().matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))

    private fun isValidSriLankanPhone(phone: String): Boolean {
        val c = phone.replace(" ", "").replace("-", "")
        return c.matches(Regex("^(\\+?94|0)?7\\d{8}$"))
    }

    companion object {
        /** Password requirement checklist — shared with the Signup screen UI. */
        fun passwordRules(pw: String): List<PasswordRule> = listOf(
            PasswordRule("At least 8 characters", pw.length >= 8),
            PasswordRule("One uppercase letter", pw.any { it.isUpperCase() }),
            PasswordRule("One lowercase letter", pw.any { it.isLowerCase() }),
            PasswordRule("One number", pw.any { it.isDigit() }),
            PasswordRule("One special character", pw.any { !it.isLetterOrDigit() })
        )
    }
}

data class PasswordRule(val label: String, val met: Boolean)