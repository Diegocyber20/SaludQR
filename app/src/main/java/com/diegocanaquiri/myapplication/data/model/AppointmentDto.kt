package com.diegocanaquiri.myapplication.data.model

import com.google.firebase.Timestamp
import com.diegocanaquiri.myapplication.domain.model.Appointment
import com.diegocanaquiri.myapplication.domain.model.AppointmentStatus

data class AppointmentDto(
    val id: String = "",
    val eventId: String = "", // Coincide con la guía
    val patientId: String = "",
    val patientName: String = "",
    val appointmentTime: String = "",
    val code: String = "", // Coincide con la guía (antes qrCode)
    val status: String = "ISSUED",
    val usedAt: Timestamp? = null, // Coincide con la guía (antes checkedInAt)
    val walletObjectId: String = ""
) {
    fun toDomain(id: String): Appointment {
        return Appointment(
            id = id,
            shiftId = eventId,
            patientId = patientId,
            patientName = patientName,
            appointmentTime = appointmentTime,
            qrCode = code, // Mapeamos 'code' al campo qrCode del dominio
            status = try { AppointmentStatus.valueOf(status) } catch (e: Exception) { AppointmentStatus.ISSUED },
            checkedInAt = usedAt?.toDate(),
            walletObjectId = walletObjectId
        )
    }
}
