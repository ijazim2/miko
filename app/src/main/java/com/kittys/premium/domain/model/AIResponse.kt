package com.kittys.premium.domain.model

// ════════════════════════════════════════════════════════════════
//   MIKO — AI Response model
//   What the Gemini service returns to the ViewModel
// ════════════════════════════════════════════════════════════════

data class AIResponse(
    val text: String,
    val productIds: List<String> = emptyList()
)