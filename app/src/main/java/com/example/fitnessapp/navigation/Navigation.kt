package com.example.fitnessapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fitnessapp.presentation.ui.screen.*
import com.example.fitnessapp.presentation.viewmodel.FitnessViewModel

/**
 * Список экранов приложения для навигации.
 */
sealed class Screen(val route: String) {
    object AuthChoice : Screen("auth_choice")
    object Registration : Screen("registration")
    object Login : Screen("login")
    object Home : Screen("home")
    object Exercises : Screen("exercises")
    object ExerciseDetail : Screen("exercise/{exerciseId}") {
        fun createRoute(exerciseId: Int) = "exercise/$exerciseId"
    }
    object ActivityCalendar : Screen("calendar")
    object Settings : Screen("settings")
}

/**
 * Корневая навигация приложения.
 *
 * @param navController NavController (по умолчанию будет создан локальный)
 */
@Composable
fun Navigation(navController: NavHostController = rememberNavController(), viewModel: FitnessViewModel) {
    // Используем экран выбора авторизации/регистрации как стартовый
    NavHost(navController, startDestination = Screen.AuthChoice.route) {
        composable(Screen.AuthChoice.route) {
            AuthChoiceScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Registration.route) {
            RegistrationScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Exercises.route) {
            ExercisesScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            Screen.ExerciseDetail.route,
            arguments = listOf(navArgument("exerciseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getInt("exerciseId") ?: -1
            ExerciseDetailScreen(navController = navController, exerciseId = exerciseId, viewModel = viewModel)
        }
        composable(Screen.ActivityCalendar.route) {
            ActivityCalendarScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController, viewModel = viewModel)
        }
    }
}
