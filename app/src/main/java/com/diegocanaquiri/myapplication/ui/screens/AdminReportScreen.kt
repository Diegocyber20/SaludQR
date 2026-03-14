package com.diegocanaquiri.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.diegocanaquiri.myapplication.domain.model.Appointment
import com.diegocanaquiri.myapplication.viewmodel.AdminReportViewModel
import com.diegocanaquiri.myapplication.viewmodel.ReportUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportScreen(
    onBack: () -> Unit,
    viewModel: AdminReportViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.reportState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reporte de Asistencia") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is ReportUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is ReportUiState.Empty -> {
                    Text("No hay registros de asistencia hoy.", Modifier.align(Alignment.Center))
                }
                is ReportUiState.Success -> {
                    AttendanceList(state.appointments)
                }
                is ReportUiState.Error -> {
                    Text("Error: ${state.message}", Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun AttendanceList(appointments: List<Appointment>) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text(
                text = "Pacientes que han ingresado:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        items(appointments) { appointment ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Cita: ${appointment.appointmentTime}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text(text = "Estado: ✓ ${appointment.status}", color = MaterialTheme.colorScheme.primary)
                    Text(text = "ID Ticket: ${appointment.id.take(8)}...", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Total Asistencias: ${appointments.size}",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
