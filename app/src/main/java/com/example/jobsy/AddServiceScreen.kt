package com.example.jobsy

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServiceScreen(navController: NavController, supabase: SupabaseClient, authViewModel: AuthViewModel) {
    val currentUserId = authViewModel.currentUserId
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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
                title = { Text("Добавить объявление") },
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
            // Поле для заголовка
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Заголовок") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Поле для краткого описания
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Краткое описание") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Поле для полного описания (больше строк)
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Полное описание") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Поле для цены
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Цена") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка для выбора изображения
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Выбрать изображение")
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Отображение выбранного изображения
            imageUri?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "Выбранное изображение",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            }

            // Кнопка для публикации объявления
            Button(
                onClick = {
                    coroutineScope.launch {
                        imageUri?.let { uri ->
                            val contentResolver = context.contentResolver
                            val inputStream = contentResolver.openInputStream(uri)
                            val byteArray = inputStream?.readBytes()
                            val fileName = "images/${System.currentTimeMillis()}.jpg"
                            val storage = supabase.storage.from("images")
                            val result = byteArray?.let { storage.upload(fileName, it) }
                            val imageUrl = storage.publicUrl(fileName)
                            try {
                                // Создание нового объявления
                                val newService = Service(
                                    id = 0, // ID будет сгенерирован автоматически
                                    user_id = currentUserId, // Замените на ID текущего пользователя
                                    title = title,
                                    description = description,
                                    content = content,
                                    price = price.toFloatOrNull() ?: 0f,
                                    category_id = null, // Категория временно убрана
                                    image_url = imageUrl,
                                    created_at = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                                )

                                // Добавление объявления в таблицу services
                                supabase.from("services")
                                    .insert(newService)

                                // Возврат на предыдущий экран
                                navController.popBackStack()
                            } catch (e: Exception) {
                                Log.e("Supabase", "Ошибка при добавлении объявления: ${e.message}")
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Опубликовать")
            }
        }
    }
}