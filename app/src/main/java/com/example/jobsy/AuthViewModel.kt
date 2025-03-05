package com.example.jobsy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userId: Int?) : AuthState() // userId может быть null
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val supabase: SupabaseClient) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    var currentUserId: Int = -1
        private set
    var currentUserName: String = "Гость" // Имя текущего пользователя
        private set
    var currentUserAvatarUrl: String? = null // URL аватарки текущего пользователя
    var currentUserBio: String? = null


    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Проверка, существует ли пользователь с таким email
                val existingUser = supabase.from("users")
                    .select {
                        filter {
                            eq("email", email)
                        }
                    }
                    .decodeSingleOrNull<User>()

                if (existingUser != null) {
                    _authState.value = AuthState.Error("Пользователь с таким email уже существует")
                } else {
                    // Создание нового пользователя (id не передаем)
                    val newUser = User(
                        email = email,
                        password = password, // Сохраняем пароль в чистом виде
                        name = name,
                        bio = "",
                        avatar_url = "",
                        rating = 0f,
                        created_at = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                    )

                    // Добавление пользователя в таблицу
                    try {
                        // Пытаемся вставить пользователя и получить ответ
                        val insertedUsers = supabase.from("users")
                            .insert(newUser)
                            .decodeList<User>() // Используем decodeList, так как Supabase возвращает массив

                        // Извлекаем первого пользователя из массива
                        val insertedUser = insertedUsers.firstOrNull()

                        if (insertedUser != null) {
                            // Сохраняем ID нового пользователя
                            currentUserId = insertedUser.id ?: -1
                            currentUserName = insertedUser.name
                            currentUserAvatarUrl = insertedUser.avatar_url

                            // Устанавливаем состояние успешной регистрации
                            _authState.value = AuthState.Success(insertedUser.id ?: -1)
                        } else {
                            // Если массив пустой, считаем, что пользователь создан, но не получили ID
                            _authState.value = AuthState.Error("Пользователь создан, но не удалось получить ID")
                        }
                    } catch (e: Exception) {
                        // Ловим ошибку "Expected start of the array '[', but had 'EOF' instead"
                        if (e.message?.contains("Expected start of the array") == true) {
                            // Если ошибка возникает, вручную получаем ID нового пользователя
                            val newUserId = getUserIdByEmail(email)

                            if (newUserId != null) {
                                // Сохраняем ID нового пользователя
                                currentUserId = newUserId
                                currentUserName = name
                                currentUserAvatarUrl = null

                                // Устанавливаем состояние успешной регистрации
                                _authState.value = AuthState.Success(newUserId)
                            } else {
                                // Если не удалось получить ID, выводим сообщение
                                _authState.value = AuthState.Error("Пользователь создан, но не удалось получить ID")
                            }
                        } else {
                            // Если это другая ошибка, выводим сообщение
                            _authState.value = AuthState.Error("Ошибка при регистрации: ${e.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Ошибка при регистрации: ${e.message}")
            }
        }
    }

    // Метод для получения ID пользователя по email
    private suspend fun getUserIdByEmail(email: String): Int? {
        return try {
            val user = supabase.from("users")
                .select {
                    filter {
                        eq("email", email)
                    }
                }
                .decodeSingleOrNull<User>()

            user?.id
        } catch (e: Exception) {
            null
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Поиск пользователя по email и паролю
                val user = supabase.from("users")
                    .select {
                        filter {
                            eq("email", email)
                            eq("password", password) // Хэшируем пароль
                        }
                    }
                    .decodeSingleOrNull<User>()

                if (user != null) {
                    currentUserId = user.id!!
                    currentUserName = user.name
                    currentUserAvatarUrl = user.avatar_url
                    _authState.value = AuthState.Success(user.id)
                } else {
                    _authState.value = AuthState.Error("Неверный email или пароль")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Ошибка при авторизации: ${e.message}")
            }
        }
    }

    fun logout() {
        currentUserId = -1
        currentUserName = "Гость"
        currentUserAvatarUrl = null
        _authState.value = AuthState.Idle
    }

    fun setError(message: String) {
        _authState.value = AuthState.Error(message)
    }

    suspend fun loadUserData(userId: Int) {
        try {
            val user = supabase.from("users")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<User>()

            if (user != null) {
                currentUserId = user.id ?: -1
                currentUserName = user.name
                currentUserBio = user.bio
                currentUserAvatarUrl = user.avatar_url
            }
        } catch (e: Exception) {
            Log.e("Supabase", "Ошибка при загрузке данных пользователя: ${e.message}")
        }
    }
}