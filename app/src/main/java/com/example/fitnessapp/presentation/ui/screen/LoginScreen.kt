package com.example.fitnessapp.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fitnessapp.presentation.viewmodel.AuthUiState
import com.example.fitnessapp.presentation.viewmodel.FitnessViewModel
import kotlinx.coroutines.delay

/**
 * Экран авторизации.
 */
@Composable
fun LoginScreen(navController: NavHostController, viewModel: FitnessViewModel) {
    val loginState by viewModel.loginState.collectAsState()
    val savedEmail by viewModel.email.collectAsState()
    var email by rememberSaveable(savedEmail) { mutableStateOf(savedEmail) }
    var password by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(loginState) {
        if (loginState is AuthUiState.Success || loginState is AuthUiState.Error) {
            // Scroll to top or add visual feedback if needed
        }


        TODO("Temporary bypass for testing navigation, remove '|| true' in production, Убрать условие true после тестирования")

        if (loginState is AuthUiState.Success || true) {
            delay(2000)
            viewModel.resetLoginState()
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f))
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Ты вернулся!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 34.sp
                    )
                )
                Text(
                    text = "Введи логин и пароль, и продолжишь жить по-новому.",
                    style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.SansSerif)
                )
            }
        }

        if (loginState is AuthUiState.Success || loginState is AuthUiState.Error) {
            val state = loginState
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
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Пароль") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = { viewModel.login(email.trim(), password) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            enabled = email.isNotBlank() && password.isNotBlank() && loginState !is AuthUiState.Loading,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = if (loginState is AuthUiState.Loading) "Проверяем..." else "Войти")
        }


        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Нет учётной записи? И ты ещё здесь???",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            Button(onClick = { navController.navigate("registration") }) {
                Text("Регистрация")
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}
