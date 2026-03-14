package com.diegocanaquiri.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegocanaquiri.myapplication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            val result = authRepository.login(email, pass)
            
            _authState.value = if (result.isSuccess) {
                val user = result.getOrNull()
                val role = user?.let { authRepository.getUserRole(it.uid) } ?: "PATIENT"
                AuthUiState.Success(role)
            } else {
                AuthUiState.Error(result.exceptionOrNull()?.message ?: "Error al iniciar sesión")
            }
        }
    }

    fun register(email: String, pass: String, name: String, role: String) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            val result = authRepository.register(email, pass, name, role)
            _authState.value = if (result.isSuccess) {
                AuthUiState.Success(role)
            } else {
                AuthUiState.Error(result.exceptionOrNull()?.message ?: "Error al registrarse")
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthUiState.Idle
    }

    fun resetState() {
        _authState.value = AuthUiState.Idle
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val role: String) : AuthUiState() // Ej: PATIENT o STAFF
    data class Error(val message: String) : AuthUiState()
}
