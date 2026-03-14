package com.diegocanaquiri.myapplication.domain.model

import java.util.Date

data class Appointment(
    val id: String = "",
    val shiftId: String = "",
    val patientId: String = "",
    val patientName: String = "",
    val appointmentTime: String = "", // Ej: "08:30 AM"
    val qrCode: String = "", // Identificador único para el QR
    val status: AppointmentStatus = AppointmentStatus.ISSUED,
    val checkedInAt: Date? = null, // Fecha de escaneo por enfermera
    val walletObjectId: String = "" // Google Wallet Object ID
)

enum class AppointmentStatus {
    ISSUED, // Cita creada pero no escaneada
    CHECKED_IN, // El paciente ya llegó y se escaneó su QR
    CANCELLED, // Cita anulada
    MISSED // El paciente no llegó
}
