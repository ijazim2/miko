package com.kittys.premium.core.common

// ═══════════════════════════════════════════════════════
//   MIKO — App-wide Constants
// ═══════════════════════════════════════════════════════

object Constants {

    // ── App ──────────────────────────────────────────
    const val APP_NAME             = "MIKO"
    const val APP_PACKAGE          = "com.kittys.premium"
    const val SUPPORT_EMAIL        = "support@miko.lk"
    const val SUPPORT_PHONE        = "+94767203437"
    const val SUPPORT_WHATSAPP     = "+94767203437"
    const val PLAYSTORE_URL        = "https://play.google.com/store/apps/details?id=$APP_PACKAGE"

    // ── Business rules ───────────────────────────────
    const val FREE_DELIVERY_THRESHOLD_LKR = 3000
    const val STANDARD_DELIVERY_FEE_LKR   = 350
    const val EXPRESS_DELIVERY_FEE_LKR    = 650
    const val MIN_REVIEW_LENGTH            = 10
    const val MAX_REVIEW_LENGTH            = 500
    const val MAX_CART_ITEM_QTY            = 10
    const val MAX_PHOTOS_PER_REVIEW        = 3
    const val MAX_PRODUCT_PHOTOS           = 5
    const val REVIEW_RETURN_WINDOW_DAYS    = 7
    const val VENDOR_APPROVAL_DAYS         = 3

    // ── Pagination ───────────────────────────────────
    const val PAGE_SIZE                    = 20
    const val FEATURED_PAGE_SIZE           = 10
    const val SEARCH_DEBOUNCE_MS           = 420L

    // ── Cache ────────────────────────────────────────
    const val CACHE_STALE_THRESHOLD_MS     = 3_600_000L   // 1 hour
    const val MAX_RECENT_SEARCHES          = 5

    // ── AI ───────────────────────────────────────────
    const val GEMINI_MODEL                 = "gemini-2.0-flash"
    const val AI_SYSTEM_PROMPT             = """
        You are MIKO AI, a friendly and expert children's fashion stylist for MIKO,
        Sri Lanka's premier kids fashion marketplace. You speak warmly and knowledgeably.
        You help parents find the right sizes, styles, and outfits for their children.
        Keep responses concise (2-4 sentences) unless asked for detail.
        You know about Sri Lankan culture, weather, school uniforms, and local occasions like Avurudu.
        Always suggest products naturally without being pushy.
        If asked about sizing, use age + height + weight for accuracy.
        Respond in the same language the user writes in (English, Sinhala, or Tamil).
    """

    // ── Supabase table names ─────────────────────────
    object Tables {
        const val USERS          = "users"
        const val PRODUCTS       = "products"
        const val CART           = "cart"
        const val ORDERS         = "orders"
        const val ORDER_ITEMS    = "order_items"
        const val WISHLIST       = "wishlist"
        const val CHILD_PROFILES = "child_profiles"
        const val REVIEWS        = "reviews"
        const val VENDORS        = "vendors"
        const val BANNERS        = "banners"
        const val NOTIFICATIONS  = "notifications"
        const val POSTS          = "community_posts"
        const val REELS          = "reels"
        const val WARDROBE       = "wardrobe"
        const val PROMO_CODES    = "promo_codes"
    }

    // ── Supabase storage buckets ─────────────────────
    object Buckets {
        const val PRODUCT_IMAGES  = "product-images"
        const val AVATARS         = "avatars"
        const val REVIEW_PHOTOS   = "review-photos"
        const val POST_IMAGES     = "post-images"
        const val REEL_VIDEOS     = "reel-videos"
        const val BANNERS         = "banners"
    }

    // ── Deep link scheme ─────────────────────────────
    object DeepLinks {
        const val SCHEME          = "miko://"
        const val HOME            = "miko://home"
        const val CART            = "miko://cart"
        const val AI              = "miko://ai"
        const val ORDERS          = "miko://orders"
        const val PRODUCT         = "miko://product/"     // + productId
        const val ORDER           = "miko://order/"       // + orderId
    }

    // ── FCM notification types ───────────────────────
    object NotifTypes {
        const val ORDER    = "order"
        const val PROMO    = "promo"
        const val AI       = "ai"
        const val SYSTEM   = "system"
        const val WISHLIST = "wishlist"
        const val LIVE     = "live"
    }

    // ── DataStore keys (string values) ───────────────
    object PrefKeys {
        const val ONBOARDING_SEEN    = "onboarding_seen"
        const val SAVED_USER_ID      = "saved_user_id"
        const val SELECTED_LANGUAGE  = "selected_language"
        const val NOTIFICATIONS_ON   = "notifications_on"
        const val AI_SUGGESTIONS_ON  = "ai_suggestions_on"
        const val FCM_TOKEN          = "fcm_token"
    }

    // ── Sri Lanka specific ───────────────────────────
    object SriLanka {
        val DISTRICTS = listOf(
            "Colombo","Gampaha","Kalutara","Kandy","Matale","Nuwara Eliya",
            "Galle","Matara","Hambantota","Jaffna","Kilinochchi","Mannar",
            "Vavuniya","Mullaitivu","Batticaloa","Ampara","Trincomalee",
            "Kurunegala","Puttalam","Anuradhapura","Polonnaruwa","Badulla",
            "Monaragala","Ratnapura","Kegalle"
        )
        val PAYMENT_METHODS = listOf(
            "Credit / Debit Card","Cash on Delivery","eZ Cash","mCash","FriMi"
        )
        const val CURRENCY              = "LKR"
        const val CURRENCY_SYMBOL       = "LKR"
        val OCCASION_TAGS               = listOf(
            "Casual","Birthday","School","Party","Sport",
            "Avurudu","Beach","Poya Day","Formal","Outdoor"
        )
    }
}