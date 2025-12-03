package com.example.icare.view

import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.icare.viewmodel.UserViewModel

sealed class Screen(val route: String, val label: String, val icon: @Composable () -> Unit) {
    object Remedios : Screen("remedios", "Remédios", { Icon(Icons.Default.AddCircle, contentDescription = null) })
    object SOS : Screen("sos", "SOS", { Icon(Icons.Default.Sos, contentDescription = null) })
    object Ajustes : Screen("ajustes", "Ajustes", { Icon(Icons.Default.Settings, contentDescription = null) })
}

// A lista agora contém apenas os itens antes de "Agenda"
val itemsBeforeAgenda = listOf(
    Screen.Remedios,
    Screen.SOS,
)

@Composable
fun MainScreen(mainNavController: NavController, userViewModel: UserViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.primary) { 
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val itemColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.primary
                )

                // Itens antes da Agenda
                itemsBeforeAgenda.forEach { screen ->
                    NavigationBarItem(
                        colors = itemColors,
                        icon = screen.icon,
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = { navController.navigate(screen.route) }
                    )
                }

                // Item da Agenda
                NavigationBarItem(
                    colors = itemColors,
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendário") },
                    label = { Text("Agenda") },
                    selected = false, 
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = CalendarContract.CONTENT_URI
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Nenhum aplicativo de agenda encontrado.", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // Item de Ajustes movido para o final
                NavigationBarItem(
                    colors = itemColors,
                    icon = Screen.Ajustes.icon,
                    label = { Text(Screen.Ajustes.label) },
                    selected = currentDestination?.hierarchy?.any { it.route == Screen.Ajustes.route } == true,
                    onClick = { navController.navigate(Screen.Ajustes.route) }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController, startDestination = Screen.SOS.route) { 
                composable(Screen.Remedios.route) { RemediosScreen(navController) }
                composable(Screen.SOS.route) { SOSScreen(navController) } 
                composable(Screen.Ajustes.route) { 
                    AjustesScreen(navController, mainNavController, userViewModel) 
                }
                composable("sos_config") { SOSConfigScreen(navController) }
            }
        }
    }
}
