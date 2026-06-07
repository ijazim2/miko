package com.kittys.premium.features.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kittys.premium.domain.model.formatThousands
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Admin Panel
//   Models (AdminStats, FlaggedItem, AdminUiState) live in AdminViewModel.kt
// ════════════════════════════════════════════════════════════════

@Composable
fun AdminPanelScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Overview", "Vendors", "Users", "Fraud", "Analytics")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background)
    ) {
        // ─── Admin Header ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .background(Brush.linearGradient(MikoColors.GradientButton))
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            "ADMIN CONSOLE",
                            style = MikoTypography.Caption.copy(
                                color = Color.White.copy(alpha = 0.7f),
                                letterSpacing = 1.5.sp
                            )
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            "MIKO",
                            style = MikoTypography.Headline.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    if (uiState.flaggedItems.isNotEmpty()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MikoColors.Error.copy(alpha = 0.9f))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                "⚠ ${uiState.flaggedItems.size} Flags",
                                style = MikoTypography.Caption.copy(
                                    color = Color.White, fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AdminKpiBubble("Users", "${uiState.stats.totalUsers}")
                    AdminKpiBubble("Vendors", "${uiState.stats.totalVendors}")
                    AdminKpiBubble("Orders", "${uiState.stats.totalOrders}")
                    AdminKpiBubble("Revenue", "LKR ${uiState.stats.totalRevenueLkr / 1000}K")
                }
            }
        }

        // ─── Tabs ───
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(vertical = 14.dp)
        ) {
            itemsIndexed(tabs) { index, tab ->
                AdminTab(tab, uiState.selectedTab == index) { viewModel.setTab(index) }
            }
        }

        // ─── Content ───
        when (uiState.selectedTab) {
            0 -> AdminOverviewTab(uiState)
            1 -> AdminVendorsTab(uiState)
            2 -> AdminUsersTab(uiState)
            3 -> AdminFraudTab(uiState, onResolve = { viewModel.resolveFlag(it) })
            4 -> AdminAnalyticsTab(uiState)
        }
    }
}

// ─── Tab chip ───
@Composable
private fun AdminTab(label: String, selected: Boolean, onClick: () -> Unit) {
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
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 9.dp)
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

// ═══ OVERVIEW TAB ═══
@Composable
private fun AdminOverviewTab(uiState: AdminUiState) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .neuRaised(cornerRadius = 18.dp, shadowOffset = 8.dp)
                    .padding(20.dp)
            ) {
                Text("Today's Snapshot", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
                Spacer(Modifier.height(14.dp))
                Row(Modifier.fillMaxWidth()) {
                    AdminMiniStat("📦 Orders", "${uiState.stats.todayOrders}", Modifier.weight(1f))
                    Spacer(Modifier.width(10.dp))
                    AdminMiniStat("💰 Revenue", "LKR ${uiState.stats.todayRevenue.formatThousands()}", Modifier.weight(1f))
                    Spacer(Modifier.width(10.dp))
                    AdminMiniStat("🎫 Tickets", "${uiState.stats.openTickets}", Modifier.weight(1f))
                }
            }
        }

        if (uiState.stats.pendingVendors > 0) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MikoColors.WarningSoft)
                        .border(1.dp, MikoColors.Warning.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text("⏳", fontSize = 22.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "${uiState.stats.pendingVendors} Vendors Awaiting Approval",
                            style = MikoTypography.Subtitle.copy(
                                color = MikoColors.Warning, fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text("Review new vendor applications",
                            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                    }
                    Text("Review →", style = MikoTypography.Label.copy(color = MikoColors.Warning))
                }
            }
        }

        item {
            Text("Top Vendors This Month", style = MikoTypography.Subtitle.copy(
                color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
            ))
        }
        items(uiState.topVendors) { (name, revenue) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .neuRaised(cornerRadius = 14.dp, shadowOffset = 5.dp)
                    .padding(14.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(MikoColors.GradientPrimary))
                ) { Text("🏪", fontSize = 18.sp) }
                Spacer(Modifier.width(12.dp))
                Text(name, style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold
                ), modifier = Modifier.weight(1f))
                Text("LKR ${revenue.formatThousands()}", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
                ))
            }
        }

        item {
            Text("Recent Signups", style = MikoTypography.Subtitle.copy(
                color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
            ))
            Spacer(Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp)
                    .padding(16.dp)
            ) {
                uiState.recentSignups.forEachIndexed { i, name ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(34.dp).clip(CircleShape)
                                .background(Brush.linearGradient(MikoColors.GradientPrimary))
                        ) { Text(name.take(1), style = MikoTypography.Label.copy(color = Color.White)) }
                        Spacer(Modifier.width(12.dp))
                        Text(name, style = MikoTypography.Body.copy(color = MikoColors.TextPrimary), modifier = Modifier.weight(1f))
                        Text("New", style = MikoTypography.Caption.copy(color = MikoColors.Success))
                    }
                    if (i < uiState.recentSignups.size - 1) {
                        HorizontalDivider(color = MikoColors.Divider.copy(alpha = 0.4f))
                    }
                }
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

