package com.example.icare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class UserViewModel : ViewModel() {
    // Mantém o estado do nome do usuário
    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username.asStateFlow()

    // Novo estado para guardar o resultado da validação ("Válido", "Inválido" ou erro)
    private val _validationResult = MutableStateFlow<String>("")
    val validationResult: StateFlow<String> = _validationResult.asStateFlow()

    fun setUsername(name: String) {
        _username.value = name
    }

    fun clearUsername() {
        _username.value = null
    }

    // A MÁGICA ACONTECE AQUI
    fun validarCpf(cpf: String) {
        _validationResult.value = "Verificando..."

        viewModelScope.launch(Dispatchers.IO) {
            var socket: Socket? = null
            try {
                // 10.0.2.2 é o 'localhost' visto de dentro do emulador Android
                // Se estiver testando no celular físico via USB, precisará do IP da sua máquina (ex: 192.168.x.x)
                socket = Socket("10.0.2.2", 3000)

                val output = PrintWriter(socket.getOutputStream(), true)
                val input = BufferedReader(InputStreamReader(socket.getInputStream()))

                // Envia o comando no formato texto simples que combinamos
                output.println("VALIDAR:$cpf")

                // Lê a resposta do servidor
                val resposta = input.readLine() // Espera receber "RESULTADO:VALIDO" etc.

                if (resposta != null && resposta.contains("VALIDO")) {
                    _validationResult.value = "CPF Válido!"
                } else {
                    _validationResult.value = "CPF Inválido ou Recusado."
                }

                // Envia tchau para desconectar bonitinho
                output.println("SAIR")

            } catch (e: Exception) {
                e.printStackTrace()
                _validationResult.value = "Erro de Conexão: ${e.message}"
            } finally {
                try { socket?.close() } catch (e: Exception) {}
            }
        }
    }
}