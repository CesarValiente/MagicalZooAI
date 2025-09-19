package com.cesarvaliente.magicalzooai

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class TopicActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val kidName = intent.getStringExtra("KID_NAME") ?: ""
        val animalName = intent.getStringExtra("ANIMAL_NAME") ?: ""
        val animalType = intent.getStringExtra("ANIMAL_TYPE") ?: ""

        setContent {
            TopicScreen(
                kidName = kidName,
                animalName = animalName,
                onTopicChosen = { topic ->
                    val chatIntent = Intent(this, ChatActivity::class.java).apply {
                        putExtra("KID_NAME", kidName)
                        putExtra("ANIMAL_NAME", animalName)
                        putExtra("ANIMAL_TYPE", animalType)
                        putExtra("TOPIC", topic)
                    }
                    startActivity(chatIntent)
                    overridePendingTransition(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                    )
                },
                onBack = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TopicScreen(
    kidName: String,
    animalName: String,
    onTopicChosen: (String) -> Unit,
    onBack: () -> Unit
) {
    val skyBlue = Color(0xFF87CEEB)
    val buttonSize = 120.dp
    val buttonShape = RoundedCornerShape(32.dp)
    val topics = listOf(
        TopicButtonData("maths", R.drawable.maths, Color(0xFF7E9EFF), "Maths"),
        TopicButtonData("science", R.drawable.science, Color(0xFF7EFF86), "Science"),
        TopicButtonData(
            "history & geography",
            R.drawable.history_and_geography,
            Color(0xFFFF847E),
            "History & Geography"
        ),
        TopicButtonData("chatting", R.drawable.chatting, Color(0xFFFFE47E), "Just Chat")
    )
    var selectedTopic by remember { mutableStateOf<TopicButtonData?>(null) }
    var buttonText by remember { mutableStateOf("Accept") }

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
                    Text(
                        text = "Topic selection",
                        fontSize = 18.sp,
                        fontFamily = Utils.myFontFamily,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$kidName, what would you like to talk about with $animalName?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Utils.myFontFamily,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            // First row
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, false)
            ) {
                TopicButton(
                    data = topics[0],
                    selected = selectedTopic == topics[0],
                    size = buttonSize,
                    shape = buttonShape,
                    onClick = {
                        selectedTopic = topics[0]
                        buttonText = if (topics[0].id == "chatting") "Just chat."
                        else "I want to learn ${topics[0].id}."
                    }
                )
                Spacer(modifier = Modifier.width(32.dp))
                TopicButton(
                    data = topics[1],
                    selected = selectedTopic == topics[1],
                    size = buttonSize,
                    shape = buttonShape,
                    onClick = {
                        selectedTopic = topics[1]
                        buttonText = if (topics[1].id == "chatting") "Just chat."
                        else "I want to learn ${topics[1].id}."
                    }
                )
            }
            Spacer(modifier = Modifier.height(28.dp))
            // Second row
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, false)
            ) {
                TopicButton(
                    data = topics[2],
                    selected = selectedTopic == topics[2],
                    size = buttonSize,
                    shape = buttonShape,
                    onClick = {
                        selectedTopic = topics[2]
                        buttonText = if (topics[2].id == "chatting") "Just chat."
                        else "I want to learn ${topics[2].id}."
                    }
                )
                Spacer(modifier = Modifier.width(32.dp))
                TopicButton(
                    data = topics[3],
                    selected = selectedTopic == topics[3],
                    size = buttonSize,
                    shape = buttonShape,
                    onClick = {
                        selectedTopic = topics[3]
                        buttonText = if (topics[3].id == "chatting") "Just chat."
                        else "I want to learn ${topics[3].id}."
                    }
                )
            }
            Spacer(modifier = Modifier.height(48.dp))

            // Accept button with animated visibility
            AnimatedVisibility(
                visible = selectedTopic != null,
                enter = fadeIn() + expandVertically() + scaleIn(
                    initialScale = 0.7f,
                    animationSpec = tween(300, easing = EaseOutBack)
                ),
                exit = fadeOut() + shrinkVertically() + scaleOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFF9C27B0).copy(alpha = 0.8f),
                        shadowElevation = 8.dp,
                        modifier = Modifier.clickable {
                            if (selectedTopic != null) {
                                onTopicChosen(selectedTopic!!.id)
                            }
                        }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
                        ) {
                            AnimatedContent(
                                targetState = buttonText,
                                transitionSpec = {
                                    // Text slides up and fades in while old text slides down and fades out
                                    (slideInVertically { height -> height } + fadeIn()) togetherWith
                                            (slideOutVertically { height -> -height } + fadeOut())
                                },
                                label = "TopicAcceptButtonAnimation"
                            ) { targetText ->
                                Text(
                                    text = targetText,
                                    fontSize = 20.sp,
                                    fontFamily = Utils.myFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    // Hint text below the button
                    Text(
                        text = "(tap the above button to continue)",
                        fontSize = 14.sp,
                        color = Color.Black.copy(alpha = 0.6f),
                        fontFamily = Utils.myFontFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TopicButton(
    data: TopicButtonData,
    selected: Boolean,
    size: androidx.compose.ui.unit.Dp,
    shape: RoundedCornerShape,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = shape,
            color = if (selected) data.color.copy(alpha = 0.7f) else Color.White,
            shadowElevation = if (selected) 12.dp else 6.dp,
            border = null,
            modifier = Modifier
                .size(size)
                .clickable { onClick() }
                .graphicsLayer {
                    scaleX = if (selected) 1.08f else 1f
                    scaleY = if (selected) 1.08f else 1f
                }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = data.iconRes),
                    contentDescription = data.id,
                    modifier = Modifier.size(size * 0.6f)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = data.displayName,
            fontSize = 22.sp,
            fontFamily = FontFamily.Cursive,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

data class TopicButtonData(
    val id: String,
    val iconRes: Int,
    val color: Color,
    val displayName: String
)

