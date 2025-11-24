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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.icare.viewmodel.UserViewModel

@Composable
fun HomeScreen(navController: NavController, userViewModel: UserViewModel) {
    val innerNavController = rememberNavController()
    val items = listOf(
        Screen.Home,
        Screen.Remedios,
        Screen.Calendario,
        Screen.Ajustes
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
                    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.route, tint = Color.Black) },
                            label = { Text(screen.route, color = Color.Black) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                innerNavController.navigate(screen.route) {
                                    popUpTo(innerNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
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
        NavHost(
            innerNavController,
            startDestination = Screen.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { Text("Bem-vindo à Home!") }
            composable(Screen.Remedios.route) { RemediosScreen(navController = innerNavController) }
            composable(Screen.Calendario.route) { CalendarioScreen(navController = innerNavController) }
            composable(Screen.Ajustes.route) { AjustesScreen(navController = navController, userViewModel = userViewModel) }
        }
    }
}

sealed class Screen(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("Home", Icons.Default.Home)
    object Remedios : Screen("Remédios", Icons.Default.Medication)
    object Calendario : Screen("Calendário", Icons.Default.DateRange)
    object Ajustes : Screen("Ajustes", Icons.Default.Settings)
}