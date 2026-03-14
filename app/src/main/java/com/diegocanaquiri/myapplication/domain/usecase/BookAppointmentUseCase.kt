package com.diegocanaquiri.myapplication.domain.usecase

import com.diegocanaquiri.myapplication.domain.model.Booking
import com.diegocanaquiri.myapplication.domain.repository.AppointmentRepository

class BookAppointmentUseCase(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(booking: Booking): Result<String> {
        // Regla de negocio: Un paciente no puede tener dos citas en la misma jornada
        if (booking.patientId.isEmpty() || booking.shiftId.isEmpty()) {
            return Result.failure(Exception("Información de reserva incompleta"))
        }
        
        return repository.bookAppointment(booking)
    }
}
