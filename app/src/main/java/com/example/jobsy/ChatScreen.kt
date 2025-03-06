package com.example.jobsy

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ChatScreen(
    userId: Int,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var newMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Загрузка сообщений
    LaunchedEffect(userId) {
        isLoading = true
        authViewModel.getMessages(
            userId = userId,
            onSuccess = { loadedMessages ->
                Log.d("ChatScreen", "Загружено сообщений: ${loadedMessages.size}")
                messages = loadedMessages
                isLoading = false
            },
            onError = { message ->
                Log.e("ChatScreen", "Ошибка загрузки сообщений: $message")
                errorMessage = message
                isLoading = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(messages) { message ->
                    MessageItem(message = message, isCurrentUser = message.sender_id == authViewModel.currentUserId)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newMessage,
                    onValueChange = { newMessage = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Введите сообщение") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (newMessage.isNotBlank()) {
                            authViewModel.sendMessage(
                                receiverId = userId,
                                message = newMessage,
                                onSuccess = {
                                    // Добавляем новое сообщение в список
                                    val newMessageObj = Message(
                                        id = 0, // ID будет сгенерирован автоматически
                                        sender_id = authViewModel.currentUserId!!,
                                        receiver_id = userId,
                                        message = newMessage,
                                        created_at = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                                            .format(java.util.Date()) // Текущее время
                                    )
                                    messages = messages + newMessageObj // Обновляем список сообщений
                                    newMessage = "" // Очищаем поле ввода
                                },
                                onError = { message ->
                                    errorMessage = message
                                }
                            )
                        }
                    }
                ) {
                    Text("Отправить")
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: Message, isCurrentUser: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        Text(
            text = message.message,
            modifier = Modifier
                .background(
                    color = if (isCurrentUser) Color.Blue else Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp),
            color = Color.White
        )
        // Вырезаем время из строки created_at (например, "14:48" из "2023-10-05T14:48:00.000Z")
        val time = if (message.created_at.length >= 16) {
            message.created_at.substring(11, 16) // Берем подстроку с 11 по 16 символы (часы и минуты)
        } else {
            "Некорректная дата"
        }
        Text(
            text = time,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}