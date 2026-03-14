package com.diegocanaquiri.myapplication.domain.usecase

import com.diegocanaquiri.myapplication.domain.repository.AppointmentRepository

class ScanQrUseCase(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(qrCode: String): Result<Boolean> {
        // Lógica de negocio adicional: Verificación de formato, etc.
        if (qrCode.isBlank()) return Result.failure(Exception("El código QR no es válido"))
        
        return repository.validateCheckIn(qrCode)
    }
}
