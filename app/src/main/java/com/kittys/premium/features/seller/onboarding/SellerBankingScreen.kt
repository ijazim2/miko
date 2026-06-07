package com.kittys.premium.features.seller.onboarding

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
import com.kittys.premium.domain.model.SellerLevel
import com.kittys.premium.domain.model.SriLankanBank
import com.kittys.premium.core.ui.components.MikoPrimaryButton
import com.kittys.premium.core.ui.components.MikoTextButton
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Seller Onboarding Step 4: Banking Details
// ════════════════════════════════════════════════════════════════

@Composable
fun SellerBankingScreen(
    navController: NavController,
    viewModel: SellerOnboardingViewModel = hiltViewModel()
) {
    var accountHolder by remember { mutableStateOf("") }
    var selectedBank by remember { mutableStateOf<SriLankanBank?>(null) }
    var accountNumber by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }
    var addMobileWallet by remember { mutableStateOf(false) }
    var ezCashNumber by remember { mutableStateOf("") }
    var showBankPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background)
    ) {
        SellerProgressTopBar(
            currentStep = 4,
            totalSteps = 4,
            title = "Banking Details",
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
                        "Where should we send your earnings?",
                        style = MikoTypography.Headline.copy(
                            color = MikoColors.TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Add your bank account or mobile wallet. We'll transfer your earnings within 24 hours of order completion.",
                        style = MikoTypography.Body.copy(
                            color = MikoColors.TextSecondary,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            item {
                Text(
                    "🏦 Bank Account",
                    style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            item {
                FormField(label = "Account Holder Name", required = true) {
                    OutlinedTextField(
                        value = accountHolder,
                        onValueChange = { accountHolder = it },
                        placeholder = { Text("As shown on your bank statement") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MikoColors.PrimaryStart,
                            unfocusedBorderColor = MikoColors.BorderLight,
                            cursorColor = MikoColors.PrimaryStart
                        )
                    )
                }
            }

            item {
                FormField(label = "Bank", required = true) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MikoColors.Surface)
                            .border(1.dp, MikoColors.BorderLight, RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { showBankPicker = true }
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                selectedBank?.displayName ?: "Select your bank",
                                style = MikoTypography.Body.copy(
                                    color = if (selectedBank != null) MikoColors.TextPrimary else MikoColors.TextMuted,
                                    fontWeight = if (selectedBank != null) FontWeight.Medium else FontWeight.Normal
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text("▼", color = MikoColors.TextMuted, fontSize = 12.sp)
                        }
                    }
                }
            }

            item {
                FormField(label = "Account Number", required = true) {
                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = {
                            if (it.all { c -> c.isDigit() }) accountNumber = it
                        },
                        placeholder = { Text("Your account number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MikoColors.PrimaryStart,
                            unfocusedBorderColor = MikoColors.BorderLight,
                            cursorColor = MikoColors.PrimaryStart
                        )
                    )
                }
            }

            item {
                FormField(label = "Branch Name") {
                    OutlinedTextField(
                        value = branch,
                        onValueChange = { branch = it },
                        placeholder = { Text("e.g. Colombo Main") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MikoColors.PrimaryStart,
                            unfocusedBorderColor = MikoColors.BorderLight,
                            cursorColor = MikoColors.PrimaryStart
                        )
                    )
                }
            }

            // ─── Mobile Wallet (Optional) ───
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .neuRaised(cornerRadius = 12.dp, shadowOffset = 4.dp)
                        .padding(14.dp)
                ) {
                    Text("📱", fontSize = 22.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Add mobile wallet (Optional)",
                            style = MikoTypography.Subtitle.copy(
                                color = MikoColors.TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            "eZ Cash, mCash, FriMi",
                            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                        )
                    }
                    Switch(
                        checked = addMobileWallet,
                        onCheckedChange = { addMobileWallet = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MikoColors.PrimaryStart,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = MikoColors.BorderMedium
                        )
                    )
                }
            }

            if (addMobileWallet) {
                item {
                    FormField(label = "Mobile Wallet Number") {
                        OutlinedTextField(
                            value = ezCashNumber,
                            onValueChange = {
                                if (it.length <= 10 && it.all { c -> c.isDigit() }) {
                                    ezCashNumber = it
                                }
                            },
                            placeholder = { Text("e.g. 0771234567") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = { Text("📱", fontSize = 16.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MikoColors.PrimaryStart,
                                unfocusedBorderColor = MikoColors.BorderLight,
                                cursorColor = MikoColors.PrimaryStart
                            )
                        )
                    }
                }
            }

            // ─── Security Notice ───
            item {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MikoColors.SuccessSoft)
                        .padding(14.dp)
                ) {
                    Text("🔐", fontSize = 20.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            "Your information is secure",
                            style = MikoTypography.Subtitle.copy(
                                color = MikoColors.Success,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "Bank details are encrypted with bank-grade security. Only you and our payment partner can access them.",
                            style = MikoTypography.Caption.copy(
                                color = MikoColors.TextSecondary,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(20.dp)) }
        }

        // ─── Bottom Button ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MikoColors.Surface)
                .padding(20.dp)
        ) {
            val isValid = accountHolder.isNotBlank() &&
                    selectedBank != null &&
                    accountNumber.length >= 8

            MikoPrimaryButton(
                text = "Complete Setup",
                enabled = isValid,
                trailingIcon = null,
                onClick = {
                    viewModel.updateBanking(
                        accountHolder = accountHolder,
                        bank = selectedBank!!,
                        accountNumber = accountNumber,
                        branch = branch
                    )
                    if (addMobileWallet && ezCashNumber.isNotBlank()) {
                        viewModel.updateEzCash(ezCashNumber)
                    }
                    viewModel.completeOnboarding()
                    navController.navigate(Screen.SellerDashboard.route) {
                        popUpTo(Screen.SellerIntro.route) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // ─── Bank Picker Bottom Sheet ───
    if (showBankPicker) {
        BankPickerSheet(
            selected = selectedBank,
            onSelect = {
                selectedBank = it
                viewModel.updateBank(it)
                showBankPicker = false
            },
            onDismiss = { showBankPicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BankPickerSheet(
    selected: SriLankanBank?,
    onSelect: (SriLankanBank) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MikoColors.Surface
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Select Your Bank",
                style = MikoTypography.Title.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SriLankanBank.values()) { bank ->
                    val isSelected = bank == selected
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MikoColors.PastelPink else MikoColors.Background
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onSelect(bank) }
                            .padding(16.dp)
                    ) {
                        Text(
                            bank.displayName,
                            style = MikoTypography.Body.copy(
                                color = if (isSelected) MikoColors.PrimaryEnd else MikoColors.TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        if (isSelected) {
                            Text("✓", color = MikoColors.PrimaryStart, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}


// ════════════════════════════════════════════════════════════════
//   SELLER SUCCESS SCREEN (shown after onboarding completes)
// ════════════════════════════════════════════════════════════════

@Composable
fun SellerSuccessScreen(navController: NavController) {

    val level = SellerLevel.LEVEL_1_NEW

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(40.dp))

        // Big success icon
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(MikoColors.SuccessSoft)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MikoColors.Success)
            ) {
                Text("✓", color = Color.White, fontSize = 56.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "You're now a seller! 🎉",
            style = MikoTypography.Headline.copy(
                color = MikoColors.TextPrimary,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "Welcome to the MIKO seller family. Your store is ready!",
            style = MikoTypography.Body.copy(
                color = MikoColors.TextSecondary,
                lineHeight = 22.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        // ─── Seller Level Badge ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(MikoColors.GradientButton))
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MikoColors.Premium)
                ) {
                    Text("🥉", fontSize = 30.sp)
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "LEVEL 1 SELLER",
                        style = MikoTypography.Caption.copy(
                            color = MikoColors.Premium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    )
                    Text(
                        level.displayName,
                        style = MikoTypography.Title.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        "Can list up to ${level.maxProducts} products",
                        style = MikoTypography.Caption.copy(
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ─── Next Steps ───
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp)
                .padding(16.dp)
        ) {
            Text(
                "What's next?",
                style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(Modifier.height(12.dp))
            NextStepRow("1", "Add your first product")
            NextStepRow("2", "Get more visibility (verify ID for free)")
            NextStepRow("3", "Set up shipping options")
            NextStepRow("4", "Share your store with friends")
        }

        Spacer(Modifier.height(32.dp))

        // ─── Actions ───
        MikoPrimaryButton(
            text = "Add Your First Product",
            onClick = {
                navController.navigate(Screen.ProductUploadBasics.route)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        MikoTextButton(
            text = "Go to Dashboard",
            onClick = {
                navController.navigate(Screen.SellerDashboard.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            color = MikoColors.TextSecondary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun NextStepRow(number: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MikoColors.PastelPink)
        ) {
            Text(
                number,
                style = MikoTypography.Label.copy(
                    color = MikoColors.PrimaryStart,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text,
            style = MikoTypography.Body.copy(color = MikoColors.TextSecondary)
        )
    }
}