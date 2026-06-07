package com.kittys.premium.features.orders

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.domain.model.formatThousands
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ─── Lottie imports (uncomment after adding the dependency — see notes below) ───
// import com.airbnb.lottie.compose.*

// ════════════════════════════════════════════════════════════════
//   MIKO — Order Success Screen
//   Shown after checkout. Has a slot for a Lottie success animation.
// ════════════════════════════════════════════════════════════════

@Composable
fun OrderSuccessScreen(
    orderId: String,
    totalLkr: Int,
    navController: NavController
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(40.dp))

        // ══════════════════════════════════════════════
        //   LOTTIE ANIMATION SLOT
        //   Drop your success animation here.
        // ══════════════════════════════════════════════
        OrderSuccessAnimation()

        Spacer(Modifier.height(28.dp))

        Text(
            "Order Placed! 🎉",
            style = MikoTypography.Display.copy(
                color = MikoColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Thank you for shopping with MIKO",
            style = MikoTypography.Body.copy(color = MikoColors.TextSecondary)
        )

        Spacer(Modifier.height(28.dp))

        // ─── Order summary card ───
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .neuRaised(cornerRadius = 18.dp, shadowOffset = 8.dp)
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Order Number", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                Text("#$orderId", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
            }
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Amount Paid", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                Text("LKR ${totalLkr.formatThousands()}", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
                ))
            }
        }

        Spacer(Modifier.height(16.dp))

        // ─── Escrow protection note ───
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MikoColors.SuccessSoft)
                .padding(16.dp)
        ) {
            Text("🔒", fontSize = 24.sp)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    "Payment held safely in escrow",
                    style = MikoTypography.Subtitle.copy(
                        color = MikoColors.Success, fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    "Your money is released to the seller only after you confirm delivery — or automatically after 7 days.",
                    style = MikoTypography.Caption.copy(color = MikoColors.TextSecondary, lineHeight = 17.sp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ─── What's next ───
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp)
                .padding(16.dp)
        ) {
            Text("What happens next?", style = MikoTypography.Subtitle.copy(
                color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
            ))
            Spacer(Modifier.height(12.dp))
            NextStep("1", "The seller prepares and ships your order")
            NextStep("2", "Track your delivery in real time")
            NextStep("3", "Confirm delivery to complete the order")
        }

        Spacer(Modifier.height(32.dp))

        // ─── Actions ───
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(MikoColors.GradientButton))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    navController.navigate(Screen.OrderDetail.createRoute(orderId)) {
                        popUpTo(Screen.Home.route)
                    }
                }
        ) {
            Text("Track My Order", style = MikoTypography.Button.copy(
                color = Color.White, fontWeight = FontWeight.Bold
            ))
        }

        Spacer(Modifier.height(10.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MikoColors.Surface)
                .border(1.5.dp, MikoColors.PrimaryStart, RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
        ) {
            Text("Continue Shopping", style = MikoTypography.Button.copy(
                color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
            ))
        }

        Spacer(Modifier.height(30.dp))
    }
}

// ════════════════════════════════════════════════════════════════
//   ★ LOTTIE ANIMATION SLOT ★
//   ----------------------------------------------------------------
//   STEP 1: Add the dependency in app/build.gradle.kts:
//       implementation("com.airbnb.android:lottie-compose:6.4.0")
//
//   STEP 2: Put your animation JSON at:
//       app/src/main/res/raw/order_success.json
//       (download from lottiefiles.com — search "order success" or "celebration")
//
//   STEP 3: Replace the body of this function with the LOTTIE VERSION
//           shown in the comment block below, and uncomment the
//           Lottie import at the top of the file.
// ════════════════════════════════════════════════════════════════
@Composable
private fun OrderSuccessAnimation() {

    /*  ░░░ LOTTIE VERSION — paste this in once the dependency is added ░░░

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(com.kittys.premium.R.raw.order_success)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,          // play once; use LottieConstants.IterateForever to loop
        speed = 1f
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(200.dp)
    )

    ░░░ end Lottie version ░░░  */

    // ─── FALLBACK animation (works now, no dependency needed) ───
    // Animated pop-in checkmark — remove this once Lottie is wired.
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "pop"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(200.dp)
            .scale(scale)
    ) {
        // outer soft ring
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(MikoColors.SuccessSoft)
        )
        // inner gradient circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(MikoColors.GradientButton))
        ) {
            Text("✓", color = Color.White, fontSize = 64.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun NextStep(number: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(MikoColors.GradientPrimary))
        ) {
            Text(number, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Text(text, style = MikoTypography.Body.copy(color = MikoColors.TextSecondary))
    }
}