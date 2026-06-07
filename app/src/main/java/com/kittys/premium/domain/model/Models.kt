package com.kittys.premium.domain.model

// ════════════════════════════════════════════════════════════════
//   MIKO — Core Domain Models (pure data, no Android deps)
// ════════════════════════════════════════════════════════════════

// ─── PRODUCT ───
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val brand: String,
    val vendorId: String,
    val vendorName: String,
    val vendorRating: Float,

    val priceInt: Int,
    val originalPriceInt: Int?,          // null if not on sale
    val discountPercent: Int = 0,

    val images: List<String>,
    val imageUrl: String = images.firstOrNull() ?: "",

    val sizes: List<String>,
    val colors: List<String>,

    val category: String,                // "Girls" | "Boys" | "Babies" | "Accessories" ...
    val tags: List<String>,
    val rating: Float,
    val reviewCount: Int,
    val isWishlisted: Boolean = false,
    val stockQty: Int = 0,
    val createdAt: Long = 0L
) {
    val isOnSale: Boolean get() = originalPriceInt != null && originalPriceInt > priceInt
    val inStock: Boolean get() = stockQty > 0
}

// ─── CART ITEM ───
data class CartItem(
    val id: String,
    val productId: String,
    val name: String,
    val imageUrl: String,
    val brand: String,
    val size: String,
    val color: String,
    val priceInt: Int,
    val quantity: Int
) {
    val lineTotal: Int get() = priceInt * quantity
}

// ─── USER ───
data class User(
    val id: String,
    val fullName: String,
    val firstName: String = fullName.split(" ").firstOrNull() ?: fullName,
    val email: String,
    val phone: String = "",
    val avatarUrl: String? = null,
    val role: UserRole = UserRole.CUSTOMER,
    val memberSince: String = "",
    val isPremium: Boolean = false,
    val orderCount: Int = 0,
    val wishlistCount: Int = 0,
    val reviewCount: Int = 0
)

enum class UserRole { CUSTOMER, VENDOR, ADMIN }

// ─── CHILD PROFILE ───
data class ChildProfile(
    val id: String,
    val userId: String,
    val name: String,
    val gender: String,
    val dateOfBirth: String,
    val ageInYears: Int,
    val heightCm: Int? = null,
    val weightKg: Float? = null,
    val currentSize: String = "",
    val favoriteColors: List<String> = emptyList()
)

// ─── ORDER ───
data class Order(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val totalLkr: Int,
    val deliveryAddress: DeliveryAddress,
    val paymentMethod: String,
    val createdAt: Long
)

data class OrderItem(
    val productId: String,
    val name: String,
    val imageUrl: String,
    val size: String,
    val color: String,
    val priceInt: Int,
    val quantity: Int
)

enum class OrderStatus {
    PENDING, CONFIRMED, PROCESSING, SHIPPED,
    OUT_FOR_DELIVERY, DELIVERED, CANCELLED, RETURNED
}

// ─── DELIVERY ADDRESS ───
data class DeliveryAddress(
    val id: String = "",
    val label: String = "Home",
    val fullName: String,
    val phone: String,
    val line1: String,
    val line2: String = "",
    val city: String,
    val district: String,
    val postalCode: String = "",
    val isDefault: Boolean = false
)

// ─── VENDOR ───
data class Vendor(
    val id: String,
    val userId: String,
    val storeName: String,
    val description: String,
    val logoUrl: String?,
    val bannerUrl: String?,
    val rating: Float,
    val verified: Boolean,
    val productCount: Int,
    val totalSales: Int
)

// ─── REVIEW ───
data class Review(
    val id: String,
    val productId: String,
    val userId: String,
    val userName: String,
    val avatarUrl: String?,
    val rating: Int,              // 1–5
    val comment: String,
    val images: List<String>,
    val sizeAccuracy: String,     // "Runs small" | "True to size" | "Runs large"
    val createdAt: Long
)
