package com.example.icare.view

import android.app.DatePickerDialog
import android.widget.DatePicker
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

    // Observa o resultado E a mensagem de erro vinda do servidor
    val cadastroStatus by userViewModel.cadastroStatus.collectAsState()
    val mensagemErro by userViewModel.mensagemErro.collectAsState()

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Lógica de navegação automática em caso de sucesso
    if (cadastroStatus == true) {
        userViewModel.resetCadastroStatus()
        navController.navigate("home") {
            popUpTo("login") { inclusive = true }
        }
    }

    // Validações locais (Frontend)
    val isEmailValid = email.isNotBlank() && email.contains("@")
    val emailError = email.isNotBlank() && !email.contains("@")
    val passwordMismatch = password.isNotBlank() && confirmPassword.isNotBlank() && password != confirmPassword

    val isFormValid = name.isNotBlank() &&
            dob.isNotBlank() &&
            cpf.length == 11 &&
            isEmailValid &&
            password.isNotBlank() &&
            confirmPassword == password &&
            !passwordMismatch

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
        // Fundo Verde Ondulado
        Canvas(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            val curveHeight = 50.dp.toPx()
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height - curveHeight)
                quadraticBezierTo(size.width / 2, size.height, 0f, size.height - curveHeight)
                close()
            }
            drawPath(path, color = Color(0xFF88e788))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text("Criar Nova Conta", fontSize = 32.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Spacer(modifier = Modifier.height(135.dp))

            // Campos de Texto
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nome Completo") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                leadingIcon = { Icon(Icons.Default.Person, null, tint = Color.Black) }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = dob, onValueChange = { dob = it },
                label = { Text("Data de Nascimento") }, modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = LocalContentColor.current.copy(alpha = 1f),
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = Color.Black
                ),
                leadingIcon = { Icon(Icons.Default.DateRange, null) }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = cpf,
                onValueChange = { if (it.length <= 11 && it.all { c -> c.isDigit() }) cpf = it },
                label = { Text("CPF") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Default.Person, null, tint = Color.Black) }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                isError = emailError,
                leadingIcon = { Icon(Icons.Default.Email, null, tint = Color.Black) }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Senha") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color.Black) },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(image, null, tint = Color.Black)
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword, onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Senha") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color.Black) },
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(image, null, tint = Color.Black)
                    }
                },
                isError = passwordMismatch,
                supportingText = { if (passwordMismatch) Text("As senhas não correspondem", color = MaterialTheme.colorScheme.error) }
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Botão de Cadastro
            Button(
                onClick = {
                    userViewModel.cadastrarUsuario(name, cpf, email, password)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF88e788)),
                enabled = isFormValid
            ) {
                Text("Cadastrar")
            }

            // MENSAGEM DE ERRO (Vinda do Servidor)
            if (cadastroStatus == false && mensagemErro != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = mensagemErro ?: "Erro desconhecido",
                    color = Color.Red,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
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