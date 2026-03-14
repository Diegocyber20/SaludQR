package com.diegocanaquiri.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegocanaquiri.myapplication.domain.model.Booking
import com.diegocanaquiri.myapplication.domain.model.MedicalService
import com.diegocanaquiri.myapplication.domain.repository.AppointmentRepository
import com.diegocanaquiri.myapplication.domain.repository.MedicalServiceRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShiftsViewModel(
    private val medicalServiceRepository: MedicalServiceRepository,
    private val appointmentRepository: AppointmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShiftsUiState>(ShiftsUiState.Loading)
    val uiState: StateFlow<ShiftsUiState> = _uiState.asStateFlow()

    init {
        loadServices()
    }

    private fun loadServices() {
        viewModelScope.launch {
            medicalServiceRepository.getActiveServices().collect { services ->
                _uiState.value = if (services.isEmpty()) {
                    ShiftsUiState.Empty
                } else {
                    ShiftsUiState.Success(services)
                }
            }
        }
    }

    fun bookAppointment(service: MedicalService) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        
        viewModelScope.launch {
            _uiState.value = ShiftsUiState.BookingLoading
            
            val booking = Booking(
                shiftId = service.id,
                patientId = userId,
                appointmentTime = "${service.title} - ${service.startAt}"
            )
            
            val result = appointmentRepository.bookAppointment(booking)
            
            if (result.isSuccess) {
                _uiState.value = ShiftsUiState.BookingSuccess
            } else {
                _uiState.value = ShiftsUiState.Error(result.exceptionOrNull()?.message ?: "Error al reservar")
            }
        }
    }

    fun resetBookingState() {
        loadServices() // Volver a cargar la lista
    }
}

sealed class ShiftsUiState {
    object Loading : ShiftsUiState()
    object Empty : ShiftsUiState()
    data class Success(val services: List<MedicalService>) : ShiftsUiState()
    object BookingLoading : ShiftsUiState()
    object BookingSuccess : ShiftsUiState()
    data class Error(val message: String) : ShiftsUiState()
}
