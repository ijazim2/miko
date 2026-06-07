package com.kittys.premium.features.checkout

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
//   MIKO — Checkout ViewModel (self-contained, sample totals)
// ════════════════════════════════════════════════════════════════

data class CheckoutUiState(
    val subtotal: Int = 7750,
    val deliveryFee: Int = 0,
    val discount: Int = 0,
    val isLoading: Boolean = false,
    val orderPlaced: Boolean = false
) {
    val total: Int get() = (subtotal + deliveryFee - discount).coerceAtLeast(0)
}

@HiltViewModel
class CheckoutViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    fun placeOrder(paymentMethod: String, address: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(800)  // simulate network / payment processing
            // TODO: send order to Supabase + create escrow transaction
            _uiState.update { it.copy(isLoading = false, orderPlaced = true) }
        }
    }
}