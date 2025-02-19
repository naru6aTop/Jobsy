package com.example.jobsy

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.jan.supabase.SupabaseClient

@Composable
fun AuthNavigation(authViewModel: AuthViewModel, supabase: SupabaseClient) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("login") { LoginScreen(navController, authViewModel) }
        composable("register") { RegisterScreen(navController, authViewModel) }
        composable("home") { HomeScreen(navController, userName = "naru6aTop", avatarResId = R.drawable.default_pfp) }
        composable("ads") { ServicesScreen(navController, supabase) }
        composable("orders") { OrdersScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
    }
}
