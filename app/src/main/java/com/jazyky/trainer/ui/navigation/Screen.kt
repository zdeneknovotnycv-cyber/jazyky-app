package com.jazyky.trainer.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object LessonList : Screen("lessons/{lang}")
    object Lesson : Screen("lesson/{lang}/{file}")
    object Exercise : Screen("exercise/{lang}/{file}")

    companion object {
        fun lessonListRoute(lang: String) = "lessons/$lang"
        fun lessonRoute(lang: String, file: String) = "lesson/$lang/$file"
        fun exerciseRoute(lang: String, file: String) = "exercise/$lang/$file"
    }
}
