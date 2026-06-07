package com.kittys.premium.features.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import com.kittys.premium.core.ui.components.MikoPrimaryButton
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Edit Profile Screen
// ════════════════════════════════════════════════════════════════

@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var fullName by remember(state.userName) { mutableStateOf(state.userName) }
    var phone by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }

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
                "Edit Profile",
                style = MikoTypography.Title.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {

            // ─── Avatar ───
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(MikoColors.GradientPrimary))
                        ) {
                            Text(
                                state.userName.take(1).uppercase(),
                                style = MikoTypography.Display.copy(
                                    color = Color.White,
                                    fontSize = 40.sp
                                )
                            )
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(MikoColors.GradientButton))
                                .border(2.dp, MikoColors.Background, CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { /* TODO: image picker */ }
                        ) {
                            Text("📷", fontSize = 13.sp)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Tap to change photo",
                        style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // ─── Form fields ───
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .neuRaised(cornerRadius = 18.dp, shadowOffset = 8.dp)
                    .padding(20.dp)
            ) {
                EditField(
                    label = "Full Name",
                    value = fullName,
                    onChange = { fullName = it },
                    hint = "Your full name"
                )
                Spacer(Modifier.height(16.dp))

                EditField(
                    label = "Email Address",
                    value = state.userEmail,
                    onChange = {},
                    hint = state.userEmail,
                    enabled = false
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Email cannot be changed. Contact support if needed.",
                    style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                )
                Spacer(Modifier.height(16.dp))

                EditField(
                    label = "Phone Number",
                    value = phone,
                    onChange = { phone = it },
                    hint = "+94 7X XXX XXXX",
                    keyboardType = KeyboardType.Phone
                )
            }

            Spacer(Modifier.height(20.dp))

            // ─── Language ───
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp)
                    .padding(16.dp)
            ) {
                Text("Language Preference", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
                Spacer(Modifier.height(12.dp))
                var selectedLang by remember { mutableStateOf("English") }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("English", "සිංහල", "தமிழ்").forEach { lang ->
                        val selected = selectedLang == lang
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .then(
                                    if (selected)
                                        Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                                    else
                                        Modifier.background(MikoColors.SurfaceVariant)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { selectedLang = lang }
                        ) {
                            Text(
                                lang,
                                style = MikoTypography.Caption.copy(
                                    color = if (selected) Color.White else MikoColors.TextSecondary,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ─── Notification preferences ───
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp)
                    .padding(16.dp)
            ) {
                Text("Notification Preferences", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
                Spacer(Modifier.height(12.dp))

                val toggles = remember {
                    mutableStateListOf(
                        Triple("Order Updates", "Track your orders in real time", true),
                        Triple("Deals & Promos", "Flash sales and promo codes", true),
                        Triple("AI Picks", "Personalised recommendations", true),
                        Triple("New Arrivals", "First to know about new products", false)
                    )
                }

                toggles.forEachIndexed { i, item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(item.first, style = MikoTypography.Subtitle.copy(
                                color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold
                            ))
                            Text(item.second, style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                        }
                        Switch(
                            checked = item.third,
                            onCheckedChange = { toggles[i] = item.copy(third = it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MikoColors.PrimaryStart,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = MikoColors.BorderMedium
                            )
                        )
                    }
                    if (i < toggles.size - 1) {
                        HorizontalDivider(color = MikoColors.Divider.copy(alpha = 0.5f))
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // ─── Save button ───
            when {
                saved -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MikoColors.SuccessSoft)
                            .border(1.dp, MikoColors.Success.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    ) {
                        Text(
                            "✓ Profile saved!",
                            style = MikoTypography.Subtitle.copy(
                                color = MikoColors.Success, fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
                isSaving -> {
                    Box(
                        Modifier.fillMaxWidth().height(56.dp),
                        Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MikoColors.PrimaryStart,
                            modifier = Modifier.size(30.dp),
                            strokeWidth = 3.dp
                        )
                    }
                }
                else -> {
                    MikoPrimaryButton(
                        text = "Save Changes",
                        trailingIcon = null,
                        enabled = fullName.isNotBlank(),
                        onClick = {
                            isSaving = true
                            // TODO: viewModel.updateProfile(fullName, phone)
                            isSaving = false
                            saved = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ─── Edit field ───
@Composable
private fun EditField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    hint: String,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Text(
        label,
        style = MikoTypography.Label.copy(
            color = MikoColors.TextSecondary,
            letterSpacing = 0.5.sp
        )
    )
    Spacer(Modifier.height(7.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) MikoColors.SurfaceVariant else MikoColors.Divider.copy(alpha = 0.4f))
            .border(1.dp, MikoColors.BorderLight, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onChange,
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = TextStyle(
                fontSize = 15.sp,
                color = if (enabled) MikoColors.TextPrimary else MikoColors.TextMuted,
                fontWeight = FontWeight.Medium
            ),
            cursorBrush = SolidColor(MikoColors.PrimaryStart),
            modifier = Modifier.weight(1f).padding(vertical = 15.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(hint, style = MikoTypography.Body.copy(color = MikoColors.TextMuted))
                }
                inner()
            }
        )
    }
}