package com.example.fitnessapp.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fitnessapp.presentation.viewmodel.FitnessViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

/**
 * Экран календаря активности.
 */
@Composable
fun ActivityCalendarScreen(navController: NavHostController, viewModel: FitnessViewModel) {
    val history = viewModel.history.collectAsState().value
    val scrollState = rememberScrollState()
    var expandFraction by remember { mutableFloatStateOf(0.15f) }
    val now = LocalDate.now()
    var selectedMonth by remember { mutableStateOf(YearMonth.from(now)) }
    val caloriesByDate = history.mapNotNull { runCatching { LocalDate.parse(it.date) to it.calories }.getOrNull() }.toMap()
    val activeDates = caloriesByDate.keys
    val selectedDay = if (now.year == selectedMonth.year && now.month == selectedMonth.month) now else selectedMonth.atDay(1)
    val currentWeekStart = selectedDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val currentWeek = (0..6).map { currentWeekStart.plusDays(it.toLong()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Твоя месячная жизнь, дружок",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { selectedMonth = selectedMonth.minusMonths(1) },
                modifier = Modifier.weight(1f)
            ) { Text("←") }

            Button(
                onClick = { selectedMonth = selectedMonth.plusMonths(1) },
                modifier = Modifier.weight(1f)
            ) { Text("→") }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        expandFraction = (expandFraction - dragAmount / 500f).coerceIn(0.15f, 1f)
                    }
                },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(4.dp)
                        .fillMaxWidth(0.18f)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), RoundedCornerShape(99.dp))
                )

                Text(
                    text = selectedMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()) + " ${selectedMonth.year}",
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                )

                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach {
                        Text(text = it, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                    }
                }

                if (expandFraction < 0.35f) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        currentWeek.forEach { day ->
                            DayChip(
                                day = day,
                                active = day in activeDates,
                                selected = day == selectedDay,
                                calories = caloriesByDate[day],
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                } else {
                    val firstDayOfMonth = selectedMonth.atDay(1)
                    val start = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    val days = (0 until 42).map { start.plusDays(it.toLong()) }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        days.chunked(7).forEach { week ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                week.forEach { day ->
                                        DayCell(
                                            day = day,
                                            currentMonth = day.month == selectedMonth.month,
                                            active = day in activeDates,
                                            selected = day == selectedDay,
                                            calories = caloriesByDate[day],
                                            modifier = Modifier.weight(1f)
                                        )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height((expandFraction * 40).dp))
            }
        }

        Text(
            text = "Активных дней в этом месяце: ${activeDates.count { it.year == selectedMonth.year && it.month == selectedMonth.month }}",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
        )
        Text(
            text = "Потяни календарь вниз — он раскроется в полный месяц.",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}

@Composable
private fun DayChip(day: LocalDate, active: Boolean, selected: Boolean, calories: Int? = null, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(
                color = when {
                    selected -> MaterialTheme.colorScheme.primary
                    active -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = day.dayOfMonth.toString(), fontWeight = FontWeight.Bold)
        if (calories != null && calories > 0) {
            Text(text = "${calories} kcal", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
        } else {
            Text(text = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()), fontSize = 11.sp)
        }
    }
}

@Composable
private fun DayCell(day: LocalDate, currentMonth: Boolean, active: Boolean, selected: Boolean, calories: Int? = null, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(
                color = when {
                    selected -> MaterialTheme.colorScheme.primary
                    active -> MaterialTheme.colorScheme.primaryContainer
                    currentMonth -> MaterialTheme.colorScheme.surfaceVariant
                    else -> Color.Transparent
                },
                shape = RoundedCornerShape(14.dp)
            )
            .padding(vertical = 10.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (selected || active) FontWeight.Bold else FontWeight.Normal,
                color = if (currentMonth) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        if (calories != null && calories > 0) {
            Text(text = "${calories} kcal", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
        } else {
            Text(
                text = if (active) "●" else "",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}