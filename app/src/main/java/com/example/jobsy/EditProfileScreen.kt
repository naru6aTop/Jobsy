package com.example.jobsy

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    supabase: SupabaseClient
) {
    var bio by remember { mutableStateOf(authViewModel.currentUserBio ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Лончер для выбора изображения
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            imageUri = uri
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактирование профиля") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Аватарка пользователя
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { imagePickerLauncher.launch("image/*") }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = imageUri ?: authViewModel.currentUserAvatarUrl,
                        error = rememberAsyncImagePainter(R.drawable.default_pfp)
                    ),
                    contentDescription = "Аватарка",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Поле для редактирования bio
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("О себе") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка для сохранения изменений
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            // Загрузка аватарки, если выбрана новая
                            var newAvatarUrl: String? = null
                            imageUri?.let { uri ->
                                val contentResolver = context.contentResolver
                                val inputStream = contentResolver.openInputStream(uri)
                                val byteArray = inputStream?.readBytes()
                                val fileName = "${System.currentTimeMillis()}.jpg"
                                val storage = supabase.storage.from("avatars")
                                byteArray?.let { storage.upload(fileName, it) }
                                newAvatarUrl = storage.publicUrl(fileName)
                            }

                            // Обновление данных пользователя
                            supabase.from("users")
                                .update({
                                    set("bio", bio)
                                    if (newAvatarUrl != null) {
                                        set("avatar_url", newAvatarUrl)
                                    }
                                }) {
                                    filter {
                                        eq("id", authViewModel.currentUserId)
                                    }
                                }

                            // Обновление данных в ViewModel
                            authViewModel.currentUserBio = bio
                            if (newAvatarUrl != null) {
                                authViewModel.currentUserAvatarUrl = newAvatarUrl
                            }

                            // Возврат на предыдущий экран
                            navController.popBackStack()
                        } catch (e: Exception) {
                            Log.e("Supabase", "Ошибка при обновлении профиля: ${e.message}")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }
        }
    }
}