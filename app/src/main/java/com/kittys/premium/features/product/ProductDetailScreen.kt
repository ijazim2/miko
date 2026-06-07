package com.kittys.premium.features.product

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
//   MIKO — Product Detail Screen (+ AI Size Tip sheet)
// ════════════════════════════════════════════════════════════════

@Composable
fun ProductDetailScreen(
    productId: String,
    navController: NavController
) {
    val product = remember(productId) {
        HomeViewModel.sampleProducts.find { it.id == productId }
            ?: HomeViewModel.sampleProducts.first()
    }

    var selectedSize by remember { mutableStateOf<String?>(null) }
    var selectedColor by remember { mutableStateOf(product.colors.firstOrNull()) }
    var selectedImage by remember { mutableStateOf(0) }
    var isWishlisted by remember { mutableStateOf(product.isWishlisted) }
    var showSizeTip by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(MikoColors.Background)) {

        LazyColumn(contentPadding = PaddingValues(bottom = 110.dp)) {

            // ─── Image gallery ───
            item {
                Box {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp)
                            .background(MikoColors.PastelPink)
                    ) {
                        Text("🖼", fontSize = 90.sp)
                    }

                    // Top bar overlay
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        CircleIcon("←") { navController.popBackStack() }
                        Spacer(Modifier.weight(1f))
                        CircleIcon(if (isWishlisted) "♥" else "♡", tint = MikoColors.PrimaryStart) {
                            isWishlisted = !isWishlisted
                        }
                        Spacer(Modifier.width(8.dp))
                        CircleIcon("🛒") { navController.navigate(Screen.Cart.route) }
                    }

                    // Sale badge
                    if (product.isOnSale) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .background(MikoColors.PrimaryEnd)
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text("-${product.discountPercent}% OFF", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Thumbnail dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(12.dp)
                    ) {
                        repeat(4) { i ->
                            Box(
                                modifier = Modifier
                                    .size(if (i == selectedImage) 10.dp else 7.dp)
                                    .clip(CircleShape)
                                    .background(if (i == selectedImage) MikoColors.PrimaryStart else MikoColors.Surface.copy(alpha = 0.7f))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { selectedImage = i }
                            )
                        }
                    }
                }
            }

            // ─── Info section ───
            item {
                Column(Modifier.padding(20.dp)) {

                    // Vendor badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(MikoColors.GradientPrimary))
                        ) { Text("🏪", fontSize = 13.sp) }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            product.vendorName,
                            style = MikoTypography.Caption.copy(
                                color = MikoColors.TextSecondary, fontWeight = FontWeight.Medium
                            )
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("✓", color = MikoColors.Info, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.weight(1f))
                        Text(
                            "⭐ ${product.vendorRating}",
                            style = MikoTypography.Caption.copy(color = MikoColors.TextSecondary)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        product.name,
                        style = MikoTypography.Headline.copy(
                            color = MikoColors.TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )

                    Spacer(Modifier.height(6.dp))

                    // Rating row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${product.rating}",
                            style = MikoTypography.Subtitle.copy(
                                color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "(${product.reviewCount} reviews)",
                            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Price
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "LKR ${product.priceInt.formatThousands()}",
                            style = MikoTypography.Display.copy(
                                color = MikoColors.PrimaryEnd,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp
                            )
                        )
                        if (product.isOnSale && product.originalPriceInt != null) {
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "LKR ${product.originalPriceInt.formatThousands()}",
                                style = MikoTypography.Body.copy(
                                    color = MikoColors.TextMuted,
                                    textDecoration = TextDecoration.LineThrough
                                ),
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // ─── Color picker ───
                    if (product.colors.isNotEmpty()) {
                        Text("Color", style = MikoTypography.Subtitle.copy(
                            color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                        ))
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            product.colors.forEach { color ->
                                val selected = selectedColor == color
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(if (selected) MikoColors.PastelPink else MikoColors.SurfaceVariant)
                                        .border(
                                            width = 1.5.dp,
                                            color = if (selected) MikoColors.PrimaryStart else MikoColors.BorderLight,
                                            shape = RoundedCornerShape(50.dp)
                                        )
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { selectedColor = color }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        color,
                                        style = MikoTypography.Caption.copy(
                                            color = if (selected) MikoColors.PrimaryEnd else MikoColors.TextSecondary,
                                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                    }

                    // ─── Size picker + AI Size Tip ───
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Size", style = MikoTypography.Subtitle.copy(
                            color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                        ), modifier = Modifier.weight(1f))

                        // AI Size Tip button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(Brush.linearGradient(MikoColors.GradientButton))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { showSizeTip = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("✨", fontSize = 12.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "AI Size Tip",
                                style = MikoTypography.Caption.copy(
                                    color = Color.White, fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        product.sizes.forEach { size ->
                            val selected = selectedSize == size
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .then(
                                        if (selected)
                                            Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                                        else
                                            Modifier.background(MikoColors.SurfaceVariant)
                                                .border(1.dp, MikoColors.BorderLight, RoundedCornerShape(12.dp))
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { selectedSize = size }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    size,
                                    style = MikoTypography.Subtitle.copy(
                                        color = if (selected) Color.White else MikoColors.TextPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // ─── Description ───
                    Text("Description", style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                    ))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        product.description.ifBlank {
                            "Made with premium soft fabric, perfect for your little one's comfort. Easy to wash and gentle on sensitive skin."
                        },
                        style = MikoTypography.Body.copy(
                            color = MikoColors.TextSecondary, lineHeight = 22.sp
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    // ─── Stock + delivery info ───
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MikoColors.SuccessSoft)
                            .padding(14.dp)
                    ) {
                        Text("🚚", fontSize = 18.sp)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                "Island-wide delivery",
                                style = MikoTypography.Caption.copy(
                                    color = MikoColors.Success, fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                "Protected by MIKO escrow · Pay safely",
                                style = MikoTypography.Caption.copy(color = MikoColors.TextSecondary, fontSize = 10.sp)
                            )
                        }
                    }
                }
            }
        }

        // ─── Bottom action bar ───
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(MikoColors.Surface)
                .padding(16.dp)
        ) {
            // Add to cart (outline)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MikoColors.Surface)
                    .border(1.5.dp, MikoColors.PrimaryStart, RoundedCornerShape(16.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        // TODO: add to cart with selectedSize/color
                        navController.navigate(Screen.Cart.route)
                    }
            ) {
                Text("Add to Cart", style = MikoTypography.Button.copy(
                    color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
                ))
            }
            // Buy now (filled)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(MikoColors.GradientButton))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        navController.navigate(Screen.Checkout.route)
                    }
            ) {
                Text("Buy Now", style = MikoTypography.Button.copy(
                    color = Color.White, fontWeight = FontWeight.Bold
                ))
            }
        }
    }

    // ─── AI Size Tip Bottom Sheet ───
    if (showSizeTip) {
        AiSizeTipSheet(
            availableSizes = product.sizes,
            onDismiss = { showSizeTip = false },
            onApply = { size ->
                selectedSize = size
                showSizeTip = false
            }
        )
    }
}

