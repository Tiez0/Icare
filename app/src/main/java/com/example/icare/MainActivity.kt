package com.example.icare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.icare.ui.theme.IcareTheme
import com.example.icare.view.LoginScreen
import com.example.icare.view.MainScreen
import com.example.icare.view.RegistrationScreen
import com.example.icare.view.SOSConfigScreen
import com.example.icare.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userViewModel: UserViewModel = viewModel()
            // Lê o estado de alto contraste do ViewModel
            val useHighContrast by userViewModel.isHighContrastEnabled.collectAsState()

            // Aplica o tema com base no estado lido
            IcareTheme(useHighContrast = useHighContrast) {
                AppNavigation(userViewModel = userViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(userViewModel: UserViewModel) { // Recebe o ViewModel como parâmetro
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("registration") {
            RegistrationScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("main") { 
            MainScreen(mainNavController = navController, userViewModel = userViewModel)
        }
        composable("sos_config") {
            SOSConfigScreen(navController = navController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    IcareTheme {
        // O Preview usará uma instância própria do ViewModel
        AppNavigation(userViewModel = viewModel())
    }
}
