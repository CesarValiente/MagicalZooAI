package com.cesarvaliente.magicalzooai.api

import android.util.Log
import com.cesarvaliente.magicalzooai.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AIClient private constructor() {
    private val api: OpenAIApi

    init {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(OpenAIApi::class.java)
    }

    suspend fun generateChatResponse(animalName: String, animalType: String, userMessage: String): String {
        val systemPrompt = "You are $animalName, a magical ${animalType.lowercase()} who is very friendly and loves to chat with kids. " +
                "Keep responses simple, safe, and engaging for children. Stay in character as $animalName the ${animalType.lowercase()}. " +
                "Keep responses short, about 1-2 sentences."

        val request = ChatCompletionRequest(
            model = "gpt-4.1-nano",  // Changed from "gpt-5-nano" to the correct model name
            messages = listOf(
                ChatCompletionRequest.Message("system", systemPrompt),
                ChatCompletionRequest.Message("user", userMessage)
            )
        )

        return try {
            val response = withContext(Dispatchers.IO) {
                api.createChatCompletion(request).execute()
            }

            if (response.isSuccessful) {
                response.body()?.choices?.firstOrNull()?.message?.content
                    ?: "I'm sorry, I couldn't understand that. Could you try again?"
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AIClient", "OpenAI API Error: $errorBody")
                Log.e("AIClient", "Response code: ${response.code()}")
                "Oops! I had trouble understanding. Could you try asking me something else?"
            }
        } catch (e: Exception) {
            Log.e("AIClient", "Exception calling OpenAI API", e)
            "I'm having trouble thinking right now. Could you try again?"
        }
    }

    companion object {
        @Volatile
        private var instance: AIClient? = null

        fun getInstance(): AIClient {
            return instance ?: synchronized(this) {
                instance ?: AIClient().also { instance = it }
            }
        }
    }
}

interface OpenAIApi {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    fun createChatCompletion(
        @Body request: ChatCompletionRequest
    ): Call<ChatCompletionResponse>
}
