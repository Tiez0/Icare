package com.example.icare.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                            label = { Text(screen.label, color = Color.Black, fontSize = 10.sp) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                innerNavController.navigate(screen.route) {
                                    popUpTo(innerNavController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.White.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            val contatoSOS by userViewModel.contatoSOS.collectAsState()

            FloatingActionButton(
                onClick = {
                    if (contatoSOS != null) {
                        // Se já tem contato, LIGA (tela vermelha)
                        navController.navigate("sos_call")
                    } else {
                        // Se não tem, vai configurar
                        navController.navigate("sos_config")
                    }
                },
                containerColor = Color.Red,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Sos, contentDescription = "SOS")
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeContent(innerNavController, userViewModel) }
            composable(Screen.Remedios.route) { RemediosScreen(navController = innerNavController, userViewModel = userViewModel) }
            composable(Screen.Calendario.route) { CalendarioScreen(navController = innerNavController, userViewModel = userViewModel) }
            composable(Screen.Ajustes.route) { AjustesScreen(navController = navController, userViewModel = userViewModel) }
        }
    }
}

@Composable
fun HomeContent(navController: NavController, userViewModel: UserViewModel) {
    val username by userViewModel.username.collectAsState()
    val remedios by userViewModel.remedios.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        Canvas(modifier = Modifier.fillMaxWidth().height(250.dp)) {
            val curveHeight = 50.dp.toPx()
            val path = Path().apply {
                moveTo(0f, 0f); lineTo(size.width, 0f); lineTo(size.width, size.height - curveHeight)
                quadraticBezierTo(size.width / 2, size.height, 0f, size.height - curveHeight); close()
            }
            drawPath(path, color = Color(0xFF88e788))
        }


        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Permite rolar a tela toda
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Olá, ${username?.substringBefore("@") ?: "Usuário"}!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Bem-vindo ao iCare",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Lista de medicamentos",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))


            if (remedios.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().height(100.dp).clickable { navController.navigate(Screen.Remedios.route) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                        Text("Toque para adicionar um remédio", color = Color.Gray)
                    }
                }
            } else {

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    remedios.forEach { remedio ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Column {
                                    Text(
                                        text = remedio.nome,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF2E7D32)
                                    )
                                    Text(
                                        text = remedio.dosagem,
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }


                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF88e788).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = remedio.frequencia,
                                        fontSize = 12.sp,
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home_tab", "Home", Icons.Default.Home)
    object Remedios : Screen("remedios_tab", "Remédios", Icons.Default.Medication)
    object Calendario : Screen("calendario_tab", "Calendário", Icons.Default.DateRange)
    object Ajustes : Screen("ajustes_tab", "Ajustes", Icons.Default.Settings)
}