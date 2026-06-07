package com.kittys.premium.features.orders

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
import androidx.compose.ui.unit.*
import com.kittys.premium.domain.model.EscrowStatus
import com.kittys.premium.domain.model.EscrowTransaction
import com.kittys.premium.domain.model.formatThousands
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Escrow Status Card (reusable)
//   Shows held amount, status, countdown, and confirm/report actions.
//   Drop into Order Detail / Order History screens.
// ════════════════════════════════════════════════════════════════

@Composable
fun EscrowStatusCard(
    escrow: EscrowTransaction,
    onConfirmDelivery: () -> Unit = {},
    onReportDamage: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val (accent, soft, label, icon) = escrowVisuals(escrow.status)
    val daysLeft = escrow.daysUntilAutoRelease()
    val canAct = escrow.status == EscrowStatus.AWAITING_CONFIRMATION ||
            escrow.status == EscrowStatus.DELIVERED

    Column(
        modifier = modifier
            .fillMaxWidth()
            .neuRaised(cornerRadius = 18.dp, shadowOffset = 8.dp)
            .padding(18.dp)
    ) {
        // ─── Header: status pill + amount ───
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(soft)
            ) {
                Text(icon, fontSize = 20.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "Escrow Protected",
                    style = MikoTypography.Caption.copy(color = MikoColors.TextMuted, letterSpacing = 0.5.sp)
                )
                Text(
                    label,
                    style = MikoTypography.Subtitle.copy(color = accent, fontWeight = FontWeight.Bold)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "LKR ${escrow.amountLkr.formatThousands()}",
                    style = MikoTypography.Subtitle.copy(color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold)
                )
                Text(
                    "held safely",
                    style = MikoTypography.Caption.copy(color = MikoColors.TextMuted, fontSize = 10.sp)
                )
            }
        }

        // ─── Progress timeline ───
        Spacer(Modifier.height(16.dp))
        EscrowTimeline(escrow.status)

        // ─── Countdown (only while awaiting confirmation) ───
        if (escrow.status == EscrowStatus.AWAITING_CONFIRMATION && escrow.autoReleaseAt != null) {
            Spacer(Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MikoColors.PastelButter)
                    .padding(12.dp)
            ) {
                Text("⏳", fontSize = 18.sp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        if (daysLeft > 0)
                            "Auto-releases in $daysLeft ${if (daysLeft == 1) "day" else "days"}"
                        else
                            "Releasing soon…",
                        style = MikoTypography.Caption.copy(
                            color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        "Confirm now to release payment to the seller immediately",
                        style = MikoTypography.Caption.copy(color = MikoColors.TextSecondary, fontSize = 10.sp)
                    )
                }
            }
        }

        // ─── Resolved states info ───
        when (escrow.status) {
            EscrowStatus.RELEASED_TO_VENDOR, EscrowStatus.AUTO_RELEASED -> {
                Spacer(Modifier.height(12.dp))
                ResolvedNote("✓", "Payment released to the seller. Thank you!", MikoColors.Success, MikoColors.SuccessSoft)
            }
            EscrowStatus.REFUNDED_TO_CUSTOMER -> {
                Spacer(Modifier.height(12.dp))
                ResolvedNote("↩", "You were refunded LKR ${escrow.amountLkr.formatThousands()}.", MikoColors.Info, MikoColors.InfoSoft)
            }
            EscrowStatus.DISPUTED, EscrowStatus.UNDER_REVIEW -> {
                Spacer(Modifier.height(12.dp))
                ResolvedNote("⚠", "Your report is under review by MIKO admin.", MikoColors.Warning, MikoColors.WarningSoft)
            }
            else -> {}
        }

        // ─── Action buttons ───
        if (canAct) {
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Report damage (outline)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MikoColors.Surface)
                        .border(1.5.dp, MikoColors.Error, RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onReportDamage() }
                ) {
                    Text("Report Issue", style = MikoTypography.Label.copy(
                        color = MikoColors.Error, fontWeight = FontWeight.SemiBold
                    ))
                }
                // Confirm (filled)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1.4f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(MikoColors.GradientButton))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onConfirmDelivery() }
                ) {
                    Text("Confirm Delivery", style = MikoTypography.Label.copy(
                        color = Color.White, fontWeight = FontWeight.Bold
                    ))
                }
            }
        }
    }
}

// ─── Status → visuals (accent color, soft bg, label, icon) ───
private data class EscrowVisuals(val accent: Color, val soft: Color, val label: String, val icon: String)

@Composable
private fun escrowVisuals(status: EscrowStatus): EscrowVisuals = when (status) {
    EscrowStatus.HELD ->
        EscrowVisuals(MikoColors.PrimaryStart, MikoColors.PastelPink, "Payment Held", "🔒")
    EscrowStatus.DELIVERED ->
        EscrowVisuals(MikoColors.Info, MikoColors.InfoSoft, "Delivered", "📦")
    EscrowStatus.AWAITING_CONFIRMATION ->
        EscrowVisuals(MikoColors.Warning, MikoColors.WarningSoft, "Awaiting Your Confirmation", "⏳")
    EscrowStatus.CONFIRMED ->
        EscrowVisuals(MikoColors.Success, MikoColors.SuccessSoft, "Confirmed", "✓")
    EscrowStatus.DISPUTED, EscrowStatus.UNDER_REVIEW ->
        EscrowVisuals(MikoColors.Warning, MikoColors.WarningSoft, "Under Review", "⚠")
    EscrowStatus.RELEASED_TO_VENDOR, EscrowStatus.AUTO_RELEASED ->
        EscrowVisuals(MikoColors.Success, MikoColors.SuccessSoft, "Completed", "✓")
    EscrowStatus.REFUNDED_TO_CUSTOMER ->
        EscrowVisuals(MikoColors.Info, MikoColors.InfoSoft, "Refunded", "↩")
}

// ─── Mini timeline showing escrow progress ───
@Composable
private fun EscrowTimeline(status: EscrowStatus) {
    // Map status to a 0..3 stage for the simple 4-dot timeline
    val stage = when (status) {
        EscrowStatus.HELD -> 0
        EscrowStatus.DELIVERED, EscrowStatus.AWAITING_CONFIRMATION -> 1
        EscrowStatus.CONFIRMED, EscrowStatus.DISPUTED, EscrowStatus.UNDER_REVIEW -> 2
        EscrowStatus.RELEASED_TO_VENDOR, EscrowStatus.AUTO_RELEASED, EscrowStatus.REFUNDED_TO_CUSTOMER -> 3
    }
    val labels = listOf("Paid", "Delivered", "Confirmed", "Released")

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        labels.forEachIndexed { i, label ->
            val reached = i <= stage
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .then(
                            if (reached) Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                            else Modifier.background(MikoColors.BorderLight)
                        )
                ) {
                    if (reached) Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    label,
                    style = MikoTypography.Caption.copy(
                        color = if (reached) MikoColors.PrimaryEnd else MikoColors.TextMuted,
                        fontWeight = if (reached) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 9.sp
                    )
                )
            }
            if (i < labels.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(if (i < stage) MikoColors.PrimaryStart else MikoColors.BorderLight)
                )
            }
        }
    }
}

@Composable
private fun ResolvedNote(icon: String, text: String, color: Color, soft: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(soft)
            .padding(12.dp)
    ) {
        Text(icon, fontSize = 16.sp, color = color)
        Spacer(Modifier.width(10.dp))
        Text(
            text,
            style = MikoTypography.Caption.copy(color = color, fontWeight = FontWeight.SemiBold)
        )
    }
}