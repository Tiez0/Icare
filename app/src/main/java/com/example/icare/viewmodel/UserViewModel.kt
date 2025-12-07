package com.example.icare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icare.model.PedidoDeCadastro
import com.example.icare.model.Resultado
import com.example.icare.model.PedidoDeLogin
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

    private val _loginStatus = MutableStateFlow<Boolean?>(null)
    val loginStatus: StateFlow<Boolean?> = _loginStatus.asStateFlow()


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

                _mensagemErro.value = null


                val ipServidor = "10.0.2.2"

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


                _mensagemErro.value = resposta.mensagem

                if (resposta.isValido) {
                    // Sucesso
                    _username.value = nome
                    _cadastroStatus.value = true
                } else {

                    _cadastroStatus.value = false
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _cadastroStatus.value = false

                _mensagemErro.value = "Falha na conexão: Verifique se o servidor está rodando."
            } finally {
                try { socket?.close() } catch (e: Exception) {}
            }
        }
    }


    fun resetCadastroStatus() {
        _cadastroStatus.value = null
        _mensagemErro.value = null
    }
    fun fazerLogin(email: String, senha: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var socket: Socket? = null
            try {

                _mensagemErro.value = null
                _loginStatus.value = null


                val ipServidor = "10.0.2.2"

                socket = Socket()
                socket.connect(InetSocketAddress(ipServidor, 3000), 5000)

                val output = ObjectOutputStream(socket.getOutputStream())
                val input = ObjectInputStream(socket.getInputStream())

                // Envia o PedidoDeLogin
                val pedido = PedidoDeLogin(email, senha)
                output.writeObject(pedido)
                output.flush()

                // Recebe o Resultado
                val resposta = input.readObject() as Resultado

                // Atualiza a mensagem na tela (Erro ou Sucesso)
                _mensagemErro.value = resposta.mensagem

                if (resposta.isValido) {

                    _username.value = email
                    _loginStatus.value = true
                } else {
                    _loginStatus.value = false
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _loginStatus.value = false
                _mensagemErro.value = "Erro de conexão: Verifique o servidor."
            } finally {
                try { socket?.close() } catch (e: Exception) {}
            }
        }
    }

    fun resetLoginStatus() {
        _loginStatus.value = null
        _mensagemErro.value = null
    }
}