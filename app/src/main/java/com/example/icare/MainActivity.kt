package com.example.icare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.icare.ui.theme.IcareTheme
import com.example.icare.view.HomeScreen
import com.example.icare.view.LoginScreen
import com.example.icare.view.RegistrationScreen
import com.example.icare.view.SOSCallScreen
import com.example.icare.view.SOSConfigScreen
import com.example.icare.viewmodel.UserViewModel

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
    val userViewModel: UserViewModel = viewModel()
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("registration") {
            RegistrationScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("home") {
            HomeScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("sos_config") {
            SOSConfigScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("sos_config") {
            SOSConfigScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("sos_call") {
            SOSCallScreen(navController = navController, userViewModel = userViewModel)
        }
    }
}

