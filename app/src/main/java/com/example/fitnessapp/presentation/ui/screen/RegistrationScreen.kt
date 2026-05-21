package com.example.fitnessapp.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fitnessapp.domain.model.Gender
import com.example.fitnessapp.presentation.ui.component.GenderSelector
import com.example.fitnessapp.presentation.viewmodel.AuthUiState
import com.example.fitnessapp.presentation.viewmodel.FitnessViewModel
import com.example.fitnessapp.presentation.util.EmailValidator
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border

/**
 * Экран регистрации пользователя.
 */
@Composable
fun RegistrationScreen(navController: NavHostController, viewModel: FitnessViewModel) {
    val scrollState = rememberScrollState()
    val registrationState by viewModel.registrationState.collectAsState()

    var firstName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var height by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf(Gender.FEMALE) }

    LaunchedEffect(registrationState) {
        if (registrationState is AuthUiState.Success || registrationState is AuthUiState.Error) {
            scrollState.animateScrollTo(0)
        }
        if (registrationState is AuthUiState.Success) {
            delay(2000)
            viewModel.resetRegistrationState()
            navController.navigate("login") {
                popUpTo("registration") { inclusive = true }
            }
        }
    }

    val ageVal = age.toIntOrNull() ?: 0
    val heightVal = height.toDoubleOrNull() ?: 0.0
    val weightVal = weight.toDoubleOrNull() ?: 0.0

    val ageError = if (age.isNotBlank() && (ageVal < 13 || ageVal > 120)) "Возраст должен быть от 13 до 120" else ""
    val heightError = if (height.isNotBlank() && (heightVal < 100 || heightVal > 250)) "Рост должен быть от 100 до 250 см" else ""
    val weightError = if (weight.isNotBlank() && (weightVal < 30 || weightVal > 200)) "Вес должен быть от 30 до 200 кг" else ""
    val emailError = if (email.isNotBlank() && !EmailValidator.isValid(email)) EmailValidator.getErrorMessage(email) else ""

    val isValid = firstName.isNotBlank() && email.isNotBlank() && emailError.isEmpty() &&
                  age.isNotBlank() && ageError.isEmpty() &&
                  password.isNotBlank() &&
                  height.isNotBlank() && heightError.isEmpty() &&
                   weight.isNotBlank() && weightError.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Крошечный, но неотъемлемый шаг",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 34.sp
                    )
                )
                Text(
                    text = "Укажи свои параметры, новичок",
                    style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.SansSerif)
                )
            }
        }

        if (registrationState is AuthUiState.Success || registrationState is AuthUiState.Error) {
            val state = registrationState
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state is AuthUiState.Success) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = if (state is AuthUiState.Success) state.message else (state as AuthUiState.Error).message,
                    modifier = Modifier.padding(20.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Имя") },
            singleLine = true
        )


        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (emailError.isNotEmpty()) 1.dp else 0.dp,
                    color = if (emailError.isNotEmpty()) Color.Red else Color.Transparent,
                    shape = RoundedCornerShape(4.dp)
                ),
            label = { Text("Email") },
            singleLine = true,
            isError = emailError.isNotEmpty()
        )
        if (emailError.isNotEmpty()) {
            Text(emailError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        GenderSelector(
            selectedGender = gender,
            onGenderSelected = { gender = it }
        )

        OutlinedTextField(
            value = age,
            onValueChange = { age = it.filter(Char::isDigit).take(3) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Возраст") },
            singleLine = true,
            isError = ageError.isNotEmpty()
        )
        if (ageError.isNotEmpty()) {
            Text(ageError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Пароль") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = height,
            onValueChange = { height = it.filter { c -> c.isDigit() || c == '.' }.take(5) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Рост (см)") },
            singleLine = true,
            isError = heightError.isNotEmpty()
        )
        if (heightError.isNotEmpty()) {
            Text(heightError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' }.take(5) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Вес (кг)") },
            singleLine = true,
            isError = weightError.isNotEmpty()
        )
        if (weightError.isNotEmpty()) {
            Text(weightError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Button(
            onClick = {
                viewModel.registerUser(
                    firstName = firstName.trim(),
                    gender = gender,
                    email = email.trim(),
                    age = age.toIntOrNull() ?: 0,
                    password = password,
                    height = height.toDoubleOrNull() ?: 0.0,
                    weight = weight.toDoubleOrNull() ?: 0.0
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            enabled = isValid && registrationState !is AuthUiState.Loading,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = if (registrationState is AuthUiState.Loading) "Отправляем..." else "Я сделал это!")
        }

        Text(
            text = "Можно соврать, но ради чего?",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Приступ ностальгии?",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = { navController.navigate("login") }
            ) {
                Text("Я уже в деле!")
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}