// ─── Circle icon button ───
@Composable
private fun CircleIcon(symbol: String, tint: Color = MikoColors.TextPrimary, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MikoColors.Surface.copy(alpha = 0.92f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Text(symbol, fontSize = 17.sp, color = tint)
    }
}

// ════════════════════════════════════════════════════════════════
//   AI SIZE TIP BOTTOM SHEET
// ════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AiSizeTipSheet(
    availableSizes: List<String>,
    onDismiss: () -> Unit,
    onApply: (String) -> Unit
) {
    var gender by remember { mutableStateOf("girl") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var recommendation by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MikoColors.Surface
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(MikoColors.GradientButton))
                ) { Text("✨", fontSize = 16.sp) }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("AI Size Tip", style = MikoTypography.Title.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                    ))
                    Text("Get the perfect fit for your child", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                }
            }

            Spacer(Modifier.height(20.dp))

            // Gender toggle
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("girl" to "👧 Girl", "boy" to "👦 Boy").forEach { (value, label) ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .then(
                                if (gender == value) Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                                else Modifier.background(MikoColors.SurfaceVariant)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { gender = value }
                    ) {
                        Text(label, style = MikoTypography.Subtitle.copy(
                            color = if (gender == value) Color.White else MikoColors.TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        ))
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Age / height / weight
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SizeTipField("Age (yr)", age, { age = it.filter { c -> c.isDigit() } }, Modifier.weight(1f))
                SizeTipField("Height (cm)", height, { height = it.filter { c -> c.isDigit() } }, Modifier.weight(1f))
                SizeTipField("Weight (kg)", weight, { weight = it.filter { c -> c.isDigit() || c == '.' } }, Modifier.weight(1f))
            }

            Spacer(Modifier.height(18.dp))

            // Calculate button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .then(
                        if (age.isNotBlank())
                            Modifier.background(Brush.linearGradient(MikoColors.GradientButton))
                        else
                            Modifier.background(MikoColors.BorderMedium)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = age.isNotBlank()
                    ) {
                        recommendation = recommendSize(age.toIntOrNull() ?: 0, height.toIntOrNull(), availableSizes)
                    }
            ) {
                Text("Get My Size", style = MikoTypography.Button.copy(color = Color.White, fontWeight = FontWeight.Bold))
            }

            // Result
            recommendation?.let { size ->
                Spacer(Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MikoColors.SuccessSoft)
                        .padding(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.linearGradient(MikoColors.GradientPrimary))
                    ) {
                        Text(size, style = MikoTypography.Subtitle.copy(
                            color = Color.White, fontWeight = FontWeight.Bold
                        ))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Recommended Size", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                        Text("Size $size fits best", style = MikoTypography.Subtitle.copy(
                            color = MikoColors.Success, fontWeight = FontWeight.Bold
                        ))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(MikoColors.GradientButton))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onApply(size) }
                ) {
                    Text("Select This Size", style = MikoTypography.Button.copy(
                        color = Color.White, fontWeight = FontWeight.Bold
                    ))
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SizeTipField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(label, style = MikoTypography.Caption.copy(color = MikoColors.TextMuted, fontSize = 10.sp))
        Spacer(Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MikoColors.SurfaceVariant)
                .border(1.dp, MikoColors.BorderLight, RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(fontSize = 14.sp, color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold),
                cursorBrush = SolidColor(MikoColors.PrimaryStart),
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                decorationBox = { inner ->
                    if (value.isEmpty()) Text("0", style = MikoTypography.Body.copy(color = MikoColors.TextMuted))
                    inner()
                }
            )
        }
    }
}

// Simple size recommender (matches a size string to age)
private fun recommendSize(age: Int, heightCm: Int?, available: List<String>): String {
    val guess = when {
        age <= 1 -> "0-12M"
        age <= 2 -> "1-2Y"
        age <= 3 -> "2Y"
        age <= 4 -> "3Y"
        age <= 5 -> "4Y"
        age <= 6 -> "5Y"
        age <= 8 -> "6Y"
        else -> "7Y"
    }
    // Return an available size that matches, else the middle one
    return available.firstOrNull { it.contains(guess.takeWhile { c -> c.isDigit() }) }
        ?: available.getOrNull(available.size / 2)
        ?: guess
}