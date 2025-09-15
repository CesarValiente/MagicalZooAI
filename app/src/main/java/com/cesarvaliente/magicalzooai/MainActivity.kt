package com.cesarvaliente.magicalzooai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MagicalZooStartScreen()
        }
    }
}

private enum class Animal { FOX, TORTOISE }

@Composable
fun MagicalZooStartScreen() {
    var selected by rememberSaveable { mutableStateOf<Animal?>(null) }

    val skyBlue = Color(0xFF87CEEB)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(skyBlue)
    ) {
        // Title at the top, centered horizontally, with side margins
        Text(
            text = "Welcome to MagicalZooAI.\nPlease select your favorite magical animal.\n",
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Cursive,
            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 32.dp, start = 24.dp, end = 24.dp)
        )

        // Row with two equally sized selectable areas
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Fox column uses half width
            Column(
                modifier = Modifier.fillMaxWidth(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val isSelected = selected == Animal.FOX
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color(0xFFFFF3E0) else Color.White.copy(alpha = 0.95f),
                    border = BorderStroke(if (isSelected) 4.dp else 2.dp, Color(0xFFFFA726)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(interactionSource = remember { MutableInteractionSource() }) { selected = Animal.FOX }
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.fox02_cropped),
                            contentDescription = "Fox",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(180.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rudy the fox",
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Cursive,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = Color.Black
                )
            }

            // Tortoise column uses half width
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val isSelected = selected == Animal.TORTOISE
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color(0xFFE8F5E9) else Color.White.copy(alpha = 0.95f),
                    border = BorderStroke(if (isSelected) 4.dp else 2.dp, Color(0xFF66BB6A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        // center the surface inside the column instead of expanding full width
//                        .align(Alignment.CenterHorizontally)
                        .clickable(interactionSource = remember { MutableInteractionSource() }) { selected = Animal.TORTOISE }
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.tortoise_cropped),
                            contentDescription = "Tortoise",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(180.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Maddie the tortoise",
                    fontSize = 28.sp,
                    fontFamily = FontFamily.Cursive,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = Color.Black
                )
            }
        }
    }
}

@Preview(name = "Phone", showBackground = true)
@Composable
fun DefaultPreview() {
    MagicalZooStartScreen()
}

@Preview(
    name = "Tablet",
    device = Devices.TABLET,
    showBackground = true
)
@Composable
fun TabletPreview() {
    MagicalZooStartScreen()
}
