package com.example.icare.view

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.icare.ui.theme.IcareTheme
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemediosScreen(navController: NavController) {
    var nome by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var isTipoExpanded by remember { mutableStateOf(false) }
    var forma by remember { mutableStateOf("") }
    var isFormaExpanded by remember { mutableStateOf(false) }
    var dosagem by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    var quantidadeLiquido by remember { mutableStateOf("") }
    var horario by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(
        context,
        { _, h: Int, m: Int ->
            horario = String.format("%02d:%02d", h, m)
        },
        hour,
        minute,
        true // 24 hour format
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome do Medicamento") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(expanded = isTipoExpanded, onExpandedChange = { isTipoExpanded = it }) {
            OutlinedTextField(
                value = tipo,
                onValueChange = {},
                readOnly = true,
                label = { Text("Genérico ou Manipulado") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTipoExpanded) }
            )
            ExposedDropdownMenu(expanded = isTipoExpanded, onDismissRequest = { isTipoExpanded = false }) {
                DropdownMenuItem(text = { Text("Genérico") }, onClick = {
                    tipo = "Genérico"
                    isTipoExpanded = false
                })
                DropdownMenuItem(text = { Text("Manipulado") }, onClick = {
                    tipo = "Manipulado"
                    isTipoExpanded = false
                })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(expanded = isFormaExpanded, onExpandedChange = { isFormaExpanded = it }) {
            OutlinedTextField(
                value = forma,
                onValueChange = {},
                readOnly = true,
                label = { Text("Forma do Medicamento") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isFormaExpanded) }
            )
            ExposedDropdownMenu(expanded = isFormaExpanded, onDismissRequest = { isFormaExpanded = false }) {
                DropdownMenuItem(text = { Text("Cápsula") }, onClick = {
                    forma = "Cápsula"
                    isFormaExpanded = false
                })
                DropdownMenuItem(text = { Text("Líquido") }, onClick = {
                    forma = "Líquido"
                    isFormaExpanded = false
                })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val dosagemUnit = when (forma) {
            "Cápsula" -> "mg"
            "Líquido" -> "ml"
            else -> ""
        }

        OutlinedTextField(
            value = dosagem,
            onValueChange = { if (it.all { char -> char.isDigit() }) dosagem = it },
            label = { Text("Dosagem") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            suffix = { if (dosagem.isNotEmpty() && dosagemUnit.isNotEmpty()) Text(dosagemUnit) }
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (forma == "Cápsula") {
            OutlinedTextField(
                value = quantidade,
                onValueChange = { if (it.all { char -> char.isDigit() }) quantidade = it },
                label = { Text("Quantidade de Cápsulas") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (forma == "Líquido") {
            OutlinedTextField(
                value = quantidadeLiquido,
                onValueChange = { if (it.all { char -> char.isDigit() }) quantidadeLiquido = it },
                label = { Text("Quantidade no Frasco") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                suffix = { if (quantidadeLiquido.isNotEmpty()) Text("ml") }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = horario,
            onValueChange = {},
            label = { Text("Horário") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { timePickerDialog.show() },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = LocalContentColor.current.copy(LocalContentColor.current.alpha),
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            leadingIcon = {
                Icon(Icons.Default.Schedule, contentDescription = "Horário Icon")
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RemediosScreenPreview() {
    IcareTheme {
        RemediosScreen(navController = rememberNavController())
    }
}
