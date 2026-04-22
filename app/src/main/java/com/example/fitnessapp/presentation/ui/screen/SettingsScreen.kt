package com.example.fitnessapp.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fitnessapp.presentation.viewmodel.FitnessViewModel

/**
 * Экран настроек пользователя.
 * Показывает опции: возраст, имя, рост, вес, статус активности и цель по снижению веса.
 */
@Composable
fun SettingsScreen(navController: NavHostController, viewModel: FitnessViewModel) {
    // Для корректного использования StateFlow в Compose нужно собрать их как State через collectAsState()
    val age by viewModel.age.collectAsState()
    val name by viewModel.name.collectAsState()
    val height by viewModel.height.collectAsState()
    val weight by viewModel.weight.collectAsState()
    val statusActive by viewModel.statusActive.collectAsState()
    val goal by viewModel.goal.collectAsState()

    var nameInput by remember { mutableStateOf(name) }
    var ageInput by remember { mutableStateOf(age.toString()) }
    var heightInput by remember { mutableStateOf(height.toString()) }
    var weightInput by remember { mutableStateOf(weight.toString()) }
    var goalInput by remember { mutableStateOf(goal.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Настройки профиля", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = nameInput, onValueChange = { nameInput = it }, label = { Text("Имя") })
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = ageInput, onValueChange = { ageInput = it }, label = { Text("Возраст") })
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = heightInput, onValueChange = { heightInput = it }, label = { Text("Рост") })
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = weightInput, onValueChange = { weightInput = it }, label = { Text("Вес") })
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = goalInput, onValueChange = { goalInput = it }, label = { Text("Цель (кг)") })
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = if (statusActive) "Статус: активен" else "Статус: не активен")
        Spacer(modifier = Modifier.height(8.dp))
        Switch(checked = statusActive, onCheckedChange = { viewModel.setStatusActive(it) })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Применяем изменения: парсим и записываем
            viewModel.setName(nameInput)
            val a = ageInput.toIntOrNull() ?: age
            viewModel.setAge(a)
            val h = heightInput.toDoubleOrNull() ?: height
            viewModel.setHeight(h)
            val w = weightInput.toDoubleOrNull() ?: weight
            viewModel.setWeight(w)
            val g = goalInput.toIntOrNull() ?: goal
            viewModel.setGoal(g)
        }) {
            Text("Сохранить")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { navController.navigate("home") }) {
            Text("Назад на главную")
        }
    }
}
