package com.diegocanaquiri.myapplication.domain.repository

interface WalletRepository {
    /**
     * Obtiene la URL firmada (JWT) para mostrar el botón de Google Wallet.
     * Esta URL se genera normalmente en una Firebase Cloud Function por seguridad.
     */
    suspend fun getWalletSaveUrl(appointmentId: String): Result<String>
}
