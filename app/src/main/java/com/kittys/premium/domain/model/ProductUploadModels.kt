package com.kittys.premium.domain.model

// ════════════════════════════════════════════════════════════════
//   MIKO — Product Upload Models (multi-variant support)
//   Package stays com.kittys.premium (internal package name)
// ════════════════════════════════════════════════════════════════

/**
 * The main product the seller is uploading.
 * Has many ProductVariants (one per color).
 */
data class ProductUpload(

    // ── Step 1: Basics ──
    val name            : String = "",
    val description     : String = "",
    val category        : ProductCategory? = null,
    val subCategory     : String = "",
    val brand           : String = "",

    // ── Step 2: Audience ──
    val gender          : ProductGender = ProductGender.UNISEX,
    val ageGroups       : List<AgeGroup> = emptyList(),
    val season          : Season? = null,
    val style           : ProductStyle? = null,

    // ── Step 3: Variants (THE BIG ONE) ──
    val hasVariants     : Boolean = false,
    val variants        : List<ProductVariant> = listOf(ProductVariant()),
    val baseSku         : String = "",

    // ── Step 4: Details ──
    val material        : String = "",
    val fabricCare      : FabricCare? = null,
    val pattern         : Pattern? = null,
    val countryOfOrigin : String = "Sri Lanka",
    val safetyTags      : List<SafetyTag> = emptyList(),
    val customTags      : List<String> = emptyList(),

    // ── Step 5: Shipping ──
    val weightGrams      : Int = 0,
    val processingTime   : ProcessingTime = ProcessingTime.STANDARD,
    val shippingMethods  : List<ShippingMethod> = listOf(ShippingMethod.STANDARD),
    val freeShipping     : Boolean = false,
    val freeShippingAbove: Int = 0,
    val acceptsReturns   : Boolean = true,
    val returnDays       : Int = 7,

    // ── Step 6: Meta ──
    val status          : ProductStatus = ProductStatus.DRAFT,
    val launchDate      : Long? = null,
    val isFeatured      : Boolean = false
) {
    /** Total stock across all variants and sizes */
    val totalStock: Int
        get() = variants.sumOf { v -> v.sizeStocks.values.sum() }

    /** Lowest price across variants */
    val minPrice: Int
        get() = variants.minOfOrNull { it.priceLkr } ?: 0

    /** Highest price across variants */
    val maxPrice: Int
        get() = variants.maxOfOrNull { it.priceLkr } ?: 0

    /** Price display, e.g. "LKR 2,850" or "LKR 2,850 – 2,950" */
    val priceRangeDisplay: String
        get() = if (minPrice == maxPrice)
            "LKR ${minPrice.formatThousands()}"
        else
            "LKR ${minPrice.formatThousands()} – ${maxPrice.formatThousands()}"

    /** Total number of photos across all variants */
    val totalPhotos: Int
        get() = variants.sumOf { it.photoUris.size }

    /** Distinct colors used */
    val colorCount: Int
        get() = variants.mapNotNull { it.color?.name }.distinct().size

    /** Whether this upload is valid to publish */
    fun isValid(): Boolean =
        isStep1Valid() && isStep2Valid() && isStep3Valid() && isStep5Valid()

    fun isStep1Valid(): Boolean =
        name.trim().length in 3..80 &&
                description.trim().length >= 20 &&
                category != null

    fun isStep2Valid(): Boolean =
        ageGroups.isNotEmpty()

    fun isStep3Valid(): Boolean =
        variants.isNotEmpty() && variants.all { it.isValid() }

    fun isStep5Valid(): Boolean =
        weightGrams > 0 && shippingMethods.isNotEmpty()

    /** Human-readable reason the product can't be published yet (or null if valid). */
    fun validationError(): String? = when {
        name.trim().length < 3        -> "Product name must be at least 3 characters"
        description.trim().length < 20 -> "Description must be at least 20 characters"
        category == null              -> "Please choose a category"
        ageGroups.isEmpty()           -> "Select at least one age group"
        variants.isEmpty()            -> "Add at least one variant"
        variants.any { it.color == null }      -> "Every variant needs a color"
        variants.any { it.photoUris.isEmpty() } -> "Every variant needs at least one photo"
        variants.any { it.sizeStocks.isEmpty() } -> "Every variant needs at least one size"
        variants.any { it.priceLkr <= 0 }      -> "Every variant needs a price"
        weightGrams <= 0              -> "Enter the package weight"
        shippingMethods.isEmpty()     -> "Choose at least one shipping method"
        else -> null
    }
}

/**
 * A single variant — one color with multiple size/stock combinations.
 * If product has 3 colors, there are 3 variants.
 */
