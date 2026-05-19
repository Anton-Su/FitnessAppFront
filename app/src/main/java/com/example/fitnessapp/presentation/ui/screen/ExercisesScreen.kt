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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fitnessapp.domain.model.Exercise
import com.example.fitnessapp.presentation.viewmodel.FitnessViewModel

/**
 * Экран со списком упражнений.
 */
@Composable
fun ExercisesScreen(navController: NavHostController, viewModel: FitnessViewModel) {
    val exercises by viewModel.exercises.collectAsState()
    val filteredExercises by viewModel.filteredExercises.collectAsState()
    val scrollState = rememberScrollState()

    var selectedType by remember { mutableStateOf("все") }
    var isFiltering by remember { mutableStateOf(false) }
    var expandedTypeMenu by remember { mutableStateOf(false) }

    val exerciseTypes = listOf("все", "кардио", "силовая", "растяжка", "баланс", "йога")
    val displayedExercises = if (isFiltering) filteredExercises else exercises

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Сборник упражнений",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Text(
            text = "Здесь найдётся всё, даже больше",
            style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.SansSerif)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Фильтр по типу
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { expandedTypeMenu = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(selectedType)
            }

            Button(
                onClick = {
                    if (selectedType == "все") {
                        isFiltering = false
                    } else {
                        viewModel.filterExercisesByType(selectedType)
                        isFiltering = true
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Фильтр")
            }

            DropdownMenu(
                expanded = expandedTypeMenu,
                onDismissRequest = { expandedTypeMenu = false }
            ) {
                exerciseTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedType = type
                            expandedTypeMenu = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (displayedExercises.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "Упражнения загружаются с сервера...",
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            displayedExercises.forEach { exercise ->
                ExerciseCard(exercise = exercise, onOpen = { navController.navigate("exercise/${exercise.id}") })
            }
        }
    }
}

@Composable
private fun ExerciseCard(exercise: Exercise, onOpen: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = exercise.title,
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
            )
            Text(
                text = exercise.description,
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif)
            )
            Text(
                text = "Тип: ${exercise.type}",
                style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary)
            )
            Button(onClick = onOpen) {
                Text("Открыть детали")
            }
        }
    }
}
