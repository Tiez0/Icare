package com.example.icare.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.icare.ui.theme.IcareTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSConfigScreen(navController: NavController) {
    var nome by remember { mutableStateOf("") }
    var sobrenome by remember { mutableStateOf("") }
    var countryCode by remember { mutableStateOf("+55") }
    var isCountryExpanded by remember { mutableStateOf(false) }
    var ddd by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var endereco by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = sobrenome,
            onValueChange = { sobrenome = it },
            label = { Text("Sobrenome") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            ExposedDropdownMenuBox(
                expanded = isCountryExpanded,
                onExpandedChange = { isCountryExpanded = it },
                modifier = Modifier.weight(0.3f)
            ) {
                OutlinedTextField(
                    value = countryCode,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCountryExpanded) }
                )
                ExposedDropdownMenu(expanded = isCountryExpanded, onDismissRequest = { isCountryExpanded = false }) {
                    DropdownMenuItem(text = { Text("+55 (Brasil)") }, onClick = {
                        countryCode = "+55"
                        isCountryExpanded = false
                    })
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = ddd,
                onValueChange = { ddd = it },
                label = { Text("DDD") },
                modifier = Modifier.weight(0.2f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = telefone,
                onValueChange = { telefone = it },
                label = { Text("Telefone") },
                modifier = Modifier.weight(0.5f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = endereco,
            onValueChange = { endereco = it },
            label = { Text("Endereço") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = { /* TODO: Save contact logic */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Salvar Contato")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SOSConfigScreenPreview() {
    IcareTheme {
        SOSConfigScreen(navController = rememberNavController())
    }
}
