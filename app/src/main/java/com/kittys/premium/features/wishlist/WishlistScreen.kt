package com.kittys.premium.features.wishlist

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
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.domain.model.Product
import com.kittys.premium.domain.model.formatThousands
import com.kittys.premium.features.home.HomeViewModel
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Wishlist Screen
//   Saved items · move to cart · remove
// ════════════════════════════════════════════════════════════════

@Composable
fun WishlistScreen(navController: NavController) {

    // Seed with a few sample products (swap for real wishlist repo later)
    val wishlist = remember {
        mutableStateListOf<Product>().apply {
            addAll(HomeViewModel.sampleProducts.take(4))
        }
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
                "My Wishlist",
                style = MikoTypography.Title.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )
            if (wishlist.isNotEmpty()) {
                Text(
                    "${wishlist.size} saved",
                    style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                )
            }
        }

        if (wishlist.isEmpty()) {
            // ─── Empty state ───
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().padding(32.dp)
            ) {
                Text("💔", fontSize = 64.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    "No favourites yet",
                    style = MikoTypography.Headline.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Tap the ♡ on any product to save it here",
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
                    Text("Explore Products", style = MikoTypography.Button.copy(
                        color = Color.White, fontWeight = FontWeight.Bold
                    ))
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(wishlist, key = { it.id }) { product ->
                    WishlistRow(
                        product = product,
                        onOpen = { navController.navigate(Screen.ProductDetail.createRoute(product.id)) },
                        onRemove = { wishlist.remove(product) },
                        onMoveToCart = {
                            // TODO: add to cart
                            wishlist.remove(product)
                            navController.navigate(Screen.Cart.route)
                        }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

// ─── Wishlist row ───
@Composable
private fun WishlistRow(
    product: Product,
    onOpen: () -> Unit,
    onRemove: () -> Unit,
    onMoveToCart: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp)
            .padding(12.dp)
    ) {
        // Thumbnail
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MikoColors.PastelPink)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onOpen() }
        ) {
            Text("🖼", fontSize = 30.sp)
        }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                product.name,
                style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1
            )
            Text(
                product.vendorName,
                style = MikoTypography.Caption.copy(color = MikoColors.TextMuted),
                maxLines = 1
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "LKR ${product.priceInt.formatThousands()}",
                    style = MikoTypography.Subtitle.copy(
                        color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
                    )
                )
                if (product.isOnSale && product.originalPriceInt != null) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "LKR ${product.originalPriceInt.formatThousands()}",
                        style = MikoTypography.Caption.copy(
                            color = MikoColors.TextMuted,
                            textDecoration = TextDecoration.LineThrough,
                            fontSize = 10.sp
                        )
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            // Move to cart button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(Brush.linearGradient(MikoColors.GradientButton))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onMoveToCart() }
                    .padding(horizontal = 16.dp, vertical = 7.dp)
            ) {
                Text(
                    "Move to Cart",
                    style = MikoTypography.Caption.copy(
                        color = Color.White, fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        // Remove (heart) button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MikoColors.PastelPink)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onRemove() }
        ) {
            Text("♥", color = MikoColors.PrimaryStart, fontSize = 16.sp)
        }
    }
}