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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Signup Screen
//   Name · Email · Phone · Password (+ live rules) · Confirm · Terms
// ════════════════════════════════════════════════════════════════

@Composable
fun SignupScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }

    val rules = remember(password) { AuthViewModel.passwordRules(password) }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            navController.navigate(Screen.Home.route) {
                popUpTo(0) { inclusive = true }
            }
        }
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

        Spacer(Modifier.height(20.dp))

        Text(
            "Create Account",
            style = MikoTypography.Display.copy(
                color = MikoColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Join MIKO and start shopping for your little ones",
            style = MikoTypography.Body.copy(color = MikoColors.TextSecondary)
        )

        Spacer(Modifier.height(28.dp))

        // ─── Full name ───
        SignupFieldLabel("Full Name")
        SignupField(fullName, { fullName = it; viewModel.clearError() }, "Your full name", KeyboardType.Text, "👤")

        Spacer(Modifier.height(16.dp))

        // ─── Email ───
        SignupFieldLabel("Email Address")
        SignupField(email, { email = it; viewModel.clearError() }, "you@email.com", KeyboardType.Email, "✉")

        Spacer(Modifier.height(16.dp))

        // ─── Phone ───
        SignupFieldLabel("Phone Number")
        SignupField(phone, { phone = it; viewModel.clearError() }, "07X XXX XXXX", KeyboardType.Phone, "📱")

        Spacer(Modifier.height(16.dp))

        // ─── Password ───
        SignupFieldLabel("Password")
        SignupField(
            value = password,
            onChange = { password = it; viewModel.clearError() },
            hint = "Create a password",
            keyboardType = KeyboardType.Password,
            leading = "🔒",
            isPassword = true,
            passwordVisible = passwordVisible,
            onToggleVisibility = { passwordVisible = !passwordVisible }
        )

        // ─── Live password rules checklist ───
        if (password.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MikoColors.SurfaceVariant)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                rules.forEach { rule ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(if (rule.met) MikoColors.Success else MikoColors.BorderMedium)
                        ) {
                            Text(if (rule.met) "✓" else "", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            rule.label,
                            style = MikoTypography.Caption.copy(
                                color = if (rule.met) MikoColors.Success else MikoColors.TextMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ─── Confirm password ───
        SignupFieldLabel("Confirm Password")
        SignupField(
            value = confirmPassword,
            onChange = { confirmPassword = it; viewModel.clearError() },
            hint = "Re-enter your password",
            keyboardType = KeyboardType.Password,
            leading = "🔒",
            isPassword = true,
            passwordVisible = passwordVisible,
            onToggleVisibility = { passwordVisible = !passwordVisible }
        )
        if (confirmPassword.isNotEmpty() && confirmPassword != password) {
            Spacer(Modifier.height(6.dp))
            Text(
                "Passwords don't match",
                style = MikoTypography.Caption.copy(color = MikoColors.Error)
            )
        }

        Spacer(Modifier.height(20.dp))

        // ─── Terms checkbox ───
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { agreedToTerms = !agreedToTerms }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .then(
                        if (agreedToTerms)
                            Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                        else
                            Modifier.background(MikoColors.SurfaceVariant)
                                .border(1.5.dp, MikoColors.BorderMedium, RoundedCornerShape(6.dp))
                    )
            ) {
                if (agreedToTerms) Text("✓", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(10.dp))
            Text(
                "I agree to the Terms & Privacy Policy",
                style = MikoTypography.Caption.copy(color = MikoColors.TextSecondary)
            )
        }

        // ─── Error ───
        if (uiState.error != null) {
            Spacer(Modifier.height(14.dp))
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

        Spacer(Modifier.height(24.dp))

        // ─── Create account button ───
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
                ) {
                    viewModel.signup(fullName, email, phone, password, confirmPassword, agreedToTerms)
                }
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
            } else {
                Text("Create Account", style = MikoTypography.Button.copy(
                    color = Color.White, fontWeight = FontWeight.Bold
                ))
            }
        }

        Spacer(Modifier.height(20.dp))

        // ─── Divider ───
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = MikoColors.Divider)
            Text("  or  ", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
            HorizontalDivider(modifier = Modifier.weight(1f), color = MikoColors.Divider)
        }

        Spacer(Modifier.height(20.dp))

        // ─── Google sign-up ───
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MikoColors.Surface)
                .border(1.5.dp, MikoColors.BorderMedium, RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = !uiState.isLoading
                ) { viewModel.signInWithGoogle() }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, MikoColors.BorderLight, CircleShape)
            ) {
                Text("G", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))
            }
            Spacer(Modifier.width(12.dp))
            Text(
                "Continue with Google",
                style = MikoTypography.Button.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold
                )
            )
        }

        Spacer(Modifier.height(28.dp))

        // ─── Footer: Sign In ───
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Already have an account? ",
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

        Spacer(Modifier.height(30.dp))
    }
}

// ─── Shared label ───
@Composable
private fun SignupFieldLabel(text: String) {
    Text(text, style = MikoTypography.Label.copy(color = MikoColors.TextSecondary))
    Spacer(Modifier.height(8.dp))
}

// ─── Shared field ───
@Composable
private fun SignupField(
    value: String,
    onChange: (String) -> Unit,
    hint: String,
    keyboardType: KeyboardType,
    leading: String,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onToggleVisibility: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MikoColors.SurfaceVariant)
            .border(1.dp, MikoColors.BorderLight, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp)
    ) {
        Text(leading, fontSize = 15.sp)
        Spacer(Modifier.width(10.dp))
        BasicTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            textStyle = TextStyle(fontSize = 15.sp, color = MikoColors.TextPrimary, fontWeight = FontWeight.Medium),
            cursorBrush = SolidColor(MikoColors.PrimaryStart),
            modifier = Modifier.weight(1f).padding(vertical = 15.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) Text(hint, style = MikoTypography.Body.copy(color = MikoColors.TextMuted))
                inner()
            }
        )
        if (isPassword) {
            Text(
                if (passwordVisible) "🙈" else "👁",
                fontSize = 15.sp,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onToggleVisibility() }
            )
        }
    }
}