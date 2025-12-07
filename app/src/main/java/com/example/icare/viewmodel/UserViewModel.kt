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
import java.net.InetSocketAddress
import java.net.Socket

class UserViewModel : ViewModel() {
    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username.asStateFlow()

    // Estado para saber se o cadastro deu certo ou errado
    private val _cadastroStatus = MutableStateFlow<Boolean?>(null)
    val cadastroStatus: StateFlow<Boolean?> = _cadastroStatus.asStateFlow()

    // NOVO: Estado para guardar a mensagem de erro específica (ex: "CPF Inválido")
    private val _mensagemErro = MutableStateFlow<String?>(null)
    val mensagemErro: StateFlow<String?> = _mensagemErro.asStateFlow()

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
                // Limpa mensagem anterior
                _mensagemErro.value = null

                // --- CONFIGURAÇÃO DO IP ---
                // Se usar Emulador: "10.0.2.2"
                // Se usar Celular Físico: "192.168.X.X" (Seu IP do computador)
                val ipServidor = "10.0.2.2" // <--- ATUALIZE AQUI SE PRECISAR

                socket = Socket()
                socket.connect(InetSocketAddress(ipServidor, 3000), 5000) // Timeout de 5s

                val output = ObjectOutputStream(socket.getOutputStream())
                val input = ObjectInputStream(socket.getInputStream())

                // Cria o pedido usando a classe do pacote model
                val pedido = PedidoDeCadastro(nome, cpf, email, senha)

                // Envia
                output.writeObject(pedido)
                output.flush()

                // Recebe a resposta do servidor
                val resposta = input.readObject() as Resultado

                // Atualiza a mensagem na tela com o que veio do servidor
                _mensagemErro.value = resposta.mensagem

                if (resposta.isValido) {
                    // Sucesso
                    _username.value = nome
                    _cadastroStatus.value = true
                } else {
                    // Falha (O motivo estará em _mensagemErro)
                    _cadastroStatus.value = false
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _cadastroStatus.value = false
                // Mensagem amigável se der erro de conexão
                _mensagemErro.value = "Falha na conexão: Verifique se o servidor está rodando."
            } finally {
                try { socket?.close() } catch (e: Exception) {}
            }
        }
    }

    // Reseta o status para não exibir erro/sucesso repetidamente ao voltar pra tela
    fun resetCadastroStatus() {
        _cadastroStatus.value = null
        _mensagemErro.value = null
    }
}