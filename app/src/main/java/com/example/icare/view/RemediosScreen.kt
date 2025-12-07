package com.example.icare.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.icare.viewmodel.UserViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemediosScreen(navController: NavController, userViewModel: UserViewModel) {
    var nome by remember { mutableStateOf("") }
    var dosagem by remember { mutableStateOf("") }
    var frequencia by remember { mutableStateOf("") }
    val diasSelecionados = remember { mutableStateListOf(false, false, false, false, false, false, false) }
    val diasLabels = listOf("D", "S", "T", "Q", "Q", "S", "S")

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Medicamento") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF88e788), titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = nome, onValueChange = { nome = it },
                label = { Text("Nome do Remédio") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = dosagem, onValueChange = { dosagem = it },
                label = { Text("Dosagem (ex: 500mg)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = frequencia, onValueChange = { frequencia = it },
                label = { Text("Frequência (ex: 8 em 8h)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Dias da Semana", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                diasLabels.forEachIndexed { index, label ->
                    FilterChip(
                        selected = diasSelecionados[index],
                        onClick = { diasSelecionados[index] = !diasSelecionados[index] },
                        label = { Text(label) },
                        shape = CircleShape,
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF88e788))
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    if (nome.isNotBlank() && dosagem.isNotBlank()) {
                        userViewModel.adicionarRemedio(nome, dosagem, frequencia, diasSelecionados.toList())
                        Toast.makeText(context, "Remédio Salvo!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack() // Volta para Home
                    } else {
                        Toast.makeText(context, "Preencha nome e dosagem", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF88e788))
            ) {
                Text("Salvar Medicamento", fontSize = 18.sp)
            }
        }
    }
}