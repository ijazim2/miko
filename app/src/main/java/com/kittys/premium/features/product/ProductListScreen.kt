package com.kittys.premium.features.product

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
import androidx.compose.foundation.interaction.MutableInteractionSource
// ════════════════════════════════════════════════════════════════
//   MIKO — Product List ViewModel (self-contained)
// ════════════════════════════════════════════════════════════════

data class ProductCard(
    val id: String,
    val name: String,
    val priceLkr: Int,
    val rating: String,
    val emoji: String
)

data class ProductListUiState(
    val isLoading: Boolean = false,
    val products: List<ProductCard> = emptyList(),
    val category: String? = null
)

@HiltViewModel
class ProductViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    init { loadProducts() }

    fun loadProducts(category: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, category = category) }
            delay(200)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    products = sampleProducts()
                )
            }
        }
    }

    private fun sampleProducts(): List<ProductCard> = listOf(
        ProductCard("p0", "Floral Cotton Dress", 2850, "4.8", "👗"),
        ProductCard("p1", "Princess Tulle Dress", 3450, "4.9", "👗"),
        ProductCard("p2", "Lace Party Dress", 2990, "4.7", "👗"),
        ProductCard("p3", "Summer Frock", 1890, "4.6", "👗"),
        ProductCard("p4", "Cotton Top", 1650, "4.5", "👚"),
        ProductCard("p5", "Denim Dress", 2250, "4.8", "👗"),
        ProductCard("p6", "Party Skirt", 1950, "4.7", "👗"),
        ProductCard("p7", "Casual Set", 2490, "4.6", "👕")
    )
}