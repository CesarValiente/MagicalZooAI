package com.cesarvaliente.magicalzooai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cesarvaliente.magicalzooai.model.ChatMessage
import com.cesarvaliente.magicalzooai.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    fun sendMessage(animalName: String, animalType: String) {
        val currentInput = _inputText.value
        if (currentInput.isBlank()) return

        viewModelScope.launch {
            // Add user message
            val userMessage = ChatMessage(currentInput, true)
            _messages.value = _messages.value + userMessage

            // Clear input immediately
            _inputText.value = ""

            // Get AI response
            val response = chatRepository.generateResponse(animalName, animalType, currentInput)
            val aiMessage = ChatMessage(response, false)
            _messages.value = _messages.value + aiMessage
        }
    }

    fun addInitialGreeting(animalName: String, kidName: String) {
        val greeting = "Hello! I'm $animalName. How can I help you today, $kidName?"
        _messages.value = listOf(ChatMessage(greeting, false))
    }

    class Factory(private val chatRepository: ChatRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                return ChatViewModel(chatRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
