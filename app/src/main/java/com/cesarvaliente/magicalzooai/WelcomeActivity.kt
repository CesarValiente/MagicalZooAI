package com.cesarvaliente.magicalzooai

import android.R
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WelcomeActivity : ComponentActivity() {
    private val viewModel: WelcomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WelcomeScreen(viewModel = viewModel, onNavigate = {
                val intent = Intent(this, PetSelectionActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                finish()
            })
        }
    }
}

@Composable
fun WelcomeScreen(viewModel: WelcomeViewModel, onNavigate: () -> Unit) {
    val skyBlue = Color(0xFF87CEEB)
    val name by viewModel.name.collectAsStateWithLifecycle()
    val isNameValid by viewModel.isNameValid.collectAsStateWithLifecycle()
    val saveSuccess by viewModel.saveSuccess.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    // Animation for text and stars
    var textVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        textVisible = true
    }

    // Animated stars
    val starCount = 7
    val starAnimatables = remember { List(starCount) { Animatable(0f) } }
    LaunchedEffect(textVisible) {
        if (textVisible) {
            starAnimatables.forEachIndexed { i, anim ->
                coroutineScope.launch {
                    delay(i * 120L)
                    anim.animateTo(1f, animationSpec = tween(700))
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(skyBlue)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = textVisible) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Welcome to the Magical Zoo AI.",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Utils.myFontFamily,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Text(
                        text = "What is your name?",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Utils.myFontFamily,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp, start = 24.dp, end = 24.dp)
                    )
                    // Magic stars
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        for (i in 0 until starCount) {
                            val alpha = starAnimatables[i].value
                            val rotation = 360f * starAnimatables[i].value
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .alpha(alpha)
                                    .rotate(rotation)
                                    .padding(horizontal = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "★",
                                    fontSize = 22.sp,
                                    color = Color(0xFFFFEB3B),
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            // Input box
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    TextField(
                        value = name,
                        onValueChange = {
                            if (it.length <= 20 && it.all { c -> c.isLetter() }) {
                                viewModel.onNameChange(it)
                            }
                        },
                        placeholder = { Text(
                            text = "Enter your name",
                            fontFamily = Utils.myFontFamily,
                            color = Color.Gray) },
                        singleLine = true,
                        isError = !isNameValid,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        visualTransformation = VisualTransformation.None,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorContainerColor = Color(0xFFFFCDD2)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF9C27B0).copy(alpha = 0.8f),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(48.dp)
                            .let { mod ->
                                if (isNameValid && name.isNotBlank())
                                    mod
                                else
                                    mod.alpha(0.5f)
                            }
                            .background(Color.Transparent)
                            .run {
                                if (isNameValid && name.isNotBlank())
                                    clickable { viewModel.saveName() }
                                else this
                            }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Accept",
                            tint = Color.White,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
            if (!isNameValid) {
                Text(
                    text = "Please enter only letters (max 20).",
                    color = Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        // Navigation effect
        LaunchedEffect(saveSuccess) {
            if (saveSuccess) {
                onNavigate()
                viewModel.resetSaveSuccess()
            }
        }
    }
}
