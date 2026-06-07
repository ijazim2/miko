package com.kittys.premium.features.seller.onboarding

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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.core.ui.components.MikoPrimaryButton
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Seller Intro Screen (Onboarding Step 1)
//   Path: Profile → "Start Selling" → here
// ════════════════════════════════════════════════════════════════

@Composable
fun SellerIntroScreen(navController: NavController) {

    val benefits = listOf(
        BenefitItem("💰", "Earn from your closet", "Turn kids' outgrown clothes & unused toys into cash"),
        BenefitItem("🚀", "Reach 10,000+ parents", "Sri Lanka's largest kids marketplace"),
        BenefitItem("📦", "We handle payments", "Secure escrow protects you and buyers"),
        BenefitItem("📊", "Smart seller tools", "AI helps write descriptions & set prices"),
        BenefitItem("🆓", "Free to start", "No setup fees, just 10% only when you sell"),
        BenefitItem("✓", "Verified buyers", "All buyers are real, verified parents")
    )

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
                .padding(horizontal = 16.dp, vertical = 14.dp)
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
            Spacer(Modifier.weight(1f))
            Text(
                "Already a seller? Sign in",
                style = MikoTypography.Label.copy(
                    color = MikoColors.PrimaryStart,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { navController.navigate(Screen.SellerDashboard.route) }
            )
        }

        LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {

            // ─── HERO BANNER ───
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.linearGradient(MikoColors.GradientButton))
                        .padding(24.dp)
                ) {
                    Column {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(Color.White.copy(alpha = 0.22f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    "🔥 NEW",
                                    style = MikoTypography.Caption.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    "Free to start",
                                    style = MikoTypography.Caption.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Text(
                            "Sell on",
                            style = MikoTypography.Headline.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 22.sp
                            )
                        )
                        Text(
                            "MIKO",
                            style = MikoTypography.Display.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        )

                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Join 200+ Sri Lankan sellers earning extra income from kids products",
                            style = MikoTypography.Body.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 22.sp
                            )
                        )

                        Spacer(Modifier.height(20.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            HeroStat("LKR 18K", "avg monthly", Modifier.weight(1f))
                            HeroStat("4.8★", "seller rating", Modifier.weight(1f))
                            HeroStat("24h", "payout time", Modifier.weight(1f))
                        }
                    }
                }
            }

            // ─── BENEFITS ───
            item {
                Text(
                    "Why sell on MIKO?",
                    style = MikoTypography.Title.copy(
                        color = MikoColors.TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp)
                )
            }

            items(benefits) { benefit -> BenefitCard(benefit) }

            // ─── HOW IT WORKS ───
            item {
                Text(
                    "How it works",
                    style = MikoTypography.Title.copy(
                        color = MikoColors.TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp)
                )
            }

            item { StepCard("1", "Set up your store", "Add a name, logo, and what you sell", "📝") }
            item { StepCard("2", "Verify yourself", "Quick phone OTP to get started", "📱") }
            item { StepCard("3", "Add bank details", "For receiving your earnings", "🏦") }
            item { StepCard("4", "List your first product", "Photos, price, sizes, colors", "📸") }
            item { StepCard("5", "Start earning!", "Get paid 24h after delivery", "💸") }

            // ─── TESTIMONIAL ───
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MikoColors.PastelPink)
                        .padding(20.dp)
                ) {
                    Column {
                        Text("⭐⭐⭐⭐⭐", fontSize = 16.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "\"I earned LKR 45,000 in my first month selling kids clothes my daughter outgrew. The platform is amazing!\"",
                            style = MikoTypography.Body.copy(
                                color = MikoColors.TextPrimary,
                                lineHeight = 22.sp,
                                fontStyle = FontStyle.Italic
                            )
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(MikoColors.GradientPrimary))
                            ) {
                                Text("N", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    "Nayomi Silva",
                                    style = MikoTypography.Subtitle.copy(
                                        color = MikoColors.TextPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    "Little Treasures Store · Colombo",
                                    style = MikoTypography.Caption.copy(
                                        color = MikoColors.TextMuted
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // ─── BOTTOM CTA ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, MikoColors.Background, MikoColors.Background)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            MikoPrimaryButton(
                text = "Get Started",
                onClick = { navController.navigate(Screen.SellerStoreInfo.route) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ─── Hero Stat ───
@Composable
private fun HeroStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            value,
            style = MikoTypography.Subtitle.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            label,
            style = MikoTypography.Caption.copy(
                color = Color.White.copy(alpha = 0.7f)
            )
        )
    }
}

// ─── Benefit Card ───
data class BenefitItem(val emoji: String, val title: String, val subtitle: String)

@Composable
private fun BenefitCard(benefit: BenefitItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp)
            .padding(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MikoColors.PastelPink)
        ) {
            Text(benefit.emoji, fontSize = 22.sp)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                benefit.title,
                style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                benefit.subtitle,
                style = MikoTypography.Caption.copy(
                    color = MikoColors.TextSecondary,
                    lineHeight = 18.sp
                )
            )
        }
    }
}

// ─── Step Card ───
@Composable
private fun StepCard(number: String, title: String, subtitle: String, emoji: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(MikoColors.GradientPrimary))
        ) {
            Text(
                number,
                style = MikoTypography.Subtitle.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                subtitle,
                style = MikoTypography.Caption.copy(
                    color = MikoColors.TextSecondary
                )
            )
        }
        Text(emoji, fontSize = 28.sp)
    }
}