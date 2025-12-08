package com.example.icare.view

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.icare.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSConfigScreen(navController: NavController, userViewModel: UserViewModel) {
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }


    val contatoSalvo by userViewModel.contatoSOS.collectAsState()
    LaunchedEffect(contatoSalvo) {
        contatoSalvo?.let {
            nome = it.nome
            telefone = it.telefone
        }
    }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar Emergência") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF88e788),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {

            Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                val curveHeight = 50.dp.toPx()
                val path = Path().apply {
                    moveTo(0f, 0f); lineTo(size.width, 0f); lineTo(size.width, size.height - curveHeight)
                    quadraticBezierTo(size.width / 2, size.height, 0f, size.height - curveHeight); close()
                }
                drawPath(path, color = Color(0xFF88e788))
            }

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))


                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.White
                )

                Spacer(modifier = Modifier.height(80.dp))

                Text(
                    text = "Quem devemos contatar?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
                Text(
                    text = "Ao apertar o botão SOS, simularemos uma ligação para este número.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do Contato (ex: Mãe)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = telefone,
                    onValueChange = { telefone = it },
                    label = { Text("Telefone (ex: 190)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = { Icon(Icons.Default.Phone, null) },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (nome.isNotBlank() && telefone.isNotBlank()) {
                            userViewModel.salvarContatoSOS(nome, telefone)
                            Toast.makeText(context, "Contato de Emergência Salvo!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF88e788))
                ) {
                    Text("Salvar Configuração", fontSize = 18.sp)
                }
            }
        }
    }
}