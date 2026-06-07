package com.kittys.premium.features.seller.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.domain.model.formatThousands
import com.kittys.premium.core.ui.components.LogoPlaceholder
import com.kittys.premium.core.ui.components.MikoPrimaryButton
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Seller Dashboard
//   The hub: store header, stats, quick actions, recent orders
// ════════════════════════════════════════════════════════════════

@Composable
fun SellerDashboardScreen(navController: NavController) {

    // TODO: pull from SellerViewModel — placeholder data for now
    val storeName = "MIKO Store"
    val sellerLevel = "Premium Seller"
    val rating = 4.8f
    val totalSalesLkr = 1_258_000
    val ordersCount = 320
    val productsCount = 48
    val viewsCount = 8_450

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background)
    ) {
        // ─── Top Bar ───
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .neuRaised(cornerRadius = 20.dp, shadowOffset = 4.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.popBackStack() }
            ) {
                Text("←", fontSize = 18.sp, color = MikoColors.TextPrimary)
            }
            Spacer(Modifier.weight(1f))
            Text(
                "Seller Dashboard",
                style = MikoTypography.Title.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(Modifier.weight(1f))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .neuRaised(cornerRadius = 20.dp, shadowOffset = 4.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.navigate(Screen.Notifications.route) }
            ) {
                Text("🔔", fontSize = 16.sp)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ─── Store header card ───
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .neuRaised(cornerRadius = 18.dp, shadowOffset = 8.dp)
                        .padding(16.dp)
                ) {
                    LogoPlaceholder(size = 56.dp)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            storeName,
                            style = MikoTypography.Title.copy(
                                color = MikoColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(Brush.linearGradient(MikoColors.GradientPrimary))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    sellerLevel,
                                    style = MikoTypography.Caption.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "⭐ $rating",
                                style = MikoTypography.Caption.copy(
                                    color = MikoColors.TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            // ─── Earnings highlight ───
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Brush.linearGradient(MikoColors.GradientButton))
                        .padding(20.dp)
                ) {
                    Text(
                        "Total Sales",
                        style = MikoTypography.Caption.copy(color = Color.White.copy(alpha = 0.8f))
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "LKR ${totalSalesLkr.formatThousands()}",
                        style = MikoTypography.Display.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { navController.navigate(Screen.SellerEarnings.route) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "View Earnings  →",
                            style = MikoTypography.Caption.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            // ─── Stats grid ───
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("📦", "$ordersCount", "Orders", Modifier.weight(1f))
                    StatCard("🏷", "$productsCount", "Products", Modifier.weight(1f))
                    StatCard("👁", viewsCount.formatThousands(), "Views", Modifier.weight(1f))
                }
            }

            // ─── Add Product CTA ───
            item {
                MikoPrimaryButton(
                    text = "+ Add New Product",
                    trailingIcon = null,
                    onClick = { navController.navigate(Screen.ProductUploadBasics.route) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ─── Quick actions ───
            item {
                Text(
                    "Manage",
                    style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickAction("📋", "My Products", Modifier.weight(1f)) {
                        navController.navigate(Screen.SellerProducts.route)
                    }
                    QuickAction("🛒", "Orders", Modifier.weight(1f)) {
                        navController.navigate(Screen.SellerOrders.route)
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickAction("💰", "Earnings", Modifier.weight(1f)) {
                        navController.navigate(Screen.SellerEarnings.route)
                    }
                    QuickAction("📊", "Analytics", Modifier.weight(1f)) {
                        navController.navigate(Screen.SellerAnalytics.route)
                    }
                }
            }

            // ─── Recent orders ───
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Recent Orders",
                        style = MikoTypography.Subtitle.copy(
                            color = MikoColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "View All",
                        style = MikoTypography.Caption.copy(
                            color = MikoColors.PrimaryEnd,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { navController.navigate(Screen.SellerOrders.route) }
                    )
                }
            }

            items(sampleRecentOrders) { order ->
                RecentOrderRow(order)
            }

            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

// ─── Stat card ───
@Composable
private fun StatCard(emoji: String, value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .neuRaised(cornerRadius = 14.dp, shadowOffset = 6.dp)
            .padding(vertical = 16.dp)
    ) {
        Text(emoji, fontSize = 22.sp)
        Spacer(Modifier.height(6.dp))
        Text(
            value,
            style = MikoTypography.Subtitle.copy(
                color = MikoColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            label,
            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted, fontSize = 10.sp)
        )
    }
}

// ─── Quick action ───
@Composable
private fun QuickAction(emoji: String, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .neuRaised(cornerRadius = 14.dp, shadowOffset = 6.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(16.dp)
    ) {
        Text(emoji, fontSize = 22.sp)
        Spacer(Modifier.width(10.dp))
        Text(
            label,
            style = MikoTypography.Body.copy(
                color = MikoColors.TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

// ─── Recent order row ───
private data class RecentOrder(
    val id: String,
    val amount: Int,
    val status: String,
    val statusColor: Color
)

private val sampleRecentOrders = listOf(
    RecentOrder("#12345", 7900, "Delivered", MikoColors.Success),
    RecentOrder("#12344", 4900, "Shipped", MikoColors.Info),
    RecentOrder("#12343", 5900, "Processing", MikoColors.Warning)
)

@Composable
private fun RecentOrderRow(order: RecentOrder) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .neuRaised(cornerRadius = 12.dp, shadowOffset = 4.dp)
            .padding(14.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MikoColors.PastelPink)
        ) {
            Text("📦", fontSize = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                "Order ${order.id}",
                style = MikoTypography.Body.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                "LKR ${order.amount.formatThousands()}",
                style = MikoTypography.Caption.copy(color = MikoColors.TextSecondary)
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(order.statusColor.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                order.status,
                style = MikoTypography.Caption.copy(
                    color = order.statusColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                )
            )
        }
    }
}