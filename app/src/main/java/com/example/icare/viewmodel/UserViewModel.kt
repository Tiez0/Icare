package com.example.icare.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.icare.model.PedidoDeCadastro
import com.example.icare.model.PedidoDeLogin
import com.example.icare.model.Resultado
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetSocketAddress
import java.net.Socket


data class Remedio(
    val nome: String,
    val dosagem: String,
    val frequencia: String,
    val diasDaSemana: List<Boolean> = List(7) { true }
)

data class ContatoSOS(
    val nome: String,
    val telefone: String
)


class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val prefs = context.getSharedPreferences("icare_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()


    // Usuário Logado
    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username.asStateFlow()

    // Lista de Remédios
    private val _remedios = MutableStateFlow<List<Remedio>>(emptyList())
    val remedios: StateFlow<List<Remedio>> = _remedios.asStateFlow()

    // Contato de Emergência
    private val _contatoSOS = MutableStateFlow<ContatoSOS?>(null)
    val contatoSOS: StateFlow<ContatoSOS?> = _contatoSOS.asStateFlow()

    // Status das Operações de Rede
    private val _cadastroStatus = MutableStateFlow<Boolean?>(null)
    val cadastroStatus: StateFlow<Boolean?> = _cadastroStatus.asStateFlow()

    private val _loginStatus = MutableStateFlow<Boolean?>(null)
    val loginStatus: StateFlow<Boolean?> = _loginStatus.asStateFlow()

    private val _mensagemErro = MutableStateFlow<String?>(null)
    val mensagemErro: StateFlow<String?> = _mensagemErro.asStateFlow()


    init {

        val salvo = prefs.getString("username", null)
        if (salvo != null) {
            _username.value = salvo
            carregarRemedios()
            carregarContatoSOS()
        }
    }



    fun adicionarRemedio(nome: String, dosagem: String, frequencia: String, dias: List<Boolean>) {
        val novo = Remedio(nome, dosagem, frequencia, dias)
        val listaAtual = _remedios.value.toMutableList()
        listaAtual.add(novo)
        _remedios.value = listaAtual
        salvarRemediosLocalmente()
    }

    private fun salvarRemediosLocalmente() {
        val json = gson.toJson(_remedios.value)
        prefs.edit().putString("lista_remedios", json).apply()
    }

    private fun carregarRemedios() {
        val json = prefs.getString("lista_remedios", null)
        if (json != null) {
            val tipo = object : TypeToken<List<Remedio>>() {}.type
            _remedios.value = gson.fromJson(json, tipo)
        } else {
            _remedios.value = emptyList()
        }
    }



    fun salvarContatoSOS(nome: String, telefone: String) {
        val novoContato = ContatoSOS(nome, telefone)
        _contatoSOS.value = novoContato

        val json = gson.toJson(novoContato)
        prefs.edit().putString("contato_sos", json).apply()
    }

    private fun carregarContatoSOS() {
        val json = prefs.getString("contato_sos", null)
        if (json != null) {
            val tipo = object : TypeToken<ContatoSOS>() {}.type
            _contatoSOS.value = gson.fromJson(json, tipo)
        }
    }

    // --- GERENCIAMENTO DE SESSÃO ---

    fun setUsername(name: String) {
        _username.value = name
        prefs.edit().putString("username", name).apply()
        // Carrega dados específicos deste login (se existissem perfis separados)
        carregarRemedios()
        carregarContatoSOS()
    }

    fun logout() {
        _username.value = null
        _remedios.value = emptyList()
        _contatoSOS.value = null

        // Limpa tudo do SharedPreferences para garantir que saia mesmo
        prefs.edit().clear().apply()
    }

    fun resetCadastroStatus() {
        _cadastroStatus.value = null
        _mensagemErro.value = null
    }

    fun resetLoginStatus() {
        _loginStatus.value = null
        _mensagemErro.value = null
    }



    fun cadastrarUsuario(nome: String, cpf: String, email: String, senha: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var socket: Socket? = null
            try {
                _mensagemErro.value = null


                val ipServidor = "10.0.2.2"

                socket = Socket()
                socket.connect(InetSocketAddress(ipServidor, 3000), 5000)

                val output = ObjectOutputStream(socket.getOutputStream())
                val input = ObjectInputStream(socket.getInputStream())


                val pedido = PedidoDeCadastro(nome, cpf, email, senha)

                output.writeObject(pedido)
                output.flush()

                val resposta = input.readObject() as Resultado
                _mensagemErro.value = resposta.mensagem

                if (resposta.isValido) {

                    _cadastroStatus.value = true
                } else {
                    _cadastroStatus.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _cadastroStatus.value = false
                _mensagemErro.value = "Falha na conexão com o servidor."
            } finally {
                try { socket?.close() } catch (e: Exception) {}
            }
        }
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

                val pedido = PedidoDeLogin(email, senha)
                output.writeObject(pedido)
                output.flush()

                val resposta = input.readObject() as Resultado
                _mensagemErro.value = resposta.mensagem

                if (resposta.isValido) {

                    setUsername(email)
                    _loginStatus.value = true
                } else {
                    _loginStatus.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _loginStatus.value = false
                _mensagemErro.value = "Falha na conexão com o servidor."
            } finally {
                try { socket?.close() } catch (e: Exception) {}
            }
        }
    }
}