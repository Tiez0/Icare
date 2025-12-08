package com.example.icare.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.icare.viewmodel.UserViewModel

@Composable
fun AjustesScreen(navController: NavController, userViewModel: UserViewModel) {
    val username by userViewModel.username.collectAsState()
    val context = LocalContext.current

    // Estado para controlar a visibilidade do Popup "Sobre"
    var showAboutDialog by remember { mutableStateOf(false) }

    // Estado para controlar a visibilidade do Popup de "Configuração SOS"
    var showSOSConfigDialog by remember { mutableStateOf(false) }

    // Dados do contato SOS vindos da ViewModel
    val contatoSalvo by userViewModel.contatoSOS.collectAsState()

    // --- POPUP (ALERT DIALOG) SOBRE ---
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = "Sobre o Aplicativo", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Projeto Integrador do 4º Semestre de Engenharia de Software do Grupo 20, composto por:",
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("• Flávio Augusto Dario de Moraes", textAlign = TextAlign.Center)
                    Text("• Gabriel Henrique Pera Coelho", textAlign = TextAlign.Center)
                    Text("• Narayan Fonseca Jakowatz", textAlign = TextAlign.Center)
                    Text("• Pedro Tiezo Sales Shimizu", textAlign = TextAlign.Center)
                }
            },
            confirmButton = {
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

    // --- POPUP (ALERT DIALOG) CONFIGURAÇÃO SOS ---
    if (showSOSConfigDialog) {
        // Estados locais para os campos de texto do diálogo
        // Inicializa com o valor salvo ou vazio
        var nomeTemp by remember { mutableStateOf(contatoSalvo?.nome ?: "") }
        var telefoneTemp by remember { mutableStateOf(contatoSalvo?.telefone ?: "") }

        AlertDialog(
            onDismissRequest = { showSOSConfigDialog = false },
            title = {
                Text(text = "Configurar Emergência", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            },
            text = {
                Column {
                    Text(
                        text = "Defina quem será contatado ao acionar o botão SOS.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = nomeTemp,
                        onValueChange = { nomeTemp = it },
                        label = { Text("Nome do Contato") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = telefoneTemp,
                        onValueChange = { telefoneTemp = it },
                        label = { Text("Telefone") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (nomeTemp.isNotBlank() && telefoneTemp.isNotBlank()) {
                            userViewModel.salvarContatoSOS(nomeTemp, telefoneTemp)
                            Toast.makeText(context, "Contato de Emergência Salvo!", Toast.LENGTH_SHORT).show()
                            showSOSConfigDialog = false
                        } else {
                            Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF88e788))
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSOSConfigDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                ) {
                    Text("Cancelar")
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

        // Botão modificado para abrir o diálogo de Configuração SOS
        OptionItem(
            icon = Icons.Default.Settings,
            title = "Configurações (SOS)",
            onClick = { showSOSConfigDialog = true }
        )

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