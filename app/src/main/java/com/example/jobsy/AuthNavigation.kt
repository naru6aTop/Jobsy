package com.example.jobsy

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AuthNavigation(authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("login") { LoginScreen(navController, authViewModel) }
        composable("register") { RegisterScreen(navController, authViewModel) }
        composable("home") { HomeScreen(navController, userName = "naru6aTop", avatarResId = R.drawable.default_pfp) }
        composable("ads") { ServiceScreen(navController) }
        composable("orders") { OrdersScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
    }
}
