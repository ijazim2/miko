package com.kittys.premium.features.seller.onboarding

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
import com.kittys.premium.core.ui.components.MikoPrimaryButton
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography
import kotlinx.coroutines.delay

// ════════════════════════════════════════════════════════════════
//   MIKO — Seller Onboarding Step 3: Phone Verification
// ════════════════════════════════════════════════════════════════

@Composable
fun SellerVerificationScreen(
    navController: NavController,
    viewModel: SellerOnboardingViewModel = hiltViewModel()
) {
    var phoneNumber by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }
    var resendTimer by remember { mutableStateOf(0) }

    LaunchedEffect(resendTimer) {
        if (resendTimer > 0) {
            delay(1000)
            resendTimer -= 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background)
    ) {
        SellerProgressTopBar(
            currentStep = 3,
            totalSteps = 4,
            title = "Verify Yourself",
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
                        if (!otpSent) "Verify your phone" else "Enter verification code",
                        style = MikoTypography.Headline.copy(
                            color = MikoColors.TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        if (!otpSent)
                            "We'll send you a 6-digit code via SMS to confirm it's really you"
                        else
                            "Enter the 6-digit code we sent to +94 $phoneNumber",
                        style = MikoTypography.Body.copy(
                            color = MikoColors.TextSecondary,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            if (!otpSent) {
                // ─── Phone Input ───
                item {
                    FormField(label = "Mobile Number", required = true) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MikoColors.Surface)
                                .border(1.dp, MikoColors.BorderLight, RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Text("🇱🇰", fontSize = 22.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "+94",
                                style = MikoTypography.Subtitle.copy(
                                    color = MikoColors.TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(24.dp)
                                    .background(MikoColors.BorderLight)
                            )
                            Spacer(Modifier.width(12.dp))
                            BasicTextField(
                                value = phoneNumber,
                                onValueChange = {
                                    if (it.length <= 9 && it.all { c -> c.isDigit() }) {
                                        phoneNumber = it
                                    }
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                textStyle = TextStyle(
                                    fontSize = 16.sp,
                                    color = MikoColors.TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.weight(1f).padding(vertical = 16.dp),
                                cursorBrush = SolidColor(MikoColors.PrimaryStart),
                                decorationBox = { inner ->
                                    if (phoneNumber.isEmpty()) {
                                        Text(
                                            "77 123 4567",
                                            style = MikoTypography.Body.copy(
                                                color = MikoColors.TextMuted
                                            )
                                        )
                                    }
                                    inner()
                                }
                            )
                        }
                    }
                }

                item {
                    InfoBox(
                        icon = "🔒",
                        text = "Your phone number is private and will never be shared with buyers"
                    )
                }

            } else {
                // ─── OTP Input ───
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(6) { index ->
                            OtpDigitBox(
                                digit = otpCode.getOrNull(index)?.toString() ?: "",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Hidden input for OTP
                item {
                    BasicTextField(
                        value = otpCode,
                        onValueChange = {
                            if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                                otpCode = it
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        textStyle = TextStyle(color = Color.Transparent),
                        cursorBrush = SolidColor(Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                    )
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (resendTimer > 0) {
                            Text(
                                "Resend code in ${resendTimer}s",
                                style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                            )
                        } else {
                            Text(
                                "Resend code",
                                style = MikoTypography.Body.copy(
                                    color = MikoColors.PrimaryStart,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    resendTimer = 60
                                    otpCode = ""
                                    viewModel.sendOtp()
                                }
                            )
                        }
                    }
                }

                item {
                    Text(
                        "Change phone number",
                        style = MikoTypography.Body.copy(
                            color = MikoColors.TextSecondary,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                otpSent = false
                                otpCode = ""
                            },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // ─── Bottom Button ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MikoColors.Surface)
                .padding(20.dp)
        ) {
            val isValid = if (!otpSent) phoneNumber.length == 9 else otpCode.length == 6

            MikoPrimaryButton(
                text = if (!otpSent) "Send Verification Code" else "Verify & Continue",
                enabled = isValid,
                trailingIcon = null,
                onClick = {
                    if (!otpSent) {
                        viewModel.updatePhone("+94$phoneNumber")
                        viewModel.sendOtp()
                        otpSent = true
                        resendTimer = 60
                    } else {
                        viewModel.verifyOtp(otpCode)
                        navController.navigate(Screen.SellerBanking.route)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ─── OTP Digit Box ───
@Composable
private fun OtpDigitBox(digit: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(0.9f)
            .clip(RoundedCornerShape(12.dp))
            .background(MikoColors.Surface)
            .border(
                width = 1.5.dp,
                color = if (digit.isNotEmpty()) MikoColors.PrimaryStart else MikoColors.BorderLight,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Text(
            digit,
            style = MikoTypography.Headline.copy(
                color = MikoColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun InfoBox(icon: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MikoColors.InfoSoft)
            .padding(14.dp)
    ) {
        Text(icon, fontSize = 20.sp)
        Spacer(Modifier.width(10.dp))
        Text(
            text,
            style = MikoTypography.Caption.copy(
                color = MikoColors.TextSecondary,
                lineHeight = 18.sp
            )
        )
    }
}