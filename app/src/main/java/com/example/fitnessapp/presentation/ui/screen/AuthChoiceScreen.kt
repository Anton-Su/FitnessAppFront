package com.example.fitnessapp.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "Добро пожаловать в FitnessApp", modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "Выберите действие")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navController.navigate("registration") }) {
                Text(text = "Регистрация")
            }
            Button(onClick = { navController.navigate("login") }) {
                Text(text = "Вход")
            }
        }
    }
}

