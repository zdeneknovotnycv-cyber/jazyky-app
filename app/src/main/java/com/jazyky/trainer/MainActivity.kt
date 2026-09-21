package com.jazyky.trainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jazyky.trainer.ui.navigation.Screen
import com.jazyky.trainer.ui.screens.ExerciseScreen
import com.jazyky.trainer.ui.screens.HomeScreen
import com.jazyky.trainer.ui.screens.LessonListScreen
import com.jazyky.trainer.ui.screens.LessonScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(onLanguageSelected = { lang ->
                navController.navigate(Screen.lessonListRoute(lang))
            })
        }

        composable(
            route = Screen.LessonList.route,
            arguments = listOf(navArgument("lang") { type = NavType.StringType })
        ) { backStackEntry ->
            val lang = backStackEntry.arguments?.getString("lang").orEmpty()
            LessonListScreen(
                languageCode = lang,
                onBack = { navController.popBackStack() },
                onLessonSelected = { file ->
                    navController.navigate(Screen.lessonRoute(lang, file))
                }
            )
        }

        composable(
            route = Screen.Lesson.route,
            arguments = listOf(
                navArgument("lang") { type = NavType.StringType },
                navArgument("file") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lang = backStackEntry.arguments?.getString("lang").orEmpty()
            val file = backStackEntry.arguments?.getString("file").orEmpty()
            LessonScreen(
                languageCode = lang,
                fileName = file,
                onBack = { navController.popBackStack() },
                onStartExercises = {
                    navController.navigate(Screen.exerciseRoute(lang, file))
                }
            )
        }

        composable(
            route = Screen.Exercise.route,
            arguments = listOf(
                navArgument("lang") { type = NavType.StringType },
                navArgument("file") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lang = backStackEntry.arguments?.getString("lang").orEmpty()
            val file = backStackEntry.arguments?.getString("file").orEmpty()
            ExerciseScreen(
                languageCode = lang,
                fileName = file,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
