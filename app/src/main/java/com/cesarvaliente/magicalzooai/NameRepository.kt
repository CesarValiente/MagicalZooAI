package com.cesarvaliente.magicalzooai

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.content.edit

class NameRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    suspend fun saveName(name: String) = withContext(Dispatchers.IO) {
        prefs.edit { putString(KEY_NAME, name) }
    }

    suspend fun getName(): String? = withContext(Dispatchers.IO) {
        prefs.getString(KEY_NAME, null)
    }

    companion object {
        private const val PREFS_NAME = "magical_zoo_prefs"
        private const val KEY_NAME = "user_name"
    }
}

