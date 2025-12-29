package com.udvash.student.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.udvash.student.ui.screens.classes.ClassListScreen
import com.udvash.student.ui.screens.dashboard.DashboardScreen
import com.udvash.student.ui.screens.login.LoginScreen

@Composable
fun UdvashNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable(Screen.PastClasses.route) {
            ClassListScreen(navController = navController)
        }
    }
}
