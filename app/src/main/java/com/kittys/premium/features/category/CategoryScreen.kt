package com.kittys.premium.features.categories

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
import com.kittys.premium.domain.model.Product
import com.kittys.premium.features.home.HomeViewModel
import com.kittys.premium.features.home.ProductCard
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Categories Screen
//   Category pills + filtered product grid
// ════════════════════════════════════════════════════════════════

private data class CategoryTab(
    val id: String,
    val label: String,
    val emoji: String
)

private val categoryTabs = listOf(
    CategoryTab("all", "All", "✨"),
    CategoryTab("Babies", "Babies", "👶"),
    CategoryTab("Girls", "Girls", "👧"),
    CategoryTab("Boys", "Boys", "👦"),
    CategoryTab("Accessories", "Accessories", "🎀")
)

@Composable
fun CategoriesScreen(navController: NavController) {

    // Reuse the sample catalog from HomeViewModel
    val allProducts = remember { HomeViewModel.sampleProducts }
    var selectedCategory by remember { mutableStateOf("all") }
    var wishlisted by remember { mutableStateOf(setOf<String>()) }

    val filtered = remember(selectedCategory) {
        if (selectedCategory == "all") allProducts
        else allProducts.filter { it.category == selectedCategory }
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
            Text(
                "Shop by Category",
                style = MikoTypography.Title.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MikoColors.SurfaceVariant)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.navigate(Screen.Search.route) }
            ) {
                Text("🔍", fontSize = 16.sp)
            }
        }

        // ─── Category pills ───
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            items(categoryTabs) { tab ->
                val selected = selectedCategory == tab.id
                val count = if (tab.id == "all") allProducts.size
                else allProducts.count { it.category == tab.id }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { selectedCategory = tab.id }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .then(
                                if (selected)
                                    Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                                else
                                    Modifier.background(MikoColors.SurfaceVariant)
                            )
                    ) {
                        Text(tab.emoji, fontSize = 26.sp)
                    }
                    Spacer(Modifier.height(5.dp))
                    Text(
                        tab.label,
                        style = MikoTypography.Caption.copy(
                            color = if (selected) MikoColors.PrimaryEnd else MikoColors.TextSecondary,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                    Text(
                        "$count items",
                        style = MikoTypography.Caption.copy(color = MikoColors.TextMuted, fontSize = 9.sp)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // ─── Result count ───
        Text(
            "${filtered.size} ${if (selectedCategory == "all") "products" else "in ${categoryTabs.first { it.id == selectedCategory }.label}"}",
            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        // ─── Product grid ───
        if (filtered.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().padding(32.dp)
            ) {
                Text("🛍", fontSize = 48.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Nothing here yet",
                    style = MikoTypography.Title.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    "Check back soon for new arrivals",
                    style = MikoTypography.Body.copy(color = MikoColors.TextMuted)
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered.chunked(2)) { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { product ->
                            val isWish = wishlisted.contains(product.id)
                            Box(Modifier.weight(1f)) {
                                ProductCard(
                                    product = product.copy(isWishlisted = isWish),
                                    onClick = { navController.navigate(Screen.ProductDetail.createRoute(product.id)) },
                                    onWishlist = {
                                        wishlisted = if (isWish) wishlisted - product.id else wishlisted + product.id
                                    },
                                    onAdd = { /* TODO: add to cart */ }
                                )
                            }
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}