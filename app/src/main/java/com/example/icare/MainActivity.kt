package com.example.icare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.icare.ui.theme.IcareTheme
import com.example.icare.view.AjustesScreen
import com.example.icare.view.CalendarioScreen
import com.example.icare.view.HomeScreen
import com.example.icare.view.LoginScreen
import com.example.icare.view.RemediosScreen
import com.example.icare.view.RegistrationScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IcareTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("registration") {
            RegistrationScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("remedios") {
            RemediosScreen(navController = navController)
        }
        composable("calendario") {
            CalendarioScreen(navController = navController)
        }
        composable("ajustes") {
            AjustesScreen(navController = navController)
        }
    }
}