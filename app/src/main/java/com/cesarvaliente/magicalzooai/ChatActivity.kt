package com.cesarvaliente.magicalzooai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val animalType = intent.getStringExtra("ANIMAL_TYPE") ?: ""
        val animalName = intent.getStringExtra("ANIMAL_NAME") ?: ""

        setContent {
            AnimalChatScreen(
                animalType = animalType,
                animalName = animalName,
                onBack = { finish() } // Pass the activity's finish method as a lambda
            )
        }
    }
}

// Data class for chat messages
data class ChatMessage(
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun AnimalChatScreen(
    animalType: String,
    animalName: String,
    onBack: () -> Unit
){

    val skyBlue = Color(0xFF87CEEB)
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var inputText by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Add initial greeting when screen loads
    LaunchedEffect(Unit) {
        messages.add(ChatMessage("Hello! I'm $animalName. How can I help you today?", false))
    }

    val animalImageRes = if (animalType == "FOX") R.drawable.fox02_cropped else R.drawable.tortoise_cropped

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(skyBlue.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Surface(
                color = skyBlue,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .clickable { onBack() }
                            .padding(end = 16.dp)
                            .size(24.dp)
                    )

                    Text(
                        text = "Talking with $animalName",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Cursive,
                    )
                }
            }

            // Chat messages
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                reverseLayout = false,
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(
                        message = message,
                        animalImageRes = animalImageRes
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Input field
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Type a message...") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3
                    )

                    FloatingActionButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                // Add user message
                                messages.add(ChatMessage(inputText, true))

                                // Simulate animal response
                                coroutineScope.launch {
                                    delay(1000)
                                    messages.add(
                                        ChatMessage(
                                            "I'm $animalName and I'm responding to: $inputText",
                                            false
                                        )
                                    )
                                    scrollState.animateScrollToItem(messages.size - 1)
                                }

                                inputText = ""

                                // Scroll to bottom
                                coroutineScope.launch {
                                    scrollState.animateScrollToItem(messages.size - 1)
                                }
                            }
                        },
                        containerColor = if (animalType == "FOX") Color(0xFFFFA726) else Color(
                            0xFF66BB6A
                        ),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage, animalImageRes: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom  // Added to align items at the bottom
    ) {
        if (!message.isFromUser) {
            // Animal profile picture
            Surface(
                shape = CircleShape,
                modifier = Modifier.size(36.dp)  // Removed .align modifier
            ) {
                Image(
                    painter = painterResource(id = animalImageRes),
                    contentDescription = "Animal",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Message bubble
        Surface(
            color = if (message.isFromUser) Color(0xFF9C27B0).copy(alpha = 0.8f) else Color.White,
            shape = RoundedCornerShape(
                topStart = if (message.isFromUser) 16.dp else 0.dp,
                topEnd = if (message.isFromUser) 0.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            shadowElevation = 2.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                color = if (message.isFromUser) Color.White else Color.Black,
                modifier = Modifier.padding(12.dp)
            )
        }

        if (message.isFromUser) {
            // Empty space for user avatar (for symmetry)
            Spacer(modifier = Modifier.width(8.dp))
            Spacer(modifier = Modifier.size(36.dp))
        }
    }
}
