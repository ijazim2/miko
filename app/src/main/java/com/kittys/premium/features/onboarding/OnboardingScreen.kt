package com.kittys.premium.features.onboarding

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.core.ui.components.CharacterPlaceholder
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography
import kotlinx.coroutines.launch
import com.kittys.premium.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Text
// ════════════════════════════════════════════════════════════════
//   MIKO — Onboarding (3 pages)
//   Each page has its own BACKGROUND + CHARACTER image slot.
//   Exact spec text baked in.
// ════════════════════════════════════════════════════════════════

// ─── Page model. Add your drawable resource ids in the slots below. ───
private data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val backgroundRes: Int,
    val accent: Color
)

private val onboardingPages = listOf(
    OnboardingPage(
        title = "Discover & Shop",
        subtitle = "Explore lovely collections made for your little ones",
        backgroundRes = R.drawable.onboard_bg_1,
        accent = MikoColors.PrimaryStart
    ),
    OnboardingPage(
        title = "AI Stylist",
        subtitle = "We know your child's size, style, and the Sri Lankan weather.",
        backgroundRes = R.drawable.onboard_bg_2,
        accent = MikoColors.AccentBright
    ),
    OnboardingPage(
        title = "Fast & Safe Delivery",
        subtitle = "Island-wide delivery with real-time tracking.",
        backgroundRes = R.drawable.onboard_bg_3,
        accent = MikoColors.PrimaryEnd
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {

    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == onboardingPages.size - 1

    fun finishOnboarding() {
        navController.navigate(Screen.Login.route) {
            popUpTo(Screen.Onboarding.route) { inclusive = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MikoColors.Background)) {

        // ─── Pager ───
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            OnboardingPageContent(page = onboardingPages[pageIndex])
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MikoColors.Background.copy(alpha = 0.20f),
                            MikoColors.Background.copy(alpha = 0.60f),
                            MikoColors.Background
                        )
                    )
                )
        )

        // ─── Skip button (top-right, hidden on last page) ───
        if (!isLastPage) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(MikoColors.Surface.copy(alpha = 0.85f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { finishOnboarding() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    "Skip",
                    style = MikoTypography.Caption.copy(
                        color = MikoColors.TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        // ─── Bottom controls: dots + button ───
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            // Page dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                onboardingPages.indices.forEach { i ->
                    val selected = pagerState.currentPage == i
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (selected) 24.dp else 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .then(
                                if (selected)
                                    Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                                else
                                    Modifier.background(MikoColors.BorderMedium)
                            )
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // Next / Get Started button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(MikoColors.GradientButton))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (isLastPage) {
                            finishOnboarding()
                        } else {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        }
                    }
            ) {
                Text(
                    if (isLastPage) "Get Started" else "Next",
                    style = MikoTypography.Button.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

// ─── Single page content ───
@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Box(modifier = Modifier.fillMaxSize()) {

        // ══════════════════════════════════════════════
        //   BACKGROUND IMAGE SLOT (per page)
        //   When you add PNGs, replace this Box with:
        //       Image(
        //           painter = painterResource(page.backgroundRes),
        //           contentDescription = null,
        //           contentScale = ContentScale.Crop,
        //           modifier = Modifier.fillMaxSize()
        //       )
        //   (add: )
        // ══════════════════════════════════════════════
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            page.accent.copy(alpha = 0.14f),
                            MikoColors.Background
                        )
                    )
                )
        )

        // ─── Foreground content ───
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(bottom = 140.dp) // leave room for bottom controls
        ) {

            // ══════════════════════════════════════════
            //   CHARACTER IMAGE SLOT (per page)
            //   When you add PNGs, replace CharacterPlaceholder with:
            //       Image(
            //           painter = painterResource(page.characterRes),
            //           contentDescription = page.title,
            //           modifier = Modifier.size(280.dp)
            //       )
            // ══════════════════════════════════════════
            //CharacterPlaceholder(size = 260.dp)

            //Spacer(Modifier.height(40.dp))

            Text(
                page.title,
                style = MikoTypography.Display.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                page.subtitle,
                style = MikoTypography.Body.copy(
                    color = MikoColors.TextSecondary,
                    lineHeight = 24.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}