package com.kittys.premium.features.admin.fraud

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
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Fraud Detection Screen
//   Admin tool: review flagged transactions, users, vendors
// ════════════════════════════════════════════════════════════════

data class FraudCase(
    val id: String,
    val severity: String,    // "high" | "medium" | "low"
    val type: String,        // "card" | "account" | "review" | "return-abuse"
    val title: String,
    val description: String,
    val flaggedBy: String,
    val timestamp: String,
    val riskScore: Int,      // 0-100
    val signals: List<String>
)

@Composable
fun FraudDetectionScreen(navController: NavController) {
    var selectedSeverity by remember { mutableStateOf("All") }
    val severities = listOf("All", "High", "Medium", "Low")

    val cases = remember {
        mutableStateListOf(
            FraudCase(
                "f1", "high", "card",
                "Multiple Failed Card Payments",
                "User attempted 7 card payments with 5 different cards in 12 minutes.",
                "AI System", "8 min ago", 94,
                listOf(
                    "5 unique card numbers",
                    "Different billing addresses",
                    "All transactions from same IP",
                    "VPN/proxy detected"
                )
            ),
            FraudCase(
                "f2", "high", "review",
                "Suspicious Review Activity",
                "Vendor account posted 23 five-star reviews from newly created accounts within 1 hour.",
                "AI System", "32 min ago", 89,
                listOf(
                    "All accounts created today",
                    "Similar review wording",
                    "Same device fingerprint",
                    "No purchase history"
                )
            ),
            FraudCase(
                "f3", "medium", "return-abuse",
                "Excessive Return Rate",
                "Customer has returned 78% of orders in last 30 days (industry avg: 12%).",
                "AI System", "2h ago", 71,
                listOf(
                    "11 returns in 30 days",
                    "Items returned worn",
                    "Negative reviews after refund"
                )
            ),
            FraudCase(
                "f4", "low", "account",
                "Stale Vendor Account",
                "Vendor has been inactive for 90+ days with unfulfilled orders.",
                "Vendor Report", "1d ago", 42,
                listOf("No login in 92 days", "3 pending orders unshipped")
            )
        )
    }

    val filteredCases = if (selectedSeverity == "All") cases
    else cases.filter { it.severity.equals(selectedSeverity, true) }

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
                "Fraud Detection 🛡",
                style = MikoTypography.Title.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // ─── Stats bar ───
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            FraudStat("Active", "${cases.size}", MikoColors.Error, Modifier.weight(1f))
            Spacer(Modifier.width(10.dp))
            FraudStat("High", "${cases.count { it.severity == "high" }}", MikoColors.Error, Modifier.weight(1f))
            Spacer(Modifier.width(10.dp))
            FraudStat("Medium", "${cases.count { it.severity == "medium" }}", MikoColors.Warning, Modifier.weight(1f))
            Spacer(Modifier.width(10.dp))
            FraudStat("Low", "${cases.count { it.severity == "low" }}", MikoColors.TextMuted, Modifier.weight(1f))
        }

        // ─── Severity filter ───
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            items(severities) { sev ->
                SeverityChip(sev, selectedSeverity == sev) { selectedSeverity = sev }
            }
        }

        // ─── Cases list ───
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredCases, key = { it.id }) { case ->
                FraudCaseCard(
                    case = case,
                    onResolve = { cases.removeAll { it.id == case.id } },
                    onBan = { cases.removeAll { it.id == case.id } }
                )
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun SeverityChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .then(
                if (selected) Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                else Modifier.background(MikoColors.SurfaceVariant)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            label,
            style = MikoTypography.Caption.copy(
                color = if (selected) Color.White else MikoColors.TextSecondary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        )
    }
}

@Composable
private fun FraudStat(label: String, value: String, color: Color, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.neuRaised(cornerRadius = 14.dp, shadowOffset = 5.dp).padding(12.dp)
    ) {
        Text(value, style = MikoTypography.Headline.copy(
            color = color, fontWeight = FontWeight.Bold, fontSize = 20.sp
        ))
        Text(label, style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
    }
}

@Composable
private fun FraudCaseCard(
    case: FraudCase,
    onResolve: () -> Unit,
    onBan: () -> Unit
) {
    val severityColor = when (case.severity) {
        "high" -> MikoColors.Error
        "medium" -> MikoColors.Warning
        else -> MikoColors.TextMuted
    }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(severityColor.copy(alpha = 0.05f))
            .border(1.dp, severityColor.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { expanded = !expanded }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(54.dp).clip(CircleShape)
                    .background(severityColor.copy(alpha = 0.15f))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${case.riskScore}", style = MikoTypography.Subtitle.copy(
                        color = severityColor, fontWeight = FontWeight.Bold, fontSize = 18.sp
                    ))
                    Text("risk", style = MikoTypography.Caption.copy(color = severityColor, fontSize = 8.sp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(case.severity.uppercase(), style = MikoTypography.Caption.copy(
                        color = severityColor, fontWeight = FontWeight.Bold, fontSize = 10.sp
                    ))
                    Spacer(Modifier.width(8.dp))
                    Text("• ${case.type}", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                }
                Spacer(Modifier.height(2.dp))
                Text(case.title, style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold
                ))
                Spacer(Modifier.height(3.dp))
                Text("${case.flaggedBy} • ${case.timestamp}",
                    style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(case.description, style = MikoTypography.Caption.copy(
            color = MikoColors.TextSecondary, lineHeight = 19.sp
        ))

        if (expanded) {
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = severityColor.copy(alpha = 0.2f))
            Spacer(Modifier.height(10.dp))
            Text("Risk Signals", style = MikoTypography.Label.copy(
                color = severityColor, fontWeight = FontWeight.SemiBold
            ))
            Spacer(Modifier.height(6.dp))
            case.signals.forEach { signal ->
                Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 3.dp)) {
                    Text("• ", color = severityColor)
                    Text(signal, style = MikoTypography.Caption.copy(
                        color = MikoColors.TextSecondary, lineHeight = 18.sp
                    ))
                }
            }

            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f).height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MikoColors.SuccessSoft)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onResolve() }
                ) {
                    Text("✓ Mark Safe", style = MikoTypography.Label.copy(
                        color = MikoColors.Success, fontWeight = FontWeight.SemiBold
                    ))
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f).height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MikoColors.Error)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onBan() }
                ) {
                    Text("✗ Ban Account", style = MikoTypography.Label.copy(
                        color = Color.White, fontWeight = FontWeight.SemiBold
                    ))
                }
            }
        } else {
            Spacer(Modifier.height(8.dp))
            Text("Tap to view details →", style = MikoTypography.Caption.copy(
                color = severityColor.copy(alpha = 0.85f), fontWeight = FontWeight.Medium
            ))
        }
    }
}