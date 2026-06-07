package com.kittys.premium.features.ai.outfit

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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.kittys.premium.domain.model.formatThousands
import com.kittys.premium.core.ui.components.MikoPrimaryButton
import com.kittys.premium.core.ui.components.MikoSecondaryButton
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ════════════════════════════════════════════════════════════════
//   MIKO — Outfit Generator (occasion + budget + AI match score)
// ════════════════════════════════════════════════════════════════

data class OutfitPiece(
    val id: String,
    val category: String,   // Top | Bottom | Outerwear | Shoes | Accessory
    val name: String,
    val priceLkr: Int,
    val color: String
)

data class GeneratedOutfit(
    val pieces: List<OutfitPiece>,
    val totalPrice: Int,
    val confidence: Int,    // 0-100 AI match score
    val occasion: String,
    val styleTag: String
)

data class OutfitGenState(
    val occasion: String = "Casual",
    val budget: Int = 5000,
    val colorPalette: String = "Auto",
    val currentOutfit: GeneratedOutfit? = null,
    val isGenerating: Boolean = false
)

@HiltViewModel
class OutfitGeneratorViewModel @Inject constructor(
    // private val geminiService: GeminiService  // wire later for real generation
) : ViewModel() {

    private val _uiState = MutableStateFlow(OutfitGenState())
    val uiState: StateFlow<OutfitGenState> = _uiState.asStateFlow()

    fun setOccasion(occ: String) = _uiState.update { it.copy(occasion = occ) }
    fun setBudget(b: Int) = _uiState.update { it.copy(budget = b) }
    fun setColorPalette(c: String) = _uiState.update { it.copy(colorPalette = c) }

    fun generate() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }
            delay(1800) // simulate AI latency
            // TODO: call Gemini with structured prompt for real product IDs
            _uiState.update {
                it.copy(
                    isGenerating = false,
                    currentOutfit = GeneratedOutfit(
                        pieces = listOf(
                            OutfitPiece("p1", "Top", "Floral Top Blue", 1200, "Blue"),
                            OutfitPiece("p2", "Bottom", "White Denim Shorts", 980, "White"),
                            OutfitPiece("p3", "Shoes", "Pink Canvas Sneakers", 1500, "Pink"),
                            OutfitPiece("p4", "Accessory", "Floral Hair Clip Set", 340, "Pink")
                        ),
                        totalPrice = 4020,
                        confidence = 92,
                        occasion = it.occasion,
                        styleTag = "Playful Summer"
                    )
                )
            }
        }
    }

    fun shufflePiece(category: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }
            delay(800)
            _uiState.update { it.copy(isGenerating = false) }
        }
    }
}

@Composable
fun OutfitGeneratorScreen(
    navController: NavController,
    viewModel: OutfitGeneratorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val occasions = listOf("Casual", "Birthday", "School", "Party", "Sport", "Avurudu", "Beach", "Family")
    val palettes = listOf("Auto", "Pastels", "Bold", "Pink", "Blue", "Earth", "Black & White")

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
                "AI Outfit Generator",
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ─── Hero card ───
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Brush.linearGradient(MikoColors.GradientButton))
                    .padding(20.dp)
            ) {
                Column {
                    Text("Build the Perfect Outfit", style = MikoTypography.Headline.copy(
                        color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp
                    ))
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Tell MIKO AI what you need and get a complete look for your child.",
                        style = MikoTypography.Body.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 20.sp
                        )
                    )
                }
            }

            // ─── Occasion ───
            Text("Occasion", style = MikoTypography.Subtitle.copy(
                color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
            ))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(occasions) { occ ->
                    SelectChip(occ, uiState.occasion == occ) { viewModel.setOccasion(occ) }
                }
            }

            // ─── Budget ───
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Budget", style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                    ))
                    Text(
                        "LKR ${uiState.budget.formatThousands()}",
                        style = MikoTypography.Subtitle.copy(
                            color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
                        )
                    )
                }
                Slider(
                    value = uiState.budget.toFloat(),
                    onValueChange = { viewModel.setBudget(it.toInt()) },
                    valueRange = 1500f..15000f,
                    colors = SliderDefaults.colors(
                        thumbColor = MikoColors.PrimaryStart,
                        activeTrackColor = MikoColors.PrimaryStart,
                        inactiveTrackColor = MikoColors.BorderLight
                    )
                )
            }

            // ─── Color palette ───
            Text("Color Palette", style = MikoTypography.Subtitle.copy(
                color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
            ))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(palettes) { c ->
                    SelectChip(c, uiState.colorPalette == c) { viewModel.setColorPalette(c) }
                }
            }

            // ─── Generate button / loading ───
            if (uiState.isGenerating) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp)
                        .padding(20.dp)
                ) {
                    CircularProgressIndicator(
                        color = MikoColors.PrimaryStart,
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 3.dp
                    )
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("MIKO AI is creating an outfit…", style = MikoTypography.Subtitle.copy(
                            color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold
                        ))
                        Text(
                            "Analysing thousands of products",
                            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                        )
                    }
                }
            } else {
                MikoPrimaryButton(
                    text = if (uiState.currentOutfit == null) "Generate Outfit" else "Generate Another",
                    trailingIcon = null,
                    onClick = { viewModel.generate() },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ─── Generated outfit ───
            uiState.currentOutfit?.let { outfit ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neuRaised(cornerRadius = 18.dp, shadowOffset = 8.dp)
                        .padding(18.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(outfit.styleTag, style = MikoTypography.Title.copy(
                                color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
                            ))
                            Text(
                                "AI Match: ${outfit.confidence}%",
                                style = MikoTypography.Caption.copy(
                                    color = MikoColors.Success, fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.linearGradient(MikoColors.GradientPrimary))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                "LKR ${outfit.totalPrice.formatThousands()}",
                                style = MikoTypography.Subtitle.copy(
                                    color = Color.White, fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    outfit.pieces.forEach { piece ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .neuRaised(cornerRadius = 12.dp, shadowOffset = 4.dp)
                                .padding(12.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MikoColors.PastelPink)
                            ) {
                                Text(
                                    when (piece.category) {
                                        "Top" -> "👕"
                                        "Bottom" -> "👖"
                                        "Shoes" -> "👟"
                                        "Accessory" -> "🎀"
                                        else -> "👔"
                                    },
                                    fontSize = 26.sp
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    piece.category.uppercase(),
                                    style = MikoTypography.Caption.copy(
                                        color = MikoColors.TextMuted,
                                        letterSpacing = 1.sp,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(piece.name, style = MikoTypography.Subtitle.copy(
                                    color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold
                                ))
                                Text(
                                    "LKR ${piece.priceLkr.formatThousands()} • ${piece.color}",
                                    style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
                                )
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MikoColors.PrimaryStart.copy(alpha = 0.12f))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { viewModel.shufflePiece(piece.category) }
                            ) {
                                Text("🔄", fontSize = 14.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MikoSecondaryButton(
                            text = "Save Outfit",
                            onClick = { /* TODO: save */ },
                            modifier = Modifier.weight(1f),
                            height = 48.dp
                        )
                        Box(Modifier.weight(1f)) {
                            MikoPrimaryButton(
                                text = "Add All to Cart",
                                trailingIcon = null,
                                onClick = { /* TODO: add to cart */ },
                                modifier = Modifier.fillMaxWidth(),
                                height = 48.dp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ─── Selectable chip ───
@Composable
private fun SelectChip(label: String, selected: Boolean, onClick: () -> Unit) {
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