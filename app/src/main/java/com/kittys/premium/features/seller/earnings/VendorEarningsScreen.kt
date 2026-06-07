package com.kittys.premium.features.seller.earnings

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
//   MIKO — Vendor Earnings Screen
//   Available balance + pending escrow + stats + pending payouts
// ════════════════════════════════════════════════════════════════

@Composable
fun VendorEarningsScreen(navController: NavController) {
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
                "Earnings",
                style = MikoTypography.Title.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ─── 1. AVAILABLE BALANCE (Hero Card) ───
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.linearGradient(MikoColors.GradientButton))
                        .padding(24.dp)
                ) {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "AVAILABLE BALANCE",
                                style = MikoTypography.Caption.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    letterSpacing = 1.5.sp
                                )
                            )
                            Text("💰", fontSize = 28.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "LKR ${156_800.formatThousands()}",
                            style = MikoTypography.Display.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp
                            )
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Ready to withdraw",
                            style = MikoTypography.Caption.copy(color = Color.White.copy(alpha = 0.85f))
                        )
                        Spacer(Modifier.height(16.dp))

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color.White)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { /* TODO: withdraw flow */ }
                                .padding(vertical = 14.dp)
                        ) {
                            Text(
                                "Withdraw to Bank",
                                style = MikoTypography.Subtitle.copy(
                                    brush = Brush.linearGradient(MikoColors.GradientButton),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            // ─── 2. PENDING ESCROW ───
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(MikoColors.PastelButter)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⏳", fontSize = 32.sp)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Pending in Escrow",
                                style = MikoTypography.Caption.copy(
                                    color = MikoColors.TextMuted,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                "LKR ${42_300.formatThousands()}",
                                style = MikoTypography.Headline.copy(
                                    color = MikoColors.TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                )
                            )
                            Text(
                                "From 12 orders awaiting customer confirmation",
                                style = MikoTypography.Caption.copy(color = MikoColors.TextSecondary)
                            )
                        }
                    }
                }
            }

            // ─── 3. EARNING STATS ───
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatBox("📈", "LKR 284K", "This Month", Modifier.weight(1f))
                    StatBox("💎", "LKR 2.8M", "Lifetime", Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatBox("📊", "5%", "Platform Fee", Modifier.weight(1f))
                    StatBox("↩", "LKR 8K", "Refunded", Modifier.weight(1f))
                }
            }

            // ─── 4. PENDING PAYOUTS ───
            item {
                Text(
                    "Pending Payouts",
                    style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            items(3) { index ->
                PendingPayoutRow(
                    orderId = "ORD-${1234 + index}",
                    amount = listOf(3200, 5400, 2800)[index],
                    daysLeft = listOf(2, 5, 6)[index],
                    customer = listOf("Amali P.", "Nisha R.", "Priya T.")[index]
                )
            }

            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun StatBox(emoji: String, value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .neuRaised(cornerRadius = 16.dp, shadowOffset = 5.dp)
            .padding(vertical = 14.dp)
    ) {
        Text(emoji, fontSize = 22.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            style = MikoTypography.Subtitle.copy(
                color = MikoColors.PrimaryEnd,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            label,
            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted, fontSize = 10.sp)
        )
    }
}

@Composable
private fun PendingPayoutRow(
    orderId: String,
    amount: Int,
    daysLeft: Int,
    customer: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .neuRaised(cornerRadius = 14.dp, shadowOffset = 4.dp)
            .padding(14.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(MikoColors.PastelButter)
        ) {
            Text("⏳", fontSize = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                "#$orderId",
                style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                "$customer • Releases in ${daysLeft}d",
                style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
            )
        }
        Text(
            "LKR ${amount.formatThousands()}",
            style = MikoTypography.Subtitle.copy(
                color = MikoColors.PrimaryEnd,
                fontWeight = FontWeight.Bold
            )
        )
    }
}