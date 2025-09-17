package com.cesarvaliente.magicalzooai.api

data class ChatCompletionRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double = 0.7
) {
    data class Message(
        val role: String,
        val content: String
    )
}

data class ChatCompletionResponse(
    val choices: List<Choice>
) {
    data class Choice(
        val index: Int,
        val message: Message
    )

    data class Message(
        val role: String,
        val content: String
    )
}