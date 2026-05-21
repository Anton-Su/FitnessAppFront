package com.example.fitnessapp.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fitnessapp.presentation.ui.component.FitnessTopBar
import com.example.fitnessapp.navigation.Screen
import com.example.fitnessapp.presentation.ui.component.ExerciseCard
import com.example.fitnessapp.presentation.viewmodel.FitnessViewModel

/**
 * Экран со списком упражнений.
 */
@Composable
fun ExercisesScreen(navController: NavHostController, viewModel: FitnessViewModel) {
    val exercises by viewModel.exercises.collectAsState()
    val filteredExercises by viewModel.filteredExercises.collectAsState()
    val scrollState = rememberScrollState()

    var selectedType by remember { mutableStateOf("всё") }
    var expandedTypeMenu by remember { mutableStateOf(false) }

    val exerciseTypes = listOf("всё", "кардио", "силовая", "растяжка", "баланс", "йога")
    // Вместо отдельного флага isFiltering используем текущий выбранный тип — это упрощает логику
    val displayedExercises = if (selectedType == "всё") exercises else filteredExercises

    Scaffold(
        topBar = { FitnessTopBar(title = "Упражнения", canNavigateBack = true, onBackClick = { navController.navigate(Screen.Home.route) }) }
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
            text = "Здесь найдётся всё, даже больше",
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        )

        Text(
            text = "Найдено: ${displayedExercises.size}",
            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Фильтр по типу",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
                    OutlinedButton(
                        onClick = { expandedTypeMenu = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = selectedType.replaceFirstChar { it.uppercase() },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
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
                                    viewModel.filterExercisesByType(type)
                                }
                            )
                        }
                    }
                }
            }
        }

         if (displayedExercises.isEmpty()) {
             Card(
                 modifier = Modifier.fillMaxWidth(),
                 shape = RoundedCornerShape(20.dp),
                 colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
             ) {
                 Column(
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(20.dp),
                     horizontalAlignment = Alignment.CenterHorizontally,
                     verticalArrangement = Arrangement.Center
                 ) {
                     Text(
                         text = "😴",
                         style = MaterialTheme.typography.headlineLarge
                     )
                     Spacer(modifier = Modifier.height(8.dp))
                     Text(
                         text = if (selectedType == "всё") "Упражнения загружаются с сервера..." else "По этому типу упражнений не найдено",
                         style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                         textAlign = TextAlign.Center
                     )
                 }
             }
         } else {
             Column(
                 modifier = Modifier.fillMaxWidth(),
                 horizontalAlignment = Alignment.CenterHorizontally,
                 verticalArrangement = Arrangement.spacedBy(14.dp)
             ) {
                 displayedExercises.forEach { exercise ->
                     ExerciseCard(exercise = exercise, onOpen = { navController.navigate(Screen.ExerciseDetail.createRoute(exercise.id)) })
                 }
             }
         }
        }
    }
}
