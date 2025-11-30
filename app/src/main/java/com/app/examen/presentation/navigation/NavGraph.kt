package com.app.examen.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.examen.presentation.screens.SudokuScreen
import com.app.examen.presentation.screens.home.HomeScreen

sealed class Screen(
    val route: String,
) {
    object Home : Screen("home")
    object Sudoku : Screen("sudokugenerate?difficulty={difficulty}&width={width}&height={height}") {
        fun createRoute(difficulty: String, width: Int, height: Int) = "sudokugenerate?difficulty=$difficulty&width=$width&height=$height"
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun SudokuNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                onStart = { difficulty, width, height ->
                    navController.navigate(Screen.Sudoku.createRoute(difficulty, width, height))
                }
            )
        }

        composable(
            route = Screen.Sudoku.route,
            arguments = listOf(
                navArgument("difficulty") { type = NavType.StringType },
                navArgument("width") { type = NavType.IntType },
                navArgument("height") { type = NavType.IntType },
            )
        ) { backStackEntry ->

            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: ""
            val width = backStackEntry.arguments?.getInt("width") ?: 0
            val height = backStackEntry.arguments?.getInt("height") ?: 0

            SudokuScreen(
                difficulty = difficulty,
                width = width,
                height = height,
                onBackClick = { navController.navigate(Screen.Home.route) },
            )
        }
    }
}