package com.diegocanaquiri.myapplication.domain.usecase

import com.diegocanaquiri.myapplication.domain.model.MedicalShift
import com.diegocanaquiri.myapplication.domain.repository.MedicalShiftRepository
import kotlinx.coroutines.flow.Flow

class GetActiveShiftsUseCase(
    private val repository: MedicalShiftRepository
) {
    operator fun invoke(): Flow<List<MedicalShift>> {
        return repository.getActiveMedicalShifts()
    }
}
