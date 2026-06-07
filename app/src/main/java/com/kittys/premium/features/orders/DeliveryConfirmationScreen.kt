package com.kittys.premium.features.orders

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.kittys.premium.R
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.core.ui.components.*
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography
import com.kittys.premium.domain.model.formatThousands

// ═══════════════════════════════════════════════════════
//   MIKO — DELIVERY CONFIRMATION SCREEN
//   Customer uploads photo proof, rating → money released
// ═══════════════════════════════════════════════════════

@Composable
fun DeliveryConfirmationScreen(
    orderId: String,
    navController: NavController,
    viewModel: EscrowViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var rating       by remember { mutableStateOf(0) }
    var notes        by remember { mutableStateOf("") }
    var photoUris    by remember { mutableStateOf<List<String>>(emptyList()) }
    var showSuccess  by remember { mutableStateOf(false) }

    LaunchedEffect(orderId) { viewModel.loadOrder(orderId) }

    if (showSuccess) {
        SuccessAnimationScreen(onContinue = { navController.popBackStack() })
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.NeuBackground)
    ) {
        MikoTopBar(
            title = "Confirm Delivery",
            onBackClick = { navController.popBackStack() }
        )

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ─── 1. INFO BANNER ───
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(listOf(MikoColors.PastelMint, MikoColors.PastelSky))
                        )
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🛡", fontSize = 32.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Buyer Protection Active",
                                style = MikoTypography.titleSmall.copy(
                                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                "Money is released to seller only after you confirm.",
                                style = MikoTypography.bodySmall.copy(
                                    color = MikoColors.TextSecondary, lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }
            }

            // ─── 2. ESCROW / ORDER PREVIEW ───
            item {
                NeuCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MikoColors.PastelPink)
                        ) { Text("📦", fontSize = 26.sp) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Order #${orderId.take(8).uppercase()}",
                                style = MikoTypography.labelSmall.copy(color = MikoColors.TextMuted)
                            )
                            Text(
                                "Payment held in escrow",
                                style = MikoTypography.titleSmall
                            )
                            Text(
                                "LKR ${(uiState.escrow?.amountLkr ?: 0).formatThousands()}",
                                style = MikoTypography.titleSmall.copy(
                                    brush = Brush.linearGradient(MikoColors.GradientPink),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            // ─── 3. RATING ───
            item {
                NeuCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(18.dp)) {
                    Text(
                        "How was your order?",
                        style = MikoTypography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(5) { i ->
                            val filled = i < rating
                            Text(
                                if (filled) "⭐" else "☆",
                                fontSize = 38.sp,
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { rating = i + 1 }
                            )
                        }
                    }
                    if (rating > 0) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            listOf("", "Terrible", "Poor", "Okay", "Good", "Excellent")[rating],
                            style = MikoTypography.titleSmall.copy(
                                brush = Brush.linearGradient(MikoColors.GradientPink)
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // ─── 4. PHOTO PROOF (REQUIRED) ───
            item {
                NeuCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Photo Proof",
                            style = MikoTypography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MikoColors.Coral.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "Required",
                                style = MikoTypography.labelSmall.copy(
                                    color = MikoColors.Coral, fontWeight = FontWeight.Bold, fontSize = 9.sp
                                )
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Upload at least 1 photo of the received product",
                        style = MikoTypography.bodySmall.copy(color = MikoColors.TextMuted)
                    )
                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        repeat(3) { index ->
                            val hasPhoto = index < photoUris.size
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .then(
                                        if (hasPhoto)
                                            Modifier.background(MikoColors.PastelPink)
                                        else
                                            Modifier
                                                .background(MikoColors.PinkPrimary.copy(alpha = 0.08f))
                                                .border(
                                                    width = 1.dp,
                                                    color = MikoColors.PinkPrimary.copy(alpha = 0.3f),
                                                    shape = RoundedCornerShape(14.dp)
                                                )
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        photoUris = photoUris + "photo_$index"
                                    }
                            ) {
                                if (hasPhoto) {
                                    Text("✓", fontSize = 22.sp, color = MikoColors.Success)
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("📷", fontSize = 20.sp)
                                        Text(
                                            if (index == 0) "Add" else "+",
                                            style = MikoTypography.labelSmall.copy(
                                                color = MikoColors.PinkPrimary, fontSize = 9.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ─── 5. NOTES (Optional) ───
            item {
                NeuCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(18.dp)) {
                    Text(
                        "Notes (Optional)",
                        style = MikoTypography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(Modifier.height(8.dp))
                    MikoTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        hint = "Share your experience...",
                        modifier = Modifier.fillMaxWidth().height(80.dp)
                    )
                }
            }

            // ─── 6. WHAT HAPPENS NEXT ───
            item {
                NeuCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MikoColors.PastelLavender.copy(alpha = 0.3f)),
                    padding = PaddingValues(16.dp)
                ) {
                    Text(
                        "What happens next?",
                        style = MikoTypography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(Modifier.height(8.dp))
                    StepRow("✓", "Money released to seller immediately")
                    StepRow("⭐", "Your rating helps other parents")
                    StepRow("🎁", "Earn 50 loyalty points")
                }
            }

            // ─── 7. SUBMIT ───
            item {
                Spacer(Modifier.height(8.dp))
                GradientButton(
                    text = "Confirm Receipt & Release Payment",
                    onClick = {
                        viewModel.confirmDelivery(
                            orderId = orderId,
                            rating = rating,
                            photoUrls = photoUris,
                            notes = notes
                        )
                        showSuccess = true
                    },
                    enabled = rating > 0 && photoUris.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Product damaged or wrong?",
                        style = MikoTypography.bodySmall.copy(color = MikoColors.TextMuted)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Report a problem",
                        style = MikoTypography.bodySmall.copy(
                            color = MikoColors.Coral, fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { navController.navigate(Screen.ReportDamage.createRoute(orderId)) }
                    )
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun StepRow(emoji: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 3.dp)
    ) {
        Text(emoji, fontSize = 14.sp)
        Spacer(Modifier.width(10.dp))
        Text(text, style = MikoTypography.bodySmall.copy(color = MikoColors.TextSecondary))
    }
}

// ─── Success Screen with Celebration ───
@Composable
fun SuccessAnimationScreen(onContinue: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.NeuBackground)
            .padding(32.dp)
    ) {
        Text("🎉", fontSize = 90.sp)

        Spacer(Modifier.height(16.dp))
        Text(
            "Delivery Confirmed!",
            style = MikoTypography.headlineMedium.copy(
                brush = Brush.linearGradient(MikoColors.GradientPink),
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Thank you! Payment has been released to the seller.\n+50 loyalty points earned 🎁",
            style = MikoTypography.bodyMedium.copy(
                color = MikoColors.TextSecondary, lineHeight = 22.sp
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        GradientButton(
            text = "Continue Shopping",
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth()
        )
    }
}