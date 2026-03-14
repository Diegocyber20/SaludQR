package com.diegocanaquiri.myapplication.domain.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // Observar el estado del usuario en tiempo real
    val currentUser: Flow<FirebaseUser?>

    // Iniciar sesión con email y contraseña
    suspend fun login(email: String, pass: String): Result<FirebaseUser?>

    // Registrar nuevo usuario
    suspend fun register(email: String, pass: String, name: String, role: String): Result<FirebaseUser?>

    // Cerrar sesión
    fun logout()

    // Obtener el rol del usuario (Guardado en Firestore)
    suspend fun getUserRole(uid: String): String
}
