package com.kittys.premium.features.search

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.features.home.HomeViewModel
import com.kittys.premium.features.home.ProductCard
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Search Screen
//   Live filter + recent searches + trending chips
// ════════════════════════════════════════════════════════════════

@Composable
fun SearchScreen(navController: NavController) {

    val allProducts = remember { HomeViewModel.sampleProducts }
    var query by remember { mutableStateOf("") }
    var wishlisted by remember { mutableStateOf(setOf<String>()) }

    val recentSearches = remember {
        mutableStateListOf("party dress", "school shoes", "baby bodysuit")
    }
    val trending = listOf("Girls dresses", "Boys denim", "Hair clips", "Newborn", "Backpacks", "Sale")

    val results = remember(query) {
        if (query.isBlank()) emptyList()
        else allProducts.filter {
            it.name.contains(query, true) ||
                    it.category.contains(query, true) ||
                    it.brand.contains(query, true) ||
                    it.tags.any { tag -> tag.contains(query, true) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background)
    ) {
        // ─── Search bar row ───
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.popBackStack() }
            ) {
                Text("←", fontSize = 20.sp, color = MikoColors.TextPrimary)
            }
            Spacer(Modifier.width(8.dp))

            // Search field
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MikoColors.SurfaceVariant)
                    .border(1.dp, MikoColors.BorderLight, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp)
            ) {
                Text("🔍", fontSize = 15.sp)
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        color = MikoColors.TextPrimary
                    ),
                    cursorBrush = SolidColor(MikoColors.PrimaryStart),
                    modifier = Modifier.weight(1f).padding(vertical = 14.dp),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text(
                                "Search kids fashion...",
                                style = MikoTypography.Body.copy(color = MikoColors.TextMuted)
                            )
                        }
                        inner()
                    }
                )
                if (query.isNotEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MikoColors.BorderMedium)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { query = "" }
                    ) {
                        Text("×", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        when {
            // ─── No query → recent + trending ───
            query.isBlank() -> {
                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    if (recentSearches.isNotEmpty()) {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Recent",
                                    style = MikoTypography.Subtitle.copy(
                                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    "Clear",
                                    style = MikoTypography.Caption.copy(color = MikoColors.PrimaryEnd),
                                    modifier = Modifier.clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { recentSearches.clear() }
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            recentSearches.forEach { term ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { query = term }
                                        .padding(vertical = 10.dp)
                                ) {
                                    Text("🕐", fontSize = 14.sp)
                                    Spacer(Modifier.width(12.dp))
                                    Text(term, style = MikoTypography.Body.copy(color = MikoColors.TextSecondary), modifier = Modifier.weight(1f))
                                    Text("↖", color = MikoColors.TextMuted, fontSize = 14.sp)
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            "Trending",
                            style = MikoTypography.Subtitle.copy(
                                color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(Modifier.height(12.dp))
                        // Trending chips (wrap in rows of 2)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            trending.chunked(2).forEach { row ->
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    row.forEach { term ->
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(50.dp))
                                                .background(MikoColors.PastelPink)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) { query = term }
                                                .padding(vertical = 12.dp, horizontal = 12.dp)
                                        ) {
                                            Text(
                                                "🔥 $term",
                                                style = MikoTypography.Caption.copy(
                                                    color = MikoColors.PrimaryEnd,
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                    if (row.size == 1) Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // ─── Query but no results ───
            results.isEmpty() -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize().padding(32.dp)
                ) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No results for \"$query\"",
                        style = MikoTypography.Title.copy(
                            color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Try a different keyword or browse categories",
                        style = MikoTypography.Body.copy(color = MikoColors.TextMuted)
                    )
                }
            }

            // ─── Results grid ───
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            "${results.size} results",
                            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                        )
                    }
                    items(results.chunked(2)) { row ->
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
                                        onAdd = { /* TODO */ }
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
}