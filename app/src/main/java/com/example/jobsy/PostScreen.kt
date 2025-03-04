package com.example.jobsy

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun PostScreen(
    serviceId: Int,
    navController: NavController,
    supabase: SupabaseClient
) {
    var service by remember { mutableStateOf<Service?>(null) }
    var user by remember { mutableStateOf<User?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Загрузка данных о посте и авторе
    LaunchedEffect(serviceId) {
        coroutineScope.launch {
            try {
                service = supabase.from("services")
                    .select {
                        filter {
                            eq("id", serviceId) // Используем eq внутри filter
                        }
                    }
                    .decodeSingle<Service>()

                // Загрузка данных о пользователе
                if (service != null) {
                    user = supabase.from("users")
                        .select {
                            filter {
                                eq("id", service!!.user_id) // Используем eq внутри filter
                            }
                        }
                        .decodeSingle<User>()
                }
            } catch (e: Exception) {
                Log.e("Supabase", "Ошибка загрузки данных: ${e.message}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали объявления") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (service != null && user != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Заголовок
                Text(
                    text = service!!.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Изображение
                GlideImage(
                    model = service!!.image_url ?: R.drawable.default_cover,
                    contentDescription = "Service Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Описание
                Text(
                    text = service!!.description,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Полное описание
                Text(
                    text = service!!.content,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Цена
                Text(
                    text = "Цена: ${service!!.price} ₽",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Контакты автора
                Text(
                    text = "Контакты автора",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Имя пользователя
                Text(
                    text = "Имя: ${user!!.name}",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Электронная почта
                Text(
                    text = "Email: ${user!!.email}",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Кнопка для перехода к профилю автора
                Button(
                    onClick = {
                        navController.navigate("profile/${user!!.id}")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Перейти к профилю")
                }
            }
        } else {
            CircularProgressIndicator() // Индикатор загрузки
        }
    }
}