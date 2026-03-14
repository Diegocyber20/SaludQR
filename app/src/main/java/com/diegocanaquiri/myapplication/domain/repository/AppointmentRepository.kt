package com.diegocanaquiri.myapplication.domain.repository

import com.diegocanaquiri.myapplication.domain.model.Appointment
import com.diegocanaquiri.myapplication.domain.model.Booking
import kotlinx.coroutines.flow.Flow

interface AppointmentRepository {
    // Reservar una cita (Inicia el flujo de creación del pase)
    suspend fun bookAppointment(booking: Booking): Result<String>
    
    // Obtener la cita por su código QR (Para el personal médico)
    suspend fun getAppointmentByQrCode(qrCode: String): Appointment?
    
    // Validar el ingreso del paciente (Punto 7: scanTicket)
    suspend fun validateCheckIn(qrCode: String): Result<Boolean>
    
    // Obtener las citas de un paciente específico
    fun getPatientAppointments(patientId: String): Flow<List<Appointment>>
}
