package com.kittys.premium.features.seller.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kittys.premium.domain.model.SriLankanBank
import com.kittys.premium.domain.model.StoreCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ════════════════════════════════════════════════════════════════
//   MIKO — Seller Onboarding ViewModel
//   Shared state across the 4 onboarding steps:
//     Step 1 → Intro (no input)
//     Step 2 → Store Info
//     Step 3 → Verification (phone OTP)
//     Step 4 → Banking
// ════════════════════════════════════════════════════════════════

data class SellerOnboardingState(

    // ─── Navigation ───
    val currentStep     : Int = 1,            // 1..4

    // ─── Step 2: Store Info ───
    val storeName       : String = "",
    val description     : String = "",
    val storeCategory   : StoreCategory? = null,
    val location        : String = "",
    val logoUri         : String? = null,
    val bannerUri       : String? = null,

    // ─── Step 3: Verification ───
    val phone           : String = "",
    val otpSent         : Boolean = false,
    val otpCode         : String = "",
    val phoneVerified   : Boolean = false,
    val isSendingOtp    : Boolean = false,

    // ─── Step 4: Banking ───
    val accountHolder   : String = "",
    val bank            : SriLankanBank? = null,
    val accountNumber   : String = "",
    val branch          : String = "",
    val ezCashNumber    : String? = null,
    val mCashNumber     : String? = null,
    val friMiNumber     : String? = null,

    // ─── Loading / errors ───
    val isSubmitting    : Boolean = false,
    val error           : String? = null,
    val isComplete      : Boolean = false
)

