package com.kittys.premium.features.cart

import androidx.lifecycle.ViewModel
import com.kittys.premium.domain.model.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ════════════════════════════════════════════════════════════════
//   MIKO — Cart ViewModel (self-contained)
// ════════════════════════════════════════════════════════════════

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val promoCode: String = "",
    val promoApplied: Boolean = false,
    val promoDiscount: Int = 0,
    val isLoading: Boolean = false
) {
    val subtotal: Int get() = items.sumOf { it.lineTotal }

    // Free shipping above LKR 5,000, else LKR 350
    val freeShippingThreshold: Int get() = 5000
    val qualifiesFreeShipping: Boolean get() = subtotal >= freeShippingThreshold
    val deliveryFee: Int get() = if (qualifiesFreeShipping || items.isEmpty()) 0 else 350
    val amountToFreeShipping: Int get() = (freeShippingThreshold - subtotal).coerceAtLeast(0)

    val total: Int get() = (subtotal + deliveryFee - promoDiscount).coerceAtLeast(0)
    val itemCount: Int get() = items.sumOf { it.quantity }
}

@HiltViewModel
class CartViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState(items = sampleCart()))
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun increaseQty(itemId: String) = _uiState.update { state ->
        state.copy(items = state.items.map {
            if (it.id == itemId) it.copy(quantity = it.quantity + 1) else it
        })
    }

    fun decreaseQty(itemId: String) = _uiState.update { state ->
        state.copy(items = state.items.mapNotNull {
            if (it.id == itemId) {
                if (it.quantity <= 1) null else it.copy(quantity = it.quantity - 1)
            } else it
        })
    }

    fun removeItem(itemId: String) = _uiState.update { state ->
        state.copy(items = state.items.filterNot { it.id == itemId })
    }

    fun updatePromoCode(code: String) = _uiState.update { it.copy(promoCode = code) }

    fun applyPromo() = _uiState.update { state ->
        // Demo promo codes
        val discount = when (state.promoCode.trim().uppercase()) {
            "MIKO10" -> (state.subtotal * 0.10).toInt()
            "WELCOME" -> 500
            "FREESHIP" -> 350
            else -> 0
        }
        state.copy(
            promoApplied = discount > 0,
            promoDiscount = discount
        )
    }

    fun clearCart() = _uiState.update { it.copy(items = emptyList(), promoApplied = false, promoDiscount = 0) }

    private fun sampleCart(): List<CartItem> = listOf(
        CartItem("c1", "p1", "Lavender Bow Party Dress", "", "Little Stars", "4Y", "Pink", 2850, 1),
        CartItem("c2", "p4", "Newborn Soft Bodysuit Set", "", "Baby Boutique", "3-6M", "White", 2450, 2)
    )
}