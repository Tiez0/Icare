package com.example.icare.view

import android.Manifest
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.CalendarContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.icare.ui.theme.IcareTheme
import java.util.Calendar
import java.util.TimeZone

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
    var tipoDeUso by remember { mutableStateOf("") }
    var isUsoExpanded by remember { mutableStateOf(false) }
    var duracaoTratamento by remember { mutableStateOf("") }
    var horario by remember { mutableStateOf("") }

    val daysOfWeek = listOf("DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SAB")
    var selectedDays by remember { mutableStateOf(emptySet<String>()) }

    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allPermissionsGranted = permissions.values.all { it }
        if (allPermissionsGranted) {
            Toast.makeText(context, "Permissão concedida!", Toast.LENGTH_SHORT).show()
            addEventToCalendar(
                context = context, nome = nome, horario = horario, tipoDeUso = tipoDeUso,
                selectedDays = selectedDays, duracaoTratamento = duracaoTratamento.toIntOrNull() ?: 0
            )
            createLowStockWarningEvent(
                context = context, nome = nome, forma = forma, 
                quantidade = quantidade.toIntOrNull(), 
                quantidadeLiquido = quantidadeLiquido.toIntOrNull(), 
                dosagem = dosagem.toIntOrNull(),
                selectedDays = selectedDays, horario = horario
            )
        } else {
            Toast.makeText(context, "Permissão negada. Não é possível adicionar o alerta.", Toast.LENGTH_LONG).show()
        }
    }

    val isFormValid = nome.isNotBlank() &&
            tipo.isNotBlank() &&
            forma.isNotBlank() &&
            dosagem.isNotBlank() &&
            (when (forma) {
                "Cápsula" -> quantidade.isNotBlank()
                "Líquido" -> quantidadeLiquido.isNotBlank()
                else -> false
            }) &&
            tipoDeUso.isNotBlank() &&
            (if (tipoDeUso == "Uso Temporário") duracaoTratamento.isNotBlank() else true) &&
            horario.isNotBlank() &&
            selectedDays.isNotEmpty()

    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(
        context,
        { _, h: Int, m: Int -> horario = String.format("%02d:%02d", h, m) },
        hour,
        minute,
        true
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
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
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTipoExpanded) }
            )
            ExposedDropdownMenu(expanded = isTipoExpanded, onDismissRequest = { isTipoExpanded = false }) {
                DropdownMenuItem(text = { Text("Genérico") }, onClick = { tipo = "Genérico"; isTipoExpanded = false })
                DropdownMenuItem(text = { Text("Manipulado") }, onClick = { tipo = "Manipulado"; isTipoExpanded = false })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(expanded = isFormaExpanded, onExpandedChange = { isFormaExpanded = it }) {
            OutlinedTextField(
                value = forma,
                onValueChange = {},
                readOnly = true,
                label = { Text("Forma do Medicamento") },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isFormaExpanded) }
            )
            ExposedDropdownMenu(expanded = isFormaExpanded, onDismissRequest = { isFormaExpanded = false }) {
                DropdownMenuItem(text = { Text("Cápsula") }, onClick = { forma = "Cápsula"; isFormaExpanded = false })
                DropdownMenuItem(text = { Text("Líquido") }, onClick = { forma = "Líquido"; isFormaExpanded = false })
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

        ExposedDropdownMenuBox(expanded = isUsoExpanded, onExpandedChange = { isUsoExpanded = it }) {
            OutlinedTextField(
                value = tipoDeUso,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de Uso") },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isUsoExpanded) }
            )
            ExposedDropdownMenu(expanded = isUsoExpanded, onDismissRequest = { isUsoExpanded = false }) {
                DropdownMenuItem(text = { Text("Uso Contínuo") }, onClick = { tipoDeUso = "Uso Contínuo"; isUsoExpanded = false })
                DropdownMenuItem(text = { Text("Uso Temporário") }, onClick = { tipoDeUso = "Uso Temporário"; isUsoExpanded = false })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (tipoDeUso == "Uso Temporário") {
            OutlinedTextField(
                value = duracaoTratamento,
                onValueChange = { if (it.all { char -> char.isDigit() }) duracaoTratamento = it },
                label = { Text("Duração do Tratamento (dias)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                suffix = { if (duracaoTratamento.isNotEmpty()) Text("dias") }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = horario,
            onValueChange = {},
            label = { Text("Horário") },
            modifier = Modifier.fillMaxWidth().clickable { timePickerDialog.show() },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = LocalContentColor.current.copy(LocalContentColor.current.alpha),
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = "Horário Icon") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Dias da Semana", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            daysOfWeek.take(4).forEach { day ->
                DayButton(day = day, isSelected = selectedDays.contains(day)) {
                    selectedDays = if (selectedDays.contains(day)) selectedDays - day else selectedDays + day
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            daysOfWeek.drop(4).forEach { day ->
                DayButton(day = day, isSelected = selectedDays.contains(day)) {
                    selectedDays = if (selectedDays.contains(day)) selectedDays - day else selectedDays + day
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val permissions = arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
                if (permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }) {
                    addEventToCalendar(
                        context = context, nome = nome, horario = horario, tipoDeUso = tipoDeUso,
                        selectedDays = selectedDays, duracaoTratamento = duracaoTratamento.toIntOrNull() ?: 0
                    )
                    createLowStockWarningEvent(
                        context = context, nome = nome, forma = forma, 
                        quantidade = quantidade.toIntOrNull(), 
                        quantidadeLiquido = quantidadeLiquido.toIntOrNull(), 
                        dosagem = dosagem.toIntOrNull(),
                        selectedDays = selectedDays, horario = horario
                    )
                } else {
                    permissionLauncher.launch(permissions)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF88e788))
        ) {
            Text("Adicionar Remédio e Criar Alerta")
        }
    }
}

private fun addEventToCalendar(
    context: android.content.Context,
    nome: String,
    horario: String,
    tipoDeUso: String,
    selectedDays: Set<String>,
    duracaoTratamento: Int
) {
    if (horario.isBlank() || selectedDays.isEmpty()) return

    val (hour, minute) = horario.split(":").map { it.toInt() }

    val startMillis = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (timeInMillis < System.currentTimeMillis()) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }.timeInMillis

    val rrule = StringBuilder("FREQ=WEEKLY;BYDAY=")
    val dayMap = mapOf("DOM" to "SU", "SEG" to "MO", "TER" to "TU", "QUA" to "WE", "QUI" to "TH", "SEX" to "FR", "SAB" to "SA")
    rrule.append(selectedDays.mapNotNull { dayMap[it] }.joinToString(","))

    if (tipoDeUso == "Uso Temporário" && duracaoTratamento > 0) {
        val endDate = Calendar.getInstance().apply {
            timeInMillis = startMillis
            add(Calendar.DAY_OF_YEAR, duracaoTratamento)
        }
        val untilString = String.format(
            "%04d%02d%02dT235959Z",
            endDate.get(Calendar.YEAR),
            endDate.get(Calendar.MONTH) + 1,
            endDate.get(Calendar.DAY_OF_MONTH)
        )
        rrule.append(";UNTIL=$untilString")
    }

    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, "Tomar $nome")
        putExtra(CalendarContract.Events.DESCRIPTION, "Lembrete gerado pelo Icare App.")
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startMillis + 60 * 60 * 1000)
        putExtra(CalendarContract.Events.RRULE, rrule.toString())
        putExtra(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "Nenhum app de agenda encontrado.", Toast.LENGTH_SHORT).show()
    }
}

