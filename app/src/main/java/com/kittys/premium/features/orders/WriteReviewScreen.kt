package com.kittys.premium.features.orders

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Write Review Screen
//   Star rating + photos + size-accuracy + comment
// ════════════════════════════════════════════════════════════════

@Composable
fun WriteReviewScreen(
    orderId: String,
    navController: NavController
) {
    var rating by remember { mutableStateOf(0) }
    var sizeAccuracy by remember { mutableStateOf<String?>(null) }
    var comment by remember { mutableStateOf("") }
    val photos = remember { mutableStateListOf<String>() }
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    val canSubmit = rating > 0 && comment.trim().length >= 5 && !isSubmitting

    Box(modifier = Modifier.fillMaxSize().background(MikoColors.Background)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ─── Top Bar ───
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(40.dp).neuRaised(cornerRadius = 20.dp, shadowOffset = 4.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { navController.popBackStack() }
                ) { Text("←", fontSize = 18.sp, color = MikoColors.TextPrimary) }
                Spacer(Modifier.width(12.dp))
                Text("Write a Review", style = MikoTypography.Title.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // ─── Product summary ───
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .neuRaised(cornerRadius = 16.dp, shadowOffset = 6.dp)
                        .padding(14.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp)).background(MikoColors.PastelPink)
                    ) { Text("🖼", fontSize = 24.sp) }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Lavender Bow Party Dress", style = MikoTypography.Subtitle.copy(
                            color = MikoColors.TextPrimary, fontWeight = FontWeight.SemiBold
                        ), maxLines = 1)
                        Text("Order #$orderId", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ─── Star rating ───
                Text("How would you rate it?", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    (1..5).forEach { star ->
                        Text(
                            if (star <= rating) "★" else "☆",
                            fontSize = 42.sp,
                            color = if (star <= rating) MikoColors.StarYellow else MikoColors.BorderMedium,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { rating = star }
                        )
                    }
                }
                if (rating > 0) {
                    Text(
                        when (rating) {
                            5 -> "Love it! 💜"; 4 -> "Really good 😊"; 3 -> "It's okay"
                            2 -> "Not great"; else -> "Disappointed"
                        },
                        style = MikoTypography.Caption.copy(color = MikoColors.PrimaryEnd, fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                // ─── Size accuracy ───
                Text("How was the size?", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Runs small", "True to size", "Runs large").forEach { option ->
                        val selected = sizeAccuracy == option
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .then(
                                    if (selected) Modifier.background(MikoColors.PastelPink).border(1.5.dp, MikoColors.PrimaryStart, RoundedCornerShape(12.dp))
                                    else Modifier.background(MikoColors.SurfaceVariant).border(1.dp, MikoColors.BorderLight, RoundedCornerShape(12.dp))
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { sizeAccuracy = option }
                                .padding(vertical = 12.dp, horizontal = 8.dp)
                        ) {
                            Text(
                                option,
                                style = MikoTypography.Caption.copy(
                                    color = if (selected) MikoColors.PrimaryEnd else MikoColors.TextSecondary,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ─── Comment ───
                Text("Share your thoughts", style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                ))
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 110.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MikoColors.SurfaceVariant)
                        .border(1.dp, MikoColors.BorderLight, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    BasicTextField(
                        value = comment,
                        onValueChange = { if (it.length <= 500) comment = it },
                        textStyle = TextStyle(fontSize = 14.sp, color = MikoColors.TextPrimary),
                        cursorBrush = SolidColor(MikoColors.PrimaryStart),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { inner ->
                            if (comment.isEmpty()) Text("What did you and your child think? Quality, fit, value…",
                                style = MikoTypography.Body.copy(color = MikoColors.TextMuted))
                            inner()
                        }
                    )
                }
                Text(
                    "${comment.length}/500",
                    style = MikoTypography.Caption.copy(color = MikoColors.TextMuted),
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                )

                Spacer(Modifier.height(20.dp))

                // ─── Photos ───
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Add photos", style = MikoTypography.Subtitle.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                    ), modifier = Modifier.weight(1f))
                    Text("Optional", style = MikoTypography.Caption.copy(color = MikoColors.TextMuted))
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    photos.forEach { uri ->
                        Box(
                            modifier = Modifier.size(72.dp).clip(RoundedCornerShape(12.dp)).background(MikoColors.PastelPink)
                        ) {
                            Box(Modifier.fillMaxSize(), Alignment.Center) { Text("🖼", fontSize = 24.sp) }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.align(Alignment.TopEnd).padding(3.dp).size(20.dp)
                                    .clip(CircleShape).background(MikoColors.Error)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { photos.remove(uri) }
                            ) { Text("×", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        }
                    }
                    if (photos.size < 4) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(72.dp).clip(RoundedCornerShape(12.dp))
                                .background(MikoColors.SurfaceVariant)
                                .border(1.5.dp, MikoColors.PrimaryStart, RoundedCornerShape(12.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { photos.add("rev_${System.currentTimeMillis()}") }
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📷", fontSize = 20.sp)
                                Text("Add", style = MikoTypography.Caption.copy(color = MikoColors.PrimaryStart, fontSize = 9.sp))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                // ─── Submit ───
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth().height(54.dp).clip(RoundedCornerShape(16.dp))
                        .then(
                            if (canSubmit) Modifier.background(Brush.linearGradient(MikoColors.GradientButton))
                            else Modifier.background(MikoColors.BorderMedium)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = canSubmit
                        ) {
                            isSubmitting = true
                            // TODO: submit review to repository
                            isSubmitting = false
                            showSuccess = true
                        }
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
                    } else {
                        Text("Submit Review", style = MikoTypography.Button.copy(
                            color = Color.White, fontWeight = FontWeight.Bold
                        ))
                    }
                }

                Spacer(Modifier.height(40.dp))
            }
        }

        // ─── Success overlay ───
        if (showSuccess) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().background(MikoColors.Overlay).clickable(enabled = false) {}
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp).clip(RoundedCornerShape(24.dp)).background(MikoColors.Surface).padding(28.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(80.dp).clip(CircleShape).background(MikoColors.SuccessSoft)
                    ) { Text("⭐", fontSize = 36.sp) }
                    Spacer(Modifier.height(18.dp))
                    Text("Thank You!", style = MikoTypography.Headline.copy(
                        color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                    ))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Your review helps other parents shop with confidence. You earned 50 loyalty points! 💜",
                        style = MikoTypography.Body.copy(color = MikoColors.TextSecondary, lineHeight = 20.sp),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(Modifier.height(24.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(16.dp))
                            .background(Brush.linearGradient(MikoColors.GradientButton))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { showSuccess = false; navController.popBackStack() }
                    ) {
                        Text("Done", style = MikoTypography.Button.copy(color = Color.White, fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}