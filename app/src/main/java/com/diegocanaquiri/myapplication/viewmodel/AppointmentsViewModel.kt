package com.diegocanaquiri.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegocanaquiri.myapplication.domain.model.Appointment
import com.diegocanaquiri.myapplication.domain.repository.AppointmentRepository
import com.diegocanaquiri.myapplication.domain.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppointmentsViewModel(
    private val appointmentRepository: AppointmentRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _appointmentsState = MutableStateFlow<AppointmentsUiState>(AppointmentsUiState.Loading)
    val appointmentsState: StateFlow<AppointmentsUiState> = _appointmentsState.asStateFlow()

    fun loadPatientAppointments(patientId: String) {
        viewModelScope.launch {
            appointmentRepository.getPatientAppointments(patientId).collect { appointments ->
                _appointmentsState.value = if (appointments.isEmpty()) {
                    AppointmentsUiState.Empty
                } else {
                    AppointmentsUiState.Success(appointments)
                }
            }
        }
    }

    fun getWalletUrl(appointmentId: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val result = walletRepository.getWalletSaveUrl(appointmentId)
            onResult(result.getOrNull())
        }
    }
}

sealed class AppointmentsUiState {
    object Loading : AppointmentsUiState()
    data class Success(val appointments: List<Appointment>) : AppointmentsUiState()
    object Empty : AppointmentsUiState()
    data class Error(val message: String) : AppointmentsUiState()
}
