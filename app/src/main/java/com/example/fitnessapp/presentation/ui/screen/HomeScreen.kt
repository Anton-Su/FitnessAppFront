package com.example.fitnessapp.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.clickable
import com.example.fitnessapp.presentation.viewmodel.FitnessViewModel
import java.util.Locale

/**
 * Главный экран приложения.
 */
@Composable
fun HomeScreen(navController: NavHostController, viewModel: FitnessViewModel) {
    val scrollState = rememberScrollState()
    val userName = viewModel.name.collectAsState().value.trim()
    val userId = viewModel.userId.collectAsState().value
    val stepsToday = viewModel.stepsToday.collectAsState().value
    val height = viewModel.height.collectAsState().value
    val weight = viewModel.weight.collectAsState().value
    val age = viewModel.age.collectAsState().value
    val recommendation = viewModel.recommendation.collectAsState().value

    // Расчёт BMI
    val bmi = if (height > 0 && weight > 0) {
        val heightInMeters = height / 100.0
        (weight / (heightInMeters * heightInMeters)).toInt()
    } else {
        null
    }

    // Расчёт калорий для похудения (формула Миффлина-Сан Жеора)
    // Предполагаем средний уровень активности (1.375) и дефицит 500 ккал/день
    val caloriesForWeightLoss = if (height > 0 && weight > 0 && age > 0) {
        // Базовый метаболизм (примерно для женщины, можно адаптировать)
        val bmr = 10 * weight + 6.25 * height - 5 * age - 161
        // Суточные расходы при среднем уровне активности
        val tdee = bmr * 1.375
        // Для похудения: TDEE - 500 ккал
        (tdee - 500).toInt()
    } else {
        null
    }

    val greeting = if (userName.isBlank()) "Чертила, мы тебе не рады" else "Привет, $userName"

    LaunchedEffect(userId) {
        if (userId > 0) {
            viewModel.loadRecommendation(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.displaySmall.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 38.sp
            )
        )

        Text(
            text = "Ну сегодня разве не прекрасный денёк?",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.SansSerif,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
        )

        if (bmi != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f))
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Индекс массы тела",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "$bmi",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(
                                text = "Высота: ${String.format(Locale.getDefault(), "%.1f", height)} см, вес: ${String.format(Locale.getDefault(), "%.1f", weight)} кг",
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.SansSerif)
                    )
                }
            }
        }

        if (caloriesForWeightLoss != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Для похудения",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "$caloriesForWeightLoss ккал/день",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(
                        text = "Дефицит 500 ккал для потери 0.5 кг в неделю",
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.SansSerif)
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f))
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Ты прошагал",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "$stepsToday шагов",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Text(
                    text = "А слабо сделать это число больше?",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.SansSerif)
                )
            }
        }

        recommendation?.let { exercise ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("exercise/${exercise.id}") },
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Разминка дня",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Text(
                        text = exercise.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "Нажми, чтобы начать",
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.SansSerif)
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(title = "5", subtitle = "минут сегодня", modifier = Modifier.weight(1f))
            StatCard(title = "100%", subtitle = "хорошее настроение", modifier = Modifier.weight(1f))
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
//                Text(
//                    text = "Это знать надо: ",
//                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
//                )
//                ActionHint("Упражнения", "Подбери тренировку под настроение и цель")
//                ActionHint("Календарь активности", "Посмотри, как движется твоя неделя")
//                ActionHint("Настройки", "Сделай приложение удобнее под себя")
            }
        }

        Button(
            onClick = { navController.navigate("exercises") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "Потренироваться",
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
            )
        }

        OutlinedButton(
            onClick = { navController.navigate("calendar") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = "Открыть календарь",
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold)
            )
        }

        OutlinedButton(
            onClick = { navController.navigate("settings") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = "Посмотреть правде в глаза",
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun StatCard(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.SansSerif)
            )
        }
    }
}


