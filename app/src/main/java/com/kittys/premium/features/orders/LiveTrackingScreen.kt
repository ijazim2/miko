package com.kittys.premium.features.orders

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Live Tracking Screen
//   Map placeholder + driver card + ETA + progress
// ════════════════════════════════════════════════════════════════

@Composable
fun LiveTrackingScreen(
    orderId: String,
    navController: NavController,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val order by viewModel.selectedOrder.collectAsState()

    LaunchedEffect(orderId) {
        if (order == null || order?.id != orderId) viewModel.selectOrder(orderId)
    }

    val current = order
    val driverName = current?.driverName ?: "Your driver"
    val eta = current?.driverEtaMinutes ?: 20

    Box(modifier = Modifier.fillMaxSize().background(MikoColors.Background)) {

        // ─── Map placeholder area ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .background(
                    Brush.linearGradient(
                        listOf(MikoColors.PastelLavender, MikoColors.PastelSky)
                    )
                )
        ) {
            // Faux map grid
            MapGridDecoration()

            // Route line + markers (simple visual)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text("🏪", fontSize = 30.sp)
                AnimatedRouteDots()
                Text("🛵", fontSize = 34.sp)
                AnimatedRouteDots()
                Text("🏠", fontSize = 30.sp)
            }

            Text(
                "Live map view",
                style = MikoTypography.Caption.copy(color = MikoColors.TextMuted),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 14.dp)
            )

            // Back button overlay
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MikoColors.Surface)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.popBackStack() }
            ) {
                Text("←", fontSize = 18.sp, color = MikoColors.TextPrimary)
            }
        }

        // ─── Bottom sheet: driver + ETA ───
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MikoColors.Background)
                .padding(20.dp)
        ) {
            // grab handle
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(44.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MikoColors.BorderMedium)
            )

            Spacer(Modifier.height(18.dp))

            // ETA banner
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(MikoColors.GradientButton))
                    .padding(16.dp)
            ) {
                Text("⏱", fontSize = 26.sp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "Arriving in $eta minutes",
                        style = MikoTypography.Subtitle.copy(
                            color = Color.White, fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        "Your order is on the way 🎉",
                        style = MikoTypography.Caption.copy(color = Color.White.copy(alpha = 0.9f))
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Driver card
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp)
                    .padding(14.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(MikoColors.GradientPrimary))
                ) {
                    Text(driverName.take(1).uppercase(), color = Color.White,
                        style = MikoTypography.Title.copy(fontWeight = FontWeight.Bold))
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(driverName, style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                    ))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐ 4.9", style = MikoTypography.Caption.copy(color = MikoColors.TextSecondary))
                        Spacer(Modifier.width(8.dp))
                        Text("· MIKO Delivery", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                    }
                }
                // Call + chat
                CircleAction("📞") { /* TODO: call */ }
                Spacer(Modifier.width(8.dp))
                CircleAction("💬") { /* TODO: chat */ }
            }

            Spacer(Modifier.height(16.dp))

            // Delivery progress
            val stages = listOf("Picked up", "On the way", "Almost there", "Delivered")
            val currentStage = 1
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                stages.forEachIndexed { i, stage ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .then(
                                    if (i <= currentStage) Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                                    else Modifier.background(MikoColors.BorderLight)
                                )
                        ) {
                            if (i <= currentStage) Text("✓", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            stage,
                            style = MikoTypography.Caption.copy(
                                color = if (i <= currentStage) MikoColors.PrimaryEnd else MikoColors.TextMuted,
                                fontSize = 8.sp,
                                fontWeight = if (i == currentStage) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Delivery address
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MikoColors.SurfaceVariant)
                    .padding(12.dp)
            ) {
                Text("📍", fontSize = 16.sp)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Delivering to", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted, fontSize = 10.sp))
                    Text("Home · Colombo 05", style = MikoTypography.Caption.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold
                    ))
                }
            }
        }
    }
}

// ─── Animated route dots ───
@Composable
private fun AnimatedRouteDots() {
    val transition = rememberInfiniteTransition(label = "route")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        repeat(3) { i ->
            val alpha by transition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = i * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot$i"
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 3.dp)
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(MikoColors.PrimaryStart.copy(alpha = alpha))
            )
        }
    }
}

// ─── Faux map grid lines ───
@Composable
private fun MapGridDecoration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val step = 60f
        val lineColor = Color.White.copy(alpha = 0.35f)
        var x = 0f
        while (x < size.width) {
            drawLine(lineColor, start = androidx.compose.ui.geometry.Offset(x, 0f),
                end = androidx.compose.ui.geometry.Offset(x, size.height), strokeWidth = 1.5f)
            x += step
        }
        var y = 0f
        while (y < size.height) {
            drawLine(lineColor, start = androidx.compose.ui.geometry.Offset(0f, y),
                end = androidx.compose.ui.geometry.Offset(size.width, y), strokeWidth = 1.5f)
            y += step
        }
    }
}

@Composable
private fun CircleAction(emoji: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MikoColors.PastelPink)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Text(emoji, fontSize = 16.sp)
    }
}