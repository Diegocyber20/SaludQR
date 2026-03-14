package com.diegocanaquiri.myapplication.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.diegocanaquiri.myapplication.data.model.MedicalShiftDto
import com.diegocanaquiri.myapplication.domain.model.MedicalShift
import com.diegocanaquiri.myapplication.domain.repository.MedicalShiftRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseMedicalShiftRepository(
    private val firestore: FirebaseFirestore
) : MedicalShiftRepository {

    override fun getActiveMedicalShifts(): Flow<List<MedicalShift>> = callbackFlow {
        // Obtenemos solo las jornadas ACTIVAS (Punto 6 del modelo de datos)
        val listener = firestore.collection("medical_shifts")
            .whereEqualTo("status", "ACTIVE")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val shifts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(MedicalShiftDto::class.java)?.toDomain(doc.id)
                } ?: emptyList()

                trySend(shifts)
            }
        
        // Cerramos el listener cuando ya no sea necesario (ahorro de batería)
        awaitClose { listener.remove() }
    }

    override fun getMedicalShiftById(id: String): Flow<MedicalShift?> = callbackFlow {
        val listener = firestore.collection("medical_shifts").document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val shift = snapshot?.toObject(MedicalShiftDto::class.java)?.toDomain(snapshot.id)
                trySend(shift)
            }
        awaitClose { listener.remove() }
    }
}
