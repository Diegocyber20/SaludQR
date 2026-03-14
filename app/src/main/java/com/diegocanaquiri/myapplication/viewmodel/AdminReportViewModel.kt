package com.diegocanaquiri.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.diegocanaquiri.myapplication.data.model.AppointmentDto
import com.diegocanaquiri.myapplication.domain.model.Appointment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminReportViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _reportState = MutableStateFlow<ReportUiState>(ReportUiState.Loading)
    val reportState: StateFlow<ReportUiState> = _reportState.asStateFlow()

    init {
        loadReport()
    }

    fun loadReport() {
        viewModelScope.launch {
            _reportState.value = ReportUiState.Loading
            try {
                // Buscamos todas las citas que ya han sido escaneadas (asistencia registrada)
                val result = firestore.collection("appointments")
                    .whereEqualTo("status", "CHECKED_IN")
                    .get()
                    .await()
                
                val appointments = result.documents.mapNotNull { doc ->
                    doc.toObject(AppointmentDto::class.java)?.toDomain(doc.id)
                }
                
                _reportState.value = if (appointments.isEmpty()) {
                    ReportUiState.Empty
                } else {
                    ReportUiState.Success(appointments)
                }
            } catch (e: Exception) {
                _reportState.value = ReportUiState.Error(e.message ?: "Error al cargar el reporte")
            }
        }
    }
}

sealed class ReportUiState {
    object Loading : ReportUiState()
    object Empty : ReportUiState()
    data class Success(val appointments: List<Appointment>) : ReportUiState()
    data class Error(val message: String) : ReportUiState()
}
