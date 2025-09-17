package com.cesarvaliente.magicalzooai.repository

import com.cesarvaliente.magicalzooai.api.AIClient

class ChatRepository(private val aiClient: AIClient) {
    suspend fun generateResponse(animalName: String, animalType: String, message: String): String {
        return aiClient.generateChatResponse(animalName, animalType, message)
    }
}
