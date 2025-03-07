package com.example.jobsy.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.jobsy.R
import com.example.jobsy.data.Order
import com.example.jobsy.data.Service
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

// Данные для постов
data class OrderPost(val id: Int, val username: String, val text: String, val imageResId: Int?)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(navController: NavController, supabase: SupabaseClient) {
    var orders by remember { mutableStateOf<List<Service>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                orders = supabase.from("orders")
                    .select()
                    .decodeList<Service>()
            } catch (e: Exception) {
                Log.e("Supabase", "Ошибка загрузки данных: ${e.message}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Заказчики") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addOrder") }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить объявление")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Поиск") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(orders.filter { it.title.contains(searchQuery, ignoreCase = true) }) { service ->
                    ServiceItem(service = service, onClick = {
                        navController.navigate("orderDetail/${service.id}")
                    })
                }
            }
        }
    }
}

// Модель данных для объявления


// Отображение одного поста
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun OrderItem(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Заголовок и дата создания
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(order.title, style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold))
                Text(
                    order.created_at,
                    style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Описание и цена
            Text(order.description, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Цена: ${order.budget} ₽", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)

            Spacer(modifier = Modifier.height(8.dp))

            GlideImage(
                model = order.image_url ?: R.drawable.default_cover,
                contentDescription = "Order Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
