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
                title = { 
                    Column {
                        Text("Hola,", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Text("Servicios Médicos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToMyAppointments,
                        modifier = Modifier.padding(end = 8.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape)
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = "Mis Citas", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Fondo sutil
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)))

            when (val state = uiState) {
                is ShiftsUiState.Loading -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
                is ShiftsUiState.Empty -> Box(Modifier.fillMaxSize()) { Text("No hay consultas disponibles.", Modifier.align(Alignment.Center), color = Color.Gray) }
                is ShiftsUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(24.dp)) {
                                    Text("Agenda tu cita hoy", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                                    Text("Verifica disponibilidad en tiempo real", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        
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
                            }, shape = RoundedCornerShape(12.dp)) { Text("VER MI TICKET") }
                        },
                        title = { Text("¡Cita Agendada!", fontWeight = FontWeight.Bold) },
                        text = { Text("Tu cupo ha sido reservado exitosamente. Puedes encontrar tu pase en la sección 'Mis Tickets'.") },
                        shape = RoundedCornerShape(28.dp),
                        containerColor = Color.White
                    )
                }
                is ShiftsUiState.Error -> Box(Modifier.fillMaxSize()) { Text("Error: ${state.message}", Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@Composable
fun MedicalServiceCard(service: MedicalService, onBook: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.Person, 
                        contentDescription = null, 
                        modifier = Modifier.padding(12.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = service.title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF1B1B1F))
                    Text(text = service.description, fontSize = 14.sp, color = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ServiceInfoItem(Icons.Default.DateRange, service.startAt, Modifier.weight(1f))
                ServiceInfoItem(Icons.Default.LocationOn, service.venueName, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            val isFull = service.sold >= service.capacity
            Button(
                onClick = onBook,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !isFull,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFull) Color.LightGray else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    if (isFull) "CUPOS AGOTADOS" else "RESERVAR CUPO",
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            
            if (!isFull) {
                Text(
                    text = "${service.capacity - service.sold} cupos disponibles",
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF2D6A4F),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ServiceInfoItem(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 12.sp, color = Color.DarkGray, maxLines = 1)
    }
}
