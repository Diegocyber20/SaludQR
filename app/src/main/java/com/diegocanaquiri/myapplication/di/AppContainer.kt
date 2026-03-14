package com.diegocanaquiri.myapplication.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.diegocanaquiri.myapplication.data.repository.*
import com.diegocanaquiri.myapplication.domain.repository.*
import com.diegocanaquiri.myapplication.domain.usecase.GetActiveShiftsUseCase
import com.diegocanaquiri.myapplication.domain.usecase.ScanQrUseCase

/**
 * Contenedor de dependencias manual (Manual DI).
 * Aquí se inicializan todos los servicios de Firebase y Repositorios.
 */
class AppContainer {
    // 1. Instancias de Firebase
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val functions = FirebaseFunctions.getInstance()

    // 2. Repositorios
    val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository(auth, firestore)
    }

    val medicalShiftRepository: MedicalShiftRepository by lazy {
        FirebaseMedicalShiftRepository(firestore)
    }

    val medicalServiceRepository: MedicalServiceRepository by lazy {
        FirebaseMedicalServiceRepository(firestore)
    }

    val appointmentRepository: AppointmentRepository by lazy {
        FirebaseAppointmentRepository(firestore)
    }

    val walletRepository: WalletRepository by lazy {
        FirebaseWalletRepository()
    }

    // 3. Casos de Uso
    val getActiveShiftsUseCase: GetActiveShiftsUseCase by lazy {
        GetActiveShiftsUseCase(medicalShiftRepository)
    }

    val scanQrUseCase: ScanQrUseCase by lazy {
        ScanQrUseCase(appointmentRepository)
    }
}