data class ProductVariant(
    val id           : String = java.util.UUID.randomUUID().toString(),
    val color        : VariantColor? = null,
    val photoUris    : List<String> = emptyList(),         // up to 5
    val sizeStocks   : Map<String, Int> = emptyMap(),      // "4Y" -> 12
    val priceLkr     : Int = 0,
    val originalPrice: Int = 0,
    val sku          : String = ""
) {
    val discountPercent: Int
        get() = if (originalPrice > priceLkr && originalPrice > 0)
            (((originalPrice - priceLkr) * 100.0) / originalPrice).toInt()
        else 0

    val totalStock: Int
        get() = sizeStocks.values.sum()

    val hasDiscount: Boolean
        get() = originalPrice > priceLkr && originalPrice > 0

    /** Price display, e.g. "LKR 2,850" */
    val priceDisplay: String
        get() = "LKR ${priceLkr.formatThousands()}"

    val maxPhotos: Int get() = 5
    val canAddPhoto: Boolean get() = photoUris.size < maxPhotos

    fun isValid(): Boolean =
        color != null &&
                photoUris.isNotEmpty() &&
                sizeStocks.isNotEmpty() &&
                sizeStocks.values.any { it > 0 } &&
                priceLkr > 0

    /** Auto-generates a SKU like "MIKO-PINK-A1B2". */
    fun generateSku(baseSku: String): String {
        val colorCode = color?.name?.take(4)?.uppercase()?.replace(" ", "") ?: "STD"
        val suffix = id.takeLast(4).uppercase()
        val prefix = baseSku.ifBlank { "MIKO" }
        return "$prefix-$colorCode-$suffix"
    }
}

/**
 * Available colors with hex codes for the color picker swatch.
 */
data class VariantColor(
    val name : String,
    val hex  : String        // "#FFB8D9"
) {
    val isGradient: Boolean get() = hex == "#GRADIENT"

    companion object {
        val DEFAULTS = listOf(
            VariantColor("Pink",       "#FFB8D9"),
            VariantColor("Hot Pink",   "#FF1493"),
            VariantColor("Red",        "#EF4444"),
            VariantColor("Coral",      "#FF6B6B"),
            VariantColor("Orange",     "#FF6B35"),
            VariantColor("Yellow",     "#FFB800"),
            VariantColor("Mint",       "#10B981"),
            VariantColor("Green",      "#22C55E"),
            VariantColor("Sky Blue",   "#38BDF8"),
            VariantColor("Blue",       "#3B82F6"),
            VariantColor("Navy",       "#1E3A8A"),
            VariantColor("Lavender",   "#A78BFA"),
            VariantColor("Purple",     "#8B5CF6"),
            VariantColor("Black",      "#000000"),
            VariantColor("White",      "#FFFFFF"),
            VariantColor("Grey",       "#9CA3AF"),
            VariantColor("Beige",      "#D6BFA6"),
            VariantColor("Brown",      "#8B4513"),
            VariantColor("Multi",      "#GRADIENT")   // special — show rainbow gradient swatch
        )
    }
}

// ════════════════════════════════════════════════════════════════
//   ENUMS
// ════════════════════════════════════════════════════════════════

enum class ProductCategory(val displayName: String, val emoji: String, val subCategories: List<String>) {
    CLOTHING        ("Clothing",         "👗", listOf("Dresses", "Tops", "Bottoms", "Sets", "Outerwear", "Sleepwear", "Underwear")),
    SHOES           ("Shoes",            "👟", listOf("Sneakers", "Sandals", "Boots", "School Shoes", "Slippers")),
    TOYS            ("Toys",             "🧸", listOf("Educational", "STEM", "Soft Toys", "Building Blocks", "Puzzles", "Outdoor")),
    BABY_ESSENTIALS ("Baby Essentials",  "🍼", listOf("Feeding", "Diapers", "Bath", "Sleep", "Strollers", "Car Seats")),
    SCHOOL          ("School & Learning","🎒", listOf("Bags", "Lunch Boxes", "Stationery", "Books")),
    ACCESSORIES     ("Accessories",      "🎀", listOf("Hair", "Hats", "Bags", "Jewelry", "Socks"))
}

enum class ProductGender(val displayName: String, val emoji: String) {
    BOY    ("Boy",     "👦"),
    GIRL   ("Girl",    "👧"),
    UNISEX ("Unisex",  "👶")
}

