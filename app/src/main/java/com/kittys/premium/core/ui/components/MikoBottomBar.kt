package com.kittys.premium.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Bottom Navigation
//     • Floating neumorphic pill (raised, lavender shadow)
//     • 5 tabs, consistent line icons
//     • AI button in CENTER, gradient #6C0853 → #FF6BCE
// ════════════════════════════════════════════════════════════════

enum class MikoTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val isAi: Boolean = false
) {
    HOME    ("home",        "Home",    Icons.Outlined.Home),
    SEARCH  ("search",      "Explore", Icons.Outlined.Search),
    AI      ("ai_stylist",  "AI",      Icons.Outlined.Star, isAi = true),
    CART    ("cart",        "Cart",    Icons.Outlined.ShoppingCart),
    PROFILE ("profile",     "Profile", Icons.Outlined.Person)
}

private val CUSTOMER_ROUTES_WITH_BOTTOM_BAR = setOf(
    "home", "search", "ai_stylist", "cart", "profile"
)

@Composable
fun shouldShowBottomBar(navController: NavController): Boolean {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route ?: return false
    val base = route.substringBefore("/").substringBefore("?")
    return base in CUSTOMER_ROUTES_WITH_BOTTOM_BAR
}

@Composable
fun MikoBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route?.substringBefore("/")?.substringBefore("?")

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // ─── Floating pill ───
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .neuRaised(
                    cornerRadius = 34.dp,
                    shadowOffset = 12.dp,
                    surfaceColor = MikoColors.Surface
                )
                .padding(horizontal = 8.dp)
        ) {
            MikoTab.values().forEach { tab ->
                if (tab.isAi) {
                    Spacer(modifier = Modifier.size(64.dp))
                } else {
                    BottomTab(
                        tab = tab,
                        selected = currentRoute == tab.route,
                        onClick = { navigateToTab(navController, tab) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ─── AI Center Button (elevated) ───
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-4).dp)
        ) {
            AiCenterButton(
                selected = currentRoute == MikoTab.AI.route,
                onClick = { navigateToTab(navController, MikoTab.AI) }
            )
        }
    }
}

@Composable
private fun BottomTab(
    tab: MikoTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint = if (selected) MikoColors.PrimaryEnd else MikoColors.TextMuted

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = tab.label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = tab.label,
            style = MikoTypography.Caption.copy(
                color = tint,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 11.sp
            )
        )
        if (selected) {
            Spacer(Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(MikoColors.GradientPrimary))
            )
        }
    }
}

@Composable
private fun AiCenterButton(
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.size(64.dp)) {

        // Soft glow halo behind
        Box(
            modifier = Modifier
                .matchParentSize()
                .neuGlow(
                    color = MikoColors.AccentSoft,
                    cornerRadius = 32.dp,
                    alpha = if (selected) 0.85f else 0.55f
                )
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(MikoColors.GradientAi))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClick() }
        ) {
            Text(
                text = "AI",
                style = MikoTypography.Button.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}

private fun navigateToTab(navController: NavController, tab: MikoTab) {
    if (navController.currentDestination?.route == tab.route) return

    navController.navigate(tab.route) {
        popUpTo(MikoTab.HOME.route) {
            saveState = true
            inclusive = false
        }
        launchSingleTop = true
        restoreState = true
    }
}