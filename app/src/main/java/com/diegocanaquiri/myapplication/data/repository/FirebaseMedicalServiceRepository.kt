package com.diegocanaquiri.myapplication.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.diegocanaquiri.myapplication.domain.model.MedicalService
import com.diegocanaquiri.myapplication.domain.repository.MedicalServiceRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseMedicalServiceRepository(
    private val firestore: FirebaseFirestore
) : MedicalServiceRepository {

    override fun getActiveServices(): Flow<List<MedicalService>> = callbackFlow {
        val listener = firestore.collection("events")
            .whereEqualTo("status", "ACTIVE")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val services = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(MedicalService::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(services)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getServiceById(id: String): MedicalService? {
        return try {
            val doc = firestore.collection("events").document(id).get().await()
            doc.toObject(MedicalService::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }
}
