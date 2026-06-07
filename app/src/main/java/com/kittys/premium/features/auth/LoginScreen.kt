package com.kittys.premium.features.auth

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
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
import com.kittys.premium.core.ui.components.LogoPlaceholder
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Login Screen
//   Email/phone + password · forgot password · Google sign-in only
// ════════════════════════════════════════════════════════════════

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Navigate home when authenticated
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

        // ─── Logo + welcome ───
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            LogoPlaceholder(size = 80.dp)
        }
        Spacer(Modifier.height(20.dp))
        Text(
            "Welcome back 👋",
            style = MikoTypography.Display.copy(
                color = MikoColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Sign in to continue shopping for your little ones",
            style = MikoTypography.Body.copy(color = MikoColors.TextSecondary)
        )

        Spacer(Modifier.height(32.dp))

        // ─── Email / phone field ───
        FieldLabel("Email or Phone")
        AuthTextField(
            value = emailOrPhone,
            onChange = { emailOrPhone = it; viewModel.clearError() },
            hint = "you@email.com or 07X XXX XXXX",
            keyboardType = KeyboardType.Email,
            leading = "✉"
        )

        Spacer(Modifier.height(16.dp))

        // ─── Password field ───
        FieldLabel("Password")
        AuthTextField(
            value = password,
            onChange = { password = it; viewModel.clearError() },
            hint = "Enter your password",
            keyboardType = KeyboardType.Password,
            leading = "🔒",
            isPassword = true,
            passwordVisible = passwordVisible,
            onToggleVisibility = { passwordVisible = !passwordVisible }
        )

        // ─── Forgot password ───
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Text(
                "Forgot password?",
                style = MikoTypography.Caption.copy(
                    color = MikoColors.PrimaryEnd,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.navigate(Screen.ForgotPassword.route) }
                    .padding(vertical = 10.dp)
            )
        }

        // ─── Error ───
        if (uiState.error != null) {
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
            Spacer(Modifier.height(12.dp))
        } else {
            Spacer(Modifier.height(8.dp))
        }

        // ─── Sign in button ───
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
                ) { viewModel.login(emailOrPhone, password) }
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
            } else {
                Text("Sign In", style = MikoTypography.Button.copy(
                    color = Color.White, fontWeight = FontWeight.Bold
                ))
            }
        }

        Spacer(Modifier.height(24.dp))

        // ─── Divider ───
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = MikoColors.Divider)
            Text(
                "  or  ",
                style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = MikoColors.Divider)
        }

        Spacer(Modifier.height(24.dp))

        // ─── Google sign-in (ONLY social option) ───
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
            // Google "G" mark (text-based, no asset needed)
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
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Spacer(Modifier.height(32.dp))

        // ─── Footer: Sign Up ───
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "New to Miko? ",
                style = MikoTypography.Body.copy(color = MikoColors.TextSecondary)
            )
            Text(
                "Sign Up",
                style = MikoTypography.Body.copy(
                    color = MikoColors.PrimaryEnd,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { navController.navigate(Screen.Signup.route) }
            )
        }

        Spacer(Modifier.height(30.dp))
    }
}

// ─── Shared field label ───
@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        style = MikoTypography.Label.copy(color = MikoColors.TextSecondary)
    )
    Spacer(Modifier.height(8.dp))
}

// ─── Shared auth text field ───
@Composable
private fun AuthTextField(
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
                if (value.isEmpty()) {
                    Text(hint, style = MikoTypography.Body.copy(color = MikoColors.TextMuted))
                }
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