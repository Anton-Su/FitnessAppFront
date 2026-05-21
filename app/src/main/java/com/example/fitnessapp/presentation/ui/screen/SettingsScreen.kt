package com.example.fitnessapp.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fitnessapp.presentation.viewmodel.FitnessViewModel
import com.example.fitnessapp.presentation.ui.component.FitnessTopBar
import com.example.fitnessapp.presentation.ui.component.GenderSelector
import com.example.fitnessapp.presentation.ui.component.ActivityStatusToggle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.outlined.Delete
import com.example.fitnessapp.presentation.util.EmailValidator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton

/**
 * Экран настроек пользователя.
 * Показывает опции: возраст, имя, рост, вес, статус активности и цель по снижению веса.
 */
@Composable
fun SettingsScreen(navController: NavHostController, viewModel: FitnessViewModel) {
    // Для корректного использования StateFlow в Compose нужно собрать их как State через collectAsState()
    val age by viewModel.age.collectAsState()
    val name by viewModel.name.collectAsState()
    val email by viewModel.email.collectAsState()
    val height by viewModel.height.collectAsState()
    val weight by viewModel.weight.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val statusActive by viewModel.statusActive.collectAsState()
    val goal by viewModel.goal.collectAsState()
    val saveState by viewModel.saveProfileState.collectAsState()

    var nameInput by remember { mutableStateOf(name) }
    var emailInput by remember { mutableStateOf(email) }
    var ageInput by remember { mutableStateOf(age.toString()) }
    var heightInput by remember { mutableStateOf(height.toString()) }
    var weightInput by remember { mutableStateOf(weight.toString()) }
    var goalInput by remember { mutableStateOf(goal.toString()) }
    var genderInput by remember { mutableStateOf(gender) }

    LaunchedEffect(gender) {
        genderInput = gender
    }


    val ageVal = ageInput.toIntOrNull() ?: 0
    val heightVal = heightInput.toDoubleOrNull() ?: 0.0
    val weightVal = weightInput.toDoubleOrNull() ?: 0.0
    val goalVal = goalInput.toIntOrNull() ?: 0

    val ageError = if (ageInput.isNotBlank() && (ageVal < 13 || ageVal > 120)) "Возраст: 13-120" else ""
    val heightError = if (heightInput.isNotBlank() && (heightVal < 100 || heightVal > 250)) "Рост: 100-250 см" else ""
    val weightError = if (weightInput.isNotBlank() && (weightVal < 30 || weightVal > 200)) "Вес: 30-200 кг" else ""
    val goalError = if (goalInput.isNotBlank() && (goalVal < 0 || goalVal > 100)) "Цель: 0-100 кг" else ""
    val emailError = if (emailInput.isNotBlank() && !EmailValidator.isValid(emailInput)) EmailValidator.getErrorMessage(emailInput) else ""

    val isValid = nameInput.isNotBlank() && emailInput.isNotBlank() && emailError.isEmpty() &&
                  ageInput.isNotBlank() && ageError.isEmpty() &&
                  heightInput.isNotBlank() && heightError.isEmpty() &&
                  weightInput.isNotBlank() && weightError.isEmpty() &&
                  goalInput.isNotBlank() && goalError.isEmpty()

    val deleteState by viewModel.deleteState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(deleteState) {
        when (deleteState) {
            is com.example.fitnessapp.presentation.viewmodel.AuthUiState.Success -> {
                // navigate to auth choice after deletion
                navController.navigate("auth_choice") {
                    popUpTo("auth_choice") { inclusive = true }
                    launchSingleTop = true
                }
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            FitnessTopBar(
                title = "Настройки",
                canNavigateBack = true,
                onBackClick = { navController.navigateUp() },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Выйти из аккаунта"
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Удалить аккаунт",
                            tint = Color(0xFFC62828)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Profile card
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(value = nameInput, onValueChange = { nameInput = it }, label = { Text("Имя") }, modifier = Modifier.fillMaxWidth(), isError = nameInput.isEmpty())
                        OutlinedTextField(value = emailInput, onValueChange = { emailInput = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), isError = emailError.isNotEmpty())
                        if (emailError.isNotEmpty()) {
                            Text(emailError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        }
                        GenderSelector(
                            selectedGender = genderInput,
                            onGenderSelected = { genderInput = it }
                        )
                    }
                }

                // Body metrics card
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(value = ageInput, onValueChange = { ageInput = it.filter(Char::isDigit).take(3) }, label = { Text("Возраст") }, modifier = Modifier.weight(1f), isError = ageError.isNotEmpty())
                            OutlinedTextField(value = goalInput, onValueChange = { goalInput = it.filter { c -> c.isDigit() }.take(3) }, label = { Text("Цель (кг)") }, modifier = Modifier.weight(1f), isError = goalError.isNotEmpty())
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            if (ageError.isNotEmpty()) {
                                Text(ageError, color = Color.Red, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            }
                            if (goalError.isNotEmpty()) {
                                Text(goalError, color = Color.Red, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(value = heightInput, onValueChange = { heightInput = it.filter { c -> c.isDigit() || c == '.' }.take(6) }, label = { Text("Рост (см)") }, modifier = Modifier.weight(1f), isError = heightError.isNotEmpty())
                            OutlinedTextField(value = weightInput, onValueChange = { weightInput = it.filter { c -> c.isDigit() || c == '.' }.take(6) }, label = { Text("Вес (кг)") }, modifier = Modifier.weight(1f), isError = weightError.isNotEmpty())
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            if (heightError.isNotEmpty()) {
                                Text(heightError, color = Color.Red, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            }
                            if (weightError.isNotEmpty()) {
                                Text(weightError, color = Color.Red, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            }
                        }
                        ActivityStatusToggle(
                            isActive = statusActive,
                            onStatusChange = { viewModel.setStatusActive(it) }
                        )
                    }
                }

                // Actions card
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    // Save (with validation)
                                    if (!isValid) return@Button
                                    val a = ageInput.toIntOrNull() ?: age
                                    val h = heightInput.toDoubleOrNull() ?: height
                                    val w = weightInput.toDoubleOrNull() ?: weight
                                    val g = goalInput.toIntOrNull() ?: goal
                                    viewModel.updateUserProfile(
                                        name = nameInput,
                                        gender = genderInput,
                                        email = emailInput,
                                        age = a,
                                        height = h,
                                        weight = w,
                                        goal = g
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                enabled = isValid,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1976D2),  // Синий
                                    contentColor = Color.White,
                                    disabledContainerColor = Color.Gray
                                )
                            ) {
                                Text("Сохранить")
                            }
                        }
                    }
                }
                // Показываем сообщение о результате сохранения
                when (saveState) {
                    is com.example.fitnessapp.presentation.viewmodel.AuthUiState.Success -> {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                text = (saveState as com.example.fitnessapp.presentation.viewmodel.AuthUiState.Success).message,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    is com.example.fitnessapp.presentation.viewmodel.AuthUiState.Error -> {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Text(
                                text = (saveState as com.example.fitnessapp.presentation.viewmodel.AuthUiState.Error).message,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    else -> {}
                }

                LaunchedEffect(saveState) {
                    if (saveState is com.example.fitnessapp.presentation.viewmodel.AuthUiState.Success || saveState is com.example.fitnessapp.presentation.viewmodel.AuthUiState.Error) {
                        delay(2000)
                        viewModel.resetSaveProfileState()
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Вы уверены?") },
                text = { Text("Это действие безвозвратно удалит ваш аккаунт. Вы уверены, что хотите продолжить?") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        viewModel.deleteUser()
                    }) { Text("Да") }
                },
                dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Нет") } }
            )
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Выйти из аккаунта?") },
                text = { Text("Вы уверены, что хотите выйти?") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        navController.navigate("auth_choice") {
                            popUpTo("auth_choice") { inclusive = false }
                            launchSingleTop = true
                        }
                    }) { Text("Да") }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Нет")
                    }
                }
            )
        }
    }
}
