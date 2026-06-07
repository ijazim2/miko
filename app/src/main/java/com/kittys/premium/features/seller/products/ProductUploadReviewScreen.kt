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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.domain.model.ProductVariant
import com.kittys.premium.domain.model.formatThousands
import com.kittys.premium.core.ui.components.MikoPrimaryButton
import com.kittys.premium.core.ui.components.MikoTextButton
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Product Upload Step 6: Review & Publish
// ════════════════════════════════════════════════════════════════

@Composable
fun ProductUploadReviewScreen(
    navController: NavController,
    viewModel: ProductUploadViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val product = state.product

    // Navigate away once published
    LaunchedEffect(state.isPublished) {
        if (state.isPublished) {
            navController.navigate(Screen.SellerDashboard.route) {
                popUpTo(Screen.SellerDashboard.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background)
    ) {
        ProductUploadTopBar(
            currentStep = 6,
            title = "Review & Publish",
            onBack = { navController.popBackStack() }
        )

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {

            item {
                Text(
                    "Almost done! 🎉",
                    style = MikoTypography.Headline.copy(
                        color = MikoColors.TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                )
            }

            // ─── Preview Card ───
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neuRaised(cornerRadius = 18.dp, shadowOffset = 8.dp)
                        .padding(16.dp)
                ) {
                    // Photo strip (first variant's photos)
                    val firstVariant = product.variants.firstOrNull()
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val photos = firstVariant?.photoUris ?: emptyList()
                        if (photos.isEmpty()) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MikoColors.PastelPink)
                            ) {
                                Text("No photos added", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                            }
                        } else {
                            photos.take(3).forEach { _ ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MikoColors.PastelPink)
                                ) { Text("🖼", fontSize = 28.sp) }
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        product.name.ifBlank { "Untitled Product" },
                        style = MikoTypography.Title.copy(
                            color = MikoColors.TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                    product.category?.let {
                        Text(
                            "${it.emoji} ${it.displayName}${if (product.subCategory.isNotBlank()) " · ${product.subCategory}" else ""}",
                            style = MikoTypography.Caption.copy(color = MikoColors.TextSecondary)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Price
                    Text(
                        product.priceRangeDisplay,
                        style = MikoTypography.Title.copy(
                            brush = Brush.linearGradient(MikoColors.GradientPrimary),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        product.description,
                        style = MikoTypography.Body.copy(
                            color = MikoColors.TextSecondary,
                            lineHeight = 20.sp
                        ),
                        maxLines = 3
                    )
                }
            }

            // ─── Quick stats ───
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MikoColors.PastelLavender)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ReviewStat("${product.colorCount}", "Colors")
                    ReviewStat("${product.totalStock}", "In Stock")
                    ReviewStat("${product.totalPhotos}", "Photos")
                    ReviewStat("${product.ageGroups.size}", "Age Groups")
                }
            }

            // ─── Variant breakdown ───
            item {
                Text(
                    "Variants",
                    style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            items(product.variants) { variant ->
                VariantSummaryRow(variant)
            }

            // ─── Detail summary ───
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neuRaised(cornerRadius = 14.dp, shadowOffset = 6.dp)
                        .padding(16.dp)
                ) {
                    SummaryLine("Gender", product.gender.displayName)
                    product.season?.let { SummaryLine("Season", it.displayName) }
                    product.style?.let { SummaryLine("Style", it.displayName) }
                    if (product.material.isNotBlank()) SummaryLine("Material", product.material)
                    product.fabricCare?.let { SummaryLine("Care", it.displayName) }
                    product.pattern?.let { SummaryLine("Pattern", it.displayName) }
                    SummaryLine("Weight", "${product.weightGrams}g")
                    SummaryLine("Processing", product.processingTime.displayName)
                    SummaryLine("Returns", if (product.acceptsReturns) "${product.returnDays} days" else "No returns")
                    if (product.freeShipping) {
                        SummaryLine(
                            "Free Shipping",
                            if (product.freeShippingAbove > 0) "Above Rs ${product.freeShippingAbove.formatThousands()}" else "Always"
                        )
                    }
                    if (product.safetyTags.isNotEmpty()) {
                        SummaryLine("Safety", product.safetyTags.joinToString(", ") { it.displayName })
                    }
                }
            }

            // ─── Validation error (if any) ───
            state.error?.let { err ->
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MikoColors.ErrorSoft)
                            .padding(14.dp)
                    ) {
                        Text("⚠", fontSize = 18.sp)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            err,
                            style = MikoTypography.Caption.copy(
                                color = MikoColors.Error,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(20.dp)) }
        }

        // ─── Bottom Actions ───
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MikoColors.Surface)
                .padding(20.dp)
        ) {
            MikoPrimaryButton(
                text = if (state.isSubmitting) "Publishing…" else "Publish Product",
                enabled = !state.isSubmitting,
                trailingIcon = null,
                onClick = { viewModel.publishProduct() },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            MikoTextButton(
                text = "Save as Draft",
                onClick = {
                    viewModel.saveDraft()
                    navController.navigate(Screen.SellerDashboard.route) {
                        popUpTo(Screen.SellerDashboard.route) { inclusive = true }
                    }
                },
                color = MikoColors.TextSecondary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ─── Variant summary row ───
@Composable
private fun VariantSummaryRow(variant: ProductVariant) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .neuRaised(cornerRadius = 12.dp, shadowOffset = 4.dp)
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    if (variant.color?.isGradient == true)
                        Brush.linearGradient(MikoColors.GradientPrimary)
                    else
                        SolidColor(parseHexColor(variant.color?.hex))
                )
                .border(1.dp, MikoColors.BorderMedium, CircleShape)
        )
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(
                variant.color?.name ?: "Color",
                style = MikoTypography.Body.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                "${variant.sizeStocks.entries.joinToString(", ") { "${it.key}(${it.value})" }}",
                style = MikoTypography.Caption.copy(color = MikoColors.TextMuted),
                maxLines = 1
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                variant.priceDisplay,
                style = MikoTypography.Subtitle.copy(
                    color = MikoColors.PrimaryEnd,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                "${variant.photoUris.size} photos",
                style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
            )
        }
    }
}

@Composable
private fun ReviewStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MikoTypography.Title.copy(
                color = MikoColors.PrimaryEnd,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        )
        Text(
            label,
            style = MikoTypography.Caption.copy(color = MikoColors.TextSecondary, fontSize = 10.sp)
        )
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
        )
        Text(
            value,
            style = MikoTypography.Caption.copy(
                color = MikoColors.TextPrimary,
                fontWeight = FontWeight.Medium
            ),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f).padding(start = 16.dp)
        )
    }
}

// ─── Helper: parse "#RRGGBB" → Color ───
private fun parseHexColor(hex: String?): Color {
    if (hex == null || hex == "#GRADIENT") return Color(0xFFCCCCCC)
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color(0xFFCCCCCC)
    }
}