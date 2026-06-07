package com.kittys.premium.features.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.core.ui.theme.MikoTypography
import kotlinx.coroutines.delay
import androidx.compose.ui.res.painterResource
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kittys.premium.R
// ════════════════════════════════════════════════════════════════
//   MIKO SPLASH SCREEN
//   Clean brand identity → auto-navigates to Onboarding.
//   No emoji / no mascot (per MIKO brand spec).
// ════════════════════════════════════════════════════════════════

@Composable
fun MikoSplashScreen(navController: NavController) {

    // Gentle pulse on the logo
    val infiniteTransition = rememberInfiniteTransition(label = "splash_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Navigate to onboarding after 2.2s
    LaunchedEffect(Unit) {
        delay(2200)
        navController.navigate(Screen.Onboarding.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.welcome_lottie)
    )
    val lottieProgress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // 1 ▸ Background image
        Image(
            painter = painterResource(id = R.drawable.miko_splash_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // ─── Main centered content ───
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.miko_splash_logo),
                contentDescription = "MIKO",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(180.dp).height(64.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Image(
                painter = painterResource(id = R.drawable.miko_character),
                contentDescription = "MIKO character",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(260.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            LottieAnimation(
                composition = composition,
                progress = { lottieProgress },
                modifier = Modifier.width(140.dp).height(72.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Kids Fashion Marketplace",
                style = MikoTypography.Body.copy(
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
            )
        }

        // ─── Bottom signature — direct child of Box ✅ ───
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)   // ✅ Valid here — BoxScope
                .padding(bottom = 40.dp)
        ) {
            Text(
                text = "Made with care in Sri Lanka",
                style = MikoTypography.Caption.copy(
                    color = Color.White.copy(alpha = 0.75f)
                )
            )
        }
    }
}

