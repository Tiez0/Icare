package com.example.icare.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
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
import com.example.icare.R
import com.example.icare.ui.theme.IcareTheme
import com.example.icare.viewmodel.UserViewModel

@Composable
fun LoginScreen(navController: NavController, userViewModel: UserViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }


    val loginStatus by userViewModel.loginStatus.collectAsState()
    val mensagemErro by userViewModel.mensagemErro.collectAsState()


    if (loginStatus == true) {
        userViewModel.resetLoginStatus()
        navController.navigate("home") {
            popUpTo("login") { inclusive = true }
        }
    }


    LaunchedEffect(loginStatus, mensagemErro) {
        if (loginStatus != null || mensagemErro != null) {
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

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
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "iCare Logo",
                modifier = Modifier.size(170.dp)
            )
            Spacer(modifier = Modifier.height(100.dp))

            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Person, null, tint = Color.Black) },
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Campo Senha
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color.Black) },
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(image, null, tint = Color.Black)
                    }
                }
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Botão Entrar
            Button(
                onClick = {
                    isLoading = true
                    // Chama o login no servidor
                    userViewModel.fazerLogin(email, password)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF88e788)),
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
            ) {
                if (isLoading) Text("Entrando...") else Text("Entrar")
            }


            if (loginStatus == false && mensagemErro != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = mensagemErro ?: "Erro desconhecido",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


            OutlinedButton(
                onClick = {
                    userViewModel.resetLoginStatus()
                    navController.navigate("registration")
                },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color(0xFF88e788)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF88e788))
            ) {
                Text("Cadastre-se")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    IcareTheme {
        LoginScreen(navController = rememberNavController(), userViewModel = UserViewModel())
    }
}