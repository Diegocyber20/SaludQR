package com.diegocanaquiri.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel
import com.diegocanaquiri.myapplication.ui.screens.AdminScreen
import com.diegocanaquiri.myapplication.ui.screens.AdminReportScreen
import com.diegocanaquiri.myapplication.ui.screens.LoginScreen
import com.diegocanaquiri.myapplication.ui.screens.MyAppointmentsScreen
import com.diegocanaquiri.myapplication.ui.screens.ScannerScreen
import com.diegocanaquiri.myapplication.ui.screens.ShiftsScreen
import com.diegocanaquiri.myapplication.viewmodel.AdminViewModel
import com.diegocanaquiri.myapplication.viewmodel.AppointmentsViewModel
import com.diegocanaquiri.myapplication.viewmodel.AuthViewModel
import com.diegocanaquiri.myapplication.viewmodel.ScannerViewModel
import com.diegocanaquiri.myapplication.viewmodel.ShiftsViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object PatientHome : Screen("patient_home")
    object MyAppointments : Screen("my_appointments")
    object StaffHome : Screen("staff_home")
    object AdminHome : Screen("admin_home")
    object AdminReport : Screen("admin_report")
}

@Composable
fun SaludNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    shiftsViewModel: ShiftsViewModel,
    scannerViewModel: ScannerViewModel,
    appointmentsViewModel: AppointmentsViewModel
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { role ->
                    when (role) {
                        "ADMIN" -> {
                            navController.navigate(Screen.AdminHome.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        "STAFF" -> {
                            navController.navigate(Screen.StaffHome.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        else -> {
                            navController.navigate(Screen.PatientHome.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.AdminHome.route) {
            AdminScreen(
                viewModel = viewModel<AdminViewModel>(),
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToReport = {
                    navController.navigate(Screen.AdminReport.route)
                }
            )
        }

        composable(Screen.AdminReport.route) {
            AdminReportScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PatientHome.route) {
            ShiftsScreen(
                viewModel = shiftsViewModel,
                onNavigateToMyAppointments = { navController.navigate(Screen.MyAppointments.route) }
            )
        }

        composable(Screen.MyAppointments.route) {
            MyAppointmentsScreen(
                viewModel = appointmentsViewModel,
                patientId = userId
            )
        }

        composable(Screen.StaffHome.route) {
            ScannerScreen(viewModel = scannerViewModel)
        }
    }
}
