package com.example.fitnessapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fitnessapp.presentation.ui.screen.*
import com.example.fitnessapp.presentation.ui.screen.AuthChoiceScreen
import com.example.fitnessapp.presentation.ui.screen.ExerciseDetailScreen
import com.example.fitnessapp.presentation.ui.screen.ExercisesScreen
import com.example.fitnessapp.presentation.ui.screen.HomeScreen
import com.example.fitnessapp.presentation.ui.screen.LoginScreen
import com.example.fitnessapp.presentation.ui.screen.RegistrationScreen
import com.example.fitnessapp.presentation.ui.screen.SettingsScreen
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
    object ExerciseDetail : Screen("exercise/{exerciseId}?fromRecommendation={fromRecommendation}") {
        fun createRoute(exerciseId: Int, fromRecommendation: Boolean = false) =
            "exercise/$exerciseId?fromRecommendation=$fromRecommendation"
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
    // Проверяем, залогинен ли пользователь
    val userId = viewModel.userId.collectAsState().value

    // Если пользователь залогинен (userId > 0), начинаем с главной страницы
    // Иначе начинаем с экрана выбора авторизации
    val startDestination = if (userId > 0) Screen.Home.route else Screen.AuthChoice.route

    NavHost(navController, startDestination = startDestination) {
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
            arguments = listOf(
                navArgument("exerciseId") { type = NavType.IntType },
                navArgument("fromRecommendation") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getInt("exerciseId") ?: -1
            val fromRecommendation = backStackEntry.arguments?.getBoolean("fromRecommendation") ?: false
            ExerciseDetailScreen(
                navController = navController,
                exerciseId = exerciseId,
                fromRecommendation = fromRecommendation,
                viewModel = viewModel
            )
        }
        composable(Screen.ActivityCalendar.route) {
            ActivityCalendarScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController, viewModel = viewModel)
        }
    }

    // Слежение за изменением userId: если он стал > 0, переходим на Home.
    LaunchedEffect(userId) {
        if (userId > 0) {
            navController.navigate(Screen.Home.route) {
                // очищаем backstack, чтобы нельзя было вернуться на экраны входа
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }
}
