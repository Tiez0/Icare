package com.example.icare.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.icare.ui.theme.IcareTheme

@Composable
fun HomeScreen(navController: NavController) {
    var selectedItem by remember { mutableStateOf("Home") }
    val items = listOf("Home", "Remédios", "Calendário", "Ajustes")
    val icons = mapOf(
        "Home" to Icons.Default.Home,
        "Remédios" to Icons.Default.Medication,
        "Calendário" to Icons.Default.DateRange,
        "Ajustes" to Icons.Default.Settings
    )

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF88e788),
                contentColor = Color.Black
            ) {
                NavigationBar(
                    containerColor = Color.Transparent
                ) {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(icons[screen]!!, contentDescription = screen, tint = Color.Black) },
                            label = { Text(screen, color = Color.Black) },
                            selected = selectedItem == screen,
                            onClick = {
                                selectedItem = screen
                                when (screen) {
                                    "Home" -> navController.navigate("home")
                                    "Remédios" -> navController.navigate("remedios")
                                    "Calendário" -> navController.navigate("calendario")
                                    "Ajustes" -> navController.navigate("ajustes")
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.White.copy(alpha = 0.5f),
                                unselectedTextColor = Color.Black,
                                selectedTextColor = Color.Black,
                                selectedIconColor = Color.Black,
                                unselectedIconColor = Color.Black
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Content for each screen will go here later
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    IcareTheme {
        HomeScreen(navController = rememberNavController())
    }
}
