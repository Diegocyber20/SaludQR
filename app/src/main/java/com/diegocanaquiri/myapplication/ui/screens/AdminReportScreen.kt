package com.diegocanaquiri.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val total = appointments.size
    val attended = appointments.count { it.status.name == "CHECKED_IN" }
    val pending = total - attended
    val attendanceRate = if (total > 0) (attended.toFloat() / total * 100).toInt() else 0

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Resumen de Gestión",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Tarjetas de Estadísticas
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Total", total.toString(), MaterialTheme.colorScheme.primaryContainer, Modifier.weight(1f))
                StatCard("Asistieron", attended.toString(), Color(0xFFD8F3DC), Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Pendientes", pending.toString(), Color(0xFFFFF3CD), Modifier.weight(1f))
                StatCard("Ratio", "$attendanceRate%", Color(0xFFE0F2FE), Modifier.weight(1f))
            }
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text(
                text = "Detalle de Pacientes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(appointments) { appointment ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Cita: ${appointment.appointmentTime}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text(text = "ID: ${appointment.id.take(8)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    
                    val statusColor = if (appointment.status.name == "CHECKED_IN") Color(0xFF2D6A4F) else Color.Gray
                    val statusText = if (appointment.status.name == "CHECKED_IN") "ASISTIÓ" else "PENDIENTE"
                    
                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = statusText,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, containerColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.labelMedium)
        }
    }
}
