package com.example.fitnessapp.presentation.ui.screen

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fitnessapp.presentation.ui.component.FitnessTopBar
import com.example.fitnessapp.presentation.viewmodel.FitnessViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

/**
 * Экран календаря активности.
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun ActivityCalendarScreen(navController: NavHostController, viewModel: FitnessViewModel) {
    val remoteHistory = viewModel.remoteHistory.collectAsState().value
    val userId = viewModel.userId.collectAsState().value
    val caloriesBurned = viewModel.caloriesBurned.collectAsState().value
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        coroutineScope.launch {
            val json = withContext(Dispatchers.IO) { viewModel.exportHistoryJson() }
            if (json.isNullOrBlank()) {
                Toast.makeText(context, "Не удалось подготовить историю", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val saved = withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openOutputStream(uri)?.use { output ->
                        output.write(json.toByteArray())
                        output.flush()
                    } ?: error("OutputStream is null")
                    true
                }.getOrElse { false }
            }

            Toast.makeText(
                context,
                if (saved) "История экспортирована в JSON" else "Не удалось сохранить файл",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    var expandFraction by remember { mutableFloatStateOf(0.15f) }
    val now = LocalDate.now()
    var selectedMonth by remember { mutableStateOf(YearMonth.from(now)) }
    LaunchedEffect(userId) {
        if (userId > 0) {
            viewModel.loadRemoteHistory(userId)
        }
    }
    val caloriesByDate: Map<LocalDate, Int> = remember(remoteHistory, now, caloriesBurned) {
        val mapped = mutableMapOf<LocalDate, Int>()
        remoteHistory.forEach { item ->
            item.date.toCalendarLocalDate()?.let { day -> mapped[day] = item.calories }
        }
        if (caloriesBurned > 0) {
            mapped[now] = maxOf(mapped[now] ?: 0, caloriesBurned)
        }
        mapped
    }
    val activeDates: Set<LocalDate> = caloriesByDate.filterValues { it > 1000 }.keys
    val activeDaysInSelectedMonth = activeDates.count { it.year == selectedMonth.year && it.month == selectedMonth.month }
    val caloriesToday = caloriesByDate[now] ?: 0
    val selectedDay = if (now.year == selectedMonth.year && now.month == selectedMonth.month) now else selectedMonth.atDay(1)
    val currentWeekStart = selectedDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val currentWeek = (0..6).map { currentWeekStart.plusDays(it.toLong()) }

    Scaffold(
        topBar = { FitnessTopBar(title = "Календарь", canNavigateBack = true, onBackClick = { navController.navigateUp() }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(animationSpec = tween(durationMillis = 220))
                .pointerInput(Unit) {
                    // Support both vertical drag (to expand/collapse) and horizontal swipe to change months
                    var horizontalAccum = 0f
                    detectDragGestures(
                        onDragStart = { horizontalAccum = 0f },
                        onDrag = { change, dragAmount ->
                            // vertical: adjust expandFraction
                            val verticalDelta = dragAmount.y
                            expandFraction = (expandFraction - verticalDelta / 500f).coerceIn(0.15f, 1f)

                            // horizontal: accumulate to detect swipe on end
                            horizontalAccum += dragAmount.x
                            change.consume()
                        },
                        onDragEnd = {
                            val threshold = 120f // pixels
                            if (horizontalAccum <= -threshold) {
                                // swipe left -> next month
                                selectedMonth = selectedMonth.plusMonths(1)
                            } else if (horizontalAccum >= threshold) {
                                // swipe right -> previous month
                                selectedMonth = selectedMonth.minusMonths(1)
                            }
                            horizontalAccum = 0f
                        }
                    )
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

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().padding(start = 0.dp)) {
                    listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach {
                        Text(text = it, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
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
            }
        }
            Text(
                text = "Потяни календарь вниз — он раскроется в полный месяц.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        Text(
            text = "Активных дней в этом месяце ( >= 1000 kcal) : $activeDaysInSelectedMonth",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
        )
        Text(
            text = "Сегодня: ${caloriesToday} kcal",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
        )
        Button(
            onClick = {
                exportLauncher.launch("history_export.json")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Экспорт истории")
        }
        }
    }
}

private fun String.toCalendarLocalDate(): LocalDate? {
    val value = trim()
    if (value.isBlank()) return null

    return runCatching { OffsetDateTime.parse(value).toLocalDate() }
        .recoverCatching { Instant.parse(value).atZone(ZoneId.systemDefault()).toLocalDate() }
        .recoverCatching { LocalDate.parse(value) }
        .getOrNull()
}

@Composable
private fun DayChip(modifier: Modifier = Modifier, day: LocalDate, active: Boolean, selected: Boolean, calories: Int? = null) {
    Column(
        modifier = modifier
            .background(
                color = when {
                    selected -> MaterialTheme.colorScheme.primary
                    active -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = day.dayOfMonth.toString(), fontWeight = FontWeight.Bold)
        if (calories != null && calories > 0) {
            Text(text = "${calories}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
        } else {
            Text(text = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()), fontSize = 11.sp)
        }
    }
}

@Composable
private fun DayCell(modifier: Modifier = Modifier, day: LocalDate, currentMonth: Boolean, active: Boolean, selected: Boolean, calories: Int? = null) {
    Column(
        modifier = modifier
            .background(
                color = when {
                    selected -> MaterialTheme.colorScheme.primary
                    active -> MaterialTheme.colorScheme.errorContainer
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
            Text(text = "${calories}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
        } else {
            Text(
                text = if (active) "●" else "",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}