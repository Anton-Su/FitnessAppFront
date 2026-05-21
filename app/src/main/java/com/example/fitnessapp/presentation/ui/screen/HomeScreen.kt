package com.example.fitnessapp.presentation.ui.screen

import android.util.Log
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.clickable
import com.example.fitnessapp.domain.model.Gender
import com.example.fitnessapp.navigation.Screen
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
    val gender = viewModel.gender.collectAsState().value
    val statusActive = viewModel.statusActive.collectAsState().value
    val recommendation = viewModel.recommendation.collectAsState().value
    val doEverytimeTask = viewModel.doEverytimeTask.collectAsState().value
    val caloriesBurned = viewModel.caloriesBurned.collectAsState().value

    // Расчёт BMI
    val bmi = if (height > 0 && weight > 0) {
        val heightInMeters = height / 100.0
        weight / (heightInMeters * heightInMeters)
    } else {
        null
    }
    val bmiCategory = bmi?.let {
        when {
            it < 16.0 -> BmiCategory.UNDERWEIGHT_SEVERE
            it < 17.0 -> BmiCategory.UNDERWEIGHT_MODERATE
            it < 18.5 -> BmiCategory.UNDERWEIGHT_MILD
            it < 25.0 -> BmiCategory.NORMAL
            it < 30.0 -> BmiCategory.OVERWEIGHT
            it < 35.0 -> BmiCategory.OBESITY_I
            it < 40.0 -> BmiCategory.OBESITY_II
            else -> BmiCategory.OBESITY_III
        }
    }
    val bmiAccentColor = when (bmiCategory) {
        BmiCategory.NORMAL -> Color(0xFF2E7D32)
        BmiCategory.OVERWEIGHT -> Color(0xFFE67E22)
        BmiCategory.OBESITY_I -> Color(0xFFEF5350)
        BmiCategory.OBESITY_II -> Color(0xFFD32F2F)
        BmiCategory.OBESITY_III -> Color(0xFF8B0000)
        BmiCategory.UNDERWEIGHT_MILD -> Color(0xFF1E88E5)
        BmiCategory.UNDERWEIGHT_MODERATE -> Color(0xFF1565C0)
        BmiCategory.UNDERWEIGHT_SEVERE -> Color(0xFF0D47A1)
        null -> MaterialTheme.colorScheme.primary
    }
    val bmiCardColor = when (bmiCategory) {
        BmiCategory.UNDERWEIGHT_MILD -> Color(0xFFE3F2FD)
        BmiCategory.UNDERWEIGHT_MODERATE -> Color(0xFFD6EAF8)
        BmiCategory.UNDERWEIGHT_SEVERE -> Color(0xFFC5DDF4)
        BmiCategory.NORMAL -> Color(0xFFE8F5E9)
        BmiCategory.OVERWEIGHT -> Color(0xFFFFF3E0)
        BmiCategory.OBESITY_I -> Color(0xFFFFE0E0)
        BmiCategory.OBESITY_II -> Color(0xFFF8C9C9)
        BmiCategory.OBESITY_III -> Color(0xFFF2B4B4)
        null -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    }
    val shouldShowWeightLossCard = bmiCategory != BmiCategory.NORMAL

    // Расчёт калорий для похудения (формула Миффлина-Сан Жеора)
    // Учитываем пол и уровень активности
    val caloriesForWeightLoss = if (height > 0 && weight > 0 && age > 0) {
        val bmr = 10 * weight + 6.25 * height - 5 * age + if (gender == Gender.MALE) 5 else -161
        val activityMultiplier = if (statusActive) 1.55 else 1.375
        val tdee = bmr * activityMultiplier
        (tdee - 500).toInt()
    } else {
        null
    }

    val greeting = if (userName.isBlank()) "Чертила, мы тебе не рады" else "Привет, $userName"

    LaunchedEffect(Unit) {
        viewModel.ensureDailyDoEverytimeTaskReset()
    }

    LaunchedEffect(userId, doEverytimeTask) {
        if (userId > 0 && !doEverytimeTask) {
            viewModel.loadRecommendation(userId)
        } else if (doEverytimeTask) {
            viewModel.clearRecommendation()
        }
    }

    Scaffold(
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
            if (!doEverytimeTask) recommendation?.let { exercise ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(Screen.ExerciseDetail.createRoute(exercise.id, fromRecommendation = true))
                        },
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Разомнёмся?",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )

                        Text(
                            text = exercise.name,
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
        if (bmi != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = bmiCardColor)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Индекс массы тела",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = bmiAccentColor
                        )
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f", bmi),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(
                        text = when (bmiCategory) {
                            BmiCategory.UNDERWEIGHT_SEVERE -> "Выраженный дефицит веса"
                            BmiCategory.UNDERWEIGHT_MODERATE -> "Дефицит веса средней степени"
                            BmiCategory.UNDERWEIGHT_MILD -> "Небольшой дефицит веса"
                            BmiCategory.NORMAL -> "Нормальный вес"
                            BmiCategory.OVERWEIGHT -> "Избыточный вес"
                            BmiCategory.OBESITY_I -> "Ожирение I степени"
                            BmiCategory.OBESITY_II -> "Ожирение II степени"
                            BmiCategory.OBESITY_III -> "Ожирение III степени"
                            null -> ""
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.SemiBold,
                            color = bmiAccentColor
                        )
                    )
                }
            }
        }
        if (caloriesForWeightLoss != null && shouldShowWeightLossCard) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Твоя прямая задача:",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color(0xFF7B1FA2)
                        )
                    )
                    Text(
                        text = "$caloriesBurned ккал / $caloriesForWeightLoss ккал на сегодня",
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
        Log.e("HomeScreen", "doEverytimeTask: $doEverytimeTask, recommendation: ${recommendation?.name ?: "null"}")


        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(title = "5", subtitle = "минут сегодня", modifier = Modifier.weight(1f))
            StatCard(title = "100%", subtitle = "хорошее настроение", modifier = Modifier.weight(1f))
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
                text = "Оценить свой успех",
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold)
            )
        }

        OutlinedButton(
            onClick = { navController.navigate(Screen.Settings.route) },
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
}

private enum class BmiCategory {
    UNDERWEIGHT_SEVERE,
    UNDERWEIGHT_MODERATE,
    UNDERWEIGHT_MILD,
    NORMAL,
    OVERWEIGHT,
    OBESITY_I,
    OBESITY_II,
    OBESITY_III
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


