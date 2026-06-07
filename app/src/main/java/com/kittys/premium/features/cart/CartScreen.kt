package com.kittys.premium.features.cart

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography
import com.kittys.premium.domain.model.CartItem
import com.kittys.premium.domain.model.formatThousands

// ════════════════════════════════════════════════════════════════
//   MIKO — Cart Screen
// ════════════════════════════════════════════════════════════════

@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

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
                "My Cart",
                style = MikoTypography.Title.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                )
            )
        }

        if (state.items.isEmpty()) {
            // ─── Empty state ───
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().padding(32.dp)
            ) {
                Text("🛒", fontSize = 64.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    "Your cart is empty",
                    style = MikoTypography.Headline.copy(color = MikoColors.TextPrimary)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Browse our collections and add some cute outfits!",
                    style = MikoTypography.Body.copy(color = MikoColors.TextSecondary),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            return@Column
        }

        // ─── Free shipping progress ──
        if (!state.qualifiesFreeShipping) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MikoColors.PastelButter)
                    .padding(12.dp)
            ) {
                Text(
                    "Add LKR ${state.amountToFreeShipping.formatThousands()} more for FREE shipping! 🚚",
                    style = MikoTypography.Caption.copy(color = MikoColors.TextSecondary)
                )
            }
        }

        // ─── Cart items ───
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.items, key = { it.id }) { item ->
                CartItemRow(
                    item = item,
                    onIncrease = { viewModel.increaseQty(item.id) },
                    onDecrease = { viewModel.decreaseQty(item.id) },
                    onRemove = { viewModel.removeItem(item.id) }
                )
            }

            // Promo code
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .neuRaised(cornerRadius = 14.dp, shadowOffset = 4.dp)
                        .padding(horizontal = 14.dp)
                ) {
                    BasicTextField(
                        value = state.promoCode,
                        onValueChange = { viewModel.updatePromoCode(it) },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 14.sp, color = MikoColors.TextPrimary),
                        cursorBrush = SolidColor(MikoColors.PrimaryStart),
                        modifier = Modifier.weight(1f).padding(vertical = 16.dp),
                        decorationBox = { inner ->
                            if (state.promoCode.isEmpty()) {
                                Text("Promo code", style = MikoTypography.Body.copy(color = MikoColors.TextMuted))
                            }
                            inner()
                        }
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.linearGradient(MikoColors.GradientButton))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { viewModel.applyPromo() }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Apply", style = MikoTypography.Caption.copy(color = Color.White, fontWeight = FontWeight.Bold))
                    }
                }
                if (state.promoApplied) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "✓ Promo applied: -LKR ${state.promoDiscount.formatThousands()}",
                        style = MikoTypography.Caption.copy(color = MikoColors.Success, fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Totals
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp)
                        .padding(16.dp)
                ) {
                    TotalRow("Subtotal", "LKR ${state.subtotal.formatThousands()}")
                    Spacer(Modifier.height(4.dp))
                    TotalRow("Delivery", if (state.deliveryFee == 0) "FREE" else "LKR ${state.deliveryFee.formatThousands()}")
                    if (state.promoDiscount > 0) {
                        Spacer(Modifier.height(4.dp))
                        TotalRow("Discount", "-LKR ${state.promoDiscount.formatThousands()}")
                    }
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = MikoColors.Divider)
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Total", style = MikoTypography.Subtitle.copy(
                            color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                        ))
                        Text("LKR ${state.total.formatThousands()}", style = MikoTypography.Title.copy(
                            color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
                        ))
                    }
                }
            }
        }

        // ─── Checkout button ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MikoColors.Surface)
                .padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(MikoColors.GradientButton))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.navigate(Screen.Checkout.route) }
            ) {
                Text(
                    "Proceed to Checkout · LKR ${state.total.formatThousands()}",
                    style = MikoTypography.Button.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .neuRaised(cornerRadius = 14.dp, shadowOffset = 4.dp)
            .padding(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(10.dp)).background(MikoColors.PastelPink)
        ) { Text("🖼", fontSize = 24.sp) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(item.name, style = MikoTypography.Subtitle.copy(
                color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold
            ), maxLines = 1)
            Text("${item.size} · ${item.color}", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
            Text("LKR ${item.priceInt.formatThousands()}", style = MikoTypography.Subtitle.copy(
                color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
            ))
        }
        // Qty stepper
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                QtyButton("−", onDecrease)
                Text(
                    "${item.quantity}",
                    style = MikoTypography.Subtitle.copy(color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                QtyButton("+", onIncrease)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Remove",
                style = MikoTypography.Caption.copy(color = MikoColors.Error, fontSize = 10.sp),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onRemove() }
            )
        }
    }
}

@Composable
private fun QtyButton(symbol: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(MikoColors.SurfaceVariant)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Text(symbol, fontSize = 16.sp, color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TotalRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text(label, style = MikoTypography.Body.copy(color = MikoColors.TextSecondary))
        Text(value, style = MikoTypography.Body.copy(color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold))
    }
}