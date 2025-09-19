// Kotlin
package com.cesarvaliente.magicalzooai

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cesarvaliente.magicalzooai.api.AIClient
import com.cesarvaliente.magicalzooai.model.ChatMessage
import com.cesarvaliente.magicalzooai.repository.ChatRepository
import com.cesarvaliente.magicalzooai.viewmodel.ChatViewModel
import kotlinx.coroutines.delay
import kotlin.text.replaceFirstChar

class ChatActivity : ComponentActivity() {
    private lateinit var viewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure window to handle insets properly
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val kidName = intent.getStringExtra("KID_NAME") ?: ""
        val animalName = intent.getStringExtra("ANIMAL_NAME") ?: ""
        val animalType = intent.getStringExtra("ANIMAL_TYPE") ?: ""
        val topic  = intent.getStringExtra("TOPIC") ?: ""

        // Initialize ViewModel
        val chatRepository = ChatRepository(AIClient.getInstance())
        viewModel = ViewModelProvider(
            this,
            ChatViewModel.Factory(chatRepository)
        )[ChatViewModel::class.java]

        setContent {
            AnimalChatScreen(
                viewModel = viewModel,
                animalType = animalType,
                animalName = animalName,
                kidName = kidName,
                topic = topic,
                onBack = {
                    viewModel.clearHistory() // Clear chat history when going back
                    finish()
                }
            )
        }

        // Add initial greeting
        viewModel.addInitialGreeting(animalName, kidName)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimalChatScreen(
    viewModel: ChatViewModel,
    animalType: String,
    animalName: String,
    kidName: String,
    topic: String,
    onBack: () -> Unit
) {
    val skyBlue = Color(0xFF87CEEB)
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState()
    val context = LocalContext.current

    // Check if keyboard is visible
    val isKeyboardVisible = WindowInsets.isImeVisible
    val bottomPadding = with(LocalDensity.current) {
        WindowInsets.navigationBars.getBottom(this).toDp()
    }

    // Auto-scroll to bottom whenever messages change
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            delay(50)
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    // Auto-scroll when keyboard appears
    LaunchedEffect(isKeyboardVisible) {
        if (isKeyboardVisible && messages.isNotEmpty()) {
            delay(100) // Slight delay to ensure UI has adjusted to keyboard
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    val animalImageRes = if (animalType == "FOX") R.drawable.fox else R.drawable.tortoise

    // Function to hide keyboard
    fun hideKeyboard() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            (context as? ComponentActivity)?.currentFocus?.windowToken,
            0
        )
    }

    // Use Scaffold instead of direct Box/Column composition for better inset handling
    // Use a Scaffold for proper inset handling
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = skyBlue.copy(alpha = 0.3f),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(
                color = skyBlue,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
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
                    val justAnimalName = extractAnimalFirstName(animalName)
                    Text(
                        text = "${topic.capitalizeFirstLetter()} with $justAnimalName",
                        fontSize = 18.sp,
                        fontFamily = Utils.myFontFamily,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        },
        bottomBar = {
            // Chat messages (scrollable content)
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    // Only apply imePadding when keyboard is visible
                    .then(
                        if (isKeyboardVisible) {
                            Modifier.imePadding()
                        } else {
                            // Add extra padding to ensure text field is fully visible
                            Modifier.padding(bottom = bottomPadding + 8.dp)
                        }
                    )
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
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            lineHeight = 24.sp,
                            letterSpacing = 0.5.sp
                        ),
                        placeholder = {
                            Text(
                                text = "Type a message...",
                                fontFamily = Utils.myFontFamily,
                                fontSize = 16.sp
                            )
                        },
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
                            viewModel.sendMessage(animalName, animalType, topic)
                            hideKeyboard() // Hide keyboard after sending message
                        },
                        containerColor = if (animalType == "FOX") Color(0xFFFFA726)
                        else Color(0xFF66BB6A),
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
    ) { innerPadding ->
        // Chat messages
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding), // Apply padding from Scaffold
            reverseLayout = false,
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 50.dp // Increased bottom padding to ensure content isn't hidden
            )
        ) {
            items(messages) { message ->
                MessageBubble(
                    message = message,
                    animalImageRes = animalImageRes
                )
                Spacer(modifier = Modifier.height(8.dp))
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
            modifier = Modifier
                .widthIn(max = 320.dp)
        ) {
            Text(
                text = message.content,
                color = if (message.isFromUser) Color.White else Color.Black,
                fontSize = 16.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

fun extractAnimalFirstName(fullName: String): String {
    if (fullName.isBlank()) return ""
    return fullName.split(" ").firstOrNull() ?: ""
}

fun String.capitalizeFirstLetter(): String {
    return this.replaceFirstChar { it.uppercase() }
}
