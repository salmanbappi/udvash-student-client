package com.udvash.student.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object PastClasses : Screen("past_classes")
}
