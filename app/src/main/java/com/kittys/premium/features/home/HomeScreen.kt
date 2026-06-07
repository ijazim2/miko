package com.kittys.premium.features.home

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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.domain.model.Product
import com.kittys.premium.domain.model.formatThousands
import com.kittys.premium.core.ui.components.TextLogoPlaceholder
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Home Screen (Good Morning, search, flash sale, grid)
// ════════════════════════════════════════════════════════════════

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {

        // ─── 1. TOP BAR ───
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "${uiState.greeting},",
                        style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                    )
                    Text(
                        "${uiState.userName} 👋",
                        style = MikoTypography.Title.copy(
                            color = MikoColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Notifications
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .neuRaised(cornerRadius = 20.dp, shadowOffset = 4.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { navController.navigate(Screen.Notifications.route) }
                ) {
                    Text("🔔", fontSize = 16.sp)
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(MikoColors.Error)
                    )
                }
                Spacer(Modifier.width(8.dp))
                // Cart
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .neuRaised(cornerRadius = 20.dp, shadowOffset = 4.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { navController.navigate(Screen.Cart.route) }
                ) {
                    Text("🛒", fontSize = 16.sp)
                    if (uiState.cartCount > 0) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(MikoColors.PrimaryStart)
                        ) {
                            Text("${uiState.cartCount}", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ─── 2. SEARCH BAR (tap → search screen) ───
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MikoColors.SurfaceVariant)
                    .border(1.dp, MikoColors.BorderLight, RoundedCornerShape(16.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.navigate(Screen.Search.route) }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text("🔍", fontSize = 16.sp)
                Spacer(Modifier.width(10.dp))
                Text(
                    "Search kids fashion...",
                    style = MikoTypography.Body.copy(color = MikoColors.TextMuted)
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        // ─── 3. FLASH SALE BANNER ───
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Brush.linearGradient(MikoColors.GradientButton))
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚡", fontSize = 20.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "FLASH SALE",
                            style = MikoTypography.Label.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Up to 40% OFF",
                        style = MikoTypography.Display.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                    )
                    Text(
                        "Today only · Girls Collection",
                        style = MikoTypography.Body.copy(color = Color.White.copy(alpha = 0.9f))
                    )
                    Spacer(Modifier.height(14.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color.White)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { navController.navigate(Screen.Categories.route) }
                            .padding(horizontal = 20.dp, vertical = 9.dp)
                    ) {
                        Text(
                            "Shop Now",
                            style = MikoTypography.Label.copy(
                                brush = Brush.linearGradient(MikoColors.GradientButton),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // ─── 4. CATEGORIES (4 circles) ───
        item {
            SectionHeader("Categories", "See All") { navController.navigate(Screen.Categories.route) }
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            ) {
                val pastels = listOf(
                    MikoColors.PastelPink, MikoColors.PastelLavender,
                    MikoColors.PastelSky, MikoColors.PastelButter
                )
                uiState.categories.forEachIndexed { i, cat ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { navController.navigate(Screen.Categories.route) }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(pastels[i % pastels.size])
                        ) {
                            Text(cat.emoji, fontSize = 28.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            cat.label,
                            style = MikoTypography.Caption.copy(
                                color = MikoColors.TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // ─── 5. FLASH SALE PRODUCTS (horizontal) ───
        if (uiState.flashSaleProducts.isNotEmpty()) {
            item {
                SectionHeader("⚡ Flash Sale", null) {}
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.flashSaleProducts, key = { it.id }) { product ->
                        Box(Modifier.width(160.dp)) {
                            ProductCard(
                                product = product,
                                onClick = { navController.navigate(Screen.ProductDetail.createRoute(product.id)) },
                                onWishlist = { viewModel.toggleWishlist(product.id) },
                                onAdd = { viewModel.addToCart(product.id) }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        // ─── 6. POPULAR PRODUCTS GRID ───
        item {
            SectionHeader("Popular Now", "See All") { navController.navigate(Screen.Categories.route) }
            Spacer(Modifier.height(8.dp))
        }

        items(uiState.featuredProducts.chunked(2)) { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                row.forEach { product ->
                    Box(Modifier.weight(1f)) {
                        ProductCard(
                            product = product,
                            onClick = { navController.navigate(Screen.ProductDetail.createRoute(product.id)) },
                            onWishlist = { viewModel.toggleWishlist(product.id) },
                            onAdd = { viewModel.addToCart(product.id) }
                        )
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

// ─── Section Header ───
@Composable
private fun SectionHeader(title: String, action: String?, onActionClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
    ) {
        Text(
            title,
            style = MikoTypography.Title.copy(
                color = MikoColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            modifier = Modifier.weight(1f)
        )
        if (action != null) {
            Text(
                action,
                style = MikoTypography.Caption.copy(
                    color = MikoColors.PrimaryEnd,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onActionClick() }
            )
        }
    }
}

// ─── Reusable Product Card ───
@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onWishlist: () -> Unit,
    onAdd: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MikoColors.Surface)
            .neuRaised(cornerRadius = 18.dp, shadowOffset = 6.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        // Image placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                .background(MikoColors.PastelPink)
        ) {
            Box(Modifier.fillMaxSize(), Alignment.Center) { Text("🖼", fontSize = 40.sp) }

            // Sale badge
            if (product.isOnSale) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(MikoColors.PrimaryEnd)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("-${product.discountPercent}%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Wishlist heart
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(MikoColors.Surface.copy(alpha = 0.92f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onWishlist() }
            ) {
                Text(
                    if (product.isWishlisted) "♥" else "♡",
                    fontSize = 15.sp,
                    color = MikoColors.PrimaryStart
                )
            }
        }

        Column(Modifier.padding(12.dp)) {
            Text(
                product.name,
                style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1
            )
            Text(
                product.vendorName,
                style = MikoTypography.Caption.copy(color = MikoColors.TextMuted, fontSize = 10.sp),
                maxLines = 1
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⭐", fontSize = 11.sp)
                Spacer(Modifier.width(3.dp))
                Text(
                    "${product.rating} (${product.reviewCount})",
                    style = MikoTypography.Caption.copy(color = MikoColors.TextSecondary, fontSize = 10.sp)
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "LKR ${product.priceInt.formatThousands()}",
                        style = MikoTypography.Subtitle.copy(
                            color = MikoColors.PrimaryEnd,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    if (product.isOnSale && product.originalPriceInt != null) {
                        Text(
                            "LKR ${product.originalPriceInt.formatThousands()}",
                            style = MikoTypography.Caption.copy(
                                color = MikoColors.TextMuted,
                                fontSize = 10.sp,
                                textDecoration = TextDecoration.LineThrough
                            )
                        )
                    }
                }
                // Quick add
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(MikoColors.GradientButton))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onAdd() }
                ) {
                    Text("+", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}