package com.cesarvaliente.magicalzooai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cesarvaliente.magicalzooai.storage.NameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WelcomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NameRepository(application)

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _isNameValid = MutableStateFlow(true)
    val isNameValid: StateFlow<Boolean> = _isNameValid.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    fun onNameChange(newName: String) {
        _name.value = newName
        _isNameValid.value = validateName(newName)
    }

    fun saveName() {
        if (validateName(_name.value)) {
            viewModelScope.launch {
                repository.saveName(_name.value)
                _saveSuccess.value = true
            }
        } else {
            _isNameValid.value = false
        }
    }

    private fun validateName(name: String): Boolean {
        return name.matches(Regex("^[A-Za-z]{1,20}$"))
    }

    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }
}