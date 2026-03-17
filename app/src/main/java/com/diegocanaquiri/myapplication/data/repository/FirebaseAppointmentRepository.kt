package com.diegocanaquiri.myapplication.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.diegocanaquiri.myapplication.data.model.AppointmentDto
import com.diegocanaquiri.myapplication.domain.model.Appointment
import com.diegocanaquiri.myapplication.domain.model.Booking
import com.diegocanaquiri.myapplication.domain.repository.AppointmentRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAppointmentRepository(
    private val firestore: FirebaseFirestore
) : AppointmentRepository {

    override suspend fun bookAppointment(booking: Booking): Result<String> {
        return try {
            val orderDocRef = firestore.collection("orders").document()
            val appointmentDocRef = firestore.collection("appointments").document()
            val serviceDocRef = firestore.collection("events").document(booking.shiftId)

            firestore.runTransaction { transaction ->
                // 1. Obtener el estado actual del servicio médico
                val serviceSnapshot = transaction.get(serviceDocRef)
                if (!serviceSnapshot.exists()) throw Exception("El servicio médico ya no existe")
                
                val capacity = serviceSnapshot.getLong("capacity") ?: 0
                val sold = serviceSnapshot.getLong("sold") ?: 0

                // 2. Verificar si hay cupos disponibles
                if (sold >= capacity) {
                    throw Exception("Lo sentimos, ya no hay cupos disponibles.")
                }

                // 3. Crear la Orden (Punto 6 - Colección orders)
                val order = mapOf(
                    "id" to orderDocRef.id,
                    "userId" to booking.patientId,
                    "eventId" to booking.shiftId,
                    "status" to "COMPLETED",
                    "total" to 0.0, // En este caso gratuito o placeholder
                    "currency" to "PEN",
                    "createdAt" to System.currentTimeMillis()
                )
                transaction.set(orderDocRef, order)

                // 4. Crear la cita (Punto 6 - Colección tickets/appointments)
                val appointment = mapOf(
                    "id" to appointmentDocRef.id,
                    "orderId" to orderDocRef.id, // Referencia a la orden
                    "eventId" to booking.shiftId,
                    "patientId" to booking.patientId,
                    "appointmentTime" to booking.appointmentTime,
                    "code" to appointmentDocRef.id, 
                    "status" to "ISSUED",
                    "createdAt" to System.currentTimeMillis()
                )
                transaction.set(appointmentDocRef, appointment)

                // 5. Incrementar el contador de cupos
                transaction.update(serviceDocRef, "sold", sold + 1)
                
                appointmentDocRef.id
            }.await()
            
            Result.success(appointmentDocRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointmentByQrCode(qrCode: String): Appointment? {
        // Buscamos por el campo 'code' que pide la guía
        val query = firestore.collection("appointments")
            .whereEqualTo("code", qrCode)
            .get()
            .await()
        
        val foundByCode = query.documents.firstOrNull()?.toObject(AppointmentDto::class.java)?.toDomain(qrCode)
        if (foundByCode != null) return foundByCode

        // Fallback por ID directo
        return try {
            val doc = firestore.collection("appointments").document(qrCode).get().await()
            if (doc.exists()) {
                doc.toObject(AppointmentDto::class.java)?.toDomain(doc.id)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun validateCheckIn(qrCode: String): Result<Boolean> {
        return try {
            // Buscamos la cita por el campo 'code' (Punto 6.4 de la guía)
            val query = firestore.collection("appointments")
                .whereEqualTo("code", qrCode)
                .get()
                .await()

            val doc = query.documents.firstOrNull()
            if (doc != null) {
                val status = doc.getString("status")
                if (status == "ISSUED") {
                    // Actualizamos a CHECKED_IN (o USED como pida tu flujo)
                    doc.reference.update(mapOf(
                        "status" to "CHECKED_IN",
                        "usedAt" to com.google.firebase.Timestamp.now() // Punto 6.6 de la guía
                    )).await()
                    Result.success(true)
                } else {
                    Result.failure(Exception("Este ticket ya fue utilizado anteriormente"))
                }
            } else {
                Result.failure(Exception("Código QR no reconocido por el sistema"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPatientAppointments(patientId: String): Flow<List<Appointment>> = callbackFlow {
        val listener = firestore.collection("appointments")
            .whereEqualTo("patientId", patientId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val appointments = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(AppointmentDto::class.java)?.toDomain(doc.id)
                } ?: emptyList()
                trySend(appointments)
            }
        awaitClose { listener.remove() }
    }
}
