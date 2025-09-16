package com.cesarvaliente.magicalzooai

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

object  Utils {
        val myFontFamily = FontFamily(
            Font(R.font.playwrite_es, FontWeight.Normal),
            Font(R.font.playwrite_es, FontWeight.Bold),
            Font(R.font.playwrite_es, FontWeight.Normal, FontStyle.Italic)
        )
    }