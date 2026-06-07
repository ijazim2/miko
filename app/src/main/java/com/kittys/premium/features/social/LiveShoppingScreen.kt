package com.kittys.premium.features.social

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

// ═══════════════════════════════════════════════════════
//   MIKO — LIVE SHOPPING DATA MODELS
// ═══════════════════════════════════════════════════════

data class LiveStream(
    val id             : String,
    val vendorName     : String,
    val streamUrl      : String,   // HLS or RTMP playback URL
    val viewerCount    : Int,
    val title          : String,
    val featuredProducts: List<LiveProduct>
)

data class LiveProduct(
    val id          : String,
    val name        : String,
    val priceLkr    : Int,
    val originalPrice: Int? = null,
    val imageUrl    : String,
    val livePromo   : Boolean = false   // shown with FLASH badge
)

data class LiveChatMessage(
    val id          : String = UUID.randomUUID().toString(),
    val authorName  : String,
    val text        : String,
    val isVendor    : Boolean = false,
    val isAi        : Boolean = false
)

data class LiveUiState(
    val stream      : LiveStream? = null,
    val chat        : List<LiveChatMessage> = emptyList(),
    val isLoading   : Boolean = false,
    val viewerCount : Int = 0
)

// ═══════════════════════════════════════════════════════
//   LIVE VIEWMODEL
// ═══════════════════════════════════════════════════════

@HiltViewModel
class LiveViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(LiveUiState())
    val uiState: StateFlow<LiveUiState> = _uiState.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    viewerCount = 247,
                    stream = LiveStream(
                        id          = "live1",
                        vendorName  = "Little Stars Fashion",
                        streamUrl   = "",   // wire HLS URL here when streaming
                        viewerCount = 247,
                        title       = "🔴 LIVE: Summer Collection Reveal!",
                        featuredProducts = listOf(
                            LiveProduct("lp1","Summer Floral Dress",   2400, 3200, "", livePromo = true),
                            LiveProduct("lp2","Boys Shorts Set",        1800, 2500, "", livePromo = true),
                            LiveProduct("lp3","School Uniform Pack",   3600, null, ""),
                            LiveProduct("lp4","Baby Romper 3-pack",    2100, 2800, "", livePromo = true)
                        )
                    )
                )
            }
            simulateLiveChat()
        }
    }

    private fun simulateLiveChat() {
        viewModelScope.launch {
            val seed = listOf(
                LiveChatMessage(authorName = "Little Stars", text = "Welcome everyone! 👋", isVendor = true),
                LiveChatMessage(authorName = "Amali P.",     text = "What sizes are available for the floral dress?"),
                LiveChatMessage(authorName = "Little Stars", text = "We have 2Y to 8Y available right now!", isVendor = true),
                LiveChatMessage(authorName = "Nisha R.",     text = "Just ordered the boys set! 🛒"),
                LiveChatMessage(authorName = "Miko AI",      text = "Based on your child's profile, size 4Y is recommended ✦", isAi = true)
            )
            seed.forEach { msg ->
                delay(2200)
                _uiState.update { it.copy(chat = it.chat + msg) }
            }
        }
    }

    fun sendChat(text: String) {
        if (text.isBlank()) return
        _uiState.update {
            it.copy(chat = it.chat + LiveChatMessage(authorName = "You", text = text))
        }
    }
}

// ═══════════════════════════════════════════════════════
//   LIVE SHOPPING SCREEN
// ═══════════════════════════════════════════════════════

@OptIn(UnstableApi::class)
@Composable
fun LiveShoppingScreen(
    navController: NavController,
    viewModel    : LiveViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var chatInput by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // ── Video player ──
        if (uiState.stream?.streamUrl?.isNotEmpty() == true) {
            val player = remember {
                ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(uiState.stream!!.streamUrl))
                    prepare()
                    play()
                }
            }
            DisposableEffect(Unit) { onDispose { player.release() } }
            AndroidView(
                factory = {
                    PlayerView(it).apply {
                        this.player = player
                        useController = false
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(Color(0xFF1A1F2E), Color(0xFF2D3E6B))))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("📹", fontSize = 64.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Stream loading…",
                        style = MikoTypography.titleSmall.copy(color = Color.White.copy(alpha = 0.7f))
                    )
                }
            }
        }

        // ── Dark overlay (top) ──
        Box(
            modifier = Modifier
                .fillMaxWidth().height(160.dp).align(Alignment.TopCenter)
                .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)))
        )

        // ── Top bar (vendor + viewers + close) ──
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .fillMaxWidth()
        ) {
            // LIVE badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MikoColors.Error)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "LIVE",
                        style = MikoTypography.labelSmall.copy(
                            color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp
                        )
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    uiState.stream?.vendorName ?: "Vendor",
                    style = MikoTypography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )
                Text(
                    "👁  ${uiState.viewerCount} watching",
                    style = MikoTypography.labelSmall.copy(color = Color.White.copy(alpha = 0.85f))
                )
            }
            // Close button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp).clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.popBackStack() }
            ) {
                Text("✕", color = Color.White, fontSize = 14.sp)
            }
        }

        // ── Featured products (right side) ──
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
                .width(110.dp)
                .heightIn(max = 320.dp)
        ) {
            items(uiState.stream?.featuredProducts ?: emptyList()) { product ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {}
                        .padding(8.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(60.dp).clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Text("👗", fontSize = 26.sp)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        product.name,
                        style = MikoTypography.labelSmall.copy(color = Color.White, fontSize = 10.sp),
                        maxLines = 2
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "LKR ${product.priceLkr}",
                        style = MikoTypography.labelMedium.copy(
                            color = MikoColors.AquaAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp
                        )
                    )
                    if (product.livePromo) {
                        Spacer(Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MikoColors.Error)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                "⚡ LIVE",
                                style = MikoTypography.labelSmall.copy(
                                    color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        // ── Chat panel + input (bottom) ──
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 220.dp)
                    .padding(horizontal = 14.dp)
            ) {
                items(uiState.chat) { msg ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            msg.authorName,
                            style = MikoTypography.labelSmall.copy(
                                color = when {
                                    msg.isVendor -> MikoColors.AquaAccent
                                    msg.isAi     -> MikoColors.VioletAccent
                                    else         -> Color.White.copy(alpha = 0.85f)
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                        if (msg.isVendor) {
                            Spacer(Modifier.width(3.dp))
                            Text("✓", color = MikoColors.AquaAccent, fontSize = 10.sp)
                        }
                        Spacer(Modifier.width(6.dp))
                        Text(
                            msg.text,
                            style = MikoTypography.labelSmall.copy(color = Color.White, fontSize = 12.sp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            // Input row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White.copy(alpha = 0.20f))
                        .padding(horizontal = 16.dp)
                ) {
                    BasicTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        singleLine = true,
                        textStyle = MikoTypography.bodyMedium.copy(color = Color.White),
                        modifier = Modifier.weight(1f),
                        decorationBox = { inner ->
                            if (chatInput.isEmpty()) {
                                Text(
                                    "Say something nice...",
                                    style = MikoTypography.bodyMedium.copy(color = Color.White.copy(alpha = 0.6f))
                                )
                            }
                            inner()
                        }
                    )
                }
                Spacer(Modifier.width(10.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp).clip(CircleShape)
                        .background(Brush.linearGradient(MikoColors.GradientBlue))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { viewModel.sendChat(chatInput); chatInput = "" }
                ) {
                    Text("➤", color = Color.White, fontSize = 16.sp)
                }
                Spacer(Modifier.width(8.dp))
                Text("❤️", fontSize = 24.sp)
            }
        }
    }
}