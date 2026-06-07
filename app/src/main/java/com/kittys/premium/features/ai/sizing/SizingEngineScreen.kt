package com.kittys.premium.features.ai.sizing

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.kittys.premium.core.ui.components.MikoPrimaryButton
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ════════════════════════════════════════════════════════════════
//   MIKO — Sizing Engine
//   AI size predictor: age + height + weight + brand corrections
// ════════════════════════════════════════════════════════════════

data class SizingResult(
    val recommendedSize: String,
    val confidence: Int,
    val brandNote: String,
    val alternativeSize: String? = null,
    val growthAdvice: String
)

data class SizingUiState(
    val ageMonths: String = "",
    val heightCm: String = "",
    val weightKg: String = "",
    val gender: String = "girl",
    val brand: String = "Universal",
    val result: SizingResult? = null,
    val isCalculating: Boolean = false
)

@HiltViewModel
class SizingEngineViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SizingUiState())
    val uiState: StateFlow<SizingUiState> = _uiState.asStateFlow()

    fun update(transform: (SizingUiState) -> SizingUiState) = _uiState.update(transform)

    fun calculate() {
        val state = _uiState.value
        val ageM = state.ageMonths.toIntOrNull() ?: return
        val height = state.heightCm.toIntOrNull() ?: return
        val weight = state.weightKg.toFloatOrNull() ?: return

        _uiState.update { it.copy(isCalculating = true) }

        val sizeFromAge = ageToSize(ageM)
        val sizeFromHeight = heightToSize(height)
        val sizeFromWeight = weightToSize(weight)

        // Height is most reliable for clothing fit
        val recommended = sizeFromHeight

        val confidence = when {
            sizeFromAge == sizeFromHeight && sizeFromHeight == sizeFromWeight -> 95
            sizeFromAge == sizeFromHeight || sizeFromHeight == sizeFromWeight -> 82
            else -> 68
        }

        val brandNote = when (state.brand) {
            "Carter's"     -> "This brand tends to run SMALL — consider sizing up if your child is between sizes."
            "Zara Kids"    -> "Runs slim — perfect for lean builds. Size up if your child is sturdy."
            "H&M Kids"     -> "True to size. Standard fit."
            "Little Stars" -> "Local brand — accurate to age guide."
            else           -> "Standard age-based sizing. Add 1 size if buying ahead for growth."
        }

        val alternative = if (confidence < 85) sizeFromAge else null

        val growth = when {
            ageM < 24 -> "Babies grow fast — consider buying 1–2 months ahead."
            ageM < 60 -> "Children grow 6–8cm per year on average."
            else      -> "Growth slows after age 5. Buy current size for best fit."
        }

        _uiState.update {
            it.copy(
                isCalculating = false,
                result = SizingResult(
                    recommendedSize = recommended,
                    confidence = confidence,
                    brandNote = brandNote,
                    alternativeSize = alternative,
                    growthAdvice = growth
                )
            )
        }
    }

    private fun ageToSize(months: Int): String = when {
        months <= 3   -> "0-3M"
        months <= 6   -> "3-6M"
        months <= 12  -> "6-12M"
        months <= 18  -> "12-18M"
        months <= 24  -> "1-2Y"
        months <= 36  -> "2-3Y"
        months <= 48  -> "3-4Y"
        months <= 60  -> "4-5Y"
        months <= 72  -> "5-6Y"
        months <= 96  -> "7-8Y"
        months <= 120 -> "9-10Y"
        months <= 144 -> "11-12Y"
        else          -> "12Y+"
    }

    private fun heightToSize(cm: Int): String = when {
        cm <= 62  -> "0-3M"
        cm <= 68  -> "3-6M"
        cm <= 80  -> "6-12M"
        cm <= 86  -> "12-18M"
        cm <= 92  -> "1-2Y"
        cm <= 98  -> "2-3Y"
        cm <= 104 -> "3-4Y"
        cm <= 110 -> "4-5Y"
        cm <= 116 -> "5-6Y"
        cm <= 122 -> "6-7Y"
        cm <= 128 -> "7-8Y"
        cm <= 140 -> "9-10Y"
        cm <= 152 -> "11-12Y"
        else      -> "12Y+"
    }

    private fun weightToSize(kg: Float): String = when {
        kg <= 6f  -> "0-3M"
        kg <= 8f  -> "3-6M"
        kg <= 11f -> "6-12M"
        kg <= 13f -> "1-2Y"
        kg <= 15f -> "2-3Y"
        kg <= 17f -> "3-4Y"
        kg <= 19f -> "4-5Y"
        kg <= 22f -> "5-6Y"
        kg <= 26f -> "7-8Y"
        kg <= 32f -> "9-10Y"
        kg <= 40f -> "11-12Y"
        else      -> "12Y+"
    }
}

