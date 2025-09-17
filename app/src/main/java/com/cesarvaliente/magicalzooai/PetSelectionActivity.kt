package com.cesarvaliente.magicalzooai

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PetSelectionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val kidName = intent.getStringExtra("KID_NAME") ?: "" // read incoming name
        setContent {
            MagicalZooStartScreen(kidName = kidName)
        }
    }
}

private enum class Animal { FOX, TORTOISE }

@Composable
fun MagicalZooStartScreen(kidName: String) {
    var selected by rememberSaveable { mutableStateOf<Animal?>(null) }
    val skyBlue = Color(0xFF87CEEB)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(skyBlue)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        ) {
            // Title at the top
            Text(
                text = "Welcome, $kidName.",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Utils.myFontFamily,
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier
                    .padding(top = 10.dp, start = 24.dp, end = 24.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Please select your favorite magical animal.\n",
                fontSize = 22.sp,
                fontFamily = Utils.myFontFamily,
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier
                    .padding(top = 10.dp, start = 24.dp, end = 24.dp)
                    .align(Alignment.CenterHorizontally)
            )

            // Row with animal selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Fox column (left half)
                Column(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val isSelected = selected == Animal.FOX
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFFFFE0B2) else Color.White.copy(alpha = 0.95f),
                        border = BorderStroke(if (isSelected) 4.dp else 2.dp, Color(0xFFFFA726)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(interactionSource = remember { MutableInteractionSource() }) {
                                selected = Animal.FOX
                            }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.fox),
                                contentDescription = "Fox",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(220.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Rudy the fox",
                        fontSize = 28.sp,
                        fontFamily = FontFamily.Cursive,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = Color.Black
                    )
                }

                // Tortoise column (right half)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val isSelected = selected == Animal.TORTOISE
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFFC8E6C9) else Color.White.copy(alpha = 0.95f),
                        border = BorderStroke(if (isSelected) 4.dp else 2.dp, Color(0xFF66BB6A)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(interactionSource = remember { MutableInteractionSource() }) {
                                selected = Animal.TORTOISE
                            }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.tortoise),
                                contentDescription = "Tortoise",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(220.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Maddie the tortoise",
                        fontSize = 28.sp,
                        fontFamily = FontFamily.Cursive,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Animated confirmation button
            Spacer(modifier = Modifier.height(24.dp))
            AnimatedVisibility(
                visible = selected != null,
                enter = fadeIn() + expandVertically() + scaleIn(
                    initialScale = 0.7f,
                    animationSpec = tween(300, easing = EaseOutBack)
                ),
                exit = fadeOut() + shrinkVertically() + scaleOut()
            ) {
                val animalName = when (selected) {
                    Animal.FOX -> "Rudy the fox"
                    Animal.TORTOISE -> "Maddie the tortoise"
                    null -> ""
                }

                MagicalConfirmationButton(
                    animalName = animalName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                )
            }
        }
    }
}

@Composable
fun MagicalConfirmationButton(
    animalName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val animalType = if (animalName.contains("fox", ignoreCase = true)) "FOX" else "TORTOISE"

    val scope = rememberCoroutineScope()


    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF9C27B0).copy(alpha = 0.8f),
        shadowElevation = 8.dp,
        modifier = modifier.clickable {
            val intent = Intent(context, ChatActivity::class.java).apply {
                putExtra("ANIMAL_NAME", animalName)
                putExtra("ANIMAL_TYPE", animalType)
            }
            context.startActivity(intent)
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            AnimatedContent(
                targetState = animalName,
                transitionSpec = {
                    // Text slides up and fades in while old text slides down and fades out
                    (slideInVertically { height -> height } + fadeIn()) togetherWith
                            (slideOutVertically { height -> -height } + fadeOut())
                },
                label = "AnimalNameAnimation"
            ) { targetName ->
                Text(
                    text = "You are choosing $targetName",
                    fontSize = 20.sp,
                    fontFamily = Utils.myFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Preview(name = "Phone", showBackground = true)
@Composable
fun DefaultPreview() {
    MagicalZooStartScreen("Cesar")
}

@Preview(
    name = "Tablet",
    device = Devices.TABLET,
    showBackground = true
)
@Composable
fun TabletPreview() {
    MagicalZooStartScreen("Cesar")
}