// ═══ VENDORS TAB ═══
@Composable
private fun AdminVendorsTab(uiState: AdminUiState) {
    val vendorStatus = listOf(
        Triple("Little Stars Fashion", "Verified ✓", MikoColors.Success),
        Triple("Kids World LK", "Verified ✓", MikoColors.Success),
        Triple("New Baby Shop", "Pending ⏳", MikoColors.Warning),
        Triple("Tiny Threads", "Pending ⏳", MikoColors.Warning),
        Triple("FashionKids99", "Suspended ✗", MikoColors.Error)
    )
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("${uiState.stats.totalVendors} Vendors", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
                Text("${uiState.stats.pendingVendors} pending",
                    style = MikoTypography.Caption.copy(color = MikoColors.Warning))
            }
            Spacer(Modifier.height(6.dp))
        }
        items(vendorStatus) { (name, status, color) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .neuRaised(cornerRadius = 14.dp, shadowOffset = 5.dp)
                    .padding(14.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                        .background(MikoColors.PastelPink)
                ) { Text("🏪", fontSize = 18.sp) }
                Spacer(Modifier.width(12.dp))
                Text(name, style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold
                ), modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(status, style = MikoTypography.Caption.copy(color = color, fontWeight = FontWeight.SemiBold))
                }
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

// ═══ USERS TAB ═══
@Composable
private fun AdminUsersTab(uiState: AdminUiState) {
    val roleBreakdown = listOf(
        "👤" to ("Customers" to 4749),
        "🏪" to ("Vendors" to 67),
        "⚙️" to ("Admins" to 5)
    )
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth().neuRaised(cornerRadius = 18.dp, shadowOffset = 8.dp).padding(20.dp)
            ) {
                Text("User Breakdown", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
                Spacer(Modifier.height(14.dp))
                roleBreakdown.forEach { (emoji, pair) ->
                    val (role, count) = pair
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(emoji, fontSize = 22.sp)
                        Spacer(Modifier.width(14.dp))
                        Text(role, style = MikoTypography.Subtitle.copy(
                            color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold
                        ), modifier = Modifier.weight(1f))
                        Text("$count", style = MikoTypography.Headline.copy(
                            color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold, fontSize = 20.sp
                        ))
                    }
                    HorizontalDivider(color = MikoColors.Divider.copy(alpha = 0.4f))
                }
            }
        }
        item {
            Column(
                modifier = Modifier.fillMaxWidth().neuRaised(cornerRadius = 18.dp, shadowOffset = 8.dp).padding(20.dp)
            ) {
                Text("Quick Actions", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
                Spacer(Modifier.height(14.dp))
                listOf("Export user list (CSV)", "Send broadcast notification", "View banned users").forEach { action ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {}
                            .padding(vertical = 10.dp)
                    ) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(Brush.linearGradient(MikoColors.GradientPrimary)))
                        Spacer(Modifier.width(12.dp))
                        Text(action, style = MikoTypography.Body.copy(color = MikoColors.TextPrimary), modifier = Modifier.weight(1f))
                        Text("→", color = MikoColors.TextMuted)
                    }
                    HorizontalDivider(color = MikoColors.Divider.copy(alpha = 0.3f))
                }
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

