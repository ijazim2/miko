package com.kittys.premium.features.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kittys.premium.domain.model.AIResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ════════════════════════════════════════════════════════════════
//   MIKO — AI ViewModel
//   Manages the AI Stylist chat conversation + size/outfit helpers
// ════════════════════════════════════════════════════════════════

data class AIUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AIViewModel @Inject constructor(
    private val geminiService: GeminiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIUiState())
    val uiState: StateFlow<AIUiState> = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(welcomeMessages())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // ─── Initial greeting from MIKO AI ───
    private fun welcomeMessages(): List<ChatMessage> = listOf(
        ChatMessage(
            text = "Hi! I'm MIKO AI 💜 Your personal kids' fashion stylist. " +
                    "Ask me about outfits, sizes, or what's perfect for any occasion!",
            isAI = true
        )
    )

    // ─── Send a chat message ───
    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMsg = ChatMessage(text = text.trim(), isAI = false)
        _messages.update { it + userMsg }
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val response: AIResponse = geminiService.sendMessage(
                    userMessage = text.trim(),
                    history = _messages.value
                )
                val aiMsg = ChatMessage(
                    text = response.text,
                    isAI = true,
                    productIds = response.productIds
                )
                _messages.update { it + aiMsg }
            } catch (e: Exception) {
                val errMsg = ChatMessage(
                    text = "Sorry, I had trouble connecting. Please try again 😊",
                    isAI = true
                )
                _messages.update { it + errMsg }
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // ─── Quick suggestion prompts ───
    fun sendQuickPrompt(prompt: String) = sendMessage(prompt)

    fun clearChat() {
        _messages.value = welcomeMessages()
        _uiState.update { it.copy(error = null) }
    }
}