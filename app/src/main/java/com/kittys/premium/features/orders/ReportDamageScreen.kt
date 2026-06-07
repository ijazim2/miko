package com.kittys.premium.features.orders

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kittys.premium.domain.model.DamageType
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Report Damage (MIKO-exclusive escrow feature)
//   Damage type + description + MANDATORY photos + refund/replacement
//   → opens an admin dispute (escrow stays held)
// ════════════════════════════════════════════════════════════════

@Composable
fun ReportDamageScreen(
    orderId: String,
    navController: NavController,
    viewModel: EscrowViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedType by remember { mutableStateOf<DamageType?>(null) }
    var description by remember { mutableStateOf("") }
    val photos = remember { mutableStateListOf<String>() }
    var wantRefund by remember { mutableStateOf(true) }   // true = refund, false = replacement
    var showSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.success) {
        if (uiState.success != null) showSuccess = true
    }

    val canSubmit = selectedType != null &&
            description.trim().length >= 10 &&
            photos.isNotEmpty() &&
            !uiState.isSubmitting

    Box(modifier = Modifier.fillMaxSize().background(MikoColors.Background)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ─── Top Bar ───
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(40.dp).neuRaised(cornerRadius = 20.dp, shadowOffset = 4.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { navController.popBackStack() }
                ) { Text("←", fontSize = 18.sp, color = MikoColors.TextPrimary) }
                Spacer(Modifier.width(12.dp))
                Text("Report a Problem", style = MikoTypography.Title.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // ─── Reassurance banner ───
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MikoColors.InfoSoft)
                        .padding(14.dp)
                ) {
                    Text("🛡", fontSize = 24.sp)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Your payment is still safely held in escrow. Report the issue and our team will review it within 24 hours.",
                        style = MikoTypography.Caption.copy(color = MikoColors.Info, lineHeight = 17.sp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                // ─── Damage type ───
                Text("What went wrong?", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
                Spacer(Modifier.height(4.dp))
                Text("Required", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                Spacer(Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DamageType.values().toList().chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { type ->
                                val selected = selectedType == type
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .then(
                                            if (selected)
                                                Modifier.background(MikoColors.PastelPink).border(1.5.dp, MikoColors.PrimaryStart, RoundedCornerShape(12.dp))
                                            else
                                                Modifier.background(MikoColors.SurfaceVariant).border(1.dp, MikoColors.BorderLight, RoundedCornerShape(12.dp))
                                        )
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { selectedType = type }
                                        .padding(12.dp)
                                ) {
                                    Text(type.emoji, fontSize = 16.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        type.displayName,
                                        style = MikoTypography.Caption.copy(
                                            color = if (selected) MikoColors.PrimaryEnd else MikoColors.TextSecondary,
                                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ─── Description ───
                Text("Describe the issue", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MikoColors.SurfaceVariant)
                        .border(1.dp, MikoColors.BorderLight, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    BasicTextField(
                        value = description,
                        onValueChange = { if (it.length <= 500) description = it },
                        textStyle = TextStyle(fontSize = 14.sp, color = MikoColors.TextPrimary),
                        cursorBrush = SolidColor(MikoColors.PrimaryStart),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { inner ->
                            if (description.isEmpty()) Text("Tell us exactly what's wrong (min 10 characters)…",
                                style = MikoTypography.Body.copy(color = MikoColors.TextMuted))
                            inner()
                        }
                    )
                }
                Text(
                    if (description.length < 10) "At least ${10 - description.length} more characters" else "${description.length}/500",
                    style = MikoTypography.Caption.copy(
                        color = if (description.length < 10) MikoColors.TextMuted else MikoColors.Success
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(24.dp))

                // ─── Photos (MANDATORY) ───
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Add photo proof", style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                    ), modifier = Modifier.weight(1f))
                    Text("Required *", style = MikoTypography.Caption.copy(
                        color = MikoColors.Error, fontWeight = FontWeight.SemiBold
                    ))
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Photos are essential for us to approve a refund or replacement",
                    style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    photos.forEach { uri ->
                        Box(
                            modifier = Modifier.size(76.dp).clip(RoundedCornerShape(12.dp)).background(MikoColors.PastelPink)
                        ) {
                            Box(Modifier.fillMaxSize(), Alignment.Center) { Text("🖼", fontSize = 26.sp) }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .align(Alignment.TopEnd).padding(3.dp).size(20.dp)
                                    .clip(CircleShape).background(MikoColors.Error)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { photos.remove(uri) }
                            ) { Text("×", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        }
                    }
                    if (photos.size < 5) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(76.dp).clip(RoundedCornerShape(12.dp))
                                .background(MikoColors.SurfaceVariant)
                                .border(1.5.dp,
                                    if (photos.isEmpty()) MikoColors.Error else MikoColors.PrimaryStart,
                                    RoundedCornerShape(12.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { photos.add("dmg_${System.currentTimeMillis()}") }
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📷", fontSize = 22.sp)
                                Text("Add", style = MikoTypography.Caption.copy(color = MikoColors.PrimaryStart, fontSize = 9.sp))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ─── Resolution choice ───
                Text("What would you like?", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ResolutionOption("💸", "Refund", "Money back to you", wantRefund, Modifier.weight(1f)) { wantRefund = true }
                    ResolutionOption("🔄", "Replacement", "Get a new one", !wantRefund, Modifier.weight(1f)) { wantRefund = false }
                }

                Spacer(Modifier.height(24.dp))

                // ─── Submit ───
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth().height(54.dp).clip(RoundedCornerShape(16.dp))
                        .then(
                            if (canSubmit) Modifier.background(Brush.linearGradient(MikoColors.GradientButton))
                            else Modifier.background(MikoColors.BorderMedium)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = canSubmit
                        ) {
                            selectedType?.let { type ->
                                viewModel.reportDamage(orderId, type, description, photos.toList(), wantRefund)
                            }
                        }
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
                    } else {
                        Text("Submit Report", style = MikoTypography.Button.copy(
                            color = Color.White, fontWeight = FontWeight.Bold
                        ))
                    }
                }
                if (!canSubmit && !uiState.isSubmitting) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        when {
                            selectedType == null -> "Please select what went wrong"
                            description.trim().length < 10 -> "Please describe the issue (10+ characters)"
                            photos.isEmpty() -> "Please add at least one photo"
                            else -> ""
                        },
                        style = MikoTypography.Caption.copy(color = MikoColors.Error),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(40.dp))
            }
        }

        // ─── Success overlay ───
        if (showSuccess) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().background(MikoColors.Overlay).clickable(enabled = false) {}
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(32.dp).clip(RoundedCornerShape(24.dp)).background(MikoColors.Surface).padding(28.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(80.dp).clip(CircleShape).background(MikoColors.InfoSoft)
                    ) { Text("📋", fontSize = 36.sp) }
                    Spacer(Modifier.height(18.dp))
                    Text("Report Submitted", style = MikoTypography.Headline.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                    ))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Our team will review your report within 24 hours. Your payment stays protected in escrow until it's resolved. We'll notify you of the outcome. 💜",
                        style = MikoTypography.Body.copy(color = MikoColors.TextSecondary, lineHeight = 20.sp),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(Modifier.height(24.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth().height(50.dp).clip(RoundedCornerShape(16.dp))
                            .background(Brush.linearGradient(MikoColors.GradientButton))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                viewModel.clearMessages()
                                showSuccess = false
                                navController.popBackStack()
                            }
                    ) {
                        Text("Done", style = MikoTypography.Button.copy(color = Color.White, fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

// ─── Resolution option card ───
@Composable
private fun ResolutionOption(
    emoji: String,
    title: String,
    subtitle: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .then(
                if (selected) Modifier.background(MikoColors.PastelPink).border(1.5.dp, MikoColors.PrimaryStart, RoundedCornerShape(14.dp))
                else Modifier.background(MikoColors.SurfaceVariant).border(1.dp, MikoColors.BorderLight, RoundedCornerShape(14.dp))
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(16.dp)
    ) {
        Text(emoji, fontSize = 26.sp)
        Spacer(Modifier.height(6.dp))
        Text(title, style = MikoTypography.Subtitle.copy(
            color = if (selected) MikoColors.PrimaryEnd else MikoColors.TextPrimary,
            fontWeight = FontWeight.Bold
        ))
        Text(subtitle, style = MikoTypography.Caption.copy(color = MikoColors.TextMuted, fontSize = 10.sp))
    }
}