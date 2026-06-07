package com.kittys.premium.features.ai

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kittys.premium.core.navigation.Screen
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — AI Stylist Screen (Gemini-powered chat)
//   NOTE: ChatMessage is defined in GeminiService.kt — not here.
// ════════════════════════════════════════════════════════════════

@Composable
fun AIStylistScreen(
    navController: NavController,
    viewModel: AIViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }

    val quickPrompts = listOf(
        "Find a birthday outfit for a 5-year-old girl 🎂",
        "School uniforms under LKR 2,000 👩‍🏫",
        "Cool summer wear for baby boys ☀️",
        "Party dresses for age 8–10 💃",
        "Matching outfits for twins 👯"
    )

    LaunchedEffect(messages.size, uiState.isLoading) {
        val target = messages.size - 1 + if (uiState.isLoading) 1 else 0
        if (target >= 0) listState.animateScrollToItem(target.coerceAtLeast(0))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background)
    ) {

        // ─── AI Top Bar ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(MikoColors.GradientButton))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .align(Alignment.CenterStart)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.popBackStack() }
            ) {
                Text("←", fontSize = 18.sp, color = Color.White)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Text("AI", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        "MIKO AI",
                        style = MikoTypography.Title.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4ADE80))
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            "Online • Powered by Gemini",
                            style = MikoTypography.Caption.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Text(
                "Clear",
                style = MikoTypography.Caption.copy(color = Color.White.copy(alpha = 0.8f)),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { viewModel.clearChat() }
            )
        }

        // ─── Chat Messages ───
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {

            // Quick prompts (only when just the greeting is showing)
            if (messages.size <= 1) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Try asking:",
                            style = MikoTypography.Caption.copy(
                                color = MikoColors.TextMuted,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                        )
                        quickPrompts.forEach { prompt ->
                            QuickPromptChip(text = prompt) {
                                viewModel.sendQuickPrompt(prompt)
                            }
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { message ->
                ChatBubble(
                    message = message,
                    onProductClick = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    }
                )
            }

            if (uiState.isLoading) {
                item { AITypingIndicator() }
            }
        }

        // ─── Input Bar ───
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MikoColors.Background)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Input field
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(26.dp))
                        .background(MikoColors.SurfaceVariant)
                        .border(1.dp, MikoColors.BorderLight, RoundedCornerShape(26.dp))
                        .padding(horizontal = 16.dp)
                ) {
                    BasicTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = MikoColors.TextPrimary
                        ),
                        cursorBrush = SolidColor(MikoColors.PrimaryStart),
                        modifier = Modifier.weight(1f).padding(vertical = 14.dp),
                        decorationBox = { inner ->
                            if (inputText.isEmpty()) {
                                Text(
                                    "Ask MIKO AI about fashion…",
                                    style = MikoTypography.Body.copy(color = MikoColors.TextMuted)
                                )
                            }
                            inner()
                        }
                    )
                }

                // Send button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank())
                                Brush.linearGradient(MikoColors.GradientButton)
                            else
                                Brush.linearGradient(listOf(MikoColors.BorderMedium, MikoColors.BorderMedium))
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = inputText.isNotBlank()
                        ) {
                            if (inputText.isNotBlank()) {
                                val msg = inputText.trim()
                                inputText = ""
                                viewModel.sendMessage(msg)
                            }
                        }
                ) {
                    Text("→", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Powered by Google Gemini AI",
                style = MikoTypography.Caption.copy(
                    color = MikoColors.TextMuted,
                    fontSize = 10.sp
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

// ─── Chat Bubble ───
@Composable
fun ChatBubble(
    message: ChatMessage,
    onProductClick: (String) -> Unit
) {
    Row(
        horizontalArrangement = if (message.isAI) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (message.isAI) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(MikoColors.GradientButton))
            ) { Text("AI", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    if (message.isAI)
                        RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
                    else
                        RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
                )
                .then(
                    if (message.isAI)
                        Modifier.background(MikoColors.Surface).neuRaised(cornerRadius = 18.dp, shadowOffset = 4.dp)
                    else
                        Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                )
                .padding(12.dp)
        ) {
            Text(
                message.text,
                style = MikoTypography.Body.copy(
                    color = if (message.isAI) MikoColors.TextPrimary else Color.White,
                    lineHeight = 20.sp
                )
            )

            // Product suggestion chips
            if (message.productIds.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                message.productIds.forEach { productId ->
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MikoColors.PastelPink)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onProductClick(productId) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "View product →",
                            style = MikoTypography.Caption.copy(
                                color = MikoColors.PrimaryEnd,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }

        if (!message.isAI) Spacer(Modifier.width(8.dp))
    }
}

// ─── Quick Prompt Chip ───
@Composable
fun QuickPromptChip(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .neuRaised(cornerRadius = 14.dp, shadowOffset = 5.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text,
            style = MikoTypography.Body.copy(color = MikoColors.TextSecondary)
        )
    }
}

// ─── Typing Indicator ───
@Composable
fun AITypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(MikoColors.GradientButton))
        ) { Text("AI", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
                .background(MikoColors.Surface)
                .neuRaised(cornerRadius = 18.dp, shadowOffset = 4.dp)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                repeat(3) { index ->
                    val dotAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(400, delayMillis = index * 150),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot_$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .alpha(dotAlpha)
                            .background(Brush.linearGradient(MikoColors.GradientPrimary))
                    )
                }
            }
        }
    }
}