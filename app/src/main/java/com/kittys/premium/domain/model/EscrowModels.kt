package com.kittys.premium.domain.model

// ════════════════════════════════════════════════════════════════
//   MIKO — Escrow Payment System Models
//   Single source of truth. Matches the Supabase escrow schema.
// ════════════════════════════════════════════════════════════════

/** Tracks the state of held funds for an order. */
data class EscrowTransaction(
    val id: String,
    val orderId: String,
    val customerId: String,
    val vendorId: String,
    val amountLkr: Int,
    val platformFeeLkr: Int = 0,
    val vendorPayoutLkr: Int,
    val status: EscrowStatus,
    val createdAt: Long,
    val deliveredAt: Long? = null,
    val autoReleaseAt: Long? = null,    // = deliveredAt + 7 days
    val releasedAt: Long? = null,
    val refundedAt: Long? = null,
    val confirmationProof: DeliveryProof? = null,
    val damageReport: DamageReport? = null
) {
    /** Days remaining before auto-release (0 if past or not set). */
    fun daysUntilAutoRelease(now: Long = System.currentTimeMillis()): Int {
        val release = autoReleaseAt ?: return 0
        val diff = release - now
        return if (diff <= 0) 0 else (diff / (1000L * 60 * 60 * 24)).toInt()
    }

    val platformFeePercent: Int
        get() = if (amountLkr > 0) ((platformFeeLkr * 100) / amountLkr) else 0
}

/** Each step the money goes through. */
enum class EscrowStatus(val displayName: String) {
    HELD("Payment Held"),
    DELIVERED("Delivered"),
    AWAITING_CONFIRMATION("Awaiting Confirmation"),
    CONFIRMED("Confirmed"),
    DISPUTED("Disputed"),
    UNDER_REVIEW("Under Review"),
    RELEASED_TO_VENDOR("Released to Vendor"),
    REFUNDED_TO_CUSTOMER("Refunded"),
    AUTO_RELEASED("Auto-Released")
}

/** Customer's proof that order arrived in good condition. */
data class DeliveryProof(
    val photoUrls: List<String>,
    val confirmedAt: Long,
    val rating: Int,              // 1-5
    val notes: String = ""
)

/** Customer's report of damage / wrong item. */
data class DamageReport(
    val id: String,
    val orderId: String,
    val customerId: String,
    val type: DamageType,
    val description: String,
    val photoUrls: List<String>,  // Mandatory proof
    val videoUrl: String? = null,
    val reportedAt: Long,
    val refundRequested: Boolean, // refund or replacement
    val adminVerdict: DisputeVerdict? = null,
    val adminNotes: String? = null,
    val resolvedAt: Long? = null
)

enum class DamageType(val displayName: String, val emoji: String) {
    PHYSICAL_DAMAGE("Physical Damage", "💔"),
    WRONG_ITEM("Wrong Item", "📦"),
    WRONG_SIZE("Wrong Size", "📏"),
    MISSING_PARTS("Missing Parts", "🧩"),
    NOT_AS_DESCRIBED("Not as Described", "❓"),
    QUALITY_ISSUE("Quality Issue", "⚠️"),
    OTHER("Other", "📝")
}

enum class DisputeVerdict(val displayName: String) {
    APPROVED_REFUND("Refund Approved"),
    APPROVED_REPLACEMENT("Replacement Approved"),
    PARTIAL_REFUND("Partial Refund"),
    REJECTED("Rejected"),
    PENDING("Pending Review")
}

/** Vendor's wallet — pending vs cleared earnings. */
data class VendorWallet(
    val vendorId: String,
    val pendingLkr: Int,           // In escrow, not yet released
    val availableLkr: Int,         // Cleared and withdrawable
    val totalLifetimeLkr: Int,
    val pendingPayouts: List<EscrowTransaction>,
    val refundedAmountLkr: Int
)