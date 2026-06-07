package com.kittys.premium.features.social

import androidx.annotation.OptIn
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.material3.Text
// ════════════════════════════════════════════════════════════════
//   MIKO — Reels (merged)
//   • Real ExoPlayer video playback (auto play/pause on scroll)
//   • Rich social UI: follow, verified, music, comments, tagged product
// ════════════════════════════════════════════════════════════════

// ── Data model (self-contained) ──
data class VideoReel(
    val id                  : String,
    val vendorId            : String = "",
    val vendorName          : String,
    val vendorVerified      : Boolean = false,
    val videoUrl            : String = "",      // Supabase Storage URL; empty = gradient placeholder
    val thumbnailUrl        : String = "",
    val caption             : String = "",
    val musicTitle          : String = "Original audio",
    val likes               : Int = 0,
    val comments            : Int = 0,
    val shares              : Int = 0,
    val isLiked             : Boolean = false,
    val isFollowing         : Boolean = false,
    val taggedProductId     : String? = null,
    val taggedProductName   : String? = null,
    val taggedProductPriceLkr: Int? = null
)

// ── ViewModel ──
data class ReelsUiState(
    val reels    : List<VideoReel> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class SocialReelsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ReelsUiState())
    val uiState: StateFlow<ReelsUiState> = _uiState.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: replace with Supabase "reels" table query (video_url + product_id)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    reels = sampleReels()
                )
            }
        }
    }

    fun toggleLike(reelId: String) {
        _uiState.update { state ->
            state.copy(reels = state.reels.map { r ->
                if (r.id == reelId) r.copy(
                    isLiked = !r.isLiked,
                    likes   = if (r.isLiked) r.likes - 1 else r.likes + 1
                ) else r
            })
        }
    }

    fun toggleFollow(reelId: String) {
        _uiState.update { state ->
            state.copy(reels = state.reels.map { r ->
                if (r.id == reelId) r.copy(isFollowing = !r.isFollowing) else r
            })
        }
    }
}

// ════════════════════════════════════════════════════════════════
//   REELS SCREEN — vertical pager
// ════════════════════════════════════════════════════════════════

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReelsScreen(
    navController: NavController,
    viewModel    : SocialReelsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.reels.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().background(Color.Black)
        ) {
            CircularProgressIndicator(color = MikoColors.PrimaryStart)
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { uiState.reels.size })

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        VerticalPager(
            state    = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val reel = uiState.reels[page]
            ReelItem(
                reel           = reel,
                isPlaying      = pagerState.currentPage == page,
                onLike         = { viewModel.toggleLike(reel.id) },
                onFollow       = { viewModel.toggleFollow(reel.id) },
                onProductClick = { id -> navController.navigate(Screen.ProductDetail.createRoute(id)) },
                onComment      = { /* TODO: open comments sheet */ },
                onShare        = { /* TODO: share sheet */ }
            )
        }

        // ─── Top bar overlay ───
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.popBackStack() }
            ) {
                Text("←", fontSize = 22.sp, color = Color.White)
            }
            Spacer(Modifier.weight(1f))
            Text(
                "Reels",
                style = MikoTypography.Title.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.weight(1f))
            Box(Modifier.size(40.dp))
        }
    }
}

// ════════════════════════════════════════════════════════════════
//   SINGLE REEL ITEM
// ════════════════════════════════════════════════════════════════

