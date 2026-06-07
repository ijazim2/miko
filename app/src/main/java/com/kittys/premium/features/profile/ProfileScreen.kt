package com.kittys.premium.features.profile

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Profile Screen (with "Start Selling" hero card)
// ════════════════════════════════════════════════════════════════

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Navigate to login after logout
    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {

        // ─── 1. PROFILE HEADER ───
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(MikoColors.GradientButton))
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f))
                                .border(3.dp, Color.White, CircleShape)
                        ) {
                            Text(
                                state.userInitial,
                                color = Color.White,
                                style = MikoTypography.Headline.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                state.userName,
                                style = MikoTypography.Title.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                state.userEmail,
                                style = MikoTypography.Caption.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            )
                            if (state.loyaltyPoints > 0) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(top = 6.dp)
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("⭐", fontSize = 12.sp)
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        "${state.loyaltyPoints} points",
                                        style = MikoTypography.Caption.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                        // Edit profile
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { navController.navigate(Screen.EditProfile.route) }
                        ) {
                            Text("✏", fontSize = 18.sp)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Quick stats
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("${state.ordersCount}", "Orders", Modifier.weight(1f))
                        StatCard("${state.wishlistCount}", "Wishlist", Modifier.weight(1f))
                        StatCard("${state.reviewsCount}", "Reviews", Modifier.weight(1f))
                    }
                }
            }
        }

        // ─── 2. START SELLING / SELLER DASHBOARD ───
        if (!state.isSeller) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(MikoColors.GradientPrimary))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { navController.navigate(Screen.SellerIntro.route) }
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        ) {
                            Text("💰", fontSize = 32.sp)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Start Selling",
                                    style = MikoTypography.Title.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(Color.White)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "NEW",
                                        style = MikoTypography.Caption.copy(
                                            color = MikoColors.PrimaryEnd,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "Turn your kids' items into cash",
                                style = MikoTypography.Caption.copy(
                                    color = Color.White.copy(alpha = 0.9f),
                                    lineHeight = 18.sp
                                )
                            )
                        }
                        Text("→", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(MikoColors.GradientPrimary))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { navController.navigate(Screen.SellerDashboard.route) }
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        ) {
                            Text("🏪", fontSize = 32.sp)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                state.storeName.ifBlank { "My Store" },
                                style = MikoTypography.Subtitle.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                "Manage your store",
                                style = MikoTypography.Caption.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            )
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(MikoColors.Success)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "Active",
                                    style = MikoTypography.Caption.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                        Text("→", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ─── ADMIN ENTRY (admins only) ───
        if (state.isAdmin) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MikoColors.PastelLavender)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { navController.navigate(Screen.AdminPanel.route) }
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🛡", fontSize = 24.sp)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Admin Panel",
                            style = MikoTypography.Subtitle.copy(
                                color = MikoColors.AccentBright,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Text("→", color = MikoColors.AccentBright, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ─── 3. MENU SECTIONS ───
        item {
            MenuSection(title = "MY SHOPPING") {
                MenuItem("📦", "My Orders", "${state.ordersCount} orders") {
                    navController.navigate(Screen.Orders.route)
                }
                MenuItem("❤", "Wishlist", "${state.wishlistCount} items") {
                    navController.navigate(Screen.Wishlist.route)
                }
                MenuItem("👶", "Kids Profiles", "${state.kidsCount} profiles") {
                    navController.navigate(Screen.ChildProfiles.route)
                }
                MenuItem("📍", "Saved Addresses", null) {
                    navController.navigate(Screen.Addresses.route)
                }
                MenuItem("💳", "Payment Methods", "Cards & wallets") {
                    navController.navigate(Screen.PaymentMethods.route)
                }
            }
        }

        item {
            MenuSection(title = "PREFERENCES") {
                MenuItem("🔔", "Notifications", null) {
                    navController.navigate(Screen.Notifications.route)
                }
                MenuItem("🌐", "Language", "English") {}
                MenuItem("🎨", "Appearance", "Light") {}
            }
        }

        item {
            MenuSection(title = "SUPPORT") {
                MenuItem("❓", "Help Center", null) {
                    navController.navigate(Screen.HelpSupport.route)
                }
                MenuItem("📧", "Contact Us", null) {}
                MenuItem("⚖", "Terms & Privacy", null) {}
                MenuItem("ℹ", "About MIKO", "v1.0.0") {}
            }
        }

        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MikoColors.ErrorSoft)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { viewModel.logout() }
                    .padding(vertical = 14.dp)
            ) {
                Text(
                    "Log Out",
                    style = MikoTypography.Subtitle.copy(
                        color = MikoColors.Error,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

// ─── Stat Card ───
@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(vertical = 10.dp)
    ) {
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
                color = Color.White.copy(alpha = 0.8f)
            )
        )
    }
}

// ─── Menu Section ───
@Composable
private fun MenuSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(
            title,
            style = MikoTypography.Caption.copy(
                color = MikoColors.TextMuted,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp
            )
        )
        Spacer(Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MikoColors.Surface)
        ) {
            content()
        }
    }
}

// ─── Menu Item Row ───
@Composable
private fun MenuItem(icon: String, label: String, subtitle: String? = null, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MikoColors.SurfaceVariant)
        ) {
            Text(icon, fontSize = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                label,
                style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                )
            }
        }
        Text("›", color = MikoColors.TextMuted, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}