@HiltViewModel
class SellerOnboardingViewModel @Inject constructor(
    // private val sellerRepository: SellerRepository  // inject when data layer is ready
) : ViewModel() {

    private val _state = MutableStateFlow(SellerOnboardingState())
    val state: StateFlow<SellerOnboardingState> = _state.asStateFlow()

    // ════════════════════════════════════════════════════
    //   STEP NAVIGATION
    // ════════════════════════════════════════════════════

    fun goToStep(step: Int)  = _state.update { it.copy(currentStep = step.coerceIn(1, 4), error = null) }
    fun nextStep()           = _state.update { it.copy(currentStep = (it.currentStep + 1).coerceAtMost(4), error = null) }
    fun previousStep()       = _state.update { it.copy(currentStep = (it.currentStep - 1).coerceAtLeast(1), error = null) }

    // ════════════════════════════════════════════════════
    //   STEP 2 — STORE INFO
    // ════════════════════════════════════════════════════

    fun updateStoreName(v: String)        = _state.update { it.copy(storeName = v) }
    fun updateDescription(v: String)      = _state.update { it.copy(description = v) }
    fun updateCategory(c: StoreCategory)  = _state.update { it.copy(storeCategory = c) }
    fun updateLocation(v: String)         = _state.update { it.copy(location = v) }
    fun updateLogo(uri: String)           = _state.update { it.copy(logoUri = uri) }
    fun updateBanner(uri: String)         = _state.update { it.copy(bannerUri = uri) }

    fun isStoreInfoValid(): Boolean {
        val s = _state.value
        return s.storeName.trim().length in 3..50 &&
                s.description.trim().length >= 10 &&
                s.storeCategory != null &&
                s.location.trim().isNotEmpty()
    }

    /** url-safe slug derived from the store name, e.g. "Little Stars" → "little-stars" */
    val storeSlug: String
        get() = _state.value.storeName
            .trim()
            .lowercase()
            .replace(Regex("[^a-z0-9\\s-]"), "")
            .replace(Regex("\\s+"), "-")

    // ════════════════════════════════════════════════════
    //   STEP 3 — VERIFICATION
    // ════════════════════════════════════════════════════

    fun updatePhone(v: String) = _state.update { it.copy(phone = v) }

    fun updateOtpCode(v: String) = _state.update { it.copy(otpCode = v) }

    /** Sends an OTP to the entered phone number. */
    fun sendOtp() {
        val phone = _state.value.phone.trim()
        if (!isValidSriLankanPhone(phone)) {
            _state.update { it.copy(error = "Enter a valid Sri Lankan phone number") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSendingOtp = true, error = null) }
            // TODO: trigger Supabase Auth phone OTP
            kotlinx.coroutines.delay(800) // simulate network
            _state.update { it.copy(isSendingOtp = false, otpSent = true) }
        }
    }

    /** Verifies the OTP code the user entered. */
    fun verifyOtp(code: String = _state.value.otpCode) {
        viewModelScope.launch {
            _state.update { it.copy(isSendingOtp = true, error = null) }
            // TODO: actually verify with Supabase Auth
            kotlinx.coroutines.delay(600)
            if (code.length == 6) {
                _state.update { it.copy(isSendingOtp = false, phoneVerified = true) }
            } else {
                _state.update { it.copy(isSendingOtp = false, error = "Invalid code. Try again.") }
            }
        }
    }

    fun isVerificationValid(): Boolean = _state.value.phoneVerified

    // ════════════════════════════════════════════════════
    //   STEP 4 — BANKING
    // ════════════════════════════════════════════════════

    fun updateAccountHolder(v: String) = _state.update { it.copy(accountHolder = v) }
    fun updateBank(b: SriLankanBank)   = _state.update { it.copy(bank = b) }
    fun updateAccountNumber(v: String) = _state.update { it.copy(accountNumber = v.filter { c -> c.isDigit() }) }
    fun updateBranch(v: String)        = _state.update { it.copy(branch = v) }
    fun updateEzCash(num: String)      = _state.update { it.copy(ezCashNumber = num.ifBlank { null }) }
    fun updateMCash(num: String)       = _state.update { it.copy(mCashNumber = num.ifBlank { null }) }
    fun updateFriMi(num: String)       = _state.update { it.copy(friMiNumber = num.ifBlank { null }) }

    /** Convenience: set all banking fields at once. */
    fun updateBanking(
        accountHolder: String,
        bank: SriLankanBank,
        accountNumber: String,
        branch: String
    ) {
        _state.update {
            it.copy(
                accountHolder = accountHolder,
                bank = bank,
                accountNumber = accountNumber.filter { c -> c.isDigit() },
                branch = branch
            )
        }
    }

    fun isBankingValid(): Boolean {
        val s = _state.value
        return s.accountHolder.trim().length >= 3 &&
                s.bank != null &&
                s.accountNumber.length in 6..20 &&
                s.branch.trim().isNotEmpty()
    }

    // ════════════════════════════════════════════════════
    //   SUBMIT
    // ════════════════════════════════════════════════════

    fun completeOnboarding() {
        if (!isStoreInfoValid()) {
            _state.update { it.copy(error = "Please complete your store information", currentStep = 2) }
            return
        }
        if (!isVerificationValid()) {
            _state.update { it.copy(error = "Please verify your phone number", currentStep = 3) }
            return
        }
        if (!isBankingValid()) {
            _state.update { it.copy(error = "Please complete your banking details", currentStep = 4) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }
            // TODO: persist to Supabase via sellerRepository.createSeller(buildProfile())
            kotlinx.coroutines.delay(1200) // simulate network
            _state.update { it.copy(isSubmitting = false, isComplete = true) }
        }
    }

    fun clearError() = _state.update { it.copy(error = null) }

    // ════════════════════════════════════════════════════
    //   VALIDATION HELPERS
    // ════════════════════════════════════════════════════

    private fun isValidSriLankanPhone(phone: String): Boolean {
        val cleaned = phone.replace(" ", "").replace("-", "")
        // Accepts 0771234567, +94771234567, 94771234567
        return cleaned.matches(Regex("^(\\+?94|0)?7\\d{8}$"))
    }
}