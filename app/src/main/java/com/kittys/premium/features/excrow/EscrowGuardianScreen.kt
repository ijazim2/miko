package com.kittys.premium.features.escrow

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kittys.premium.core.ui.mascot.MikoHost
import com.kittys.premium.core.ui.neu.NeuButton
import com.kittys.premium.core.ui.neu.neuRaised
import com.kittys.premium.core.ui.system.MikoState
import com.kittys.premium.core.ui.system.MikoViewModel
import com.kittys.premium.core.ui.system.NeuElevation
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   ESCROW GUARDIAN SCREEN — Miko protects the payment.
//   Teach the feeling, not the word "escrow".
// ════════════════════════════════════════════════════════════════

enum class EscrowPhase { Protected, Delivered, Released, Disputed }

@Composable
fun EscrowGuardianScreen(
    amountLkr: Int,
    phase: EscrowPhase,
    daysUntilAutoRelease: Int,
    mikoViewModel: MikoViewModel,
    onConfirmReceipt: () -> Unit,
    onReportProblem: () -> Unit
) {
    // Drive the shared mascot to guardian/celebration based on phase.
    LaunchedEffect(phase) {
        when (phase) {
            EscrowPhase.Released -> mikoViewModel.onDeliveryConfirmed()
            else -> mikoViewModel.onEnterEscrow()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.NeuBackground)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        // Hero: Miko + pulsing shield
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(180.dp)) {
            val infinite = rememberInfiniteTransition(label = "shield")
            val pulse by infinite.animateFloat(
                initialValue = 1f, targetValue = 1.12f,
                animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
                label = "pulse"
            )
            Box(
                Modifier
                    .size(160.dp)
                    .scale(pulse)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                MikoColors.Success.copy(alpha = 0.20f),
                                MikoColors.Success.copy(alpha = 0.02f)
                            )
                        )
                    )
            )
            MikoHost(
                viewModel = mikoViewModel,
                size = 120.dp,
                stateOverride = if (phase == EscrowPhase.Released) MikoState.Celebration
                else MikoState.EscrowGuardian
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            when (phase) {
                EscrowPhase.Protected -> "Your payment is safe with me"
                EscrowPhase.Delivered -> "Delivered! Confirm to release"
                EscrowPhase.Released -> "Payment sent to the seller 🎉"
                EscrowPhase.Disputed -> "I'm holding your payment safe"
            },
            style = MikoTypography.headlineSmall.copy(
                color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(6.dp))
        Text(
            when (phase) {
                EscrowPhase.Protected -> "I'll keep it until you say your order arrived. Auto-releases in $daysUntilAutoRelease days."
                EscrowPhase.Delivered -> "Tap below once you've checked everything is perfect."
                EscrowPhase.Released -> "Thanks for confirming. +50 loyalty points earned."
                EscrowPhase.Disputed -> "Our team is reviewing. Your money stays protected meanwhile."
            },
            style = MikoTypography.bodyMedium.copy(color = MikoColors.TextSecondary),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(Modifier.height(24.dp))

        // Amount held card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .neuRaised(elevation = NeuElevation.Level2, cornerRadius = 20.dp)
                .padding(20.dp)
        ) {
            Text("Protected amount", style = MikoTypography.labelSmall.copy(color = MikoColors.TextMuted))
            Spacer(Modifier.height(4.dp))
            Text(
                "LKR $amountLkr",
                style = MikoTypography.headlineMedium.copy(
                    color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(Modifier.height(20.dp))

        // Trust timeline: Paid -> Protected -> Released
        TrustTimeline(phase)

        Spacer(Modifier.weight(1f))

        // Actions
        if (phase == EscrowPhase.Delivered || phase == EscrowPhase.Protected) {
            NeuButton(
                text = "Confirm Receipt & Release Payment",
                onClick = onConfirmReceipt,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Something wrong? Report a problem",
                style = MikoTypography.bodySmall.copy(
                    color = MikoColors.Error, fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onReportProblem() }
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun TrustTimeline(phase: EscrowPhase) {
    val activeIndex = when (phase) {
        EscrowPhase.Protected, EscrowPhase.Disputed -> 1
        EscrowPhase.Delivered -> 1
        EscrowPhase.Released -> 2
    }
    val steps = listOf("Paid", "Protected", "Released")
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        steps.forEachIndexed { i, label ->
            val done = i <= activeIndex
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            if (done) Brush.linearGradient(MikoColors.GradientPrimary)
                            else Brush.linearGradient(listOf(MikoColors.BorderLight, MikoColors.BorderLight))
                        )
                ) {
                    if (done) Text("✓", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    label,
                    style = MikoTypography.labelSmall.copy(
                        color = if (done) MikoColors.PrimaryEnd else MikoColors.TextMuted,
                        fontSize = 10.sp
                    )
                )
            }
            if (i < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(if (i < activeIndex) MikoColors.PrimaryStart else MikoColors.BorderLight)
                )
            }
        }
    }
}
