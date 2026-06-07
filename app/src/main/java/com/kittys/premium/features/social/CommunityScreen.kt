package com.kittys.premium.features.social

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.domain.model.CommunityPost
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography
import androidx.compose.material3.*
// ════════════════════════════════════════════════════════════════
//   MIKO — Community Screen
//   Parents share looks & tips · like · comment · share · shop tags
// ════════════════════════════════════════════════════════════════

@Composable
fun CommunityScreen(navController: NavController) {

    val posts = remember { mutableStateListOf(*samplePosts().toTypedArray()) }
    val tabs = listOf("For You", "Following", "Tips", "Reviews")
    var selectedTab by remember { mutableStateOf(0) }

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
            Text(
                "Community",
                style = MikoTypography.Title.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )
            // Reels shortcut
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MikoColors.SurfaceVariant)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.navigate(Screen.Reels.route) }
            ) { Text("▶", fontSize = 15.sp, color = MikoColors.PrimaryEnd) }
            Spacer(Modifier.width(8.dp))
            // Live shortcut
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(MikoColors.Error)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.navigate(Screen.LiveShopping.route) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text("● LIVE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // ─── Tabs ───
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            items(tabs.size) { i ->
                val selected = selectedTab == i
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .then(
                            if (selected) Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                            else Modifier.background(MikoColors.SurfaceVariant)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { selectedTab = i }
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Text(
                        tabs[i],
                        style = MikoTypography.Caption.copy(
                            color = if (selected) Color.White else MikoColors.TextSecondary,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // ─── Feed ───
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Create post prompt
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .neuRaised(cornerRadius = 16.dp, shadowOffset = 5.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { /* TODO: create post */ }
                        .padding(14.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                            .background(Brush.linearGradient(MikoColors.GradientPrimary))
                    ) { Text("A", color = Color.White, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Share your little one's look…",
                        style = MikoTypography.Body.copy(color = MikoColors.TextMuted),
                        modifier = Modifier.weight(1f)
                    )
                    Text("📷", fontSize = 18.sp)
                }
            }

            itemsIndexed(posts) { index, post ->
                CommunityPostCard(
                    post = post,
                    onLike = {
                        posts[index] = post.copy(
                            isLiked = !post.isLiked,
                            likes = if (post.isLiked) post.likes - 1 else post.likes + 1
                        )
                    },
                    onProductClick = { id -> navController.navigate(Screen.ProductDetail.createRoute(id)) }
                )
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

// itemsIndexed helper for SnapshotStateList
private inline fun <T> androidx.compose.foundation.lazy.LazyListScope.itemsIndexed(
    items: List<T>,
    crossinline itemContent: @androidx.compose.runtime.Composable (index: Int, item: T) -> Unit
) {
    items(items.size) { index -> itemContent(index, items[index]) }
}

@Composable
private fun CommunityPostCard(
    post: CommunityPost,
    onLike: () -> Unit,
    onProductClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .neuRaised(cornerRadius = 18.dp, shadowOffset = 6.dp)
            .padding(16.dp)
    ) {
        // ─── Author row ───
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(40.dp).clip(CircleShape)
                    .background(Brush.linearGradient(MikoColors.GradientPrimary))
            ) { Text(post.authorName.take(1), color = Color.White, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(post.authorName, style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                    ))
                    if (post.authorBadge != null) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(6.dp))
                                .background(MikoColors.PastelLavender)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(post.authorBadge, style = MikoTypography.Caption.copy(
                                color = MikoColors.AccentBright, fontSize = 9.sp, fontWeight = FontWeight.SemiBold
                            ))
                        }
                    }
                }
                Text(post.timeAgo, style = MikoTypography.Caption.copy(color = MikoColors.TextMuted, fontSize = 10.sp))
            }
            Text("⋯", fontSize = 18.sp, color = MikoColors.TextMuted)
        }

        Spacer(Modifier.height(12.dp))

        // ─── Caption ───
        Text(
            post.caption,
            style = MikoTypography.Body.copy(color = MikoColors.TextPrimary, lineHeight = 21.sp)
        )

        Spacer(Modifier.height(12.dp))

        // ─── Image(s) placeholder ───
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(MikoColors.PastelPink, MikoColors.PastelLavender)))
        ) {
            Text("🖼", fontSize = 48.sp)
            if (post.imageCount > 1) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("1/${post.imageCount}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // ─── Tagged product ───
        if (post.taggedProductId != null) {
            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MikoColors.SurfaceVariant)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onProductClick(post.taggedProductId) }
                    .padding(10.dp)
            ) {
                Text("🛍", fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Shop: ${post.taggedProductName}",
                    style = MikoTypography.Caption.copy(
                        color = MikoColors.PrimaryEnd, fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
                Text("→", color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(14.dp))
        HorizontalDivider(color = MikoColors.Divider.copy(alpha = 0.5f))
        Spacer(Modifier.height(10.dp))

        // ─── Engagement row ───
        Row(verticalAlignment = Alignment.CenterVertically) {
            EngagementButton(
                symbol = if (post.isLiked) "♥" else "♡",
                count = post.likes,
                tint = if (post.isLiked) MikoColors.PrimaryStart else MikoColors.TextSecondary,
                onClick = onLike
            )
            Spacer(Modifier.width(20.dp))
            EngagementButton("💬", post.comments, MikoColors.TextSecondary) { /* TODO comments */ }
            Spacer(Modifier.width(20.dp))
            EngagementButton("↗", post.shares, MikoColors.TextSecondary) { /* TODO share */ }
            Spacer(Modifier.weight(1f))
            Text("🔖", fontSize = 18.sp, color = MikoColors.TextMuted)
        }
    }
}

@Composable
private fun EngagementButton(symbol: String, count: Int, tint: Color, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { onClick() }
    ) {
        Text(symbol, fontSize = 19.sp, color = tint)
        Spacer(Modifier.width(5.dp))
        Text(
            "$count",
            style = MikoTypography.Caption.copy(color = MikoColors.TextSecondary, fontWeight = FontWeight.Medium)
        )
    }
}

private fun samplePosts(): List<CommunityPost> = listOf(
    CommunityPost(
        id = "c1", authorName = "Nimali Fernando", authorBadge = "Verified Parent",
        timeAgo = "2 hours ago",
        caption = "My daughter's birthday outfit from MIKO! The quality is amazing and the fit was perfect thanks to the AI size tip 💜🎀",
        imageCount = 3, likes = 234, comments = 28, shares = 12,
        taggedProductId = "p1", taggedProductName = "Lavender Bow Party Dress"
    ),
    CommunityPost(
        id = "c2", authorName = "Tharushi M.", authorBadge = "Top Reviewer",
        timeAgo = "5 hours ago",
        caption = "Tip for new moms: always size up for newborns, they grow so fast! These organic bodysuits lasted us months. Highly recommend 🤍",
        imageCount = 1, likes = 567, comments = 73, shares = 41,
        taggedProductId = "p4", taggedProductName = "Newborn Soft Bodysuit Set"
    ),
    CommunityPost(
        id = "c3", authorName = "Roshan P.",
        timeAgo = "1 day ago",
        caption = "School season sorted! Got the denim dungarees for my son. Super durable for an active 5-year-old 👦",
        imageCount = 2, likes = 145, comments = 19, shares = 8,
        taggedProductId = "p3", taggedProductName = "Boys Denim Dungaree"
    )
)