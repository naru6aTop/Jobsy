package com.example.jobsy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import io.github.jan.supabase.SupabaseClient

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    supabase: SupabaseClient,
    defaultAvatar: Int = R.drawable.default_pfp // Дефолтная аватарка
) {
    val currentUserName = authViewModel.currentUserName // Имя текущего пользователя
    val currentUserAvatarUrl = authViewModel.currentUserAvatarUrl // URL аватарки текущего пользователя
    val currentUserId = authViewModel.currentUserId // ID текущего пользователя

    // Состояние для выбранного экрана
    var selectedScreen by remember { mutableStateOf("ads") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Переход на профиль текущего пользователя
                                if (currentUserId != null) {
                                    navController.navigate("profile/$currentUserId")
                                } else {
                                    navController.navigate("login")
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Аватарка пользователя
                        GlideImage(
                            model = currentUserAvatarUrl ?: defaultAvatar,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop // Обрезаем изображение, чтобы оно заполнило круг
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentUserName, // Используем имя текущего пользователя
                            color = Color.Black
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Нижняя навигационная панель
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Объявления") },
                    label = { Text("Исполнители") },
                    selected = selectedScreen == "ads",
                    onClick = { selectedScreen = "ads" }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Заказы") },
                    label = { Text("Заказчики") },
                    selected = selectedScreen == "orders",
                    onClick = { selectedScreen = "orders" }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Email, contentDescription = "Мессенджер") },
                    label = { Text("Мессенджер") },
                    selected = selectedScreen == "messenger",
                    onClick = { selectedScreen = "messenger" }
                )
            }
        }
    ) { paddingValues ->
        // Применяем paddingValues к содержимому
        Box(modifier = Modifier.padding(paddingValues)) {
            // Отображение выбранного экрана
            when (selectedScreen) {
                "ads" -> ServicesScreen(navController, supabase)
                "orders" -> OrdersScreen(navController)
                "messenger" -> MessengerScreen(navController)
            }
        }
    }
}
