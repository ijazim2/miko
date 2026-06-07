package com.kittys.premium.core.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.kittys.premium.core.ui.components.MikoBottomBar

// ── Screen imports ──
import com.kittys.premium.features.splash.MikoSplashScreen
import com.kittys.premium.features.onboarding.OnboardingScreen
import com.kittys.premium.features.auth.LoginScreen
import com.kittys.premium.features.auth.SignupScreen
import com.kittys.premium.features.auth.ForgotPasswordScreen
import com.kittys.premium.features.home.HomeScreen
import com.kittys.premium.features.search.SearchScreen
import com.kittys.premium.features.categories.CategoriesScreen
import com.kittys.premium.features.checkout.CheckoutScreen
import com.kittys.premium.features.wishlist.WishlistScreen
import com.kittys.premium.features.product.ProductDetailScreen
import com.kittys.premium.features.notifications.NotificationsScreen
import com.kittys.premium.features.profile.ProfileScreen
import com.kittys.premium.features.profile.EditProfileScreen
import com.kittys.premium.features.profile.ChildProfilesScreen
import com.kittys.premium.features.cart.CartScreen

// Orders + Escrow
import com.kittys.premium.features.orders.OrderHistoryScreen
import com.kittys.premium.features.orders.OrderDetailScreen
import com.kittys.premium.features.orders.LiveTrackingScreen
import com.kittys.premium.features.orders.DeliveryConfirmationScreen
import com.kittys.premium.features.orders.ReportDamageScreen
import com.kittys.premium.features.orders.WriteReviewScreen
import com.kittys.premium.features.orders.OrderSuccessScreen

// AI
import com.kittys.premium.features.ai.AIStylistScreen
import com.kittys.premium.features.ai.outfit.OutfitGeneratorScreen
import com.kittys.premium.features.ai.sizing.SizingEngineScreen

// Admin
import com.kittys.premium.features.admin.AdminPanelScreen
import com.kittys.premium.features.admin.fraud.FraudDetectionScreen
import com.kittys.premium.features.admin.EscrowDisputeScreen

// Social
import com.kittys.premium.features.social.ReelsScreen
import com.kittys.premium.features.social.LiveShoppingScreen
import com.kittys.premium.features.social.CommunityScreen

// Seller flow + product upload
import com.kittys.premium.features.seller.analytics.SellerAnalyticsScreen
import com.kittys.premium.features.seller.dashboard.SellerDashboardScreen
import com.kittys.premium.features.seller.earnings.VendorEarningsScreen
import com.kittys.premium.features.seller.onboarding.SellerBankingScreen
import com.kittys.premium.features.seller.onboarding.SellerIntroScreen
import com.kittys.premium.features.seller.onboarding.SellerStoreInfoScreen
import com.kittys.premium.features.seller.onboarding.SellerVerificationScreen
import com.kittys.premium.features.seller.orders.SellerOrdersScreen
import com.kittys.premium.features.seller.products.ProductUploadAudienceScreen
import com.kittys.premium.features.seller.products.ProductUploadBasicsScreen
import com.kittys.premium.features.seller.products.ProductUploadDetailsScreen
import com.kittys.premium.features.seller.products.ProductUploadReviewScreen
import com.kittys.premium.features.seller.products.ProductUploadShippingScreen
import com.kittys.premium.features.seller.products.ProductUploadVariantsScreen
import com.kittys.premium.features.seller.products.SellerProductsScreen

// ═══════════════════════════════════════════════════════
//   ALL SCREEN ROUTES
// ═══════════════════════════════════════════════════════
sealed class Screen(val route: String) {
    object Splash         : Screen("splash")
    object Onboarding     : Screen("onboarding")

    object Login          : Screen("login")
    object Signup         : Screen("signup")
    object ForgotPassword : Screen("forgot_password")

    object Home           : Screen("home")
    object Search         : Screen("search")
    object AIStylist      : Screen("ai_stylist")
    object Cart           : Screen("cart")
    object Profile        : Screen("profile")

