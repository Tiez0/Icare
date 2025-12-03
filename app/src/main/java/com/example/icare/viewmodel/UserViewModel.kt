package com.example.icare.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel : ViewModel() {
    // Armazena o nome do usuário logado
    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username.asStateFlow()

    // Armazena o estado do modo de alto contraste
    private val _isHighContrastEnabled = MutableStateFlow(false)
    val isHighContrastEnabled: StateFlow<Boolean> = _isHighContrastEnabled.asStateFlow()

    fun setUsername(name: String) {
        _username.value = name
    }

    fun clearUsername() {
        _username.value = null
    }

    // Função para ligar ou desligar o modo de alto contraste
    fun setHighContrastEnabled(enabled: Boolean) {
        _isHighContrastEnabled.value = enabled
    }
}
