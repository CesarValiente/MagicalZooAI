package com.cesarvaliente.magicalzooai.repository

import com.cesarvaliente.magicalzooai.api.AIClient

class ChatRepository(private val aiClient: AIClient) {
    suspend fun generateResponseWithHistory(
        animalName: String,
        animalType: String,
        topic: String,
        messages: List<com.cesarvaliente.magicalzooai.api.ChatCompletionRequest.Message>
    ): String {
        return aiClient.generateChatResponseWithHistory(animalName, animalType, topic, messages)
    }
}
