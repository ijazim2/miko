package com.kittys.premium.features.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
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
import androidx.navigation.NavController
import com.kittys.premium.core.ui.components.MikoPrimaryButton
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography
import com.kittys.premium.domain.model.DisputeVerdict
// ════════════════════════════════════════════════════════════════
//   MIKO — Admin Escrow Dispute Resolution
//   Admin reviews damage report → decides verdict
// ════════════════════════════════════════════════════════════════

@Composable
fun EscrowDisputeScreen(
    reportId: String,
    navController: NavController
) {
    var adminNotes by remember { mutableStateOf("") }
    var selectedVerdict by remember { mutableStateOf<DisputeVerdict?>(null) }

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
                "Dispute Review",
                style = MikoTypography.Title.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ─── Order Summary ───
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp).padding(16.dp)
                ) {
                    Text("Order Details", style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                    ))
                    Spacer(Modifier.height(12.dp))
                    InfoRow("Order ID", "#ORD-12345")
                    InfoRow("Amount in Escrow", "LKR 5,400")
                    InfoRow("Customer", "Amali Perera")
                    InfoRow("Vendor", "Little Stars Fashion")
                    InfoRow("Reported", "2 hours ago")
                }
            }

            // ─── Damage Type ───
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MikoColors.ErrorSoft)
                        .padding(14.dp)
                ) {
                    Text("💔", fontSize = 24.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Damage Type", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                        Text("Physical Damage", style = MikoTypography.Subtitle.copy(
                            color = MikoColors.Error, fontWeight = FontWeight.Bold
                        ))
                    }
                }
            }

            // ─── Customer Description ───
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp).padding(16.dp)
                ) {
                    Text("Customer's Report", style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                    ))
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "The dress arrived with a torn sleeve on the left side. The packaging was damaged when I received it. The seams are coming apart and the fabric has a small hole near the buttons.",
                        style = MikoTypography.Body.copy(
                            color = MikoColors.TextSecondary, lineHeight = 22.sp
                        )
                    )
                }
            }

            // ─── Photo Evidence ───
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp).padding(16.dp)
                ) {
                    Text("Photo Evidence (3 photos)", style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                    ))
                    Spacer(Modifier.height(12.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(120.dp)
                    ) {
                        items(3) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MikoColors.PastelPink)
                            ) {
                                Text("📷", fontSize = 28.sp)
                            }
                        }
                    }
                }
            }

            // ─── Customer Requested ───
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MikoColors.SuccessSoft)
                        .padding(14.dp)
                ) {
                    Text("💸", fontSize = 22.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Customer wants", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                        Text("Full Refund", style = MikoTypography.Subtitle.copy(
                            color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                        ))
                    }
                }
            }

            // ─── Verdict Selection ───
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp).padding(16.dp)
                ) {
                    Text("Your Verdict *", style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                    ))
                    Spacer(Modifier.height(12.dp))

                    val options = listOf(
                        Quad(DisputeVerdict.APPROVED_REFUND, "💸", "Approve Full Refund", MikoColors.Success),
                        Quad(DisputeVerdict.APPROVED_REPLACEMENT, "🔄", "Approve Replacement", MikoColors.Info),
                        Quad(DisputeVerdict.PARTIAL_REFUND, "⚖", "Partial Refund", MikoColors.Warning),
                        Quad(DisputeVerdict.REJECTED, "❌", "Reject (Pay Vendor)", MikoColors.Error)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        options.forEach { (verdict, emoji, label, color) ->
                            VerdictRow(
                                emoji = emoji,
                                label = label,
                                tint = color,
                                selected = selectedVerdict == verdict,
                                onClick = { selectedVerdict = verdict }
                            )
                        }
                    }
                }
            }

            // ─── Admin Notes ───
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp).padding(16.dp)
                ) {
                    Text("Internal Notes", style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                    ))
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MikoColors.SurfaceVariant)
                            .border(1.dp, MikoColors.BorderLight, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        BasicTextField(
                            value = adminNotes,
                            onValueChange = { adminNotes = it },
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                color = MikoColors.TextPrimary
                            ),
                            cursorBrush = SolidColor(MikoColors.PrimaryStart),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { inner ->
                                if (adminNotes.isEmpty()) {
                                    Text("Reason for verdict… (min 10 characters)",
                                        style = MikoTypography.Body.copy(color = MikoColors.TextMuted))
                                }
                                inner()
                            }
                        )
                    }
                }
            }

            // ─── Submit ───
            item {
                Spacer(Modifier.height(8.dp))
                MikoPrimaryButton(
                    text = "Submit Verdict",
                    trailingIcon = null,
                    enabled = selectedVerdict != null && adminNotes.length >= 10,
                    onClick = {
                        // TODO: viewModel.resolveDispute(reportId, selectedVerdict, adminNotes)
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

// Helper 4-tuple
private data class Quad(
    val verdict: DisputeVerdict,
    val emoji: String,
    val label: String,
    val color: Color
)

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text(label, style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
        Text(value, style = MikoTypography.Body.copy(
            color = MikoColors.TextPrimary, fontWeight = FontWeight.Medium
        ))
    }
}

@Composable
private fun VerdictRow(
    emoji: String,
    label: String,
    tint: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (selected)
                    Modifier
                        .background(tint.copy(alpha = 0.12f))
                        .border(1.5.dp, tint, RoundedCornerShape(12.dp))
                else
                    Modifier.background(MikoColors.SurfaceVariant)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(14.dp)
    ) {
        Text(emoji, fontSize = 22.sp)
        Spacer(Modifier.width(12.dp))
        Text(
            label,
            style = MikoTypography.Body.copy(
                color = if (selected) tint else MikoColors.TextPrimary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        )
        Spacer(Modifier.weight(1f))
        if (selected) Text("✓", color = tint, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}