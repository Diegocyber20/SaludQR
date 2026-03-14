package com.diegocanaquiri.myapplication.domain.repository

import com.diegocanaquiri.myapplication.domain.model.MedicalShift
import kotlinx.coroutines.flow.Flow

interface MedicalShiftRepository {
    // Obtener todas las jornadas disponibles
    fun getActiveMedicalShifts(): Flow<List<MedicalShift>>
    
    // Obtener una jornada específica
    fun getMedicalShiftById(id: String): Flow<MedicalShift?>
}
