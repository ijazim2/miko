package com.kittys.premium.core.ui.nav

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.kittys.premium.core.ui.neu.neuRaised
import com.kittys.premium.core.ui.system.MikoMotion
import com.kittys.premium.core.ui.system.MikoViewModel
import com.kittys.premium.core.ui.system.NeuElevation
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO BOTTOM NAVIGATION — floating Level3 pill, Living Miko center.
// ════════════════════════════════════════════════════════════════

data class MikoNavItem(
    val route: String,
    val label: String,
    val emoji: String   // swap for MikoIcons ImageVectors later
)

@Composable
fun MikoBottomNavigation(
    items: List<MikoNavItem>,          // exactly 4 (2 left, 2 right of Miko)
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    onMikoClick: () -> Unit,
    mikoViewModel: MikoViewModel,
    modifier: Modifier = Modifier
) {
    val ui by mikoViewModel.ui.collectAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // The pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .neuRaised(elevation = NeuElevation.Level3, cornerRadius = 28.dp)
                .padding(horizontal = 8.dp)
        ) {
            val left = items.take(2)
            val right = items.drop(2).take(2)

            left.forEach { item ->
                NavTab(item, currentRoute == item.route) { onItemClick(item.route) }
            }
            Spacer(Modifier.size(64.dp)) // gap for Miko
            right.forEach { item ->
                NavTab(item, currentRoute == item.route) { onItemClick(item.route) }
            }
        }

        // Living Miko, raised above the pill center
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset(y = (-22).dp)
                .size(64.dp)
                .clip(CircleShape)
                .background(MikoColors.NeuSurface)
                .neuRaised(elevation = NeuElevation.Level3, cornerRadius = 32.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onMikoClick() }
        ) {
            MikoHost(viewModel = mikoViewModel, size = 52.dp)

            // Notification dot
            AnimatedVisibility(
                visible = ui.hasNotification,
                enter = fadeIn(), exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Box(
                    Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(MikoColors.Error)
                )
            }
        }
    }
}

@Composable
private fun NavTab(item: MikoNavItem, selected: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.1f else 1f,
        animationSpec = MikoMotion.spring(),
        label = "tab_scale"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .then(
                    if (selected)
                        Modifier.clip(CircleShape).background(Brush.linearGradient(MikoColors.GradientPrimary))
                    else Modifier
                )
        ) {
            Text(item.emoji, fontSize = 16.sp)
        }
        AnimatedVisibility(selected, enter = fadeIn(), exit = fadeOut()) {
            Text(
                item.label,
                style = MikoTypography.labelSmall.copy(
                    color = MikoColors.PrimaryEnd, fontSize = 9.sp, fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
