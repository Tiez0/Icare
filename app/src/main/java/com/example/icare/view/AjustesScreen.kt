package com.example.icare.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.icare.viewmodel.UserViewModel

@Composable
fun AjustesScreen(navController: NavController, userViewModel: UserViewModel) {
    val username by userViewModel.username.collectAsState()

    // Estado para controlar a visibilidade do Popup "Sobre"
    var showAboutDialog by remember { mutableStateOf(false) }

    // --- POPUP (ALERT DIALOG) CENTRALIZADO ---
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                // Centraliza o título também, se desejar
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = "Sobre o Aplicativo", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally // Centraliza os itens na coluna
                ) {
                    Text(
                        text = "Projeto Integrador do 4º Semestre de Engenharia de Software do Grupo 20, composto por:",
                        textAlign = TextAlign.Center // Centraliza o texto em si (caso quebre linha)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("• Flávio Augusto Dario de Moraes", textAlign = TextAlign.Center)
                    Text("• Gabriel Henrique Pera Coelho", textAlign = TextAlign.Center)
                    Text("• Narayan Fonseca Jakowatz", textAlign = TextAlign.Center)
                    Text("• Pedro Tiezo Sales Shimizu", textAlign = TextAlign.Center)
                }
            },
            confirmButton = {
                // Centraliza o botão "Fechar"
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TextButton(
                        onClick = { showAboutDialog = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF88e788))
                    ) {
                        Text("Fechar")
                    }
                }
            },
            containerColor = Color.White
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        // Cabeçalho com o Nome
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF88e788))
                .padding(top = 40.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botão de Voltar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Ícone de Perfil Grande
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Foto de Perfil",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Exibe o nome ou o texto padrão
                Text(
                    text = username ?: "Usuário não logado",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (username != null) {
                    Text(
                        text = "Conta Ativa",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- LISTA DE OPÇÕES ---

        OptionItem(icon = Icons.Default.Person, title = "Dados Pessoais")

        OptionItem(icon = Icons.Default.Settings, title = "Configurações")

        OptionItem(
            icon = Icons.Default.Info,
            title = "Sobre o Aplicativo",
            onClick = { showAboutDialog = true }
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Botão de Sair (Logout)
        if (username != null) {
            Button(
                onClick = {
                    userViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f))
            ) {
                Text("Sair da Conta")
            }
        }
    }
}

@Composable
fun OptionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF88e788),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}