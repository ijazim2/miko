package com.kittys.premium.domain.model

// ════════════════════════════════════════════════════════════════
//   MIKO — Social Feature Models (Reels, Live, Community)
// ════════════════════════════════════════════════════════════════

/** A short vertical video showcasing products (like Reels/TikTok). */
data class Reel(
    val id: String,
    val vendorId: String,
    val vendorName: String,
    val vendorVerified: Boolean = true,
    val caption: String,
    val musicTitle: String = "Original audio",
    val likes: Int,
    val comments: Int,
    val shares: Int,
    val isLiked: Boolean = false,
    val isFollowing: Boolean = false,
    val taggedProductId: String? = null,
    val taggedProductName: String? = null,
    val taggedProductPriceLkr: Int? = null,
    // val videoUrl: String  // add when wiring real video
)

/** A live shopping broadcast. */
data class LiveStream(
    val id: String,
    val vendorId: String,
    val vendorName: String,
    val title: String,
    val viewerCount: Int,
    val isLive: Boolean,
    val scheduledTime: String? = null,    // for upcoming streams
    val featuredProductId: String? = null,
    val featuredProductName: String? = null,
    val featuredProductPriceLkr: Int? = null,
    val featuredProductOriginalLkr: Int? = null
)

/** A live chat message during a stream. */
data class LiveChatMessage(
    val id: String,
    val userName: String,
    val message: String,
    val isVendor: Boolean = false
)

/** A community feed post (parents sharing looks, tips, reviews). */
data class CommunityPost(
    val id: String,
    val authorName: String,
    val authorBadge: String? = null,       // "Verified Parent", "Top Seller", etc.
    val timeAgo: String,
    val caption: String,
    val imageCount: Int = 1,
    val likes: Int,
    val comments: Int,
    val shares: Int,
    val isLiked: Boolean = false,
    val taggedProductId: String? = null,
    val taggedProductName: String? = null
)

/** A comment on a community post or reel. */
data class SocialComment(
    val id: String,
    val userName: String,
    val text: String,
    val timeAgo: String,
    val likes: Int = 0
)