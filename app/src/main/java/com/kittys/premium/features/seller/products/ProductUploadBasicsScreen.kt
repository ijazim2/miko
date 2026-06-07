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
import com.kittys.premium.domain.model.ProductCategory
import com.kittys.premium.core.ui.components.MikoPrimaryButton
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Product Upload Step 1: Basics
//   Name, description, category, subcategory, brand
// ════════════════════════════════════════════════════════════════

@Composable
fun ProductUploadBasicsScreen(
    navController: NavController,
    viewModel: ProductUploadViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val product = state.product

    var name by remember { mutableStateOf(product.name) }
    var description by remember { mutableStateOf(product.description) }
    var brand by remember { mutableStateOf(product.brand) }
    var selectedCategory by remember { mutableStateOf(product.category) }
    var selectedSub by remember { mutableStateOf(product.subCategory) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background)
    ) {
        ProductUploadTopBar(
            currentStep = 1,
            title = "Product Basics",
            onBack = { navController.popBackStack() }
        )

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {

            item {
                Column {
                    Text(
                        "Let's start with the basics",
                        style = MikoTypography.Headline.copy(
                            color = MikoColors.TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Give your product a clear name and description so parents can find it easily",
                        style = MikoTypography.Body.copy(
                            color = MikoColors.TextSecondary,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            // ─── Product Name ───
            item {
                UploadField(label = "Product Name", required = true) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            if (it.length <= 80) { name = it; viewModel.updateName(it) }
                        },
                        placeholder = { Text("e.g. Lavender Bow Party Dress") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = uploadFieldColors()
                    )
                    Text(
                        "${name.length}/80",
                        style = MikoTypography.Caption.copy(color = MikoColors.TextMuted),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        textAlign = TextAlign.End
                    )
                }
            }

            // ─── Description ───
            item {
                UploadField(label = "Description", required = true) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = {
                            if (it.length <= 1000) { description = it; viewModel.updateDescription(it) }
                        },
                        placeholder = { Text("Describe the material, fit, and what makes it special (min 20 characters)") },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = uploadFieldColors(),
                        maxLines = 8
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        Text(
                            if (description.length < 20) "At least ${20 - description.length} more characters" else "Looks good!",
                            style = MikoTypography.Caption.copy(
                                color = if (description.length < 20) MikoColors.TextMuted else MikoColors.Success
                            )
                        )
                        Text(
                            "${description.length}/1000",
                            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                        )
                    }
                }
            }

            // ─── Category ───
            item {
                UploadField(label = "Category", required = true) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ProductCategory.values().toList().chunked(2).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                row.forEach { category ->
                                    CategoryTile(
                                        category = category,
                                        selected = selectedCategory == category,
                                        onClick = {
                                            selectedCategory = category
                                            selectedSub = ""
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

            // ─── Subcategory (only if category chosen) ───
            selectedCategory?.let { category ->
                item {
                    UploadField(label = "Subcategory") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            category.subCategories.chunked(2).forEach { row ->
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    row.forEach { sub ->
                                        SubcategoryChip(
                                            label = sub,
                                            selected = selectedSub == sub,
                                            onClick = {
                                                selectedSub = sub
                                                viewModel.updateSubCategory(sub)
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
            }

            // ─── Brand (optional) ───
            item {
                UploadField(label = "Brand (Optional)") {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it; viewModel.updateBrand(it) },
                        placeholder = { Text("e.g. MIKO, Carter's, or your own brand") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = uploadFieldColors()
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
                enabled = viewModel.isStep1Valid(),
                onClick = {
                    viewModel.nextStep()
                    navController.navigate(Screen.ProductUploadAudience.route)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
//   SHARED WIZARD COMPONENTS (used by all 6 upload steps)
// ════════════════════════════════════════════════════════════════

@Composable
fun ProductUploadTopBar(
    currentStep: Int,
    title: String,
    onBack: () -> Unit,
    totalSteps: Int = 6
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
            Text(
                "${(currentStep * 100) / totalSteps}%",
                style = MikoTypography.Caption.copy(
                    color = MikoColors.TextMuted,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        // Progress bar
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            repeat(totalSteps) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (index < currentStep)
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
fun UploadField(
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
                Text(" *", style = MikoTypography.Subtitle.copy(color = MikoColors.PrimaryStart))
            }
        }
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
fun uploadFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MikoColors.PrimaryStart,
    unfocusedBorderColor = MikoColors.BorderLight,
    cursorColor = MikoColors.PrimaryStart,
    focusedContainerColor = MikoColors.Surface,
    unfocusedContainerColor = MikoColors.Surface
)

@Composable
private fun CategoryTile(
    category: ProductCategory,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) MikoColors.PastelPink else MikoColors.Surface)
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
            maxLines = 1
        )
    }
}

@Composable
private fun SubcategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) MikoColors.PastelPink else MikoColors.SurfaceVariant)
            .border(
                width = 1.dp,
                color = if (selected) MikoColors.PrimaryStart else MikoColors.BorderLight,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        Text(
            label,
            style = MikoTypography.Caption.copy(
                color = if (selected) MikoColors.PrimaryEnd else MikoColors.TextSecondary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            ),
            maxLines = 1
        )
    }
}