enum class AgeGroup(val displayName: String, val rangeMonths: IntRange) {
    NEWBORN_0_3M    ("Newborn (0–3M)",  0..3),
    INFANT_3_6M     ("Infant (3–6M)",   3..6),
    INFANT_6_12M    ("Infant (6–12M)",  6..12),
    TODDLER_1_2Y    ("Toddler (1–2Y)",  12..24),
    TODDLER_2_3Y    ("Toddler (2–3Y)",  24..36),
    KID_3_4Y        ("Kid (3–4Y)",      36..48),
    KID_5_6Y        ("Kid (5–6Y)",      60..72),
    KID_7_8Y        ("Kid (7–8Y)",      84..96),
    TWEEN_9_10Y     ("Tween (9–10Y)",   108..120),
    TWEEN_11_12Y    ("Tween (11–12Y)",  132..144),
    TEEN            ("Teen (13+)",      156..240)
}

enum class Season(val displayName: String, val emoji: String) {
    ALL_SEASON ("All Season", "🌈"),
    SUMMER     ("Summer",     "☀"),
    MONSOON    ("Monsoon",    "🌧"),
    WINTER     ("Winter",     "❄")
}

enum class ProductStyle(val displayName: String, val emoji: String) {
    CASUAL     ("Casual",       "👕"),
    FORMAL     ("Formal",       "🎩"),
    PARTY      ("Party",        "🎉"),
    SCHOOL     ("School",       "🎒"),
    SPORTS     ("Sports",       "⚽"),
    ETHNIC     ("Ethnic",       "🥻"),
    SLEEPWEAR  ("Sleepwear",    "🌙")
}

enum class FabricCare(val displayName: String) {
    MACHINE_WASH ("Machine Wash"),
    HAND_WASH    ("Hand Wash Only"),
    DRY_CLEAN    ("Dry Clean Only"),
    DELICATE     ("Gentle / Delicate Wash")
}

enum class Pattern(val displayName: String) {
    SOLID    ("Solid Color"),
    FLORAL   ("Floral"),
    STRIPED  ("Striped"),
    POLKA    ("Polka Dot"),
    PRINTED  ("Printed / Graphic"),
    CARTOON  ("Cartoon"),
    PLAID    ("Plaid / Checkered"),
    ABSTRACT ("Abstract")
}

enum class SafetyTag(val displayName: String, val emoji: String) {
    BPA_FREE       ("BPA Free",         "🚫"),
    NON_TOXIC      ("Non-Toxic",        "✓"),
    ORGANIC        ("Organic",          "🌱"),
    HYPOALLERGENIC ("Hypoallergenic",   "🌿"),
    CE_CERTIFIED   ("CE Certified",     "🛡"),
    LATEX_FREE     ("Latex Free",       "🔒"),
    LEAD_FREE      ("Lead Free",        "⚠"),
    PHTHALATE_FREE ("Phthalate Free",   "✦")
}

enum class ProcessingTime(val displayName: String, val days: IntRange) {
    SAME_DAY      ("Same day shipping",       0..0),
    FAST          ("1–2 business days",       1..2),
    STANDARD      ("3–5 business days",       3..5),
    EXTENDED      ("5–7 business days",       5..7),
    MADE_TO_ORDER ("Made to order (7+ days)", 7..14)
}

enum class ShippingMethod(val displayName: String, val emoji: String) {
    STANDARD ("Standard Delivery", "🚚"),
    EXPRESS  ("Express Delivery",  "⚡"),
    PICKUP   ("Local Pickup",      "📍"),
    COURIER  ("Courier",           "📦")
}

enum class ProductStatus(val displayName: String) {
    DRAFT          ("Draft"),
    PENDING_REVIEW ("Pending Review"),
    PUBLISHED      ("Published"),
    OUT_OF_STOCK   ("Out of Stock"),
    HIDDEN         ("Hidden"),
    REJECTED       ("Rejected")
}

// ════════════════════════════════════════════════════════════════
//   SIZE OPTIONS BY CATEGORY
// ════════════════════════════════════════════════════════════════

object ProductSizes {
    val CLOTHING_BY_AGE = listOf(
        "0-3M", "3-6M", "6-9M", "9-12M",
        "12-18M", "18-24M",
        "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "11Y", "12Y",
        "S", "M", "L", "XL"
    )

    val SHOES = listOf(
        "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
        "26", "27", "28", "29", "30", "31", "32", "33", "34", "35"
    )

    val GENERIC = listOf("XS", "S", "M", "L", "XL")

    fun forCategory(category: ProductCategory?): List<String> = when (category) {
        ProductCategory.CLOTHING    -> CLOTHING_BY_AGE
        ProductCategory.SHOES       -> SHOES
        ProductCategory.ACCESSORIES -> GENERIC
        else                        -> GENERIC
    }
}

// ════════════════════════════════════════════════════════════════
//   HELPERS
// ════════════════════════════════════════════════════════════════

/** Formats an Int with thousand separators, e.g. 2850 → "2,850". */
fun Int.formatThousands(): String =
    "%,d".format(this)