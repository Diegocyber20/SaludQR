package com.diegocanaquiri.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diegocanaquiri.myapplication.domain.model.MedicalService
import com.diegocanaquiri.myapplication.viewmodel.ShiftsUiState
import com.diegocanaquiri.myapplication.viewmodel.ShiftsViewModel
import com.diegocanaquiri.myapplication.ui.components.MedicalBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftsScreen(
    viewModel: ShiftsViewModel,
    onNavigateToMyAppointments: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Consultas Médicas", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToMyAppointments) {
                        Icon(Icons.Default.DateRange, contentDescription = "Mis Citas", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        MedicalBackground {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                when (val state = uiState) {
                    is ShiftsUiState.Loading -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
                    is ShiftsUiState.Empty -> Box(Modifier.fillMaxSize()) { Text("No hay consultas disponibles.", Modifier.align(Alignment.Center)) }
                    is ShiftsUiState.Success -> {
                        LazyColumn(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.services) { service ->
                                MedicalServiceCard(service) {
                                    viewModel.bookAppointment(service)
                                }
                            }
                        }
                    }
                    is ShiftsUiState.BookingLoading -> Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f))) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
                    is ShiftsUiState.BookingSuccess -> {
                        AlertDialog(
                            onDismissRequest = { viewModel.resetBookingState() },
                            confirmButton = {
                                Button(onClick = { 
                                    viewModel.resetBookingState()
                                    onNavigateToMyAppointments()
                                }, shape = RoundedCornerShape(12.dp)) { Text("Ver mi Ticket") }
                            },
                            title = { Text("Cita Agendada") },
                            text = { Text("Tu cupo ha sido reservado. Puedes encontrar tu pase en Google Wallet o en la sección 'Mis Tickets'.") },
                            shape = RoundedCornerShape(28.dp)
                        )
                    }
                    is ShiftsUiState.Error -> Box(Modifier.fillMaxSize()) { Text("Error: ${state.message}", Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}

@Composable
fun MedicalServiceCard(service: MedicalService, onBook: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(12.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = service.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = "Especialidad Médica", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(icon = Icons.Default.Person, label = "Doctor", value = service.description)
            InfoRow(icon = Icons.Default.DateRange, label = "Horario", value = service.startAt)
            InfoRow(icon = Icons.Default.LocationOn, label = "Lugar", value = service.venueName)
            
            val remaining = service.capacity - service.sold
            InfoRow(
                icon = Icons.Default.Info, 
                label = "Cupos", 
                value = "$remaining disponibles",
                valueColor = if (remaining < 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onBook,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = remaining > 0
            ) {
                Text("RESERVAR ESTA CITA", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String, valueColor: Color = Color.Black) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$label: ", fontSize = 14.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = valueColor)
    }
}
