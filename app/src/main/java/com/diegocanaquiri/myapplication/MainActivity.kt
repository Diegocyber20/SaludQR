package com.diegocanaquiri.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.diegocanaquiri.myapplication.ui.navigation.SaludNavGraph
import com.diegocanaquiri.myapplication.ui.theme.CitasMedicasAPPTheme
import com.diegocanaquiri.myapplication.viewmodel.AppointmentsViewModel
import com.diegocanaquiri.myapplication.viewmodel.AuthViewModel
import com.diegocanaquiri.myapplication.viewmodel.ScannerViewModel
import com.diegocanaquiri.myapplication.viewmodel.ShiftsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val appContainer = (application as SaludApplication).container

        val authViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(appContainer.authRepository) as T
            }
        })[AuthViewModel::class.java]

        val shiftsViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ShiftsViewModel(appContainer.medicalServiceRepository, appContainer.appointmentRepository) as T
            }
        })[ShiftsViewModel::class.java]

        val scannerViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ScannerViewModel(appContainer.scanQrUseCase, appContainer.appointmentRepository) as T
            }
        })[ScannerViewModel::class.java]

        val appointmentsViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AppointmentsViewModel(appContainer.appointmentRepository, appContainer.walletRepository) as T
            }
        })[AppointmentsViewModel::class.java]

        setContent {
            CitasMedicasAPPTheme {
                val navController = rememberNavController()
                SaludNavGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    shiftsViewModel = shiftsViewModel,
                    scannerViewModel = scannerViewModel,
                    appointmentsViewModel = appointmentsViewModel
                )
            }
        }
    }
}
