package com.diegocanaquiri.myapplication.domain.repository

import com.diegocanaquiri.myapplication.domain.model.MedicalService
import kotlinx.coroutines.flow.Flow

interface MedicalServiceRepository {
    fun getActiveServices(): Flow<List<MedicalService>>
    suspend fun getServiceById(id: String): MedicalService?
}
