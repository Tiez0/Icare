package com.example.icare.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.icare.ui.theme.IcareTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarioScreen(navController: NavController) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(modifier = Modifier.padding(16.dp)) {
        CalendarHeader(currentMonth = currentMonth,
            onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
            onNextMonth = { currentMonth = currentMonth.plusMonths(1) })
        Spacer(modifier = Modifier.height(16.dp))
        WeekDaysHeader()
        Spacer(modifier = Modifier.height(8.dp))
        CalendarGrid(currentMonth = currentMonth)
    }
}

@Composable
fun CalendarHeader(currentMonth: YearMonth, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mês Anterior")
        }
        Text(
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))} ${currentMonth.year}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Próximo Mês")
        }
    }
}

@Composable
fun WeekDaysHeader() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        val daysOfWeek = DayOfWeek.values()
        // Shift Sunday to the beginning
        val shiftedDays = daysOfWeek.slice(IntRange(6, 6)) + daysOfWeek.slice(IntRange(0, 5))
        for (day in shiftedDays) {
            Text(
                text = day.getDisplayName(TextStyle.NARROW, Locale("pt", "BR")),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(40.dp)
            )
        }
    }
}

@Composable
fun CalendarGrid(currentMonth: YearMonth) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek
    val firstDayPosition = (firstDayOfMonth.value % 7) // Adjust to start from Sunday

    LazyVerticalGrid(columns = GridCells.Fixed(7)) {
        // Add empty boxes for days before the first day of the month
        items(firstDayPosition) {
            Box {}
        }
        items(daysInMonth) { day ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(40.dp)
            ) {
                Text(text = "${day + 1}", textAlign = TextAlign.Center)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarioScreenPreview() {
    IcareTheme {
        CalendarioScreen(navController = rememberNavController())
    }
}