@Composable
fun SizingEngineScreen(
    navController: NavController,
    viewModel: SizingEngineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val brands = listOf("Universal", "Carter's", "Zara Kids", "H&M Kids", "Little Stars")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background)
    ) {
        // ─── Top Bar ───
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .neuRaised(cornerRadius = 20.dp, shadowOffset = 4.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.popBackStack() }
            ) {
                Text("←", fontSize = 18.sp, color = MikoColors.TextPrimary)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                "AI Sizing Engine",
                style = MikoTypography.Title.copy(
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ─── Intro card ───
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(MikoColors.GradientButton))
                    .padding(20.dp)
            ) {
                Column {
                    Text("📏", fontSize = 28.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Find the Perfect Size",
                        style = MikoTypography.Headline.copy(
                            color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp
                        )
                    )
                    Text(
                        "Combines age, height, and weight for precise recommendations.",
                        style = MikoTypography.Body.copy(color = Color.White.copy(alpha = 0.9f))
                    )
                }
            }

            // ─── Input form ───
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .neuRaised(cornerRadius = 18.dp, shadowOffset = 8.dp)
                    .padding(20.dp)
            ) {
                Text("Child Details", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
                Spacer(Modifier.height(14.dp))

                // Gender pills
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("girl" to "👧 Girl", "boy" to "👦 Boy").forEach { (k, label) ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .then(
                                    if (uiState.gender == k)
                                        Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                                    else
                                        Modifier.background(MikoColors.SurfaceVariant)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { viewModel.update { it.copy(gender = k) } }
                        ) {
                            Text(label, style = MikoTypography.Subtitle.copy(
                                color = if (uiState.gender == k) Color.White else MikoColors.TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            ))
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Age / Height / Weight inputs
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NumberField(
                        label = "Age",
                        value = uiState.ageMonths,
                        hint = "months",
                        decimal = false,
                        onChange = { viewModel.update { s -> s.copy(ageMonths = it) } },
                        modifier = Modifier.weight(1f)
                    )
                    NumberField(
                        label = "Height",
                        value = uiState.heightCm,
                        hint = "cm",
                        decimal = false,
                        onChange = { viewModel.update { s -> s.copy(heightCm = it) } },
                        modifier = Modifier.weight(1f)
                    )
                    NumberField(
                        label = "Weight",
                        value = uiState.weightKg,
                        hint = "kg",
                        decimal = true,
                        onChange = { viewModel.update { s -> s.copy(weightKg = it) } },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text("Brand", style = MikoTypography.Label.copy(color = MikoColors.TextSecondary))
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    brands.forEach { b ->
                        BrandChip(b, uiState.brand == b) { viewModel.update { it.copy(brand = b) } }
                    }
                }

                Spacer(Modifier.height(20.dp))

                MikoPrimaryButton(
                    text = if (uiState.isCalculating) "Calculating…" else "Calculate Size",
                    trailingIcon = null,
                    enabled = uiState.ageMonths.isNotBlank() &&
                            uiState.heightCm.isNotBlank() &&
                            uiState.weightKg.isNotBlank() &&
                            !uiState.isCalculating,
                    onClick = { viewModel.calculate() },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ─── Result ───
            uiState.result?.let { r ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neuRaised(cornerRadius = 18.dp, shadowOffset = 8.dp)
                        .padding(20.dp)
                ) {
                    Text("Recommendation", style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                    ))
                    Spacer(Modifier.height(14.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Brush.linearGradient(MikoColors.GradientPrimary))
                        ) {
                            Text(
                                r.recommendedSize,
                                style = MikoTypography.Headline.copy(
                                    color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp
                                )
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Best Match", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                            Text(
                                "${r.confidence}% confidence",
                                style = MikoTypography.Headline.copy(
                                    color = MikoColors.PrimaryEnd,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            )
                            r.alternativeSize?.let {
                                Text(
                                    "Also consider: $it",
                                    style = MikoTypography.Caption.copy(color = MikoColors.Warning)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = MikoColors.Divider)
                    Spacer(Modifier.height(14.dp))

                    Text("📝 Brand Note", style = MikoTypography.Label.copy(
                        color = MikoColors.PrimaryStart, fontWeight = FontWeight.SemiBold
                    ))
                    Spacer(Modifier.height(4.dp))
                    Text(r.brandNote, style = MikoTypography.Caption.copy(
                        color = MikoColors.TextSecondary, lineHeight = 19.sp
                    ))

                    Spacer(Modifier.height(12.dp))

                    Text("🌱 Growth Advice", style = MikoTypography.Label.copy(
                        color = MikoColors.Success, fontWeight = FontWeight.SemiBold
                    ))
                    Spacer(Modifier.height(4.dp))
                    Text(r.growthAdvice, style = MikoTypography.Caption.copy(
                        color = MikoColors.TextSecondary, lineHeight = 19.sp
                    ))
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ─── Number input field ───
@Composable
private fun NumberField(
    label: String,
    value: String,
    hint: String,
    decimal: Boolean,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(label, style = MikoTypography.Label.copy(color = MikoColors.TextSecondary))
        Spacer(Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MikoColors.SurfaceVariant)
                .border(1.dp, MikoColors.BorderLight, RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = { input ->
                    val filtered = if (decimal)
                        input.filter { it.isDigit() || it == '.' }
                    else
                        input.filter { it.isDigit() }
                    onChange(filtered)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number
                ),
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    color = MikoColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold
                ),
                cursorBrush = SolidColor(MikoColors.PrimaryStart),
                modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(hint, style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                    }
                    inner()
                }
            )
        }
    }
}

// ─── Brand chip ───
@Composable
private fun BrandChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .then(
                if (selected)
                    Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                else
                    Modifier.background(MikoColors.SurfaceVariant)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            label,
            style = MikoTypography.Caption.copy(
                color = if (selected) Color.White else MikoColors.TextSecondary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        )
    }
}