package com.kittys.premium.features.notifications

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
import com.kittys.premium.core.ui.components.MikoTopBar
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.components.*
import com.kittys.premium.core.ui.theme.MikoTypography

// ═══════════════════════════════════════════════════════
//   NOTIFICATION DATA MODEL
// ═══════════════════════════════════════════════════════

enum class NotifType { ORDER, PROMO, AI, SYSTEM, WISHLIST }

data class Notification(
    val id        : String,
    val type      : NotifType,
    val title     : String,
    val body      : String,
    val timeAgo   : String,
    val isRead    : Boolean  = false,
    val deepLink  : String?  = null
)

// ═══════════════════════════════════════════════════════
//   NOTIFICATIONS SCREEN
// ═══════════════════════════════════════════════════════

@Composable
fun NotificationsScreen(
    navController: NavController,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.NeuBackground)
    ) {
        MikoTopBar(
            title       = "Notifications",
            onBackClick = { navController.popBackStack() }
        )

        // "Mark all read" action row
        if (uiState.unreadCount > 0) {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                Text(
                    "Mark all read",
                    style = MikoTypography.labelSmall.copy(color = MikoColors.BluePrimary),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { viewModel.markAllRead() }
                )
            }
        }
        if (uiState.notifications.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔔", fontSize = 64.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("No notifications yet",
                        style = MikoTypography.headlineSmall, color = MikoColors.TextPrimary)
                    Spacer(Modifier.height(6.dp))
                    Text("We'll notify you about orders, deals & AI picks",
                        style = MikoTypography.bodySmall.copy(color = MikoColors.TextMuted))
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp, top = 12.dp, bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val today     = uiState.notifications.filter { it.timeAgo.contains("m") || it.timeAgo.contains("h") }
                val earlier   = uiState.notifications.filter { it.timeAgo.contains("d") || it.timeAgo.contains("w") }

                if (today.isNotEmpty()) {
                    item {
                        Text(
                            "Today",
                            style = MikoTypography.labelSmall.copy(
                                color         = MikoColors.TextMuted,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(today, key = { it.id }) { notif ->
                        NotifCard(notif, onTap = {
                            viewModel.markRead(notif.id)
                            notif.deepLink?.let {
                                try { navController.navigate(it) } catch (_: Exception) {}
                            }
                        })
                    }
                }

                if (earlier.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Earlier",
                            style = MikoTypography.labelSmall.copy(
                                color         = MikoColors.TextMuted,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(earlier, key = { it.id }) { notif ->
                        NotifCard(notif, onTap = {
                            viewModel.markRead(notif.id)
                            notif.deepLink?.let {
                                try { navController.navigate(it) } catch (_: Exception) {}
                            }
                        })
                    }
                }
            }
        }
    }
}

// ── Notification Card ──
@Composable
fun NotifCard(notif: Notification, onTap: () -> Unit) {
    val (emoji, gradColors) = when (notif.type) {
        NotifType.ORDER    -> "📦" to MikoColors.GradientBlue
        NotifType.PROMO    -> "🎉" to listOf(Color(0xFFF97316), Color(0xFFEF4444))
        NotifType.AI       -> "✦"  to MikoColors.GradientViolet
        NotifType.WISHLIST -> "❤️" to listOf(MikoColors.Error, Color(0xFFF43F5E))
        NotifType.SYSTEM   -> "ℹ️" to MikoColors.GradientBlue
    }

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (!notif.isRead) MikoColors.BluePrimary.copy(alpha = 0.04f)
                else MikoColors.NeuSurface
            )
            .then(
                if (!notif.isRead) Modifier.border(
                    1.dp,
                    MikoColors.BluePrimary.copy(alpha = 0.12f),
                    RoundedCornerShape(16.dp)
                ) else Modifier.neuRaised(cornerRadius = 16.dp, shadowOffset = 5.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) { onTap() }
            .padding(14.dp)
    ) {
        // Icon
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(gradColors))
        ) {
            Text(emoji, fontSize = 20.sp)
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier              = Modifier.fillMaxWidth()
            ) {
                Text(
                    notif.title,
                    style    = MikoTypography.titleSmall.copy(
                        fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.Normal
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    notif.timeAgo,
                    style = MikoTypography.labelSmall.copy(color = MikoColors.TextMuted)
                )
            }
            Spacer(Modifier.height(3.dp))
            Text(
                notif.body,
                style    = MikoTypography.bodySmall.copy(
                    color      = MikoColors.TextSecondary,
                    lineHeight = 18.sp
                ),
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }

        // Unread dot
        if (!notif.isRead) {
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(MikoColors.GradientBlue))
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

