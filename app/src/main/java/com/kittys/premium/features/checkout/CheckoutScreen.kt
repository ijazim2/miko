package com.kittys.premium.features.checkout

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kittys.premium.R
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.core.ui.components.*
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ═══════════════════════════════════════════════════════
//   MIKO — CHECKOUT SCREEN (3-step flow)
//   Step 0: Address  →  Step 1: Delivery  →  Step 2: Payment
// ═══════════════════════════════════════════════════════

@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentStep by remember { mutableStateOf(0) }

    var selectedPayment by remember { mutableStateOf("card") }

    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address1 by remember { mutableStateOf("") }
    var address2 by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("Colombo") }

    var cardNumber by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.NeuBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            MikoTopBar(
                title = "Checkout",
                onBackClick = {
                    if (currentStep > 0) currentStep-- else navController.popBackStack()
                }
            )

            CheckoutStepIndicator(currentStep = currentStep)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                when (currentStep) {

                    // ─── STEP 0: Delivery Address ───
                    0 -> {
                        Text("Delivery Address", style = MikoTypography.headlineSmall, color = MikoColors.TextPrimary)
                        Spacer(Modifier.height(4.dp))
                        Text("Where should we deliver your order?",
                            style = MikoTypography.bodySmall.copy(color = MikoColors.TextMuted))
                        Spacer(Modifier.height(24.dp))

                        NeuCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(20.dp)) {
                            CheckoutField("Full Name", fullName, { fullName = it }, "e.g. Amali Perera")
                            Spacer(Modifier.height(14.dp))
                            CheckoutField("Phone Number", phone, { phone = it }, "+94 7X XXX XXXX")
                            Spacer(Modifier.height(14.dp))
                            CheckoutField("Address Line 1", address1, { address1 = it }, "House no., Street")
                            Spacer(Modifier.height(14.dp))
                            CheckoutField("Address Line 2", address2, { address2 = it }, "Area, Landmark (optional)")
                            Spacer(Modifier.height(14.dp))
                            CheckoutField("City", city, { city = it }, "e.g. Colombo")
                            Spacer(Modifier.height(14.dp))

                            Text("District", style = MikoTypography.labelMedium.copy(color = MikoColors.TextSecondary))
                            Spacer(Modifier.height(8.dp))
                            DistrictPicker(selected = district, onSelect = { district = it })
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .neuRaised(cornerRadius = 14.dp, shadowOffset = 5.dp)
                                .padding(16.dp)
                        ) {
                            Text("📍", fontSize = 20.sp)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Save this address", style = MikoTypography.titleSmall)
                                Text("Reuse for future orders", style = MikoTypography.bodySmall.copy(color = MikoColors.TextMuted))
                            }
                            var saveAddr by remember { mutableStateOf(true) }
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(7.dp))
                                    .then(
                                        if (saveAddr) Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                                        else Modifier.neuInset(cornerRadius = 7.dp)
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { saveAddr = !saveAddr },
                                contentAlignment = Alignment.Center
                            ) {
                                if (saveAddr) Text("✓", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // ─── STEP 1: Delivery Method ───
                    1 -> {
                        Text("Delivery Method", style = MikoTypography.headlineSmall, color = MikoColors.TextPrimary)
                        Spacer(Modifier.height(4.dp))
                        Text("Choose how you'd like to receive your order",
                            style = MikoTypography.bodySmall.copy(color = MikoColors.TextMuted))
                        Spacer(Modifier.height(24.dp))

                        val deliveryOptions = listOf(
                            Triple("standard", "Standard Delivery", "3–5 business days • FREE over LKR 3,000"),
                            Triple("express", "Express Delivery", "Next business day • LKR 450"),
                            Triple("sameday", "Same Day Delivery", "Before 8 PM today • LKR 750 (Colombo only)")
                        )
                        var selectedDelivery by remember { mutableStateOf("standard") }

                        deliveryOptions.forEach { (id, title, subtitle) ->
                            DeliveryOptionCard(id, title, subtitle, selectedDelivery == id) { selectedDelivery = id }
                            Spacer(Modifier.height(12.dp))
                        }

                        Spacer(Modifier.height(8.dp))

                        NeuCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                                        .background(Brush.linearGradient(MikoColors.GradientPrimary))
                                ) { Text("📍", fontSize = 18.sp) }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("Delivering to", style = MikoTypography.labelSmall.copy(color = MikoColors.TextMuted))
                                    Text("$address1, $city, $district", style = MikoTypography.titleSmall)
                                }
                            }
                        }
                    }

                    // ─── STEP 2: Payment ───
                    2 -> {
                        Text("Payment", style = MikoTypography.headlineSmall, color = MikoColors.TextPrimary)
                        Spacer(Modifier.height(4.dp))
                        Text("Choose your payment method",
                            style = MikoTypography.bodySmall.copy(color = MikoColors.TextMuted))
                        Spacer(Modifier.height(24.dp))

                        val paymentMethods = listOf(
                            Triple("card", "Credit / Debit Card", "💳"),
                            Triple("cash", "Cash on Delivery", "💵"),
                            Triple("ezcash", "eZ Cash / mCash", "📱"),
                            Triple("frimi", "FriMi", "🟢")
                        )
                        paymentMethods.forEach { (id, name, emoji) ->
                            PaymentMethodCard(id, name, emoji, selectedPayment == id) { selectedPayment = id }
                            Spacer(Modifier.height(10.dp))
                        }

                        if (selectedPayment == "card") {
                            Spacer(Modifier.height(8.dp))
                            NeuCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(20.dp)) {
                                Text("Card Details", style = MikoTypography.titleSmall, color = MikoColors.TextPrimary)
                                Spacer(Modifier.height(16.dp))
                                CheckoutField("Card Number", cardNumber, {
                                    if (it.length <= 16) cardNumber = it.filter { c -> c.isDigit() }
                                }, "1234 5678 9012 3456")
                                Spacer(Modifier.height(14.dp))
                                CheckoutField("Name on Card", cardName, { cardName = it }, "e.g. AMALI PERERA")
                                Spacer(Modifier.height(14.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Column(Modifier.weight(1f)) {
                                        CheckoutField("Expiry", cardExpiry, {
                                            if (it.length <= 5) cardExpiry = it
                                        }, "MM/YY")
                                    }
                                    Column(Modifier.weight(1f)) {
                                        CheckoutField("CVV", cardCvv, {
                                            if (it.length <= 3) cardCvv = it.filter { c -> c.isDigit() }
                                        }, "•••")
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        NeuCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(20.dp)) {
                            Text("Order Summary", style = MikoTypography.titleSmall)
                            Spacer(Modifier.height(12.dp))
                            SummaryRow("Subtotal", "LKR ${uiState.subtotal}")
                            SummaryRow("Delivery", if (uiState.deliveryFee == 0) "FREE" else "LKR ${uiState.deliveryFee}")
                            if (uiState.discount > 0) {
                                SummaryRow("Discount", "-LKR ${uiState.discount}", MikoColors.Success)
                            }
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider(color = MikoColors.Divider)
                            Spacer(Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Total", style = MikoTypography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Text(
                                    "LKR ${uiState.total}",
                                    style = MikoTypography.titleMedium.copy(
                                        brush = Brush.linearGradient(MikoColors.GradientPrimary),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(100.dp))
            }
        }

        // ── Bottom Action Button ──
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, MikoColors.NeuBackground, MikoColors.NeuBackground)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            if (uiState.isLoading) {
                Box(Modifier.fillMaxWidth().height(54.dp), Alignment.Center) {
                    CircularProgressIndicator(color = MikoColors.PrimaryStart, modifier = Modifier.size(32.dp))
                }
            } else {
                GradientButton(
                    text = when (currentStep) {
                        0 -> "Continue to Delivery →"
                        1 -> "Continue to Payment →"
                        else -> "Place Order  •  LKR ${uiState.total}"
                    },
                    onClick = {
                        if (currentStep < 2) currentStep++
                        else viewModel.placeOrder(selectedPayment, "$address1, $city, $district")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    LaunchedEffect(uiState.orderPlaced) {
        if (uiState.orderPlaced) {
            navController.navigate(Screen.OrderSuccess.createRoute("MIKO" + System.currentTimeMillis().toString().takeLast(5), uiState.total)) {
                popUpTo(Screen.Cart.route) { inclusive = true }
            }
        }
    }
}

// ── Step Indicator ──
@Composable
fun CheckoutStepIndicator(currentStep: Int) {
    val steps = listOf("Address", "Delivery", "Payment")
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        steps.forEachIndexed { index, label ->
            val done = index < currentStep
            val active = index == currentStep

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            done || active -> Brush.linearGradient(MikoColors.GradientPrimary)
                            else -> Brush.linearGradient(listOf(MikoColors.NeuShadowDark, MikoColors.NeuShadowDark))
                        }
                    )
            ) {
                if (done) Text("✓", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                else Text("${index + 1}",
                    color = if (active) Color.White else MikoColors.TextMuted,
                    style = MikoTypography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }

            if (active || done) {
                Spacer(Modifier.width(6.dp))
                Text(label, style = MikoTypography.labelSmall.copy(
                    color = if (active) MikoColors.PrimaryEnd else MikoColors.TextMuted,
                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal
                ))
            }

            if (index < steps.size - 1) {
                Spacer(Modifier.width(if (active || done) 8.dp else 4.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(
                            if (index < currentStep) Brush.linearGradient(MikoColors.GradientPrimary)
                            else Brush.linearGradient(listOf(MikoColors.Divider, MikoColors.Divider))
                        )
                )
                Spacer(Modifier.width(4.dp))
            }
        }
    }
}

// ── Delivery Option Card ──
@Composable
fun DeliveryOptionCard(id: String, title: String, subtitle: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (isSelected)
                    Modifier.background(MikoColors.PrimaryStart.copy(alpha = 0.06f))
                        .border(1.5.dp, MikoColors.PrimaryStart.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                else Modifier.neuRaised(cornerRadius = 16.dp, shadowOffset = 5.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(20.dp).clip(CircleShape)
                .border(2.dp, if (isSelected) MikoColors.PrimaryStart else MikoColors.Divider, CircleShape)
        ) {
            if (isSelected) Box(Modifier.size(10.dp).clip(CircleShape).background(Brush.linearGradient(MikoColors.GradientPrimary)))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MikoTypography.titleSmall.copy(
                color = if (isSelected) MikoColors.PrimaryEnd else MikoColors.TextPrimary
            ))
            Text(subtitle, style = MikoTypography.bodySmall.copy(color = MikoColors.TextMuted))
        }
    }
}

// ── Payment Method Card ──
@Composable
fun PaymentMethodCard(id: String, name: String, emoji: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .then(
                if (isSelected)
                    Modifier.background(MikoColors.PrimaryStart.copy(alpha = 0.06f))
                        .border(1.5.dp, MikoColors.PrimaryStart.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                else Modifier.neuRaised(cornerRadius = 14.dp, shadowOffset = 5.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(14.dp)
    ) {
        Text(emoji, fontSize = 24.sp)
        Spacer(Modifier.width(14.dp))
        Text(name, style = MikoTypography.titleSmall.copy(
            color = if (isSelected) MikoColors.PrimaryEnd else MikoColors.TextPrimary
        ), modifier = Modifier.weight(1f))
        if (isSelected) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(22.dp).clip(CircleShape).background(Brush.linearGradient(MikoColors.GradientPrimary))
            ) { Text("✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

// ── District Picker ──
@Composable
fun DistrictPicker(selected: String, onSelect: (String) -> Unit) {
    val districts = listOf(
        "Colombo", "Gampaha", "Kalutara", "Kandy", "Matale", "Nuwara Eliya",
        "Galle", "Matara", "Hambantota", "Jaffna", "Kilinochchi", "Mannar",
        "Vavuniya", "Mullaitivu", "Batticaloa", "Ampara", "Trincomalee",
        "Kurunegala", "Puttalam", "Anuradhapura", "Polonnaruwa", "Badulla",
        "Moneragala", "Ratnapura", "Kegalle"
    )
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .neuInset(cornerRadius = 14.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { expanded = true }
                .padding(horizontal = 16.dp)
        ) {
            Text(selected, style = MikoTypography.bodyLarge.copy(color = MikoColors.TextPrimary), modifier = Modifier.weight(1f))
            Text("▼", fontSize = 10.sp, color = MikoColors.TextMuted)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            districts.forEach { d ->
                DropdownMenuItem(
                    text = { Text(d, style = MikoTypography.bodyMedium) },
                    onClick = { onSelect(d); expanded = false }
                )
            }
        }
    }
}

// ── Generic checkout field ──
@Composable
fun CheckoutField(label: String, value: String, onValueChange: (String) -> Unit, hint: String, icon: Int? = null) {
    Text(label, style = MikoTypography.labelMedium.copy(color = MikoColors.TextSecondary, letterSpacing = 0.5.sp))
    Spacer(Modifier.height(6.dp))
    MikoTextField(
        value = value,
        onValueChange = onValueChange,
        hint = hint,
        leadingIcon = icon,
        modifier = Modifier.fillMaxWidth()
    )
}

// ── Summary row helper ──
@Composable
private fun SummaryRow(label: String, value: String, valueColor: Color = MikoColors.TextPrimary) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
    ) {
        Text(label, style = MikoTypography.bodyMedium.copy(color = MikoColors.TextSecondary))
        Text(value, style = MikoTypography.bodyMedium.copy(color = valueColor, fontWeight = FontWeight.SemiBold))
    }
}