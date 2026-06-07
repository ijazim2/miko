package com.kittys.premium.features.seller.analytics

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
import androidx.navigation.NavController
import com.kittys.premium.domain.model.formatThousands
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Seller Analytics Screen
//   Sales trend chart + key metrics + top products
// ════════════════════════════════════════════════════════════════

private data class DayBar(val label: String, val value: Int)

private val weekSales = listOf(
    DayBar("Mon", 12000),
    DayBar("Tue", 8500),
    DayBar("Wed", 19000),
    DayBar("Thu", 14500),
    DayBar("Fri", 22000),
    DayBar("Sat", 31000),
    DayBar("Sun", 17500)
)

private data class TopProduct(val name: String, val sold: Int, val revenue: Int)

private val topProducts = listOf(
    TopProduct("Lavender Bow Dress", 56, 159600),
    TopProduct("Sweet Heart Top", 31, 61690),
    TopProduct("Ribbon Skirt", 18, 44100),
    TopProduct("Bunny Backpack", 12, 38400)
)

@Composable
fun SellerAnalyticsScreen(navController: NavController) {

    var selectedRange by remember { mutableStateOf("This Week") }

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
                "Analytics",
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

            // ─── Range selector ───
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("This Week", "This Month", "This Year").forEach { range ->
                        val selected = selectedRange == range
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
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
                                ) { selectedRange = range }
                                .padding(vertical = 10.dp)
                        ) {
                            Text(
                                range,
                                style = MikoTypography.Caption.copy(
                                    color = if (selected) Color.White else MikoColors.TextSecondary,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }

            // ─── Key metrics row ───
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard("Revenue", "LKR ${124_500.formatThousands()}", "+18%", true, Modifier.weight(1f))
                    MetricCard("Orders", "47", "+12%", true, Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard("Avg Order", "LKR 2,648", "+5%", true, Modifier.weight(1f))
                    MetricCard("Conversion", "3.2%", "-2%", false, Modifier.weight(1f))
                }
            }

            // ─── Sales trend chart ───
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neuRaised(cornerRadius = 18.dp, shadowOffset = 8.dp)
                        .padding(16.dp)
                ) {
                    Text(
                        "Sales Trend",
                        style = MikoTypography.Subtitle.copy(
                            color = MikoColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    BarChart(
                        bars = weekSales,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }
            }

            // ─── Top products ───
            item {
                Text(
                    "Top Products",
                    style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            itemsIndexed(topProducts) { index, product ->
                TopProductRow(rank = index + 1, product = product)
            }

            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

// ─── Metric card ───
@Composable
private fun MetricCard(
    label: String,
    value: String,
    change: String,
    positive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .neuRaised(cornerRadius = 14.dp, shadowOffset = 6.dp)
            .padding(16.dp)
    ) {
        Text(
            label,
            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            style = MikoTypography.Subtitle.copy(
                color = MikoColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background((if (positive) MikoColors.Success else MikoColors.Error).copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                "${if (positive) "↑" else "↓"} $change",
                style = MikoTypography.Caption.copy(
                    color = if (positive) MikoColors.Success else MikoColors.Error,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                )
            )
        }
    }
}

// ─── Bar chart (Compose Canvas) ───
@Composable
private fun BarChart(bars: List<DayBar>, modifier: Modifier = Modifier) {
    val maxValue = (bars.maxOfOrNull { it.value } ?: 1).toFloat()
    val gradientStart = MikoColors.PrimaryStart
    val gradientEnd = MikoColors.PrimaryEnd

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val barCount = bars.size
            val gap = size.width * 0.04f
            val barWidth = (size.width - gap * (barCount - 1)) / barCount
            val maxBarHeight = size.height

            bars.forEachIndexed { index, bar ->
                val barHeight = (bar.value / maxValue) * maxBarHeight
                val x = index * (barWidth + gap)
                val y = maxBarHeight - barHeight

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(gradientStart, gradientEnd),
                        startY = y,
                        endY = maxBarHeight
                    ),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(barWidth * 0.3f, barWidth * 0.3f)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Day labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            bars.forEach { bar ->
                Text(
                    bar.label,
                    style = MikoTypography.Caption.copy(
                        color = MikoColors.TextMuted,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

// ─── Top product row ───
@Composable
private fun TopProductRow(rank: Int, product: TopProduct) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .neuRaised(cornerRadius = 14.dp, shadowOffset = 4.dp)
            .padding(14.dp)
    ) {
        // Rank badge
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (rank <= 3)
                        Brush.linearGradient(MikoColors.GradientPrimary)
                    else
                        SolidColor(MikoColors.SurfaceVariant)
                )
        ) {
            Text(
                "$rank",
                style = MikoTypography.Caption.copy(
                    color = if (rank <= 3) Color.White else MikoColors.TextMuted,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                product.name,
                style = MikoTypography.Body.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1
            )
            Text(
                "${product.sold} sold",
                style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
            )
        }
        Text(
            "LKR ${product.revenue.formatThousands()}",
            style = MikoTypography.Subtitle.copy(
                color = MikoColors.PrimaryEnd,
                fontWeight = FontWeight.Bold
            )
        )
    }
}