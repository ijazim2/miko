package com.kittys.premium.features.seller.products

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
import com.kittys.premium.domain.model.ProductStatus
import com.kittys.premium.domain.model.formatThousands
import com.kittys.premium.core.ui.components.MikoPrimaryButton
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Seller "My Products" Screen
//   List + filter + stock badges + edit/delete actions
// ════════════════════════════════════════════════════════════════

// Lightweight row model for the list (real data comes from repo later)
data class SellerProductItem(
    val id: String,
    val name: String,
    val priceLkr: Int,
    val stock: Int,
    val colors: Int,
    val status: ProductStatus,
    val views: Int,
    val sold: Int
)

private val sampleProducts = listOf(
    SellerProductItem("1", "Lavender Bow Party Dress", 2850, 38, 3, ProductStatus.PUBLISHED, 1240, 56),
    SellerProductItem("2", "Sweet Heart Cotton Top", 1990, 12, 2, ProductStatus.PUBLISHED, 820, 31),
    SellerProductItem("3", "Ribbon Pleated Skirt", 2450, 0, 1, ProductStatus.OUT_OF_STOCK, 410, 18),
    SellerProductItem("4", "Cute Bunny Backpack", 3200, 25, 4, ProductStatus.PENDING_REVIEW, 0, 0),
    SellerProductItem("5", "Floral Summer Romper", 2100, 9, 2, ProductStatus.DRAFT, 0, 0)
)

@Composable
fun SellerProductsScreen(navController: NavController) {

    var selectedFilter by remember { mutableStateOf<ProductStatus?>(null) }

    val filtered = remember(selectedFilter) {
        if (selectedFilter == null) sampleProducts
        else sampleProducts.filter { it.status == selectedFilter }
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
                "My Products",
                style = MikoTypography.Title.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )
            Text(
                "${sampleProducts.size} items",
                style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
            )
        }

        // ─── Filter chips ───
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp)
        ) {
            FilterChip("All", selectedFilter == null) { selectedFilter = null }
            FilterChip("Published", selectedFilter == ProductStatus.PUBLISHED) {
                selectedFilter = ProductStatus.PUBLISHED
            }
            FilterChip("Out of Stock", selectedFilter == ProductStatus.OUT_OF_STOCK) {
                selectedFilter = ProductStatus.OUT_OF_STOCK
            }
            FilterChip("Pending", selectedFilter == ProductStatus.PENDING_REVIEW) {
                selectedFilter = ProductStatus.PENDING_REVIEW
            }
            FilterChip("Drafts", selectedFilter == ProductStatus.DRAFT) {
                selectedFilter = ProductStatus.DRAFT
            }
        }

        Spacer(Modifier.height(8.dp))

        if (filtered.isEmpty()) {
            // ─── Empty state ───
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Text("📦", fontSize = 48.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    "No products here yet",
                    style = MikoTypography.Title.copy(
                        color = MikoColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Add a product to start selling",
                    style = MikoTypography.Body.copy(color = MikoColors.TextSecondary)
                )
                Spacer(Modifier.height(20.dp))
                MikoPrimaryButton(
                    text = "+ Add Product",
                    trailingIcon = null,
                    onClick = { navController.navigate(Screen.ProductUploadBasics.route) },
                    modifier = Modifier.width(200.dp)
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filtered, key = { it.id }) { product ->
                    ProductRow(
                        product = product,
                        onEdit = { /* TODO: navigate to edit with product.id */ },
                        onClick = { navController.navigate(Screen.ProductDetail.createRoute(product.id)) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

// ─── Filter chip ───
@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
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
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
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

// ─── Product row ───
@Composable
private fun ProductRow(
    product: SellerProductItem,
    onEdit: () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Thumbnail placeholder
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MikoColors.PastelPink)
            ) {
                Text("🖼", fontSize = 24.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    product.name,
                    style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "LKR ${product.priceLkr.formatThousands()} · ${product.colors} colors",
                    style = MikoTypography.Caption.copy(color = MikoColors.TextSecondary)
                )
                Spacer(Modifier.height(6.dp))
                StatusBadge(product.status, product.stock)
            }
        }

        // Divider
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MikoColors.Divider)
        )
        Spacer(Modifier.height(10.dp))

        // Metrics + actions
        Row(verticalAlignment = Alignment.CenterVertically) {
            MetricMini("👁", "${product.views.formatThousands()}", "views")
            Spacer(Modifier.width(20.dp))
            MetricMini("🛒", "${product.sold}", "sold")
            Spacer(Modifier.width(20.dp))
            MetricMini("📦", "${product.stock}", "stock")
            Spacer(Modifier.weight(1f))

            // Edit button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(MikoColors.PastelPink)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onEdit() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    "Edit",
                    style = MikoTypography.Caption.copy(
                        color = MikoColors.PrimaryEnd,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: ProductStatus, stock: Int) {
    val (color, label) = when (status) {
        ProductStatus.PUBLISHED      -> MikoColors.Success to "Published"
        ProductStatus.OUT_OF_STOCK   -> MikoColors.Error to "Out of Stock"
        ProductStatus.PENDING_REVIEW -> MikoColors.Warning to "Pending Review"
        ProductStatus.DRAFT          -> MikoColors.TextMuted to "Draft"
        ProductStatus.HIDDEN         -> MikoColors.TextMuted to "Hidden"
        ProductStatus.REJECTED       -> MikoColors.Error to "Rejected"
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(color.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text(
                label,
                style = MikoTypography.Caption.copy(
                    color = color,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                )
            )
        }
        if (status == ProductStatus.PUBLISHED && stock in 1..5) {
            Spacer(Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(MikoColors.Warning.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    "Low stock",
                    style = MikoTypography.Caption.copy(
                        color = MikoColors.Warning,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun MetricMini(emoji: String, value: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = 13.sp)
        Spacer(Modifier.width(4.dp))
        Text(
            value,
            style = MikoTypography.Caption.copy(
                color = MikoColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.width(2.dp))
        Text(
            label,
            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted, fontSize = 10.sp)
        )
    }
}