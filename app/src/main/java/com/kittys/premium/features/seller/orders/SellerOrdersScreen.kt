package com.kittys.premium.features.seller.orders

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
import com.kittys.premium.domain.model.formatThousands
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Seller Orders Screen
//   Manage incoming orders: New → Accept → Ship → Delivered
// ════════════════════════════════════════════════════════════════

enum class SellerOrderStatus(val label: String, val color: Color) {
    NEW       ("New",       MikoColors.Info),
    ACCEPTED  ("Accepted",  MikoColors.PrimaryStart),
    SHIPPED   ("Shipped",   MikoColors.Warning),
    DELIVERED ("Delivered", MikoColors.Success),
    CANCELLED ("Cancelled", MikoColors.Error)
}

data class SellerOrder(
    val id: String,
    val customer: String,
    val itemSummary: String,
    val amountLkr: Int,
    val status: SellerOrderStatus,
    val escrowHeld: Boolean,
    val timeAgo: String
)

private val sampleOrders = listOf(
    SellerOrder("12345", "Amali Perera", "Lavender Bow Dress · 4Y · Pink", 7900, SellerOrderStatus.NEW, true, "5 min ago"),
    SellerOrder("12344", "Nisha Rajapaksa", "Sweet Heart Top · 3Y · White", 4900, SellerOrderStatus.ACCEPTED, true, "2 hours ago"),
    SellerOrder("12343", "Priya Thomas", "Ribbon Skirt · 5Y · Lavender", 5900, SellerOrderStatus.SHIPPED, true, "1 day ago"),
    SellerOrder("12342", "Kavya Silva", "Bunny Backpack · Blue", 3900, SellerOrderStatus.DELIVERED, false, "3 days ago")
)

@Composable
fun SellerOrdersScreen(navController: NavController) {

    var selectedTab by remember { mutableStateOf<SellerOrderStatus?>(null) }

    val filtered = remember(selectedTab) {
        if (selectedTab == null) sampleOrders
        else sampleOrders.filter { it.status == selectedTab }
    }

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
            Spacer(Modifier.width(12.dp))
            Text(
                "Orders",
                style = MikoTypography.Title.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )
            // New order count badge
            val newCount = sampleOrders.count { it.status == SellerOrderStatus.NEW }
            if (newCount > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(MikoColors.Info.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        "$newCount new",
                        style = MikoTypography.Caption.copy(
                            color = MikoColors.Info,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        // ─── Status tabs ───
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp)
        ) {
            OrderTab("All", selectedTab == null) { selectedTab = null }
            SellerOrderStatus.values().forEach { status ->
                OrderTab(status.label, selectedTab == status) { selectedTab = status }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (filtered.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().padding(32.dp)
            ) {
                Text("🛒", fontSize = 48.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    "No orders here",
                    style = MikoTypography.Title.copy(
                        color = MikoColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Orders will appear here as customers buy",
                    style = MikoTypography.Body.copy(color = MikoColors.TextSecondary)
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filtered, key = { it.id }) { order ->
                    OrderCard(
                        order = order,
                        onAccept = { /* TODO: viewModel.acceptOrder(order.id) */ },
                        onShip = { /* TODO: viewModel.markShipped(order.id) */ },
                        onView = { /* TODO: navigate to order detail */ }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

// ─── Tab chip ───
@Composable
private fun OrderTab(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .then(
                if (selected)
                    Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                else
                    Modifier.background(MikoColors.SurfaceVariant)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            label,
            style = MikoTypography.Caption.copy(
                color = if (selected) Color.White else MikoColors.TextSecondary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        )
    }
}

// ─── Order card ───
@Composable
private fun OrderCard(
    order: SellerOrder,
    onAccept: () -> Unit,
    onShip: () -> Unit,
    onView: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onView() }
            .padding(16.dp)
    ) {
        // Header: order id + status
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Order #${order.id}",
                style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(order.status.color.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    order.status.label,
                    style = MikoTypography.Caption.copy(
                        color = order.status.color,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp
                    )
                )
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            "${order.customer} · ${order.timeAgo}",
            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
        )

        Spacer(Modifier.height(10.dp))

        // Item + amount
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MikoColors.PastelPink)
            ) {
                Text("🖼", fontSize = 20.sp)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                order.itemSummary,
                style = MikoTypography.Caption.copy(color = MikoColors.TextSecondary),
                modifier = Modifier.weight(1f)
            )
            Text(
                "LKR ${order.amountLkr.formatThousands()}",
                style = MikoTypography.Subtitle.copy(
                    color = MikoColors.PrimaryEnd,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // Escrow indicator
        if (order.escrowHeld) {
            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MikoColors.SuccessSoft)
                    .padding(8.dp)
            ) {
                Text("🔒", fontSize = 13.sp)
                Spacer(Modifier.width(6.dp))
                Text(
                    "Payment secured in escrow — released after delivery",
                    style = MikoTypography.Caption.copy(
                        color = MikoColors.Success,
                        fontSize = 10.sp
                    )
                )
            }
        }

        // Action buttons by status
        when (order.status) {
            SellerOrderStatus.NEW -> {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ActionButton("Accept Order", filled = true, modifier = Modifier.weight(1f), onClick = onAccept)
                    ActionButton("View", filled = false, modifier = Modifier.weight(1f), onClick = onView)
                }
            }
            SellerOrderStatus.ACCEPTED -> {
                Spacer(Modifier.height(12.dp))
                ActionButton("Mark as Shipped", filled = true, modifier = Modifier.fillMaxWidth(), onClick = onShip)
            }
            else -> { /* shipped / delivered / cancelled — no action */ }
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    filled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (filled)
                    Modifier.background(Brush.linearGradient(MikoColors.GradientButton))
                else
                    Modifier
                        .background(MikoColors.Surface)
                        .border(1.5.dp, MikoColors.PrimaryStart, RoundedCornerShape(12.dp))
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Text(
            label,
            style = MikoTypography.Caption.copy(
                color = if (filled) Color.White else MikoColors.PrimaryEnd,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}