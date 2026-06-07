package com.kittys.premium.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kittys.premium.core.ui.mascot.MikoHost
import com.kittys.premium.core.ui.neu.neuRaised
import com.kittys.premium.core.ui.system.MikoViewModel
import com.kittys.premium.core.ui.system.NeuElevation
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   HOME SCREEN V2 — greeting → weather → AI strip → trending →
//   community → product grid. Miko + neumorphism throughout.
// ════════════════════════════════════════════════════════════════

data class HomeProduct(val id: String, val name: String, val priceLkr: Int, val emoji: String, val bg: Color)
data class AiPick(val id: String, val title: String, val emoji: String)

@Composable
fun HomeScreenV2(
    userName: String,
    weatherLine: String,
    aiPicks: List<AiPick>,
    trending: List<HomeProduct>,
    products: List<HomeProduct>,
    mikoViewModel: MikoViewModel,
    onProductClick: (String) -> Unit,
    onAiPickClick: (String) -> Unit,
    onSeeAllTrending: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .background(MikoColors.NeuBackground),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Greeting
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                MikoHost(viewModel = mikoViewModel, size = 48.dp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "Hi $userName 🌸",
                        style = MikoTypography.headlineSmall.copy(
                            color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        "Let's find something cute today",
                        style = MikoTypography.bodySmall.copy(color = MikoColors.TextMuted)
                    )
                }
            }
        }

        // Weather insight chip
        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .neuRaised(elevation = NeuElevation.Level1, cornerRadius = 14.dp)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    weatherLine,
                    style = MikoTypography.bodySmall.copy(color = MikoColors.TextSecondary)
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        // AI recommendation strip
        item {
            SectionTitle("Miko's picks for you", null)
            Spacer(Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(aiPicks) { pick ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(120.dp)
                            .neuRaised(elevation = NeuElevation.Level2, cornerRadius = 20.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(
                                interactionSource = androidx.compose.runtime.remember { MutableInteractionSource() },
                                indication = null
                            ) { onAiPickClick(pick.id) }
                            .padding(12.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.linearGradient(MikoColors.GradientAccent))
                        ) { Text(pick.emoji, fontSize = 34.sp) }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            pick.title,
                            style = MikoTypography.labelMedium.copy(color = MikoColors.TextPrimary),
                            maxLines = 2
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // Trending
        item {
            SectionTitle("Trending now", onSeeAllTrending)
            Spacer(Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(trending) { p -> ProductCardSmall(p) { onProductClick(p.id) } }
            }
            Spacer(Modifier.height(24.dp))
        }

        // Product grid (2 per row using chunked rows)
        item {
            SectionTitle("For your little one", null)
            Spacer(Modifier.height(12.dp))
        }
        items(products.chunked(2)) { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                rowItems.forEach { p ->
                    Box(modifier = Modifier.weight(1f)) {
                        ProductCardSmall(p) { onProductClick(p.id) }
                    }
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, onSeeAll: (() -> Unit)?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
    ) {
        Text(
            title,
            style = MikoTypography.titleLarge.copy(
                color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
            )
        )
        if (onSeeAll != null) {
            Text(
                "See all",
                style = MikoTypography.labelMedium.copy(color = MikoColors.PrimaryEnd),
                modifier = Modifier.clickable(
                    interactionSource = androidx.compose.runtime.remember { MutableInteractionSource() },
                    indication = null
                ) { onSeeAll() }
            )
        }
    }
}

@Composable
private fun ProductCardSmall(p: HomeProduct, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .neuRaised(elevation = NeuElevation.Level2, cornerRadius = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = androidx.compose.runtime.remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(p.bg)
        ) { Text(p.emoji, fontSize = 56.sp) }
        Column(Modifier.padding(10.dp)) {
            Text(
                p.name,
                style = MikoTypography.bodyMedium.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Medium, fontSize = 12.sp
                ),
                maxLines = 1
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "LKR ${p.priceLkr}",
                style = MikoTypography.titleSmall.copy(
                    color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
