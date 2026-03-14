package com.diegocanaquiri.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.diegocanaquiri.myapplication.domain.model.MedicalService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow<AdminUiState>(AdminUiState.Idle)
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    private val _services = MutableStateFlow<List<MedicalService>>(emptyList())
    val services: StateFlow<List<MedicalService>> = _services.asStateFlow()

    init {
        loadServices()
    }

    fun loadServices() {
        viewModelScope.launch {
            try {
                val result = firestore.collection("events").get().await()
                val list = result.documents.mapNotNull { it.toObject(MedicalService::class.java)?.copy(id = it.id) }
                _services.value = list
            } catch (e: Exception) {
                // Error silencioso o manejarlo
            }
        }
    }

    fun createMedicalService(title: String, description: String, date: String, location: String, capacity: Int) {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            try {
                val docRef = firestore.collection("events").document()
                val service = MedicalService(
                    id = docRef.id,
                    title = title,
                    description = description,
                    startAt = date,
                    venueName = location,
                    capacity = capacity
                )
                docRef.set(service).await()
                _uiState.value = AdminUiState.Success("Servicio médico creado con éxito")
                loadServices() // Recargar lista
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error(e.message ?: "Error al crear el servicio")
            }
        }
    }

    fun resetState() {
        _uiState.value = AdminUiState.Idle
    }
}

sealed class AdminUiState {
    object Idle : AdminUiState()
    object Loading : AdminUiState()
    data class Success(val message: String) : AdminUiState()
    data class Error(val message: String) : AdminUiState()
}
