package com.kittys.premium.domain.model

// ════════════════════════════════════════════════════════════════
//   MIKO — Seller Models
//   Data layer for the seller onboarding + dashboard.
//   (Package stays com.kittys.premium — internal package name only)
// ════════════════════════════════════════════════════════════════

/**
 * A user's seller profile (created during the 4-step onboarding).
 */
data class SellerProfile(
    val id              : String,
    val userId          : String,
    val storeName       : String,
    val storeSlug       : String,                   // url-safe (e.g. "little-stars")
    val description     : String,
    val logoUrl         : String? = null,
    val bannerUrl       : String? = null,
    val storeCategory   : StoreCategory,
    val location        : String,                   // city
    val phone           : String,
    val socialLinks     : SocialLinks = SocialLinks(),

    // ─── Verification status ───
    val level           : SellerLevel = SellerLevel.LEVEL_1_NEW,
    val emailVerified   : Boolean = false,
    val phoneVerified   : Boolean = false,
    val idVerified      : Boolean = false,
    val businessVerified: Boolean = false,

    // ─── Banking ───
    val bankingInfo     : BankingInfo? = null,

    // ─── Shipping ───
    val pickupAddress   : String = "",
    val processingDays  : Int = 2,
    val acceptsReturns  : Boolean = true,
    val returnDays      : Int = 7,

    // ─── Stats ───
    val totalSales      : Int = 0,
    val totalProducts   : Int = 0,
    val rating          : Float = 0f,
    val followerCount   : Int = 0,

    val isActive        : Boolean = true,
    val isVacationMode  : Boolean = false,
    val createdAt       : Long = System.currentTimeMillis()
) {
    /** Whether this seller can still add more products under their level cap. */
    fun canAddProduct(): Boolean = totalProducts < level.maxProducts

    /** Remaining product slots for Level 1 sellers (Int.MAX_VALUE shows as unlimited). */
    val remainingProductSlots: Int
        get() = (level.maxProducts - totalProducts).coerceAtLeast(0)

    /** How many verification steps are complete (out of 4). */
    val verificationProgress: Int
        get() = listOf(emailVerified, phoneVerified, idVerified, businessVerified)
            .count { it }

    val isFullyVerified: Boolean
        get() = emailVerified && phoneVerified && idVerified && businessVerified

    /** Commission amount in LKR this seller would pay on a given sale price. */
    fun commissionOn(amountLkr: Int): Int = (amountLkr * level.commission) / 100

    /** Net payout to seller after MIKO commission. */
    fun payoutFor(amountLkr: Int): Int = amountLkr - commissionOn(amountLkr)
}


/**
 * Seller tiers. Higher level = lower commission + more product slots.
 * Locked-in spec values:
 *   L1 New      → 10% commission, 10 product limit
 *   L2 Verified →  7% commission, unlimited
 *   L3 Premium  →  5% commission, unlimited
 *   L4 Elite    →  3% commission, unlimited
 */
enum class SellerLevel(
    val displayName : String,
    val commission  : Int,          // percentage MIKO takes
    val maxProducts : Int,
    val perkSummary : String
) {
    LEVEL_1_NEW(
        displayName = "New Seller",
        commission  = 10,
        maxProducts = 10,
        perkSummary = "Up to 10 products · 10% commission"
    ),
    LEVEL_2_VERIFIED(
        displayName = "Verified Seller",
        commission  = 7,
        maxProducts = Int.MAX_VALUE,
        perkSummary = "Unlimited products · 7% commission · Verified badge"
    ),
    LEVEL_3_PREMIUM(
        displayName = "Premium Seller",
        commission  = 5,
        maxProducts = Int.MAX_VALUE,
        perkSummary = "Unlimited products · 5% commission · Priority support"
    ),
    LEVEL_4_ELITE(
        displayName = "Elite Seller",
        commission  = 3,
        maxProducts = Int.MAX_VALUE,
        perkSummary = "Unlimited products · 3% commission · Featured placement"
    );

    val isUnlimited: Boolean get() = maxProducts == Int.MAX_VALUE
}


enum class StoreCategory(val displayName: String, val emoji: String) {
    BABY_BOUTIQUE   ("Baby Boutique",          "👶"),
    KIDS_FASHION    ("Kids Fashion Store",     "👗"),
    TOY_STORE       ("Toy Store",              "🧸"),
    EDUCATIONAL     ("Educational Store",      "📚"),
    HANDMADE        ("Handmade Kids Products", "✂"),
    SCHOOL_SUPPLIES ("School Supplies",        "🎒"),
    SHOES           ("Shoes & Accessories",    "👟"),
    GENERAL         ("General Kids Store",     "🌟")
}


data class SocialLinks(
    val instagram : String? = null,
    val facebook  : String? = null,
    val tiktok    : String? = null,
    val website   : String? = null
)


data class BankingInfo(
    val accountHolder : String,
    val bankName      : String,
    val accountNumber : String,
    val branch        : String,

    // Optional mobile wallets (Sri Lanka)
    val ezCashNumber  : String? = null,
    val mCashNumber   : String? = null,
    val friMiNumber   : String? = null
) {
    /** Masks the account number for display, e.g. "•••• 4242". */
    val maskedAccountNumber: String
        get() = if (accountNumber.length >= 4)
            "•••• ${accountNumber.takeLast(4)}"
        else accountNumber
}


/**
 * Sri Lankan banks for the banking dropdown.
 */
enum class SriLankanBank(val displayName: String) {
    BOC          ("Bank of Ceylon"),
    PEOPLES_BANK ("Peoples Bank"),
    COMMERCIAL   ("Commercial Bank"),
    SAMPATH      ("Sampath Bank"),
    HNB          ("Hatton National Bank"),
    NDB          ("National Development Bank"),
    DFCC         ("DFCC Bank"),
    SEYLAN       ("Seylan Bank"),
    NATIONS_TRUST("Nations Trust Bank"),
    PAN_ASIA     ("Pan Asia Bank"),
    UNION        ("Union Bank"),
    AMANA        ("Amana Bank"),
    OTHER        ("Other Bank")
}