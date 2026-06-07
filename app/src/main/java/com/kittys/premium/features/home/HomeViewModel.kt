package com.kittys.premium.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kittys.premium.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ════════════════════════════════════════════════════════════════
//   MIKO — Home ViewModel (self-contained, sample data)
// ════════════════════════════════════════════════════════════════

data class CategoryChipItem(
    val id: String,
    val label: String,
    val emoji: String
)

data class HomeUiState(
    val isLoading: Boolean = false,
    val userName: String = "there",
    val cartCount: Int = 0,
    val greeting: String = "Good Morning",
    val categories: List<CategoryChipItem> = emptyList(),
    val flashSaleProducts: List<Product> = emptyList(),
    val featuredProducts: List<Product> = emptyList(),
    val newArrivals: List<Product> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    // Wire real repos later — placeholder data for now
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    userName = "Amali",
                    cartCount = 2,
                    greeting = timeGreeting(),
                    categories = listOf(
                        CategoryChipItem("babies", "Babies", "👶"),
                        CategoryChipItem("girls", "Girls", "👧"),
                        CategoryChipItem("boys", "Boys", "👦"),
                        CategoryChipItem("accessories", "Accessories", "🎀")
                    ),
                    flashSaleProducts = sampleProducts.filter { it.isOnSale },
                    featuredProducts = sampleProducts,
                    newArrivals = sampleProducts.reversed()
                )
            }
        }
    }

    fun toggleWishlist(productId: String) {
        _uiState.update { state ->
            fun List<Product>.toggle() = map {
                if (it.id == productId) it.copy(isWishlisted = !it.isWishlisted) else it
            }
            state.copy(
                flashSaleProducts = state.flashSaleProducts.toggle(),
                featuredProducts = state.featuredProducts.toggle(),
                newArrivals = state.newArrivals.toggle()
            )
        }
    }

    fun addToCart(productId: String) {
        _uiState.update { it.copy(cartCount = it.cartCount + 1) }
        // TODO: persist to cart repository
    }

    private fun timeGreeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
    }

    companion object {
        val sampleProducts = listOf(
            Product(
                id = "p1", name = "Lavender Bow Party Dress", description = "Adorable party dress with bow detail",
                brand = "Little Stars", vendorId = "v1", vendorName = "Little Stars Fashion", vendorRating = 4.8f,
                priceInt = 2850, originalPriceInt = 3990, discountPercent = 29,
                images = emptyList(), sizes = listOf("2Y", "3Y", "4Y", "5Y", "6Y"),
                colors = listOf("Pink", "Lavender", "White"), category = "Girls",
                tags = listOf("party", "bestseller"), rating = 4.8f, reviewCount = 56,
                isWishlisted = false, stockQty = 38
            ),
            Product(
                id = "p2", name = "Sweet Heart Cotton Top", description = "Soft cotton top for everyday wear",
                brand = "Kids World", vendorId = "v2", vendorName = "Kids World LK", vendorRating = 4.6f,
                priceInt = 1990, originalPriceInt = null, discountPercent = 0,
                images = emptyList(), sizes = listOf("2Y", "3Y", "4Y"),
                colors = listOf("White", "Pink"), category = "Girls",
                tags = listOf("casual", "new"), rating = 4.6f, reviewCount = 31,
                isWishlisted = true, stockQty = 12
            ),
            Product(
                id = "p3", name = "Boys Denim Dungaree", description = "Sturdy denim dungaree for active boys",
                brand = "Tiny Trends", vendorId = "v3", vendorName = "Tiny Trends", vendorRating = 4.7f,
                priceInt = 3200, originalPriceInt = 3990, discountPercent = 20,
                images = emptyList(), sizes = listOf("3Y", "4Y", "5Y", "6Y"),
                colors = listOf("Blue", "Navy"), category = "Boys",
                tags = listOf("denim"), rating = 4.7f, reviewCount = 24,
                isWishlisted = false, stockQty = 20
            ),
            Product(
                id = "p4", name = "Newborn Soft Bodysuit Set", description = "3-pack organic cotton bodysuits",
                brand = "Baby Boutique", vendorId = "v4", vendorName = "Baby Boutique", vendorRating = 4.9f,
                priceInt = 2450, originalPriceInt = null, discountPercent = 0,
                images = emptyList(), sizes = listOf("0-3M", "3-6M", "6-12M"),
                colors = listOf("White", "Yellow", "Mint"), category = "Babies",
                tags = listOf("organic", "new"), rating = 4.9f, reviewCount = 78,
                isWishlisted = false, stockQty = 50
            ),
            Product(
                id = "p5", name = "Floral Hair Clip Set", description = "Set of 6 cute floral hair clips",
                brand = "Little Stars", vendorId = "v1", vendorName = "Little Stars Fashion", vendorRating = 4.8f,
                priceInt = 690, originalPriceInt = 990, discountPercent = 30,
                images = emptyList(), sizes = listOf("One Size"),
                colors = listOf("Multi"), category = "Accessories",
                tags = listOf("accessory", "sale"), rating = 4.5f, reviewCount = 42,
                isWishlisted = false, stockQty = 100
            ),
            Product(
                id = "p6", name = "Bunny Cotton Backpack", description = "Cute bunny backpack for preschool",
                brand = "Kids World", vendorId = "v2", vendorName = "Kids World LK", vendorRating = 4.6f,
                priceInt = 3900, originalPriceInt = null, discountPercent = 0,
                images = emptyList(), sizes = listOf("One Size"),
                colors = listOf("Pink", "Blue"), category = "Accessories",
                tags = listOf("bag", "school"), rating = 4.7f, reviewCount = 19,
                isWishlisted = false, stockQty = 25
            )
        )
    }
}