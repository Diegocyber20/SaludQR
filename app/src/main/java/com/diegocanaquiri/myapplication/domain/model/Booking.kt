package com.diegocanaquiri.myapplication.domain.model

import java.util.Date

data class Booking(
    val id: String = "",
    val patientId: String = "",
    val shiftId: String = "",
    val status: BookingStatus = BookingStatus.CONFIRMED,
    val createdAt: Date = Date(),
    val appointmentTime: String = "" // Hora específica del turno
)

enum class BookingStatus {
    CONFIRMED, CANCELLED, COMPLETED
}
