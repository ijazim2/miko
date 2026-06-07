package com.kittys.premium.features.orders

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.domain.model.EscrowStatus
import com.kittys.premium.domain.model.OrderStatus
import com.kittys.premium.domain.model.formatThousands
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Order History Screen
//   List of customer orders with status filters + escrow indicators
// ════════════════════════════════════════════════════════════════

private data class OrderFilterTab(val label: String, val status: OrderStatus?)

private val orderFilters = listOf(
    OrderFilterTab("All", null),
    OrderFilterTab("Active", OrderStatus.OUT_FOR_DELIVERY),
    OrderFilterTab("Delivered", OrderStatus.DELIVERED),
    OrderFilterTab("Cancelled", OrderStatus.CANCELLED)
)

@Composable
fun OrderHistoryScreen(
    navController: NavController,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val state by viewModel.listState.collectAsState()
    var selectedFilter by remember { mutableStateOf<OrderStatus?>(null) }

    val orders = remember(state.orders, selectedFilter) {
        if (selectedFilter == null) state.orders
        else state.orders.filter { it.status == selectedFilter }
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
                "My Orders",
                style = MikoTypography.Title.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                )
            )
        }

        // ─── Filter tabs ───
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            items(orderFilters) { tab ->
                val selected = selectedFilter == tab.status
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .then(
                            if (selected) Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                            else Modifier.background(MikoColors.SurfaceVariant)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { selectedFilter = tab.status }
                        .padding(horizontal = 18.dp, vertical = 9.dp)
                ) {
                    Text(
                        tab.label,
                        style = MikoTypography.Caption.copy(
                            color = if (selected) Color.White else MikoColors.TextSecondary,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = MikoColors.PrimaryStart)
                }
            }
            orders.isEmpty() -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize().padding(32.dp)
                ) {
                    Text("📦", fontSize = 60.sp)
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "No orders yet",
                        style = MikoTypography.Headline.copy(
                            color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "When you order, it'll appear here",
                        style = MikoTypography.Body.copy(color = MikoColors.TextMuted)
                    )
                    Spacer(Modifier.height(24.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.linearGradient(MikoColors.GradientButton))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { navController.navigate(Screen.Categories.route) }
                            .padding(horizontal = 32.dp, vertical = 14.dp)
                    ) {
                        Text("Start Shopping", style = MikoTypography.Button.copy(
                            color = Color.White, fontWeight = FontWeight.Bold
                        ))
                    }
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders, key = { it.id }) { order ->
                        OrderHistoryRow(
                            order = order,
                            onClick = {
                                viewModel.selectOrder(order.id)
                                navController.navigate(Screen.OrderDetail.createRoute(order.id))
                            }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// ─── Order row ───
@Composable
private fun OrderHistoryRow(order: CustomerOrder, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(16.dp)
    ) {
        // Header: order id + status
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Order #${order.id}",
                style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )
            OrderStatusBadge(order.status)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "${order.vendorName} · ${order.placedAt}",
            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
        )

        Spacer(Modifier.height(12.dp))

        // Item summary
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MikoColors.PastelPink)
            ) { Text("🖼", fontSize = 22.sp) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    order.firstItemName,
                    style = MikoTypography.Body.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
                Text(
                    "${order.itemCount} ${if (order.itemCount == 1) "item" else "items"}",
                    style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                )
            }
            Text(
                "LKR ${order.totalLkr.formatThousands()}",
                style = MikoTypography.Subtitle.copy(
                    color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
                )
            )
        }

        // Escrow indicator
        val showEscrow = order.escrow.status in listOf(
            EscrowStatus.HELD, EscrowStatus.AWAITING_CONFIRMATION
        )
        if (showEscrow) {
            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (order.escrow.status == EscrowStatus.AWAITING_CONFIRMATION)
                            MikoColors.WarningSoft else MikoColors.SuccessSoft
                    )
                    .padding(10.dp)
            ) {
                Text(
                    if (order.escrow.status == EscrowStatus.AWAITING_CONFIRMATION) "⏳" else "🔒",
                    fontSize = 13.sp
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (order.escrow.status == EscrowStatus.AWAITING_CONFIRMATION)
                        "Confirm delivery to release payment (${order.escrow.daysUntilAutoRelease()}d left)"
                    else
                        "Payment secured in escrow",
                    style = MikoTypography.Caption.copy(
                        color = if (order.escrow.status == EscrowStatus.AWAITING_CONFIRMATION)
                            MikoColors.Warning else MikoColors.Success,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
private fun OrderStatusBadge(status: OrderStatus) {
    val (color, label) = when (status) {
        OrderStatus.PENDING -> MikoColors.TextMuted to "Pending"
        OrderStatus.CONFIRMED -> MikoColors.Info to "Confirmed"
        OrderStatus.PROCESSING -> MikoColors.Info to "Processing"
        OrderStatus.SHIPPED -> MikoColors.Info to "Shipped"
        OrderStatus.OUT_FOR_DELIVERY -> MikoColors.Warning to "Out for Delivery"
        OrderStatus.DELIVERED -> MikoColors.Success to "Delivered"
        OrderStatus.CANCELLED -> MikoColors.Error to "Cancelled"
        OrderStatus.RETURNED -> MikoColors.Error to "Returned"
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(
            label,
            style = MikoTypography.Caption.copy(
                color = color, fontWeight = FontWeight.SemiBold, fontSize = 10.sp
            )
        )
    }
}