package com.kittys.premium.features.wardrobe

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
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
import com.kittys.premium.core.ui.components.MikoTopBar
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.kittys.premium.core.ui.components.*
import com.kittys.premium.core.ui.theme.MikoTypography

// ═══════════════════════════════════════════════════════
//   WARDROBE DATA MODELS
// ═══════════════════════════════════════════════════════

data class WardrobeItem(
    val id        : String,
    val productId : String,
    val name      : String,
    val category  : String,
    val color     : String,
    val size      : String,
    val imageUrl  : String,
    val wornCount : Int = 0,
    val lastWorn  : String? = null
)

data class WardrobeUiState(
    val items           : List<WardrobeItem> = emptyList(),
    val filterCategory  : String = "All",
    val filterChild     : String = "All Children",
    val isLoading       : Boolean = false
)

@HiltViewModel
class WardrobeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(WardrobeUiState())
    val uiState: StateFlow<WardrobeUiState> = _uiState.asStateFlow()

    private var allItems: List<WardrobeItem> = emptyList()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: replace with Supabase "wardrobe" table
            allItems = listOf(
                WardrobeItem("w1","p1","Floral Dress","Dress","Pink","4Y","",  wornCount = 12, lastWorn = "2 days ago"),
                WardrobeItem("w2","p2","School Uniform","Top","White","4Y","", wornCount = 24, lastWorn = "yesterday"),
                WardrobeItem("w3","p3","Denim Shorts","Bottom","Blue","4Y","", wornCount = 8,  lastWorn = "1 week ago"),
                WardrobeItem("w4","p4","Sneakers","Shoes","White","26","",     wornCount = 15, lastWorn = "yesterday"),
                WardrobeItem("w5","p5","Party Dress","Dress","Pink","4Y","",   wornCount = 2,  lastWorn = "1 month ago"),
                WardrobeItem("w6","p6","Cardigan","Outerwear","Beige","4Y","", wornCount = 5,  lastWorn = "2 weeks ago"),
                WardrobeItem("w7","p7","Hair Bow","Accessory","Pink","-","",   wornCount = 18, lastWorn = "today")
            )
            _uiState.update { it.copy(items = allItems, isLoading = false) }
        }
    }

    fun filterCategory(cat: String) {
        _uiState.update { state ->
            state.copy(
                filterCategory = cat,
                items = if (cat == "All") allItems else allItems.filter { it.category == cat }
            )
        }
    }
}

// ═══════════════════════════════════════════════════════
//   WARDROBE SCREEN
// ═══════════════════════════════════════════════════════

@Composable
fun WardrobeScreen(
    navController: NavController,
    viewModel    : WardrobeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories = listOf("All","Dress","Top","Bottom","Shoes","Outerwear","Accessory")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.NeuBackground)
    ) {
        MikoTopBar(
            title = "Wardrobe 👗",
            onBackClick = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ── Header stats ──
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                WardrobeStat("Total Items", "${uiState.items.size}", Modifier.weight(1f))
                Spacer(Modifier.width(10.dp))
                WardrobeStat("Most Worn",   "👕 Uniform", Modifier.weight(1f))
                Spacer(Modifier.width(10.dp))
                WardrobeStat("This Month",  "23 outfits", Modifier.weight(1f))
            }

            // ── Filter chips ──
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(categories) { cat ->
                    CategoryChip(
                        text = cat,
                        selected = uiState.filterCategory == cat,
                        onClick = { viewModel.filterCategory(cat) }
                    )
                }
            }

            // ── Grid of wardrobe items ──
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.items, key = { it.id }) { item ->
                    WardrobeItemCard(item)
                }
            }
        }
    }
}

@Composable
private fun WardrobeStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .neuRaised(cornerRadius = 14.dp, shadowOffset = 5.dp)
            .padding(12.dp)
    ) {
        Text(
            value,
            style = MikoTypography.titleSmall.copy(
                brush = Brush.linearGradient(MikoColors.GradientBlue),
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(2.dp))
        Text(label, style = MikoTypography.labelSmall.copy(color = MikoColors.TextMuted, fontSize = 10.sp))
    }
}

@Composable
private fun WardrobeItemCard(item: WardrobeItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {}
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(MikoColors.NeuBackground)
        ) {
            // Placeholder emoji per category
            Text(
                when (item.category) {
                    "Dress"     -> "👗"
                    "Top"       -> "👕"
                    "Bottom"    -> "👖"
                    "Shoes"     -> "👟"
                    "Outerwear" -> "🧥"
                    "Accessory" -> "🎀"
                    else        -> "👔"
                },
                fontSize = 56.sp,
                modifier = Modifier.align(Alignment.Center)
            )

            // Worn count badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    "${item.wornCount}×",
                    style = MikoTypography.labelSmall.copy(
                        color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Column(modifier = Modifier.padding(12.dp)) {
            Text(item.name, style = MikoTypography.titleSmall, maxLines = 1)
            Text(
                "${item.color} • Size ${item.size}",
                style = MikoTypography.bodySmall.copy(color = MikoColors.TextMuted)
            )
            item.lastWorn?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Last worn: $it",
                    style = MikoTypography.labelSmall.copy(color = MikoColors.TextMuted, fontSize = 10.sp)
                )
            }
        }
    }
}
