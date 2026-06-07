package com.kittys.premium.features.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.kittys.premium.BuildConfig
import com.kittys.premium.domain.model.AIResponse
import javax.inject.Inject
import javax.inject.Singleton

// ════════════════════════════════════════════════════════════════
//   MIKO — Gemini AI Service
//   Powers the AI Stylist chat, size prediction, outfit generation
// ════════════════════════════════════════════════════════════════

@Singleton
class GeminiService @Inject constructor() {

    // ─── System context injected into every conversation ───
    private val systemPrompt = """
        You are MIKO AI, the friendly and expert AI stylist for "MIKO" —
        Sri Lanka's premium kids and family fashion marketplace.

        Your personality:
        - Warm, helpful, and knowledgeable about kids fashion
        - You understand Sri Lankan culture, weather, and occasions
          (Avurudu, Ramadan, school uniforms, tropical climate)
        - You always give practical, size-appropriate suggestions
        - You respond in English but understand Sinhala/Tamil context

        Your capabilities:
        - Recommend outfits by age, gender, occasion, season, and budget (LKR)
        - Predict clothing sizes from age/height/weight
        - Suggest matching family outfits
        - Explain fabric care for tropical weather
        - Help with gift ideas and school essentials

        Response format:
        - Keep answers concise (2-4 sentences for simple queries)
        - Use emojis naturally (not excessively)
        - For product searches, suggest categories like:
          "Girls Party Dresses age 5", "Boys School Uniform size 8-9Y", etc.
        - If asked about price, reference LKR currency
        - Never recommend products outside MIKO

        Start responses with warmth. End with a helpful follow-up question
        if appropriate.
    """.trimIndent()

    private val model = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
        systemInstruction = content { text(systemPrompt) }
    )

    // ─── Send a message with full conversation history ───
    suspend fun sendMessage(
        userMessage: String,
        history: List<ChatMessage>
    ): AIResponse {
        val chat = model.startChat(
            history = history.map { msg ->
                content(role = if (msg.isAI) "model" else "user") {
                    text(msg.text)
                }
            }
        )

        val response = chat.sendMessage(userMessage)
        val text = response.text ?: "Sorry, I couldn't understand that. Can you rephrase?"

        return AIResponse(text = text, productIds = extractProductHints(text))
    }

    // ─── Size prediction ───
    suspend fun predictSize(
        ageYears: Int,
        heightCm: Int?,
        weightKg: Float?,
        gender: String,
        brand: String? = null
    ): String {
        val prompt = buildString {
            append("Predict the clothing size for a child:\n")
            append("- Age: $ageYears years\n")
            if (heightCm != null) append("- Height: ${heightCm}cm\n")
            if (weightKg != null) append("- Weight: ${weightKg}kg\n")
            append("- Gender: $gender\n")
            if (brand != null) append("- Brand: $brand\n")
            append("\nRespond with ONLY the size label (e.g. '4-5Y' or '110cm') and one line of advice.")
        }
        val response = model.generateContent(prompt)
        return response.text ?: "Recommended size: ${ageYears}-${ageYears + 1}Y"
    }

    // ─── Outfit generator ───
    suspend fun generateOutfit(
        occasion: String,
        ageYears: Int,
        gender: String,
        budgetLkr: Int? = null
    ): String {
        val prompt = buildString {
            append("Generate a complete kids outfit for:\n")
            append("- Occasion: $occasion\n")
            append("- Age: $ageYears years, Gender: $gender\n")
            if (budgetLkr != null) append("- Budget: LKR $budgetLkr\n")
            append("\nSuggest: top, bottom, shoes, accessories with style tips.")
        }
        val response = model.generateContent(prompt)
        return response.text ?: "Could not generate outfit. Please try again."
    }

    // ─── Extract product category hints from AI response ───
    private fun extractProductHints(text: String): List<String> {
        // In production, parse AI response for structured product intents (vector search later)
        return emptyList()
    }
}


// ════════════════════════════════════════════════════════════════
//   CHAT MESSAGE MODEL
//   (lives here so both the service and the ViewModel can use it)
// ════════════════════════════════════════════════════════════════

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isAI: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val productIds: List<String> = emptyList(),
    val isTyping: Boolean = false
)