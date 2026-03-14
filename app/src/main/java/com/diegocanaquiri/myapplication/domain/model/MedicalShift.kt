package com.diegocanaquiri.myapplication.domain.model

import java.util.Date

data class MedicalShift(
    val id: String = "",
    val title: String = "",
    val doctorName: String = "",
    val specialty: String = "",
    val clinicName: String = "",
    val clinicAddress: String = "",
    val date: Date = Date(),
    val status: ShiftStatus = ShiftStatus.ACTIVE,
    val walletClassId: String = "" // Google Wallet Class ID
)

enum class ShiftStatus {
    ACTIVE, CANCELLED, COMPLETED
}
