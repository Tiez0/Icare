package com.example.icare.view

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
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
import java.util.Calendar

@Composable
fun CalendarioScreen(navController: NavController, userViewModel: UserViewModel) {
    val remedios by userViewModel.remedios.collectAsState()
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Estado para a data selecionada (Padrão: Hoje)
    var dataSelecionada by remember { mutableStateOf("Hoje") }
    var diaDaSemanaIndex by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_WEEK) - 1) } // 0=Dom, 6=Sab

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            val c = Calendar.getInstance()
            c.set(year, month, day)
            dataSelecionada = "$day/${month + 1}/$year"
            diaDaSemanaIndex = c.get(Calendar.DAY_OF_WEEK) - 1
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Filtra remédios que estão marcados para o dia da semana atual
    val remediosDoDia = remedios.filter { it.diasDaSemana[diaDaSemanaIndex] }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Calendário de Doses", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
        Spacer(modifier = Modifier.height(16.dp))

        // Botão para trocar data
        Button(
            onClick = { datePickerDialog.show() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            elevation = ButtonDefaults.buttonElevation(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.CalendarToday, null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver doses para: $dataSelecionada", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (remediosDoDia.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhum remédio para este dia.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(remediosDoDia) { remedio ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(remedio.nome, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("${remedio.dosagem} • ${remedio.frequencia}", fontSize = 14.sp)
                            }
                            Checkbox(checked = false, onCheckedChange = {}) // Checkbox visual
                        }
                    }
                }
            }
        }
    }
}