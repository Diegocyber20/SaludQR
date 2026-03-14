package com.diegocanaquiri.myapplication.data.model

import com.google.firebase.Timestamp
import com.diegocanaquiri.myapplication.domain.model.MedicalShift
import com.diegocanaquiri.myapplication.domain.model.ShiftStatus
import java.util.Date

// Esta clase representa cómo se guarda una Jornada en Firestore
data class MedicalShiftDto(
    val title: String = "",
    val doctorName: String = "",
    val specialty: String = "",
    val clinicName: String = "",
    val clinicAddress: String = "",
    val date: Timestamp = Timestamp.now(),
    val status: String = "ACTIVE",
    val walletClassId: String = ""
) {
    // Función para convertir a modelo de dominio
    fun toDomain(id: String): MedicalShift {
        return MedicalShift(
            id = id,
            title = title,
            doctorName = doctorName,
            specialty = specialty,
            clinicName = clinicName,
            clinicAddress = clinicAddress,
            date = date.toDate(),
            status = try { ShiftStatus.valueOf(status) } catch (e: Exception) { ShiftStatus.ACTIVE },
            walletClassId = walletClassId
        )
    }
}
