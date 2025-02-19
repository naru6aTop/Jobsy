package com.example.jobsy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    var authState: AuthState = AuthState.Idle
        private set

    /*fun register(email: String, password: String) {
        viewModelScope.launch {
            authState = AuthState.Loading
            val success = authRepository.registerUser(email, password)
            authState = if (success) AuthState.Success else AuthState.Error("Ошибка регистрации")
        }
    }*/

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authState = AuthState.Loading
            val success = authRepository.authenticateUser(email, password)
            authState = if (success) AuthState.Success else AuthState.Error("Неверный email или пароль")
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            authState = AuthState.Idle
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}