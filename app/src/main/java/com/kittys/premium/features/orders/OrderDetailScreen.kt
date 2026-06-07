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
import com.kittys.premium.domain.model.OrderStatus
import com.kittys.premium.domain.model.formatThousands
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Order Detail Screen
//   Items · tracking timeline · escrow card · actions
// ════════════════════════════════════════════════════════════════

@Composable
fun OrderDetailScreen(
    orderId: String,
    navController: NavController,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val order by viewModel.selectedOrder.collectAsState()

    LaunchedEffect(orderId) {
        if (order == null || order?.id != orderId) viewModel.selectOrder(orderId)
    }

    val current = order
    if (current == null) {
        Box(Modifier.fillMaxSize().background(MikoColors.Background), Alignment.Center) {
            CircularProgressIndicator(color = MikoColors.PrimaryStart)
        }
        return
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
            Column(Modifier.weight(1f)) {
                Text(
                    "Order #${current.id}",
                    style = MikoTypography.Title.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    current.placedAt,
                    style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ─── Live tracking banner (only if out for delivery) ───
            if (current.status == OrderStatus.OUT_FOR_DELIVERY && current.driverEtaMinutes != null) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.linearGradient(MikoColors.GradientButton))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { navController.navigate(Screen.LiveTracking.createRoute(current.id)) }
                            .padding(16.dp)
                    ) {
                        Text("🚚", fontSize = 28.sp)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Arriving in ${current.driverEtaMinutes} min",
                                style = MikoTypography.Subtitle.copy(
                                    color = Color.White, fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                "${current.driverName} is on the way · Tap to track live",
                                style = MikoTypography.Caption.copy(color = Color.White.copy(alpha = 0.9f))
                            )
                        }
                        Text("→", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ─── Escrow card ───
            item {
                EscrowStatusCard(
                    escrow = current.escrow,
                    onConfirmDelivery = {
                        navController.navigate(Screen.DeliveryConfirmation.createRoute(current.id))
                    },
                    onReportDamage = {
                        navController.navigate(Screen.ReportDamage.createRoute(current.id))
                    }
                )
            }

            // ─── Items ───
            item {
                Text("Items", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
            }
            items(current.items) { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .neuRaised(cornerRadius = 14.dp, shadowOffset = 4.dp)
                        .padding(12.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp)).background(MikoColors.PastelPink)
                    ) { Text("🖼", fontSize = 24.sp) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(item.name, style = MikoTypography.Subtitle.copy(
                            color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold
                        ), maxLines = 1)
                        Text("${item.size} · ${item.color} · Qty ${item.quantity}",
                            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                    }
                    Text("LKR ${(item.priceInt * item.quantity).formatThousands()}",
                        style = MikoTypography.Subtitle.copy(
                            color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
                        ))
                }
            }

            // ─── Tracking timeline ───
            item {
                Text("Tracking", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp)
                        .padding(16.dp)
                ) {
                    current.tracking.forEachIndexed { index, step ->
                        TrackingRow(step, isLast = index == current.tracking.size - 1)
                    }
                }
            }

            // ─── Vendor + summary ───
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(36.dp).clip(CircleShape)
                                .background(Brush.linearGradient(MikoColors.GradientPrimary))
                        ) { Text("🏪", fontSize = 16.sp) }
                        Spacer(Modifier.width(10.dp))
                        Text("Sold by ${current.vendorName}", style = MikoTypography.Body.copy(
                            color = MikoColors.TextPrimary, fontWeight = FontWeight.Medium
                        ))
                    }
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = MikoColors.Divider)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Order Total", style = MikoTypography.Subtitle.copy(
                            color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                        ))
                        Text("LKR ${current.totalLkr.formatThousands()}", style = MikoTypography.Title.copy(
                            color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
                        ))
                    }
                }
            }

            // ─── Review CTA (if delivered/completed) ───
            if (current.status == OrderStatus.DELIVERED) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MikoColors.Surface)
                            .border(1.5.dp, MikoColors.PrimaryStart, RoundedCornerShape(16.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { navController.navigate(Screen.WriteReview.createRoute(current.id)) }
                    ) {
                        Text("⭐ Write a Review", style = MikoTypography.Button.copy(
                            color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
                        ))
                    }
                }
            }

            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

// ─── Tracking timeline row ───
@Composable
private fun TrackingRow(step: TrackingStep, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Dot + connector line
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .then(
                        when {
                            step.isCurrent -> Modifier.background(Brush.linearGradient(MikoColors.GradientButton))
                            step.isDone -> Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                            else -> Modifier.background(MikoColors.BorderLight)
                        }
                    )
            ) {
                if (step.isDone || step.isCurrent) {
                    Text("✓", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(MikoColors.TextMuted))
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(34.dp)
                        .background(if (step.isDone) MikoColors.PrimaryStart else MikoColors.BorderLight)
                )
            }
        }
        Spacer(Modifier.width(14.dp))
        // Step text
        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 14.dp)) {
            Text(
                step.title,
                style = MikoTypography.Subtitle.copy(
                    color = if (step.isDone || step.isCurrent) MikoColors.TextPrimary else MikoColors.TextMuted,
                    fontWeight = if (step.isCurrent) FontWeight.Bold else FontWeight.SemiBold
                )
            )
            Text(
                step.description,
                style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
            )
            if (step.isCurrent) {
                Text(
                    step.timestamp,
                    style = MikoTypography.Caption.copy(
                        color = MikoColors.PrimaryEnd, fontWeight = FontWeight.SemiBold, fontSize = 10.sp
                    )
                )
            } else if (step.isDone) {
                Text(
                    step.timestamp,
                    style = MikoTypography.Caption.copy(color = MikoColors.TextMuted, fontSize = 10.sp)
                )
            }
        }
    }
}