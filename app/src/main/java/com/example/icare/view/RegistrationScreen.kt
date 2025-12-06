package com.example.icare.view

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.icare.PedidoDeValidacao
import com.example.icare.Resultado
import com.example.icare.UsuarioMongo // <--- Usando a classe nova
import com.example.icare.ui.theme.IcareTheme
import com.example.icare.viewmodel.UserViewModel
// Imports do Driver Mongo Novo
import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import java.util.Calendar

@Composable
fun RegistrationScreen(navController: NavController, userViewModel: UserViewModel) {
    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val scope = rememberCoroutineScope()

    // Validações básicas
    val isFormValid = name.isNotBlank() && cpf.length == 11 && email.contains("@") &&
            password.isNotBlank() && confirmPassword == password

    val datePickerDialog = DatePickerDialog(
        context, { _, year, month, day -> dob = "$day/${month + 1}/$year" },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Fundo Verde
        Canvas(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            val path = Path().apply {
                moveTo(0f, 0f); lineTo(size.width, 0f); lineTo(size.width, size.height - 50.dp.toPx())
                quadraticBezierTo(size.width / 2, size.height, 0f, size.height - 50.dp.toPx()); close()
            }
            drawPath(path, color = Color(0xFF88e788))
        }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(100.dp))
            Text("Criar Nova Conta", fontSize = 32.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Spacer(modifier = Modifier.height(135.dp))

            // Campos (Resumidos para caber, mantenha o visual igual)
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Person, null) })
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = dob, onValueChange = {}, label = { Text("Nascimento") }, modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() }, enabled = false, leadingIcon = { Icon(Icons.Default.DateRange, null) })
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = cpf, onValueChange = { if (it.length <= 11) cpf = it }, label = { Text("CPF") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), leadingIcon = { Icon(Icons.Default.Person, null) })
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Email, null) })
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Senha") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), leadingIcon = { Icon(Icons.Default.Lock, null) })
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirmar") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), leadingIcon = { Icon(Icons.Default.Lock, null) })
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isLoading = true
                    scope.launch(Dispatchers.IO) {
                        var socket: Socket? = null
                        var mongoClient: MongoClient? = null

                        try {
                            // 1. VALIDAÇÃO NO SERVIDOR JAVA (SOCKET)
                            socket = Socket("10.0.2.2", 3000)
                            val output = ObjectOutputStream(socket.getOutputStream())
                            val input = ObjectInputStream(socket.getInputStream())

                            output.writeObject(PedidoDeValidacao(cpf))
                            output.flush()

                            val resposta = input.readObject()

                            // Se o servidor Java disse que é válido...
                            if (resposta is Resultado && resposta.isValido) {
                                try {
                                    // 2. SALVA NO MONGODB (CONEXÃO DIRETA)
                                    // SUBSTITUA <db_password> PELA SUA SENHA REAL DO BANCO
                                    // Note que começa só com "mongodb://" e tem vários endereços separados por vírgula
                                    val connectionString = "mongodb://flaviodario2017_db_user:Senha1@cluster0-shard-00-00.3jthsk8.mongodb.net:27017,cluster0-shard-00-01.3jthsk8.mongodb.net:27017,cluster0-shard-00-02.3jthsk8.mongodb.net:27017/?ssl=true&replicaSet=atlas-xxxxx-shard-0&authSource=admin&retryWrites=true&w=majority"
                                    mongoClient = MongoClient.create(connectionString)
                                    val database = mongoClient.getDatabase("iCare")
                                    val collection = database.getCollection<UsuarioMongo>("usuarios")

                                    val novoUsuario = UsuarioMongo(
                                        nome = name,
                                        cpf = cpf,
                                        dataNascimento = dob,
                                        email = email
                                    )

                                    collection.insertOne(novoUsuario)

                                    withContext(Dispatchers.Main) {
                                        userViewModel.setUsername(name)
                                        Toast.makeText(context, "Conta Criada e Salva!", Toast.LENGTH_LONG).show()
                                        navController.navigate("home")
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Erro Mongo: ${e.message}", Toast.LENGTH_LONG).show()
                                        e.printStackTrace()
                                    }
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "CPF Inválido!", Toast.LENGTH_LONG).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Erro Geral: ${e.message}", Toast.LENGTH_LONG).show()
                                e.printStackTrace()
                            }
                        } finally {
                            try { socket?.close(); mongoClient?.close() } catch (e: Exception) {}
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF88e788)),
                enabled = isFormValid && !isLoading
            ) {
                if (isLoading) Text("Processando...") else Text("Cadastrar")
            }
        }
    }
}