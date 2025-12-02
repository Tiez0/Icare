package com.example.icare.view

import PedidoDeCadastro
import Resultado
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.icare.ui.theme.IcareTheme
import com.example.icare.viewmodel.UserViewModel
import java.util.Calendar
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import androidx.compose.runtime.collectAsState


@Composable
fun RegistrationScreen(navController: NavController, userViewModel: UserViewModel) {
    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") } // Variável para o CPF
    val validationStatus by userViewModel.validationResult.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            dob = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            val curveHeight = 50.dp.toPx()
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height - curveHeight)
                quadraticBezierTo(
                    x1 = size.width / 2,
                    y1 = size.height,
                    x2 = 0f,
                    y2 = size.height - curveHeight
                )
                close()
            }
            drawPath(path, color = Color(0xFF88e788))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text("Criar Nova Conta", fontSize = 32.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Spacer(modifier = Modifier.height(135.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome Completo") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = "Person Icon", tint = Color.Black)
                },
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = dob,
                onValueChange = { dob = it },
                label = { Text("Data de Nascimento") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = LocalContentColor.current.copy(LocalContentColor.current.alpha),
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = Color.Black
                ),
                leadingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Select date")
                },
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // CAMPO DE CPF ADICIONADO AQUI
            OutlinedTextField(
                value = cpf,
                onValueChange = { cpf = it },
                label = { Text("CPF") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = "CPF Icon", tint = Color.Black)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Botão Pequeno para Validar
            Button(
                onClick = { userViewModel.validarCpf(cpf) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text("Verificar CPF no Servidor")
            }

            // Texto que mostra a resposta do servidor
            Text(
                text = validationStatus,
                color = if (validationStatus.contains("Válido")) Color.Green else Color.Red,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = "Email Icon", tint = Color.Black)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = "Password Icon", tint = Color.Black)
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, "toggle password visibility", tint = Color.Black)
                    }
                },
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Senha") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = "Password Icon", tint = Color.Black)
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (confirmPasswordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, "toggle password visibility", tint = Color.Black)
                    }
                },
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // validações básicas no app antes de chamar o servidor
                    if (name.isBlank() || dob.isBlank() || cpf.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                        Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    if (password != confirmPassword) {
                        Toast.makeText(context, "As senhas não coincidem", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    // IMPORTANTE:
                    // - Se estiver usando EMULADOR e o servidor Java rodando no seu PC,
                    //   use "10.0.2.2" como host.
                    // - Se estiver com dispositivo físico, use o IP da máquina na rede (ex: 192.168.x.x)
                    val host = "10.0.2.2"   // para emulador Android falando com servidor no PC
                    val porta = 3000        // mesma porta do Servidor.java (PORTA_PADRAO)

                    Thread {
                        try {
                            // Abre conexão com o servidor Java
                            val socket = Socket(host, porta)
                            val output = ObjectOutputStream(socket.getOutputStream())
                            val input = ObjectInputStream(socket.getInputStream())

                            // Monta o pedido de cadastro com TODOS os campos
                            val pedido = PedidoDeCadastro(
                                nome = name,
                                dataNascimento = dob,
                                cpf = cpf,
                                email = email,
                                senha = password
                            )

                            // Envia o objeto para o servidor
                            output.writeObject(pedido)
                            output.flush()

                            // Espera um objeto Resultado vindo do servidor
                            val resposta = input.readObject()
                            val sucesso = (resposta as? Resultado)?.isValido() ?: false

                            input.close()
                            output.close()
                            socket.close()

                            // Atualiza UI na thread principal
                            Handler(Looper.getMainLooper()).post {
                                if (sucesso) {
                                    Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show()
                                    userViewModel.setUsername(name)
                                    navController.navigate("home")
                                } else {
                                    Toast.makeText(context, "CPF inválido ou erro ao salvar cadastro.", Toast.LENGTH_LONG).show()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(context, "Erro ao conectar ao servidor.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }.start()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF88e788))
            ) {
                Text("Cadastrar")
            }

        }

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.Black)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    IcareTheme {
        RegistrationScreen(navController = rememberNavController(), userViewModel = UserViewModel())
    }
}