    object Categories     : Screen("categories")
    object ProductList    : Screen("product_list?category={category}&sort={sort}") {
        fun createRoute(category: String? = null, sort: String? = null): String {
            val cat = if (category != null) "?category=$category" else ""
            val srt = if (sort != null) "${if (cat.isEmpty()) "?" else "&"}sort=$sort" else ""
            return "product_list$cat$srt"
        }
    }
    object ProductDetail  : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }

    object Checkout       : Screen("checkout")
    object OrderSuccess   : Screen("order_success/{orderId}?total={total}") {
        fun createRoute(orderId: String, total: Int = 0) = "order_success/$orderId?total=$total"
    }
    object Orders         : Screen("orders")
    object OrderDetail    : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: String) = "order_detail/$orderId"
    }
    object LiveTracking   : Screen("live_tracking/{orderId}") {
        fun createRoute(id: String) = "live_tracking/$id"
    }
    object DeliveryConfirmation : Screen("delivery_confirmation/{orderId}") {
        fun createRoute(id: String) = "delivery_confirmation/$id"
    }
    object ReportDamage   : Screen("report_damage/{orderId}") {
        fun createRoute(id: String) = "report_damage/$id"
    }
    object WriteReview    : Screen("write_review/{orderId}") {
        fun createRoute(id: String) = "write_review/$id"
    }

    object EditProfile    : Screen("edit_profile")
    object ChildProfiles  : Screen("child_profiles")
    object Wishlist       : Screen("wishlist")
    object Notifications  : Screen("notifications")
    object Addresses      : Screen("addresses")
    object PaymentMethods : Screen("payment_methods")
    object HelpSupport    : Screen("help_support")

    object OutfitGenerator : Screen("outfit_generator")
    object SizingEngine    : Screen("sizing_engine")

    object AdminPanel      : Screen("admin_panel")
    object FraudDetection  : Screen("fraud_detection")
    object EscrowDispute   : Screen("escrow_dispute/{reportId}") {
        fun createRoute(id: String) = "escrow_dispute/$id"
    }

    // Seller flow
    object SellerIntro         : Screen("seller_intro")
    object SellerStoreInfo     : Screen("seller_store_info")
    object SellerVerification  : Screen("seller_verification")
    object SellerBanking       : Screen("seller_banking")
    object SellerDashboard     : Screen("seller_dashboard")
    object SellerProducts      : Screen("seller_products")
    object SellerOrders        : Screen("seller_orders")
    object SellerAnalytics     : Screen("seller_analytics")
    object SellerEarnings      : Screen("seller_earnings")

    // Product upload
    object ProductUploadBasics   : Screen("upload_basics")
    object ProductUploadAudience : Screen("upload_audience")
    object ProductUploadVariants : Screen("upload_variants")
    object ProductUploadDetails  : Screen("upload_details")
    object ProductUploadShipping : Screen("upload_shipping")
    object ProductUploadReview   : Screen("upload_review")

    // Social
    object Reels         : Screen("reels")
    object LiveShopping  : Screen("live_shopping")
    object Community     : Screen("community")
}

val bottomNavScreens = setOf(
    Screen.Home.route,
    Screen.Search.route,
    Screen.AIStylist.route,
    Screen.Cart.route,
    Screen.Profile.route
)

