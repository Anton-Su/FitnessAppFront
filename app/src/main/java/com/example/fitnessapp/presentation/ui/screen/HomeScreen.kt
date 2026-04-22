package com.example.fitnessapp.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fitnessapp.presentation.viewmodel.FitnessViewModel

/**
 * Главный экран приложения (заглушка).
 */
@Composable
fun HomeScreen(navController: NavHostController, viewModel: FitnessViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Добро пожаловать", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Здесь вы можете просмотреть упражнения и отслеживать активность.", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { navController.navigate("exercises") }) {
            Text("Сборник упражнений")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { navController.navigate("calendar") }) {
            Text("Календарь активности")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { navController.navigate("settings") }) {
            Text("Настройки")
        }
    }
}
