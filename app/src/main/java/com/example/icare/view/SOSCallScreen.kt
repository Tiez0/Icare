
package com.example.icare.view

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.icare.viewmodel.UserViewModel

@Composable
fun SOSCallScreen(navController: NavController, userViewModel: UserViewModel) {
    val contato by userViewModel.contatoSOS.collectAsState()
    val context = LocalContext.current


    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )


    DisposableEffect(Unit) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }


        val pattern = longArrayOf(0, 500, 1000)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, 0)
        }


        onDispose {
            vibrator.cancel()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD32F2F)), // Vermelho Emergência
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "LIGANDO PARA...",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Nome do Contato
            Text(
                text = contato?.nome ?: "Emergência",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            // Telefone
            Text(
                text = contato?.telefone ?: "",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 24.sp
            )
        }


        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .scale(scale)
                .background(Color.White.copy(alpha = 0.2f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.PhoneInTalk,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.White
            )
        }


        Button(
            onClick = { navController.popBackStack() }, // Volta para a Home
            modifier = Modifier
                .padding(bottom = 50.dp)
                .size(80.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CallEnd,
                contentDescription = "Desligar",
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(40.dp)
            )
        }
    }
}