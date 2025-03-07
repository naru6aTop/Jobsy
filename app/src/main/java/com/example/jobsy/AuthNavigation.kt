package com.example.jobsy

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jobsy.screens.AddServiceScreen
import com.example.jobsy.screens.ChatScreen
import com.example.jobsy.screens.EditProfileScreen
import com.example.jobsy.screens.HomeScreen
import com.example.jobsy.screens.LoginScreen
import com.example.jobsy.screens.PostScreen
import com.example.jobsy.screens.ProfileScreen
import com.example.jobsy.screens.RegisterScreen
import io.github.jan.supabase.SupabaseClient

@Composable
fun AuthNavigation(authViewModel: AuthViewModel, supabase: SupabaseClient) {
    val navController = rememberNavController()
    val currentUserId = authViewModel.currentUserId

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController, authViewModel) }
        composable("register") { RegisterScreen(navController, authViewModel) }
        composable("home") {
            HomeScreen(
                navController = navController,
                authViewModel = authViewModel,
                supabase = supabase
            )
        }
        composable("profile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
            if (userId != null) {
                ProfileScreen(
                    userId = userId,
                    navController = navController,
                    supabase = supabase,
                    currentUserId = authViewModel.currentUserId,
                    authViewModel = authViewModel
                )
            }
        }
        composable("addService") { AddServiceScreen(navController, supabase, authViewModel) } // Новый маршрут
        composable("serviceDetail/{serviceId}") { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId")?.toIntOrNull()
            if (serviceId != null) {
                PostScreen(
                    serviceId = serviceId,
                    navController = navController,
                    supabase = supabase
                )
            }
        }
        composable("editProfile") {
            EditProfileScreen(
                navController = navController,
                authViewModel = authViewModel,
                supabase = supabase
            )
        }
        composable("chat/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
            if (userId != null) {
                ChatScreen(
                    userId = userId,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
