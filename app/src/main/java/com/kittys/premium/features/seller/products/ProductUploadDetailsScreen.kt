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
import com.kittys.premium.domain.model.FabricCare
import com.kittys.premium.domain.model.Pattern
import com.kittys.premium.domain.model.SafetyTag
import com.kittys.premium.core.ui.components.MikoPrimaryButton
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Product Upload Step 4: Details
//   Material · fabric care · pattern · safety tags · custom tags
// ════════════════════════════════════════════════════════════════

@Composable
fun ProductUploadDetailsScreen(
    navController: NavController,
    viewModel: ProductUploadViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val product = state.product

    var material by remember { mutableStateOf(product.material) }
    var countryOfOrigin by remember { mutableStateOf(product.countryOfOrigin) }
    var customTagInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background)
    ) {
        ProductUploadTopBar(
            currentStep = 4,
            title = "Product Details",
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
                        "Add the details",
                        style = MikoTypography.Headline.copy(
                            color = MikoColors.TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "These help build trust with parents. All optional, but more detail sells better.",
                        style = MikoTypography.Body.copy(
                            color = MikoColors.TextSecondary,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            // ─── Material ───
            item {
                UploadField(label = "Material") {
                    OutlinedTextField(
                        value = material,
                        onValueChange = { material = it; viewModel.updateMaterial(it) },
                        placeholder = { Text("e.g. 100% Cotton, Organic Bamboo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = uploadFieldColors()
                    )
                }
            }

            // ─── Fabric Care ───
            item {
                UploadField(label = "Fabric Care") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FabricCare.values().toList().chunked(2).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { care ->
                                    SelectableTile(
                                        label = care.displayName,
                                        selected = product.fabricCare == care,
                                        onClick = { viewModel.updateFabricCare(care) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (row.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // ─── Pattern ───
            item {
                UploadField(label = "Pattern") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Pattern.values().toList().chunked(2).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { pattern ->
                                    SelectableTile(
                                        label = pattern.displayName,
                                        selected = product.pattern == pattern,
                                        onClick = { viewModel.updatePattern(pattern) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (row.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // ─── Safety Tags (multi-select) ───
            item {
                UploadField(label = "Safety & Certifications") {
                    Column {
                        Text(
                            "Select all that apply — parents love these!",
                            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SafetyTag.values().toList().chunked(2).forEach { row ->
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    row.forEach { tag ->
                                        val selected = product.safetyTags.contains(tag)
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(50.dp))
                                                .background(
                                                    if (selected) MikoColors.SuccessSoft else MikoColors.SurfaceVariant
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = if (selected) MikoColors.Success else MikoColors.BorderLight,
                                                    shape = RoundedCornerShape(50.dp)
                                                )
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) { viewModel.toggleSafetyTag(tag) }
                                                .padding(vertical = 10.dp, horizontal = 10.dp)
                                        ) {
                                            Text(
                                                "${tag.emoji} ${tag.displayName}",
                                                style = MikoTypography.Caption.copy(
                                                    color = if (selected) MikoColors.Success else MikoColors.TextSecondary,
                                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                                ),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                    if (row.size == 1) Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // ─── Country of Origin ───
            item {
                UploadField(label = "Country of Origin") {
                    OutlinedTextField(
                        value = countryOfOrigin,
                        onValueChange = { countryOfOrigin = it; viewModel.updateCountryOfOrigin(it) },
                        placeholder = { Text("e.g. Sri Lanka") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = uploadFieldColors()
                    )
                }
            }

            // ─── Custom Tags ───
            item {
                UploadField(label = "Search Tags (Optional)") {
                    Column {
                        Text(
                            "Add keywords so your product shows up in searches",
                            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = customTagInput,
                                onValueChange = { customTagInput = it },
                                placeholder = { Text("e.g. birthday, cotton, summer") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = uploadFieldColors()
                            )
                            Spacer(Modifier.width(8.dp))
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Brush.linearGradient(MikoColors.GradientPrimary))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        if (customTagInput.isNotBlank()) {
                                            viewModel.addCustomTag(customTagInput)
                                            customTagInput = ""
                                        }
                                    }
                            ) {
                                Text("+", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Tag chips
                        if (product.customTags.isNotEmpty()) {
                            Spacer(Modifier.height(10.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                product.customTags.chunked(3).forEach { row ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        row.forEach { tag ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(50.dp))
                                                    .background(MikoColors.PastelPink)
                                                    .clickable(
                                                        interactionSource = remember { MutableInteractionSource() },
                                                        indication = null
                                                    ) { viewModel.removeCustomTag(tag) }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    tag,
                                                    style = MikoTypography.Caption.copy(
                                                        color = MikoColors.PrimaryEnd,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                )
                                                Spacer(Modifier.width(4.dp))
                                                Text("×", color = MikoColors.PrimaryEnd, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(20.dp)) }
        }

        // ─── Bottom Continue (Details are optional → always enabled) ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MikoColors.Surface)
                .padding(20.dp)
        ) {
            MikoPrimaryButton(
                text = "Continue",
                onClick = {
                    viewModel.nextStep()
                    navController.navigate(Screen.ProductUploadShipping.route)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ─── Selectable tile (single-select for fabric care / pattern) ───
@Composable
private fun SelectableTile(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) MikoColors.PastelPink else MikoColors.SurfaceVariant)
            .border(
                width = 1.dp,
                color = if (selected) MikoColors.PrimaryStart else MikoColors.BorderLight,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(vertical = 12.dp, horizontal = 10.dp)
    ) {
        Text(
            label,
            style = MikoTypography.Caption.copy(
                color = if (selected) MikoColors.PrimaryEnd else MikoColors.TextSecondary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            ),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}