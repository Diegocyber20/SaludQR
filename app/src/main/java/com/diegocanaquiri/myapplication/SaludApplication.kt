package com.diegocanaquiri.myapplication

import android.app.Application
import com.diegocanaquiri.myapplication.di.AppContainer

/**
 * Clase principal de la aplicación.
 * Es necesario registrarla en el AndroidManifest.xml.
 */
class SaludApplication : Application() {
    
    // Contenedor de dependencias accesible desde toda la app
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}
