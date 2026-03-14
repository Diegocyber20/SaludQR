package com.diegocanaquiri.myapplication.domain.model

data class MedicalService(
    val id: String = "",
    val title: String = "",        // Especialidad (ej: Pediatría)
    val description: String = "",  // Nombre del Doctor y detalles
    val startAt: String = "",      // Fecha y Hora (Punto 6.3 de la guía)
    val venueName: String = "",    // Nombre del Hospital/Clínica (Punto 6.4)
    val venueAddress: String = "", // Dirección (Punto 6.5)
    val capacity: Int = 0,
    val sold: Int = 0,
    val status: String = "ACTIVE"
)
