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
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.domain.model.ProcessingTime
import com.kittys.premium.domain.model.ShippingMethod
import com.kittys.premium.core.ui.components.MikoPrimaryButton
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Product Upload Step 5: Shipping
//   Weight · processing time · methods · free shipping · returns
// ════════════════════════════════════════════════════════════════

@Composable
fun ProductUploadShippingScreen(
    navController: NavController,
    viewModel: ProductUploadViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val product = state.product

    var weight by remember { mutableStateOf(if (product.weightGrams == 0) "" else product.weightGrams.toString()) }
    var freeShipThreshold by remember { mutableStateOf(if (product.freeShippingAbove == 0) "" else product.freeShippingAbove.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background)
    ) {
        ProductUploadTopBar(
            currentStep = 5,
            title = "Shipping & Returns",
            onBack = { navController.popBackStack() }
        )

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.weight(1f)
        ) {

            item {
                Column {
                    Text(
                        "How will you ship?",
                        style = MikoTypography.Headline.copy(
                            color = MikoColors.TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Set your package weight, delivery options, and return policy",
                        style = MikoTypography.Body.copy(
                            color = MikoColors.TextSecondary,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            // ─── Package Weight ───
            item {
                UploadField(label = "Package Weight (grams)", required = true) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MikoColors.SurfaceVariant)
                            .border(1.dp, MikoColors.BorderLight, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("⚖", fontSize = 18.sp)
                        Spacer(Modifier.width(10.dp))
                        BasicTextField(
                            value = weight,
                            onValueChange = {
                                val cleaned = it.filter { c -> c.isDigit() }
                                weight = cleaned
                                viewModel.updateWeight(cleaned.toIntOrNull() ?: 0)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = MikoColors.TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            cursorBrush = SolidColor(MikoColors.PrimaryStart),
                            modifier = Modifier.weight(1f).padding(vertical = 16.dp),
                            decorationBox = { inner ->
                                if (weight.isEmpty()) {
                                    Text("e.g. 250", style = MikoTypography.Body.copy(color = MikoColors.TextMuted))
                                }
                                inner()
                            }
                        )
                        Text("grams", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                    }
                    Text(
                        "Tip: A baby dress is ~150-300g, a pair of shoes ~400-600g",
                        style = MikoTypography.Caption.copy(color = MikoColors.TextMuted),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // ─── Processing Time ───
            item {
                UploadField(label = "Processing Time", required = true) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ProcessingTime.values().forEach { time ->
                            val selected = product.processingTime == time
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) MikoColors.PastelPink else MikoColors.SurfaceVariant)
                                    .border(
                                        width = 1.dp,
                                        color = if (selected) MikoColors.PrimaryStart else MikoColors.BorderLight,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { viewModel.updateProcessingTime(time) }
                                    .padding(14.dp)
                            ) {
                                RadioDot(selected = selected)
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    time.displayName,
                                    style = MikoTypography.Body.copy(
                                        color = if (selected) MikoColors.PrimaryEnd else MikoColors.TextPrimary,
                                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // ─── Shipping Methods (multi-select) ───
            item {
                UploadField(label = "Shipping Methods", required = true) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ShippingMethod.values().toList().chunked(2).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { method ->
                                    val selected = product.shippingMethods.contains(method)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (selected) MikoColors.PastelPink else MikoColors.SurfaceVariant)
                                            .border(
                                                width = 1.dp,
                                                color = if (selected) MikoColors.PrimaryStart else MikoColors.BorderLight,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) { viewModel.toggleShippingMethod(method) }
                                            .padding(12.dp)
                                    ) {
                                        Text(method.emoji, fontSize = 18.sp)
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            method.displayName,
                                            style = MikoTypography.Caption.copy(
                                                color = if (selected) MikoColors.PrimaryEnd else MikoColors.TextSecondary,
                                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
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

            // ─── Free Shipping Toggle ───
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MikoColors.SurfaceVariant)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🚚", fontSize = 22.sp)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Offer Free Shipping",
                                style = MikoTypography.Subtitle.copy(
                                    color = MikoColors.TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                "Products with free shipping sell 2x faster",
                                style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                            )
                        }
                        Switch(
                            checked = product.freeShipping,
                            onCheckedChange = {
                                viewModel.toggleFreeShipping(it, freeShipThreshold.toIntOrNull() ?: 0)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MikoColors.PrimaryStart,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = MikoColors.BorderMedium
                            )
                        )
                    }

                    if (product.freeShipping) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Free shipping above (LKR) — leave 0 for always free",
                            style = MikoTypography.Caption.copy(color = MikoColors.TextSecondary)
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MikoColors.Surface)
                                .border(1.dp, MikoColors.BorderLight, RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp)
                        ) {
                            Text("Rs", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted, fontWeight = FontWeight.SemiBold))
                            Spacer(Modifier.width(6.dp))
                            BasicTextField(
                                value = freeShipThreshold,
                                onValueChange = {
                                    val cleaned = it.filter { c -> c.isDigit() }
                                    freeShipThreshold = cleaned
                                    viewModel.toggleFreeShipping(true, cleaned.toIntOrNull() ?: 0)
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
                                    if (freeShipThreshold.isEmpty()) {
                                        Text("0", style = MikoTypography.Body.copy(color = MikoColors.TextMuted))
                                    }
                                    inner()
                                }
                            )
                        }
                    }
                }
            }

            // ─── Returns Toggle ───
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MikoColors.SurfaceVariant)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("↩", fontSize = 22.sp)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Accept Returns",
                                style = MikoTypography.Subtitle.copy(
                                    color = MikoColors.TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                if (product.acceptsReturns) "${product.returnDays}-day return window" else "No returns",
                                style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                            )
                        }
                        Switch(
                            checked = product.acceptsReturns,
                            onCheckedChange = { viewModel.toggleReturns(it, 7) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MikoColors.PrimaryStart,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = MikoColors.BorderMedium
                            )
                        )
                    }

                    if (product.acceptsReturns) {
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(3, 7, 14, 30).forEach { days ->
                                val selected = product.returnDays == days
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (selected) MikoColors.PrimaryStart else MikoColors.Surface)
                                        .border(
                                            width = 1.dp,
                                            color = if (selected) MikoColors.PrimaryStart else MikoColors.BorderLight,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { viewModel.toggleReturns(true, days) }
                                        .padding(vertical = 10.dp)
                                ) {
                                    Text(
                                        "$days days",
                                        style = MikoTypography.Caption.copy(
                                            color = if (selected) Color.White else MikoColors.TextSecondary,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }
                            }
                        }
                    }
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
                text = "Review Product",
                enabled = viewModel.isStep5Valid(),
                onClick = {
                    viewModel.nextStep()
                    navController.navigate(Screen.ProductUploadReview.route)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ─── Radio dot indicator ───
@Composable
private fun RadioDot(selected: Boolean) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(if (selected) MikoColors.PrimaryStart else Color.Transparent)
            .border(
                width = 1.5.dp,
                color = if (selected) MikoColors.PrimaryStart else MikoColors.BorderMedium,
                shape = CircleShape
            )
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}