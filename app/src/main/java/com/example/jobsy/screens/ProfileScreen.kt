package com.example.jobsy.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.jobsy.AuthViewModel
import com.example.jobsy.R
import com.example.jobsy.data.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun ProfileScreen(
    userId: Int, // ID пользователя, чей профиль открыт
    navController: NavController,
    supabase: SupabaseClient,
    currentUserId: Int?,
    authViewModel: AuthViewModel// ID текущего пользователя (если null, то это чужой профиль)
) {
    var user by remember { mutableStateOf<User?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Загрузка данных пользователя
    LaunchedEffect(userId) {
        coroutineScope.launch {
            try {
                user = supabase.from("users")
                    .select {
                        filter {
                            eq("id", userId) // Используем eq внутри filter
                        }
                    }
                    .decodeSingle<User>()
            } catch (e: Exception) {
                Log.e("Supabase", "Ошибка загрузки данных пользователя: ${e.message}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (user != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Аватарка пользователя
                GlideImage(
                    model = user!!.avatar_url ?: R.drawable.default_pfp,
                    contentDescription = "Service Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Имя пользователя
                Text(
                    text = user!!.name,
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Рейтинг пользователя
                Text(
                    text = "Рейтинг: ★ ${user!!.rating}",
                    style = TextStyle(fontSize = 16.sp, color = Color.Gray)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Описание пользователя
                Text(
                    text = user!!.bio,
                    style = TextStyle(fontSize = 14.sp, color = Color.DarkGray),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопки навигации
                if (currentUserId == userId) {
                    // Кнопки для своего профиля
                    ProfileOption(text = "Новости") { navController.navigate("news") }
                    ProfileOption(text = "О сервисе") { navController.navigate("about") }
                    ProfileOption(text = "Задать вопрос") { navController.navigate("ask_question") }
                    ProfileOption(text = "Настройки профиля") { navController.navigate("editProfile") }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Кнопка выхода
                    Button(
                        onClick = { authViewModel.logout()
                            navController.navigate("login") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Выйти из аккаунта", color = Color.White)
                    }
                } else {
                    // Кнопки для чужого профиля
                    Button(
                        onClick = { navController.navigate("chat/${user!!.id}") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Написать сообщение")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { /* Логика жалобы */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Пожаловаться")
                    }
                }
            }
        } else {
            CircularProgressIndicator() // Индикатор загрузки
        }
    }
}

// Компонент кнопки
@Composable
fun ProfileOption(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}