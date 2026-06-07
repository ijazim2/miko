package com.kittys.premium.features.seller.products

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.domain.model.AgeGroup
import com.kittys.premium.domain.model.ProductGender
import com.kittys.premium.domain.model.ProductStyle
import com.kittys.premium.domain.model.Season
import com.kittys.premium.core.ui.components.MikoPrimaryButton
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Product Upload Step 2: Audience
//   Who is this product for? Gender, age groups, season, style
// ════════════════════════════════════════════════════════════════

@Composable
fun ProductUploadAudienceScreen(
    navController: NavController,
    viewModel: ProductUploadViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val product = state.product

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background)
    ) {
        ProductUploadTopBar(
            currentStep = 2,
            title = "Who's it for?",
            onBack = { navController.popBackStack() }
        )

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.weight(1f)
        ) {

            item {
                Column {
                    Text(
                        "Tell us your audience",
                        style = MikoTypography.Headline.copy(
                            color = MikoColors.TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "This helps parents filter and find your product faster",
                        style = MikoTypography.Body.copy(
                            color = MikoColors.TextSecondary,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            // ─── Gender ───
            item {
                UploadField(label = "Gender", required = true) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ProductGender.values().forEach { gender ->
                            GenderTile(
                                gender = gender,
                                selected = product.gender == gender,
                                onClick = { viewModel.updateGender(gender) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ─── Age Groups (multi-select) ───
            item {
                UploadField(label = "Age Groups", required = true) {
                    Column {
                        Text(
                            "Select all that apply",
                            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        FlowRowChips(
                            items = AgeGroup.values().toList(),
                            isSelected = { product.ageGroups.contains(it) },
                            label = { it.displayName },
                            onToggle = { viewModel.toggleAgeGroup(it) }
                        )
                    }
                }
            }

            // ─── Season ───
            item {
                UploadField(label = "Season (Optional)") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Season.values().forEach { season ->
                            SeasonTile(
                                season = season,
                                selected = product.season == season,
                                onClick = { viewModel.updateSeason(season) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ─── Style ───
            item {
                UploadField(label = "Style (Optional)") {
                    FlowRowChips(
                        items = ProductStyle.values().toList(),
                        isSelected = { product.style == it },
                        label = { "${it.emoji} ${it.displayName}" },
                        onToggle = { viewModel.updateStyle(it) }
                    )
                }
            }

            item { Spacer(Modifier.height(20.dp)) }
        }

        // ─── Bottom Continue ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MikoColors.Surface)
                .padding(20.dp)
        ) {
            MikoPrimaryButton(
                text = "Continue",
                enabled = viewModel.isStep2Valid(),
                onClick = {
                    viewModel.nextStep()
                    navController.navigate(Screen.ProductUploadVariants.route)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ─── Gender Tile ───
@Composable
private fun GenderTile(
    gender: ProductGender,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) MikoColors.PastelPink else MikoColors.Surface)
            .border(
                width = 1.5.dp,
                brush = if (selected)
                    Brush.linearGradient(MikoColors.GradientPrimary)
                else
                    Brush.linearGradient(listOf(MikoColors.BorderLight, MikoColors.BorderLight)),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(vertical = 18.dp)
    ) {
        Text(gender.emoji, fontSize = 30.sp)
        Spacer(Modifier.height(6.dp))
        Text(
            gender.displayName,
            style = MikoTypography.Subtitle.copy(
                color = if (selected) MikoColors.PrimaryEnd else MikoColors.TextPrimary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
}

// ─── Season Tile ───
@Composable
private fun SeasonTile(
    season: Season,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) MikoColors.PastelLavender else MikoColors.Surface)
            .border(
                width = 1.5.dp,
                color = if (selected) MikoColors.AccentBright else MikoColors.BorderLight,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp)
    ) {
        Text(season.emoji, fontSize = 22.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            season.displayName,
            style = MikoTypography.Caption.copy(
                color = if (selected) MikoColors.AccentBright else MikoColors.TextSecondary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            ),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

// ─── Generic flow-row of selectable chips ───
@Composable
fun <T> FlowRowChips(
    items: List<T>,
    isSelected: (T) -> Boolean,
    label: (T) -> String,
    onToggle: (T) -> Unit
) {
    // Simple wrap layout using chunked rows (2-3 per row by width)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { item ->
                    val sel = isSelected(item)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(50.dp))
                            .background(if (sel) MikoColors.PastelPink else MikoColors.SurfaceVariant)
                            .border(
                                width = 1.dp,
                                color = if (sel) MikoColors.PrimaryStart else MikoColors.BorderLight,
                                shape = RoundedCornerShape(50.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onToggle(item) }
                            .padding(vertical = 12.dp, horizontal = 14.dp)
                    ) {
                        Text(
                            label(item),
                            style = MikoTypography.Caption.copy(
                                color = if (sel) MikoColors.PrimaryEnd else MikoColors.TextSecondary,
                                fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}