// ═══ FRAUD TAB ═══
@Composable
private fun AdminFraudTab(uiState: AdminUiState, onResolve: (String) -> Unit) {
    if (uiState.flaggedItems.isEmpty()) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("✅", fontSize = 56.sp)
                Spacer(Modifier.height(14.dp))
                Text("No active flags", style = MikoTypography.Headline.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
                Spacer(Modifier.height(6.dp))
                Text("Platform is clean!", style = MikoTypography.Body.copy(color = MikoColors.TextMuted))
            }
        }
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("${uiState.flaggedItems.size} items flagged for review",
                style = MikoTypography.Caption.copy(color = MikoColors.Error))
            Spacer(Modifier.height(4.dp))
        }
        items(uiState.flaggedItems, key = { it.id }) { flag ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MikoColors.ErrorSoft)
                    .border(1.dp, MikoColors.Error.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                        .background(MikoColors.Error.copy(alpha = 0.12f))
                ) {
                    Text(
                        when (flag.type) {
                            "vendor" -> "🏪"; "review" -> "⭐"; "order" -> "📦"; else -> "👤"
                        },
                        fontSize = 18.sp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(flag.target, style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold
                    ))
                    Text(flag.reason, style = MikoTypography.Caption.copy(color = MikoColors.Error))
                    Text(flag.timeAgo, style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                .background(MikoColors.SuccessSoft)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onResolve(flag.id) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Resolve ✓", style = MikoTypography.Caption.copy(color = MikoColors.Success, fontWeight = FontWeight.SemiBold))
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                .background(MikoColors.Error.copy(alpha = 0.12f))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Suspend ✗", style = MikoTypography.Caption.copy(color = MikoColors.Error, fontWeight = FontWeight.SemiBold))
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

// ═══ ANALYTICS TAB ═══
@Composable
private fun AdminAnalyticsTab(uiState: AdminUiState) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth().neuRaised(cornerRadius = 18.dp, shadowOffset = 8.dp).padding(20.dp)
            ) {
                Text("Weekly Platform Revenue", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
                Spacer(Modifier.height(4.dp))
                Text("Last 7 days", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                Spacer(Modifier.height(16.dp))
                AdminBarChart(
                    data = uiState.weeklyRevenue.ifEmpty {
                        listOf(680000f, 920000f, 540000f, 1100000f, 870000f, 1340000f, 990000f)
                    },
                    labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                )
            }
        }
        item {
            Column(
                modifier = Modifier.fillMaxWidth().neuRaised(cornerRadius = 18.dp, shadowOffset = 8.dp).padding(20.dp)
            ) {
                Text("Platform Health", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
                Spacer(Modifier.height(14.dp))
                listOf(
                    Triple("Avg order value", "LKR 3,420", MikoColors.PrimaryStart),
                    Triple("Order success rate", "96.4%", MikoColors.Success),
                    Triple("Vendor approval time", "2.3 days", MikoColors.Warning),
                    Triple("App rating", "4.8 ⭐", MikoColors.StarYellow),
                    Triple("Active users (30d)", "1,842", MikoColors.AccentBright)
                ).forEach { (label, value, color) ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Text(label, style = MikoTypography.Body.copy(color = MikoColors.TextSecondary))
                        Text(value, style = MikoTypography.Subtitle.copy(color = color, fontWeight = FontWeight.Bold))
                    }
                    HorizontalDivider(color = MikoColors.Divider.copy(alpha = 0.4f))
                }
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

// ─── Helpers ───
@Composable
private fun AdminKpiBubble(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(value, style = MikoTypography.Subtitle.copy(color = Color.White, fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(2.dp))
        Text(label, style = MikoTypography.Caption.copy(color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp))
    }
}

@Composable
private fun AdminMiniStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.neuRaised(cornerRadius = 14.dp, shadowOffset = 5.dp).padding(12.dp)
    ) {
        Text(value, style = MikoTypography.Subtitle.copy(
            color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
        ), maxLines = 1)
        Spacer(Modifier.height(3.dp))
        Text(label, style = MikoTypography.Caption.copy(color = MikoColors.TextMuted, fontSize = 10.sp))
    }
}

@Composable
private fun AdminBarChart(data: List<Float>, labels: List<String>, modifier: Modifier = Modifier) {
    val maxV = (data.maxOrNull() ?: 1f)
    val start = MikoColors.PrimaryStart
    val end = MikoColors.PrimaryEnd
    Column(modifier) {
        Canvas(Modifier.fillMaxWidth().weight(1f)) {
            val n = data.size
            val gap = size.width * 0.04f
            val barW = (size.width - gap * (n - 1)) / n
            data.forEachIndexed { i, v ->
                val h = (v / maxV) * size.height
                val x = i * (barW + gap)
                drawRoundRect(
                    brush = Brush.verticalGradient(listOf(start, end), startY = size.height - h, endY = size.height),
                    topLeft = Offset(x, size.height - h),
                    size = Size(barW, h),
                    cornerRadius = CornerRadius(barW * 0.3f, barW * 0.3f)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            labels.forEach { Text(it, style = MikoTypography.Caption.copy(color = MikoColors.TextMuted, fontSize = 10.sp)) }
        }
    }
}