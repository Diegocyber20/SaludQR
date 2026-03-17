package com.diegocanaquiri.myapplication.domain.model

data class Order(
    val id: String = "",
    val userId: String = "",
    val eventId: String = "",
    val status: String = "COMPLETED",
    val total: Double = 0.0,
    val currency: String = "PEN",
    val createdAt: Long = System.currentTimeMillis()
)