private fun createLowStockWarningEvent(
    context: android.content.Context,
    nome: String,
    forma: String,
    quantidade: Int?,
    quantidadeLiquido: Int?,
    dosagem: Int?,
    selectedDays: Set<String>,
    horario: String
) {
    if (selectedDays.isEmpty() || horario.isBlank()) return

    val dosesToWarning: Int
    val warningMessage: String

    if (forma == "Cápsula") {
        if (quantidade == null || quantidade <= 5) return
        dosesToWarning = quantidade - 5
        warningMessage = "O REMÉDIO ${nome.uppercase()} ESTÁ PRESTES A ACABAR, (5 UNIDADES RESTANTES)"
    } else if (forma == "Líquido") {
        if (quantidadeLiquido == null || dosagem == null || dosagem == 0) return
        val totalDoses = quantidadeLiquido / dosagem
        val warningThresholdDoses = 3 // Avisar quando restarem 3 doses
        if (totalDoses <= warningThresholdDoses) return
        dosesToWarning = totalDoses - warningThresholdDoses
        warningMessage = "O REMÉDIO ${nome.uppercase()} ESTÁ PRESTES A ACABAR, (APROXIMADAMENTE 3 DOSES RESTANTES)"
    } else {
        return
    }

    val calendarDays = mapOf(
        "DOM" to Calendar.SUNDAY, "SEG" to Calendar.MONDAY, "TER" to Calendar.TUESDAY,
        "QUA" to Calendar.WEDNESDAY, "QUI" to Calendar.THURSDAY, "SEX" to Calendar.FRIDAY, "SAB" to Calendar.SATURDAY
    )
    val selectedCalDays = selectedDays.mapNotNull { calendarDays[it] }.toSet()

    val (hour, minute) = horario.split(":").map { it.toInt() }
    val warningCal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        if (timeInMillis < System.currentTimeMillis()) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    var dosesCounted = 0
    while (dosesCounted < dosesToWarning) {
        warningCal.add(Calendar.DAY_OF_YEAR, 1)
        val currentDayOfWeek = warningCal.get(Calendar.DAY_OF_WEEK)
        if (selectedCalDays.contains(currentDayOfWeek)) {
            dosesCounted++
        }
    }

    val warningStartMillis = Calendar.getInstance().apply {
        timeInMillis = warningCal.timeInMillis
        set(Calendar.HOUR_OF_DAY, 9) // Aviso às 9h da manhã
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, warningMessage)
        putExtra(CalendarContract.Events.DESCRIPTION, "É hora de providenciar mais do seu medicamento.")
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, warningStartMillis)
        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, warningStartMillis + 60 * 60 * 1000)
        putExtra(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        // Opcional
    }
}


@Composable
fun DayButton(day: String, isSelected: Boolean, onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(day)
    }
}

@Preview(showBackground = true)
@Composable
fun RemediosScreenPreview() {
    IcareTheme {
        RemediosScreen(navController = rememberNavController())
    }
}
