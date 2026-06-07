package com.kittys.premium.features.auth

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.Label
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Label
import androidx.navigation.NavController
import com.kittys.premium.core.ui.components.CharacterPlaceholder
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Forgot Password Screen
//   Email field → send reset link → success state
// ════════════════════════════════════════════════════════════════

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }

    // Reset state when leaving so it doesn't persist
    DisposableEffect(Unit) {
        onDispose { viewModel.resetState() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        // ─── Back arrow ───
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MikoColors.SurfaceVariant)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { navController.popBackStack() }
        ) {
            Text("←", fontSize = 18.sp, color = MikoColors.TextPrimary)
        }

        if (!uiState.resetEmailSent) {
            // ════════ FORM STATE ════════
            Spacer(Modifier.height(24.dp))

            // Image slot
            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                CharacterPlaceholder(size = 160.dp)
            }

            Spacer(Modifier.height(28.dp))

            Text(
                "Forgot Password?",
                style = MikoTypography.Display.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "No worries! Enter your email and we'll send you a link to reset your password.",
                style = MikoTypography.Body.copy(
                    color = MikoColors.TextSecondary,
                    lineHeight = 22.sp
                )
            )

            Spacer(Modifier.height(28.dp))

            // ─── Email field ───
            Text("Email Address", style = MikoTypography.Label.copy(color = MikoColors.TextSecondary))
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MikoColors.SurfaceVariant)
                    .border(1.dp, MikoColors.BorderLight, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp)
            ) {
                Text("✉", fontSize = 15.sp)
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = email,
                    onValueChange = { email = it; viewModel.clearError() },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    textStyle = TextStyle(fontSize = 15.sp, color = MikoColors.TextPrimary, fontWeight = FontWeight.Medium),
                    cursorBrush = SolidColor(MikoColors.PrimaryStart),
                    modifier = Modifier.weight(1f).padding(vertical = 15.dp),
                    decorationBox = { inner ->
                        if (email.isEmpty()) Text("you@email.com", style = MikoTypography.Body.copy(color = MikoColors.TextMuted))
                        inner()
                    }
                )
            }

            // ─── Error ───
            if (uiState.error != null) {
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MikoColors.ErrorSoft)
                        .padding(12.dp)
                ) {
                    Text("⚠", fontSize = 14.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(uiState.error!!, style = MikoTypography.Caption.copy(color = MikoColors.Error))
                }
            }

            Spacer(Modifier.height(28.dp))

            // ─── Send reset link button ───
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .then(
                        if (!uiState.isLoading)
                            Modifier.background(Brush.linearGradient(MikoColors.GradientButton))
                        else Modifier.background(MikoColors.BorderMedium)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = !uiState.isLoading
                    ) { viewModel.sendPasswordReset(email) }
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
                } else {
                    Text("Send Reset Link", style = MikoTypography.Button.copy(
                        color = Color.White, fontWeight = FontWeight.Bold
                    ))
                }
            }

        } else {
            // ════════ SUCCESS STATE ════════
            Spacer(Modifier.height(60.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(MikoColors.SuccessSoft)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MikoColors.Success)
                ) {
                    Text("✉", fontSize = 36.sp)
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                "Check your email 📬",
                style = MikoTypography.Display.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "We've sent a password reset link to:",
                style = MikoTypography.Body.copy(color = MikoColors.TextSecondary),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
            Text(
                email,
                style = MikoTypography.Subtitle.copy(
                    color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(8.dp))
            Text(
                "Tap the link in the email to set a new password. Check your spam folder if you don't see it.",
                style = MikoTypography.Caption.copy(color = MikoColors.TextMuted, lineHeight = 18.sp),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // Back to sign in
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
                    ) { navController.popBackStack() }
            ) {
                Text("Back to Sign In", style = MikoTypography.Button.copy(
                    color = Color.White, fontWeight = FontWeight.Bold
                ))
            }

            Spacer(Modifier.height(12.dp))

            // Resend
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { viewModel.sendPasswordReset(email) }
                    .padding(12.dp)
            ) {
                Text(
                    "Didn't get it? Resend",
                    style = MikoTypography.Caption.copy(
                        color = MikoColors.PrimaryEnd, fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // ─── Footer: Remember? Sign In ───
        if (!uiState.resetEmailSent) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
            ) {
                Text(
                    "Remember your password? ",
                    style = MikoTypography.Body.copy(color = MikoColors.TextSecondary)
                )
                Text(
                    "Sign In",
                    style = MikoTypography.Body.copy(
                        color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.popBackStack() }
                )
            }
        }
    }
}