// ═══════════════════════════════════════════════════════
//   MAIN NAV GRAPH
// ═══════════════════════════════════════════════════════
@Composable
fun MikoNavGraph(navController: NavHostController) {
    val currentRoute = navController
        .currentBackStackEntryAsState().value?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {

        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route
        ) {
            // Entry
            composable(Screen.Splash.route) { MikoSplashScreen(navController) }
            composable(Screen.Onboarding.route) { OnboardingScreen(navController) }

            // Auth
            composable(Screen.Login.route) { LoginScreen(navController) }
            composable(Screen.Signup.route) { SignupScreen(navController) }
            composable(Screen.ForgotPassword.route) { ForgotPasswordScreen(navController) }

            // Main tabs
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.Search.route) { SearchScreen(navController) }
            composable(Screen.AIStylist.route) { AIStylistScreen(navController) }
            composable(Screen.Cart.route) { CartScreen(navController) }
            composable(Screen.Profile.route) { ProfileScreen(navController) }

            // Categories
            composable(Screen.Categories.route) { CategoriesScreen(navController) }

            // Product list
            composable(
                route = Screen.ProductList.route,
                arguments = listOf(
                    navArgument("category") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("sort") { type = NavType.StringType; nullable = true; defaultValue = null }
                )
            ) {
                CategoriesScreen(navController)
            }

            // Product detail
            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) {
                val productId = it.arguments?.getString("productId") ?: return@composable
                ProductDetailScreen(productId, navController)
            }

            // Checkout & Orders
            composable(Screen.Checkout.route) { CheckoutScreen(navController) }

            composable(
                route = Screen.OrderSuccess.route,
                arguments = listOf(
                    navArgument("orderId") { type = NavType.StringType },
                    navArgument("total") { type = NavType.IntType; defaultValue = 0 }
                )
            ) {
                OrderSuccessScreen(
                    orderId = it.arguments?.getString("orderId") ?: "",
                    totalLkr = it.arguments?.getInt("total") ?: 0,
                    navController = navController
                )
            }

            composable(Screen.Orders.route) { OrderHistoryScreen(navController) }

            composable(
                route = Screen.OrderDetail.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) {
                val orderId = it.arguments?.getString("orderId") ?: return@composable
                OrderDetailScreen(orderId, navController)
            }

            composable(
                Screen.LiveTracking.route,
                listOf(navArgument("orderId") { type = NavType.StringType })
            ) {
                LiveTrackingScreen(it.arguments?.getString("orderId") ?: "", navController)
            }

            composable(
                Screen.DeliveryConfirmation.route,
                listOf(navArgument("orderId") { type = NavType.StringType })
            ) {
                DeliveryConfirmationScreen(it.arguments?.getString("orderId") ?: "", navController)
            }

            composable(
                Screen.ReportDamage.route,
                listOf(navArgument("orderId") { type = NavType.StringType })
            ) {
                ReportDamageScreen(it.arguments?.getString("orderId") ?: "", navController)
            }

            composable(
                Screen.WriteReview.route,
                listOf(navArgument("orderId") { type = NavType.StringType })
            ) {
                WriteReviewScreen(it.arguments?.getString("orderId") ?: "", navController)
            }

            // Profile sub-screens
            composable(Screen.EditProfile.route) { EditProfileScreen(navController) }
            composable(Screen.ChildProfiles.route) { ChildProfilesScreen(navController) }
            composable(Screen.Wishlist.route) { WishlistScreen(navController) }
            composable(Screen.Notifications.route) { NotificationsScreen(navController) }
            composable(Screen.Addresses.route) { EditProfileScreen(navController) }
            composable(Screen.PaymentMethods.route) { EditProfileScreen(navController) }
            composable(Screen.HelpSupport.route) { EditProfileScreen(navController) }

            // AI
            composable(Screen.OutfitGenerator.route) { OutfitGeneratorScreen(navController) }
            composable(Screen.SizingEngine.route) { SizingEngineScreen(navController) }

            // Admin
            composable(Screen.AdminPanel.route) { AdminPanelScreen(navController) }
            composable(Screen.FraudDetection.route) { FraudDetectionScreen(navController) }
            composable(
                Screen.EscrowDispute.route,
                listOf(navArgument("reportId") { type = NavType.StringType })
            ) {
                EscrowDisputeScreen(it.arguments?.getString("reportId") ?: "", navController)
            }

            // Seller flow + product upload
            composable(Screen.SellerIntro.route) { SellerIntroScreen(navController) }
            composable(Screen.SellerStoreInfo.route) { SellerStoreInfoScreen(navController) }
            composable(Screen.SellerVerification.route) { SellerVerificationScreen(navController) }
            composable(Screen.SellerBanking.route) { SellerBankingScreen(navController) }
            composable(Screen.SellerDashboard.route) { SellerDashboardScreen(navController) }
            composable(Screen.SellerProducts.route) { SellerProductsScreen(navController) }
            composable(Screen.SellerOrders.route) { SellerOrdersScreen(navController) }
            composable(Screen.SellerAnalytics.route) { SellerAnalyticsScreen(navController) }
            composable(Screen.SellerEarnings.route) { VendorEarningsScreen(navController) }
            composable(Screen.ProductUploadBasics.route) { ProductUploadBasicsScreen(navController) }
            composable(Screen.ProductUploadAudience.route) { ProductUploadAudienceScreen(navController) }
            composable(Screen.ProductUploadVariants.route) { ProductUploadVariantsScreen(navController) }
            composable(Screen.ProductUploadDetails.route) { ProductUploadDetailsScreen(navController) }
            composable(Screen.ProductUploadShipping.route) { ProductUploadShippingScreen(navController) }
            composable(Screen.ProductUploadReview.route) { ProductUploadReviewScreen(navController) }

            // Social
            composable(Screen.Reels.route) { ReelsScreen(navController) }
            composable(Screen.LiveShopping.route) { LiveShoppingScreen(navController) }
            composable(Screen.Community.route) { CommunityScreen(navController) }
        }

        // Bottom nav bar (only on main tab screens)
        if (currentRoute in bottomNavScreens) {
            MikoBottomBar(
                navController = navController,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}