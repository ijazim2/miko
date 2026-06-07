package com.kittys.premium.features.payment

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.kittys.premium.core.common.Constants
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.core.ui.components.*
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ═══════════════════════════════════════════════════════
//   PAYMENT SCREEN
//   Step 3 of checkout — select method + confirm payment
// ═══════════════════════════════════════════════════════

@Composable
fun PaymentScreen(
    totalLkr     : Int = 0,
    navController: NavController
) {
    var selectedMethod by remember { mutableStateOf("card") }

    // Card form fields
    var cardNumber by remember { mutableStateOf("") }
    var cardName   by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv    by remember { mutableStateOf("") }
    var saveCard   by remember { mutableStateOf(false) }

    // COD / local payment
    var localPhone by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.NeuBackground)
    ) {
        MikoTopBar(
            title       = "Payment",
            onBackClick = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Order total ──
            NeuCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(20.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Amount to Pay", style = MikoTypography.titleSmall)
                    Text(
                        "LKR $totalLkr",
                        style = MikoTypography.headlineSmall.copy(
                            brush = Brush.linearGradient(MikoColors.GradientBlue),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // ── Payment method selector ──
            Text("Choose Payment Method", style = MikoTypography.titleSmall)

            val methods = listOf(
                Triple("card",    "💳  Credit / Debit Card",  "Visa, Mastercard, Amex"),
                Triple("cod",     "💵  Cash on Delivery",     "Pay when you receive"),
                Triple("ezcash",  "📱  eZ Cash",              "Dialog eZ Cash"),
                Triple("mcash",   "📱  mCash",                "Mobitel mCash"),
                Triple("frimi",   "🟢  FriMi",                "Nations Trust Bank FriMi")
            )

            methods.forEach { (key, label, sub) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .then(
                            if (selectedMethod == key)
                                Modifier
                                    .background(MikoColors.BluePrimary.copy(alpha = 0.06f))
                                    .border(
                                        1.5.dp,
                                        MikoColors.BluePrimary.copy(alpha = 0.40f),
                                        RoundedCornerShape(16.dp)
                                    )
                            else
                                Modifier.neuRaised(cornerRadius = 16.dp, shadowOffset = 5.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null
                        ) { selectedMethod = key }
                        .padding(16.dp)
                ) {
                    RadioButton(
                        selected = selectedMethod == key,
                        onClick  = { selectedMethod = key },
                        colors   = RadioButtonDefaults.colors(
                            selectedColor   = MikoColors.BluePrimary,
                            unselectedColor = MikoColors.TextMuted
                        )
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(label, style = MikoTypography.titleSmall)
                        Text(sub, style = MikoTypography.bodySmall.copy(color = MikoColors.TextMuted))
                    }
                }
            }

            // ── Card form (shown only for card method) ──
            if (selectedMethod == "card") {
                NeuCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(20.dp)) {
                    Text("Card Details", style = MikoTypography.titleSmall)
                    Spacer(Modifier.height(16.dp))

                    Text("Card Number", style = MikoTypography.labelMedium.copy(color = MikoColors.TextSecondary))
                    Spacer(Modifier.height(6.dp))
                    MikoTextField(
                        value         = cardNumber,
                        onValueChange = {
                            if (it.length <= 16) cardNumber = it.filter { c -> c.isDigit() }
                        },
                        hint          = "1234 5678 9012 3456",
                        keyboardType  = KeyboardType.Number,
                        modifier      = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(14.dp))

                    Text("Cardholder Name", style = MikoTypography.labelMedium.copy(color = MikoColors.TextSecondary))
                    Spacer(Modifier.height(6.dp))
                    MikoTextField(
                        value         = cardName,
                        onValueChange = { cardName = it },
                        hint          = "As it appears on your card",
                        modifier      = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text("Expiry", style = MikoTypography.labelMedium.copy(color = MikoColors.TextSecondary))
                            Spacer(Modifier.height(6.dp))
                            MikoTextField(
                                value         = cardExpiry,
                                onValueChange = {
                                    if (it.length <= 5) {
                                        cardExpiry = it.filter { c -> c.isDigit() || c == '/' }
                                    }
                                },
                                hint         = "MM/YY",
                                keyboardType = KeyboardType.Number,
                                modifier     = Modifier.fillMaxWidth()
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            Text("CVV", style = MikoTypography.labelMedium.copy(color = MikoColors.TextSecondary))
                            Spacer(Modifier.height(6.dp))
                            MikoTextField(
                                value         = cardCvv,
                                onValueChange = {
                                    if (it.length <= 4) cardCvv = it.filter { c -> c.isDigit() }
                                },
                                hint         = "•••",
                                keyboardType = KeyboardType.NumberPassword,
                                modifier     = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = saveCard,
                            onCheckedChange = { saveCard = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MikoColors.BluePrimary
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Save card for future purchases",
                            style = MikoTypography.bodyMedium.copy(color = MikoColors.TextSecondary)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Security note
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MikoColors.Success.copy(alpha = 0.08f))
                            .padding(12.dp)
                    ) {
                        Text("🔒", fontSize = 16.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Your card details are encrypted and never stored on our servers.",
                            style = MikoTypography.bodySmall.copy(
                                color = MikoColors.Success,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }

            // ── Local payment number (eZ Cash / mCash / FriMi) ──
            if (selectedMethod in listOf("ezcash","mcash","frimi")) {
                NeuCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(20.dp)) {
                    val methodName = when (selectedMethod) {
                        "ezcash" -> "eZ Cash"; "mcash" -> "mCash"; else -> "FriMi"
                    }
                    Text("$methodName Mobile Number", style = MikoTypography.titleSmall)
                    Spacer(Modifier.height(12.dp))
                    MikoTextField(
                        value         = localPhone,
                        onValueChange = { localPhone = it },
                        hint          = "07X XXX XXXX",
                        keyboardType  = KeyboardType.Phone,
                        modifier      = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "You will receive a payment prompt on your mobile.",
                        style = MikoTypography.bodySmall.copy(color = MikoColors.TextMuted)
                    )
                }
            }

            // ── COD info ──
            if (selectedMethod == "cod") {
                NeuCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💵", fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Cash on Delivery", style = MikoTypography.titleSmall)
                            Text(
                                "Pay LKR $totalLkr when your order arrives at your door.",
                                style = MikoTypography.bodySmall.copy(
                                    color = MikoColors.TextSecondary,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }
            }

            // ── Pay now button ──
           GradientButton(
                text = when (selectedMethod) {
                    "card"   -> "Pay LKR $totalLkr"
                    "cod"    -> "Place Order (COD)"
                    "ezcash" -> "Send eZ Cash Payment"
                    "mcash"  -> "Send mCash Payment"
                    "frimi"  -> "Send FriMi Payment"
                    else     -> "Confirm Payment"
                },
                onClick = {
                    // TODO: integrate PayHere SDK or local payment gateway
                    // On success → navigate to OrderSuccess
                    navController.navigate(Screen.OrderSuccess.createRoute("NEW_ORDER_ID")) {
                        popUpTo(Screen.Cart.route) { inclusive = true }
                    }
                },
                enabled = when (selectedMethod) {
                    "card"   -> cardNumber.length == 16 && cardName.isNotBlank() &&
                                cardExpiry.length == 5 && cardCvv.length >= 3
                    "cod"    -> true
                    else     -> localPhone.length >= 10
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(80.dp))
        }
    }
}
