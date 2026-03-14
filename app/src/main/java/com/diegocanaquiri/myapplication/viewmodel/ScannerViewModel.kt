package com.diegocanaquiri.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegocanaquiri.myapplication.domain.usecase.ScanQrUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScannerViewModel(
    private val scanQrUseCase: com.diegocanaquiri.myapplication.domain.usecase.ScanQrUseCase,
    private val appointmentRepository: com.diegocanaquiri.myapplication.domain.repository.AppointmentRepository
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScannerUiState>(ScannerUiState.Idle)
    val scanState: StateFlow<ScannerUiState> = _scanState.asStateFlow()

    fun onQrScanned(qrCode: String) {
        if (_scanState.value !is ScannerUiState.Idle) return
        
        viewModelScope.launch {
            _scanState.value = ScannerUiState.Processing
            
            // 1. Validamos el ingreso en el servidor
            val result = scanQrUseCase(qrCode)
            
            if (result.isSuccess) {
                // 2. Si es éxito, buscamos los datos para mostrar quién entró
                val appointment = appointmentRepository.getAppointmentByQrCode(qrCode)
                _scanState.value = ScannerUiState.Success(
                    message = "¡Ingreso Autorizado!",
                    appointment = appointment
                )
            } else {
                _scanState.value = ScannerUiState.Error(result.exceptionOrNull()?.message ?: "Error al validar el QR")
            }
        }
    }

    fun resetScanner() {
        _scanState.value = ScannerUiState.Idle
    }
}

sealed class ScannerUiState {
    object Idle : ScannerUiState()
    object Processing : ScannerUiState()
    data class Success(val message: String, val appointment: com.diegocanaquiri.myapplication.domain.model.Appointment? = null) : ScannerUiState()
    data class Error(val message: String) : ScannerUiState()
}
