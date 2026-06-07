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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.domain.model.StoreCategory
import com.kittys.premium.core.ui.components.MikoPrimaryButton
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Seller Onboarding Step 2: Store Info
//   Seller fills: logo, store name, description, category, location
// ════════════════════════════════════════════════════════════════

@Composable
fun SellerStoreInfoScreen(
    navController: NavController,
    viewModel: SellerOnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var storeName by remember { mutableStateOf(state.storeName) }
    var description by remember { mutableStateOf(state.description) }
    var selectedCategory by remember { mutableStateOf<StoreCategory?>(state.storeCategory) }
    var location by remember { mutableStateOf(state.location) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background)
    ) {
        // ─── Top Bar with Progress ───
        SellerProgressTopBar(
            currentStep = 2,
            totalSteps = 4,
            title = "Store Information",
            onBack = { navController.popBackStack() }
        )

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {

            // ─── Intro ───
            item {
                Column {
                    Text(
                        "Tell us about your store",
                        style = MikoTypography.Headline.copy(
                            color = MikoColors.TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "This is what customers will see when they visit your store",
                        style = MikoTypography.Body.copy(
                            color = MikoColors.TextSecondary,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            // ─── Logo Upload ───
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(MikoColors.PastelPink)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { /* TODO: image picker → viewModel.updateLogo(uri) */ }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📷", fontSize = 32.sp)
                            Text(
                                "Add Logo",
                                style = MikoTypography.Caption.copy(
                                    color = MikoColors.PrimaryStart,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "PNG or JPG · 1:1 ratio",
                        style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                    )
                }
            }

            // ─── Store Name ───
            item {
                FormField(label = "Store Name", required = true) {
                    OutlinedTextField(
                        value = storeName,
                        onValueChange = {
                            if (it.length <= 40) {
                                storeName = it
                                viewModel.updateStoreName(it)
                            }
                        },
                        placeholder = { Text("e.g. Little Stars Fashion") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MikoColors.PrimaryStart,
                            unfocusedBorderColor = MikoColors.BorderLight,
                            cursorColor = MikoColors.PrimaryStart
                        )
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        Text(
                            "Choose carefully — you can't change this later",
                            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                        )
                        Text(
                            "${storeName.length}/40",
                            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                        )
                    }
                }
            }

            // ─── Description ───
            item {
                FormField(label = "Store Description", required = true) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = {
                            if (it.length <= 200) {
                                description = it
                                viewModel.updateDescription(it)
                            }
                        },
                        placeholder = { Text("What makes your store special?") },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MikoColors.PrimaryStart,
                            unfocusedBorderColor = MikoColors.BorderLight,
                            cursorColor = MikoColors.PrimaryStart
                        ),
                        maxLines = 5
                    )
                    Text(
                        "${description.length}/200",
                        style = MikoTypography.Caption.copy(color = MikoColors.TextMuted),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        textAlign = TextAlign.End
                    )
                }
            }

            // ─── Store Category ───
            item {
                FormField(label = "What do you sell?", required = true) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val categories = StoreCategory.values().toList()
                        categories.chunked(2).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                row.forEach { category ->
                                    CategoryChoice(
                                        category = category,
                                        selected = selectedCategory == category,
                                        onClick = {
                                            selectedCategory = category
                                            viewModel.updateCategory(category)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (row.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // ─── Location ───
            item {
                FormField(label = "Location (City)", required = true) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = {
                            location = it
                            viewModel.updateLocation(it)
                        },
                        placeholder = { Text("e.g. Colombo, Kandy, Galle") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Text("📍", fontSize = 16.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MikoColors.PrimaryStart,
                            unfocusedBorderColor = MikoColors.BorderLight,
                            cursorColor = MikoColors.PrimaryStart
                        )
                    )
                }
            }

            item { Spacer(Modifier.height(20.dp)) }
        }

        // ─── Bottom Continue Button ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MikoColors.Surface)
                .padding(20.dp)
        ) {
            val isValid = storeName.length >= 3 &&
                    description.length >= 10 &&
                    selectedCategory != null &&
                    location.isNotBlank()

            MikoPrimaryButton(
                text = "Continue",
                enabled = isValid,
                onClick = { navController.navigate(Screen.SellerVerification.route) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
//   SHARED COMPONENTS (used by all 4 onboarding steps)
// ════════════════════════════════════════════════════════════════

@Composable
fun SellerProgressTopBar(
    currentStep: Int,
    totalSteps: Int,
    title: String,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.background(MikoColors.Surface)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .neuRaised(cornerRadius = 20.dp, shadowOffset = 4.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onBack() }
            ) {
                Text("←", fontSize = 18.sp, color = MikoColors.TextPrimary)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "Step $currentStep of $totalSteps",
                    style = MikoTypography.Caption.copy(
                        color = MikoColors.PrimaryStart,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    title,
                    style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Progress bar
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            repeat(totalSteps) { index ->
                val isCompleted = index < currentStep
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (isCompleted)
                                Brush.linearGradient(MikoColors.GradientPrimary)
                            else
                                Brush.linearGradient(listOf(MikoColors.BorderLight, MikoColors.BorderLight))
                        )
                )
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun FormField(
    label: String,
    required: Boolean = false,
    content: @Composable () -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                label,
                style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )
            if (required) {
                Text(
                    " *",
                    style = MikoTypography.Subtitle.copy(color = MikoColors.PrimaryStart)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun CategoryChoice(
    category: StoreCategory,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (selected) MikoColors.PastelPink else MikoColors.Surface
            )
            .border(
                width = 1.5.dp,
                brush = if (selected)
                    Brush.linearGradient(MikoColors.GradientPrimary)
                else
                    Brush.linearGradient(listOf(MikoColors.BorderLight, MikoColors.BorderLight)),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(vertical = 14.dp, horizontal = 8.dp)
    ) {
        Text(category.emoji, fontSize = 26.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            category.displayName,
            style = MikoTypography.Label.copy(
                color = if (selected) MikoColors.PrimaryEnd else MikoColors.TextPrimary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}