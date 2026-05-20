package com.example.fitnessapp.presentation.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fitnessapp.presentation.ui.component.StatCard
import com.example.fitnessapp.presentation.viewmodel.FitnessViewModel

/**
 * Экран выбора: перейти к регистрации или к входу.
 *
 * Cтартовый экран приложения.
 *
 * @param navController контроллер навигации
 * @param viewModel экземпляр [FitnessViewModel], передаётся из MainActivity
 */

@Composable
fun AuthChoiceScreen(navController: NavHostController, viewModel: FitnessViewModel) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.92f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                color = Color.White.copy(alpha = 0.12f),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Добро пожаловать в FitnessApp!",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                    )
                    Text(
                        text = "Мы знаем, это сложно...",
                        color = Color.Black,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 34.sp,
                            lineHeight = 30.sp
                        )
                    )
                    Text(
                        text = "Мы знаем, это не твой первый раз...",
                        color = Color.Gray,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            lineHeight = 30.sp
                        )
                    )

                    Text(
                        text = "Ты ж не сдался?",
                        color = Color.Red,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 30.sp,
                            fontSize = 20.sp,
                        )
                    )

                    Text(
                        text = "Сделай первый шаг сегодня - через месяц первые шаги будут восхищённо делать тебе другие",
                        color = Color.Blue.copy(alpha = 0.88f),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.SansSerif,
                            lineHeight = 24.sp
                        )
                    )
                    Text(
                        text = "©Твоё приложение здравого смысла",
                        color = Color.Gray.copy(alpha = 0.88f),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.SansSerif,
                        )
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(title = "Пара дней", subtitle = "на первую привычку", modifier = Modifier.weight(1f))
                StatCard(title = "Десять минут", subtitle = "и жира меньше", modifier = Modifier.weight(1f))
                StatCard(title = "Один клик", subtitle = "прогресс под рукой", modifier = Modifier.weight(1f))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Наши преимущества:",
                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                    )
                    FeatureLine("• Считаем за вас шаги, калории")
                    FeatureLine("• Подсказываем, сколько надо двигаться, чтобы достичь цели")
                    FeatureLine("• Показываем проверенные упражнения")
                    FeatureLine("• Напоминаем, чтобы твоё завтра не наступило в декабре")
                    FeatureLine("• Никакой рекламы, только беспощадная логика наших алгоритмов")
                }
            }

            Button(
                onClick = { navController.navigate("registration") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Я изменю свою жизнь",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                )
            }

            OutlinedButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = "Я уже изменяю (жизнь)",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}





