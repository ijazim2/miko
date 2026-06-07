package com.kittys.premium.features.seller.products

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.domain.model.ProductSizes
import com.kittys.premium.domain.model.ProductVariant
import com.kittys.premium.domain.model.VariantColor
import com.kittys.premium.core.ui.components.MikoPrimaryButton
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Product Upload Step 3: Variants (THE BIG ONE)
//   Multi-color · per-color photos (5) · per-size stock · per-variant price
// ════════════════════════════════════════════════════════════════

@Composable
fun ProductUploadVariantsScreen(
    navController: NavController,
    viewModel: ProductUploadViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val product = state.product

    var colorPickerForVariant by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background)
    ) {
        ProductUploadTopBar(
            currentStep = 3,
            title = "Colors & Variants",
            onBack = { navController.popBackStack() }
        )

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {

            item {
                Column {
                    Text(
                        "Add your colors & stock",
                        style = MikoTypography.Headline.copy(
                            color = MikoColors.TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Each color is a variant. Add photos, sizes with stock, and a price for each one.",
                        style = MikoTypography.Body.copy(
                            color = MikoColors.TextSecondary,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            // ─── Variant cards ───
            itemsIndexed(product.variants, key = { _, v -> v.id }) { index, variant ->
                VariantCard(
                    index = index,
                    variant = variant,
                    canRemove = product.variants.size > 1,
                    sizeOptions = ProductSizes.forCategory(product.category),
                    onPickColor = { colorPickerForVariant = variant.id },
                    onRemove = { viewModel.removeVariant(variant.id) },
                    onAddPhoto = {
                        // TODO: open image picker → returns uri
                        viewModel.addPhotoToVariant(variant.id, "photo_${System.currentTimeMillis()}")
                    },
                    onRemovePhoto = { uri -> viewModel.removePhotoFromVariant(variant.id, uri) },
                    onToggleSize = { size -> viewModel.toggleSizeForVariant(variant.id, size) },
                    onStockChange = { size, qty -> viewModel.updateSizeStock(variant.id, size, qty) },
                    onPriceChange = { price -> viewModel.updateVariantPrice(variant.id, price) },
                    onOriginalPriceChange = { price -> viewModel.updateVariantOriginalPrice(variant.id, price) }
                )
            }

            // ─── Add another color ───
            if (product.variants.size < 10) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = 1.5.dp,
                                brush = Brush.linearGradient(MikoColors.GradientPrimary),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { viewModel.addVariant() }
                            .padding(vertical = 16.dp)
                    ) {
                        Text(
                            "+  Add Another Color",
                            style = MikoTypography.Button.copy(
                                brush = Brush.linearGradient(MikoColors.GradientPrimary),
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            // ─── Summary ───
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MikoColors.PastelLavender)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SummaryStat("${product.colorCount}", "Colors")
                    SummaryStat("${product.totalStock}", "Total Stock")
                    SummaryStat("${product.totalPhotos}", "Photos")
                }
            }

            item { Spacer(Modifier.height(20.dp)) }
        }

        // ─── Bottom Continue ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MikoColors.Surface)
                .padding(20.dp)
        ) {
            MikoPrimaryButton(
                text = "Continue",
                enabled = viewModel.isStep3Valid(),
                onClick = {
                    viewModel.nextStep()
                    navController.navigate(Screen.ProductUploadDetails.route)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // ─── Color picker sheet ───
    colorPickerForVariant?.let { variantId ->
        ColorPickerSheet(
            onSelect = { color ->
                viewModel.updateVariantColor(variantId, color)
                colorPickerForVariant = null
            },
            onDismiss = { colorPickerForVariant = null }
        )
    }
}

// ════════════════════════════════════════════════════════════════
//   VARIANT CARD
// ════════════════════════════════════════════════════════════════

@Composable
private fun VariantCard(
    index: Int,
    variant: ProductVariant,
    canRemove: Boolean,
    sizeOptions: List<String>,
    onPickColor: () -> Unit,
    onRemove: () -> Unit,
    onAddPhoto: () -> Unit,
    onRemovePhoto: (String) -> Unit,
    onToggleSize: (String) -> Unit,
    onStockChange: (String, Int) -> Unit,
    onPriceChange: (Int) -> Unit,
    onOriginalPriceChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .neuRaised(cornerRadius = 18.dp, shadowOffset = 8.dp)
            .padding(16.dp)
    ) {
        // ─── Card header: color + remove ───
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Color swatch
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (variant.color?.isGradient == true)
                            Brush.linearGradient(MikoColors.GradientPrimary)
                        else
                            SolidColor(parseHex(variant.color?.hex))
                    )
                    .border(1.dp, MikoColors.BorderMedium, CircleShape)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                variant.color?.name ?: "Color ${index + 1}",
                style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )
            if (canRemove) {
                Text(
                    "Remove",
                    style = MikoTypography.Caption.copy(color = MikoColors.Error),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onRemove() }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // ─── Pick color button ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MikoColors.SurfaceVariant)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onPickColor() }
                .padding(12.dp)
        ) {
            Text(
                if (variant.color == null) "🎨  Choose a color" else "🎨  Change color",
                style = MikoTypography.Body.copy(
                    color = MikoColors.PrimaryEnd,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Spacer(Modifier.height(14.dp))

        // ─── Photos (5 slots) ───
        Text(
            "Photos (${variant.photoUris.size}/5)",
            style = MikoTypography.Label.copy(
                color = MikoColors.TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // existing photos
            variant.photoUris.forEach { uri ->
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MikoColors.PastelPink)
                ) {
                    // Placeholder thumbnail (image picker integration later)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) { Text("🖼", fontSize = 20.sp) }
                    // remove badge
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(2.dp)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(MikoColors.Error)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onRemovePhoto(uri) }
                    ) {
                        Text("×", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            // add button
            if (variant.canAddPhoto) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MikoColors.SurfaceVariant)
                        .border(
                            width = 1.dp,
                            color = MikoColors.PrimaryStart,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onAddPhoto() }
                ) {
                    Text("+", color = MikoColors.PrimaryStart, fontSize = 24.sp)
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // ─── Sizes + stock ───
        Text(
            "Sizes & Stock",
            style = MikoTypography.Label.copy(
                color = MikoColors.TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(Modifier.height(8.dp))

        // Size selector chips
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            sizeOptions.chunked(5).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    row.forEach { size ->
                        val selected = variant.sizeStocks.containsKey(size)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) MikoColors.PrimaryStart else MikoColors.SurfaceVariant)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onToggleSize(size) }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                size,
                                style = MikoTypography.Caption.copy(
                                    color = if (selected) Color.White else MikoColors.TextSecondary,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                ),
                                maxLines = 1
                            )
                        }
                    }
                    repeat(5 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }

        // Stock inputs for selected sizes
        if (variant.sizeStocks.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            variant.sizeStocks.entries.sortedBy { it.key }.forEach { (size, stock) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MikoColors.PastelPink)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            size,
                            style = MikoTypography.Caption.copy(
                                color = MikoColors.PrimaryEnd,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Stock:",
                        style = MikoTypography.Caption.copy(color = MikoColors.TextSecondary)
                    )
                    Spacer(Modifier.width(8.dp))
                    StockStepper(
                        value = stock,
                        onChange = { onStockChange(size, it) }
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // ─── Pricing ───
        Text(
            "Pricing (LKR)",
            style = MikoTypography.Label.copy(
                color = MikoColors.TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PriceInput(
                label = "Selling Price",
                value = variant.priceLkr,
                onChange = onPriceChange,
                modifier = Modifier.weight(1f)
            )
            PriceInput(
                label = "Original (optional)",
                value = variant.originalPrice,
                onChange = onOriginalPriceChange,
                modifier = Modifier.weight(1f)
            )
        }
        if (variant.hasDiscount) {
            Spacer(Modifier.height(6.dp))
            Text(
                "${variant.discountPercent}% OFF",
                style = MikoTypography.Caption.copy(
                    color = MikoColors.Success,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

// ─── Stock stepper (− qty +) ───
@Composable
private fun StockStepper(value: Int, onChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        StepperButton("−") { onChange((value - 1).coerceAtLeast(0)) }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.width(44.dp)
        ) {
            Text(
                "$value",
                style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        StepperButton("+") { onChange(value + 1) }
    }
}

@Composable
private fun StepperButton(symbol: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(MikoColors.SurfaceVariant)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Text(symbol, color = MikoColors.PrimaryEnd, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

// ─── Price input ───
@Composable
private fun PriceInput(
    label: String,
    value: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            label,
            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
        )
        Spacer(Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MikoColors.SurfaceVariant)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                "Rs",
                style = MikoTypography.Caption.copy(
                    color = MikoColors.TextMuted,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(Modifier.width(6.dp))
            BasicTextField(
                value = if (value == 0) "" else value.toString(),
                onValueChange = {
                    val n = it.filter { c -> c.isDigit() }.toIntOrNull() ?: 0
                    onChange(n)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold
                ),
                cursorBrush = SolidColor(MikoColors.PrimaryStart),
                modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                decorationBox = { inner ->
                    if (value == 0) {
                        Text("0", style = MikoTypography.Body.copy(color = MikoColors.TextMuted))
                    }
                    inner()
                }
            )
        }
    }
}

@Composable
private fun SummaryStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MikoTypography.Title.copy(
                color = MikoColors.PrimaryEnd,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            label,
            style = MikoTypography.Caption.copy(color = MikoColors.TextSecondary)
        )
    }
}

// ─── Color picker bottom sheet ───
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorPickerSheet(
    onSelect: (VariantColor) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MikoColors.Surface
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Choose a color",
                style = MikoTypography.Title.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
            Spacer(Modifier.height(16.dp))

            VariantColor.DEFAULTS.chunked(4).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    row.forEach { color ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onSelect(color) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (color.isGradient)
                                            Brush.linearGradient(MikoColors.GradientPrimary)
                                        else
                                            SolidColor(parseHex(color.hex))
                                    )
                                    .border(1.dp, MikoColors.BorderMedium, CircleShape)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                color.name,
                                style = MikoTypography.Caption.copy(
                                    color = MikoColors.TextSecondary,
                                    fontSize = 10.sp
                                ),
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                    repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

// ─── Helper: parse "#RRGGBB" → Color (fallback grey) ───
private fun parseHex(hex: String?): Color {
    if (hex == null || hex == "#GRADIENT") return Color(0xFFCCCCCC)
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color(0xFFCCCCCC)
    }
}