@OptIn(UnstableApi::class)
@Composable
private fun ReelItem(
    reel          : VideoReel,
    isPlaying     : Boolean,
    onLike        : () -> Unit,
    onFollow      : () -> Unit,
    onProductClick: (String) -> Unit,
    onComment     : () -> Unit,
    onShare       : () -> Unit
) {
    val context = LocalContext.current

    // ExoPlayer only created when there's a real video URL
    val player = remember(reel.id) {
        if (reel.videoUrl.isNotEmpty()) {
            ExoPlayer.Builder(context).build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
                volume     = 1f
                setMediaItem(MediaItem.fromUri(reel.videoUrl))
                prepare()
            }
        } else null
    }

    LaunchedEffect(isPlaying) {
        player?.let { if (isPlaying) it.play() else it.pause() }
    }

    DisposableEffect(reel.id) {
        onDispose { player?.release() }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Video OR gradient placeholder ──
        if (player != null) {
            AndroidView(
                factory = {
                    PlayerView(it).apply {
                        this.player   = player
                        useController = false
                        resizeMode    = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MikoColors.PrimaryStart.copy(alpha = 0.55f),
                                MikoColors.PrimaryEnd.copy(alpha = 0.75f)
                            )
                        )
                    )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("▶", fontSize = 56.sp, color = Color.White.copy(alpha = 0.85f))
                    Spacer(Modifier.height(8.dp))
                    Text("Reel video", style = MikoTypography.Caption.copy(color = Color.White.copy(alpha = 0.7f)))
                }
            }
        }

        // ── Bottom gradient scrim ──
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                    )
                )
        )

        // ── Right action rail ──
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 14.dp, bottom = 36.dp)
        ) {
            ReelAction(
                symbol  = if (reel.isLiked) "♥" else "♡",
                count   = reel.likes,
                tint    = if (reel.isLiked) MikoColors.PrimaryStart else Color.White,
                onClick = onLike
            )
            ReelAction("💬", reel.comments, Color.White, onComment)
            ReelAction("↗", reel.shares, Color.White, onShare)
        }

        // ── Bottom-left info ──
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
                .padding(start = 16.dp, bottom = 36.dp)
        ) {
            // Vendor row + follow
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(MikoColors.GradientPrimary))
                ) { Text(reel.vendorName.take(1), color = Color.White, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(8.dp))
                Text(
                    reel.vendorName,
                    style = MikoTypography.Subtitle.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )
                if (reel.vendorVerified) {
                    Spacer(Modifier.width(4.dp))
                    Text("✓", color = MikoColors.PastelSky, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(10.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .then(
                            if (reel.isFollowing)
                                Modifier.border(1.dp, Color.White, RoundedCornerShape(50.dp))
                            else
                                Modifier.background(Brush.linearGradient(MikoColors.GradientButton))
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onFollow() }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (reel.isFollowing) "Following" else "Follow",
                        style = MikoTypography.Caption.copy(color = Color.White, fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Caption
            if (reel.caption.isNotEmpty()) {
                Text(
                    reel.caption,
                    style = MikoTypography.Body.copy(color = Color.White, lineHeight = 19.sp),
                    maxLines = 2
                )
                Spacer(Modifier.height(6.dp))
            }

            // Music
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("♫", color = Color.White, fontSize = 13.sp)
                Spacer(Modifier.width(6.dp))
                Text(
                    reel.musicTitle,
                    style = MikoTypography.Caption.copy(color = Color.White.copy(alpha = 0.85f)),
                    maxLines = 1
                )
            }

            // Tagged product chip
            if (reel.taggedProductId != null) {
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.95f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onProductClick(reel.taggedProductId) }
                        .padding(8.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(MikoColors.PastelPink)
                    ) { Text("🛍", fontSize = 18.sp) }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            reel.taggedProductName ?: "View product",
                            style = MikoTypography.Caption.copy(
                                color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold
                            ),
                            maxLines = 1
                        )
                        if (reel.taggedProductPriceLkr != null) {
                            Text(
                                "LKR ${formatThousands(reel.taggedProductPriceLkr)}",
                                style = MikoTypography.Caption.copy(
                                    color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold, fontSize = 11.sp
                                )
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.linearGradient(MikoColors.GradientButton))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Shop", style = MikoTypography.Caption.copy(color = Color.White, fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

@Composable
private fun ReelAction(symbol: String, count: Int, tint: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(46.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClick() }
        ) {
            Text(symbol, fontSize = 28.sp, color = tint)
        }
        Text(
            formatCount(count),
            style = MikoTypography.Caption.copy(color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
        )
    }
}

// ── Helpers ──
private fun formatCount(n: Int): String = when {
    n >= 1_000_000 -> "${n / 1_000_000}M"
    n >= 1_000     -> "${n / 1_000}K"
    else           -> "$n"
}

private fun formatThousands(n: Int): String =
    "%,d".format(n)

private fun sampleReels(): List<VideoReel> = listOf(
    VideoReel(
        id = "r1", vendorId = "v1", vendorName = "Little Stars Fashion", vendorVerified = true,
        caption = "New party dress collection just dropped! Perfect for birthdays 🎀",
        musicTitle = "Happy Vibes · Original audio",
        likes = 1240, comments = 89, shares = 45,
        taggedProductId = "p1", taggedProductName = "Lavender Bow Party Dress", taggedProductPriceLkr = 2850
    ),
    VideoReel(
        id = "r2", vendorId = "v3", vendorName = "Tiny Trends",
        caption = "Styling boys' denim for the school season 👖",
        musicTitle = "Cool Kids · Original audio",
        likes = 856, comments = 34, shares = 21,
        taggedProductId = "p3", taggedProductName = "Boys Denim Dungaree", taggedProductPriceLkr = 3200
    ),
    VideoReel(
        id = "r3", vendorId = "v4", vendorName = "Baby Boutique", vendorVerified = true,
        caption = "Softest organic cotton for your newborn 🤍 Unboxing!",
        musicTitle = "Sweet Dreams · Original audio",
        likes = 2103, comments = 156, shares = 98,
        taggedProductId = "p4", taggedProductName = "Newborn Soft Bodysuit Set", taggedProductPriceLkr = 2450
    )
)