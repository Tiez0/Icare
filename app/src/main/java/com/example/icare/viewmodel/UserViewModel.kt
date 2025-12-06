package com.example.icare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icare.model.PedidoDeCadastro
import com.example.icare.model.Resultado
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

class UserViewModel : ViewModel() {
    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username.asStateFlow()

    // Estado para saber se o cadastro deu certo ou errado (opcional, para exibir erro na tela)
    private val _cadastroStatus = MutableStateFlow<Boolean?>(null)
    val cadastroStatus: StateFlow<Boolean?> = _cadastroStatus.asStateFlow()

    fun setUsername(name: String) {
        _username.value = name
    }

    fun clearUsername() {
        _username.value = null
    }

    fun cadastrarUsuario(nome: String, cpf: String, email: String, senha: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var socket: Socket? = null
            try {
                // 10.0.2.2 é o IP do localhost do seu computador visto de dentro do emulador Android
                // Se usar telemóvel físico, troque pelo IP da sua rede (ex: 192.168.1.15)
                socket = Socket("192.168.1.104", 3000)

                val output = ObjectOutputStream(socket.getOutputStream())
                val input = ObjectInputStream(socket.getInputStream())

                // Cria o pedido usando a classe do pacote model
                val pedido = PedidoDeCadastro(nome, cpf, email, senha)

                // Envia
                output.writeObject(pedido)
                output.flush()

                // Recebe a resposta
                val resposta = input.readObject() as Resultado

                if (resposta.isValido) {
                    // Sucesso: atualiza o utilizador logado
                    _username.value = nome
                    _cadastroStatus.value = true
                    println("Android: Cadastro realizado com sucesso!")
                } else {
                    _cadastroStatus.value = false
                    println("Android: O servidor recusou o cadastro.")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _cadastroStatus.value = false
                println("Android: Erro de conexão: ${e.message}")
            } finally {
                socket?.close()
            }
        }
    }

    // Reseta o status para não exibir erro/sucesso repetidamente
    fun resetCadastroStatus() {
        _cadastroStatus.value = null
    }
}