// Kotlin
package com.cesarvaliente.magicalzooai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cesarvaliente.magicalzooai.api.AIClient
import com.cesarvaliente.magicalzooai.model.ChatMessage
import com.cesarvaliente.magicalzooai.repository.ChatRepository
import com.cesarvaliente.magicalzooai.viewmodel.ChatViewModel
import kotlinx.coroutines.delay

class ChatActivity : ComponentActivity() {
    private lateinit var viewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val animalType = intent.getStringExtra("ANIMAL_TYPE") ?: ""
        val animalName = intent.getStringExtra("ANIMAL_NAME") ?: ""

        // Initialize ViewModel
        val chatRepository = ChatRepository(AIClient.getInstance())
        viewModel = ViewModelProvider(this, ChatViewModel.Factory(chatRepository))[ChatViewModel::class.java]

        setContent {
            AnimalChatScreen(
                viewModel = viewModel,
                animalType = animalType,
                animalName = animalName,
                onBack = { finish() }
            )
        }

        // Add initial greeting
        viewModel.addInitialGreeting(animalName)
    }
}

@Composable
fun AnimalChatScreen(
    viewModel: ChatViewModel,
    animalType: String,
    animalName: String,
    onBack: () -> Unit
) {
    val skyBlue = Color(0xFF87CEEB)
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState()

    // Auto-scroll to bottom whenever messages change
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            delay(50)
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    val animalImageRes = if (animalType == "FOX") R.drawable.fox else R.drawable.tortoise

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
                        fontSize = 18.sp,
                        fontFamily = Utils.myFontFamily,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            // Chat messages
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .imePadding(),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = inputText,
                        onValueChange = { viewModel.onInputTextChanged(it) },
                        placeholder = { Text(
                            text = "Type a message...",
                            fontFamily = Utils.myFontFamily,
                            fontSize = 16.sp
                        )},
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
                        onClick = { viewModel.sendMessage(animalName, animalType) },
                        containerColor = if (animalType == "FOX") Color(0xFFFFA726) else Color(0xFF66BB6A),
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
                    contentScale = ContentScale.Fit,
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
