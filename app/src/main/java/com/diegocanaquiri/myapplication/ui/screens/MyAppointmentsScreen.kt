package com.diegocanaquiri.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diegocanaquiri.myapplication.domain.model.Appointment
import com.diegocanaquiri.myapplication.ui.components.AddToWalletButton
import com.diegocanaquiri.myapplication.ui.utils.BarcodeUtils
import com.diegocanaquiri.myapplication.viewmodel.AppointmentsUiState
import com.diegocanaquiri.myapplication.viewmodel.AppointmentsViewModel
import com.diegocanaquiri.myapplication.ui.components.MedicalBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppointmentsScreen(viewModel: AppointmentsViewModel, patientId: String) {
    val uiState by viewModel.appointmentsState.collectAsState()

    LaunchedEffect(patientId) {
        if (patientId.isNotEmpty()) {
            viewModel.loadPatientAppointments(patientId)
        }
    }

    Scaffold(
        topBar = { 
            CenterAlignedTopAppBar(
                title = { Text("Mis Citas Médicas", fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            ) 
        },
        containerColor = Color.White
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF8F9FA))) {
            when (val state = uiState) {
                is AppointmentsUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is AppointmentsUiState.Empty -> {
                    Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = Color.White
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.padding(32.dp), tint = Color.LightGray)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("No tienes citas activas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Tus próximas citas aparecerán aquí", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                }
                is AppointmentsUiState.Success -> AppointmentsList(state.appointments, viewModel)
                is AppointmentsUiState.Error -> Text("Error: ${state.message}", Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AppointmentsList(appointments: List<Appointment>, viewModel: AppointmentsViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(appointments) { appointment ->
            var walletUrl by remember { mutableStateOf<String?>(null) }
            var showQrDialog by remember { mutableStateOf(false) }
            
            LaunchedEffect(appointment.id) {
                viewModel.getWalletUrl(appointment.id) { url -> walletUrl = url }
            }

            if (showQrDialog) {
                QrCodeTicketDialog(appointment) { showQrDialog = false }
            }

            // Ticket Card Design
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header del Ticket (Azul)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("PASE DE INGRESO", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            StatusBadge(appointment.status.name)
                        }
                    }

                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = appointment.appointmentTime, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1B1B1F))
                        Text(text = "ID de seguimiento: ${appointment.id.take(8).uppercase()}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Botones de Acción
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { showQrDialog = true },
                                modifier = Modifier.weight(1.2f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                elevation = ButtonDefaults.buttonElevation(0.dp)
                            ) {
                                Text("VER QR", fontWeight = FontWeight.Bold)
                            }
                            
                            AddToWalletButton(
                                saveUrl = walletUrl,
                                modifier = Modifier.weight(1f).height(48.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val isChecked = status == "CHECKED_IN"
    Surface(
        color = if (isChecked) Color(0xFFD8F3DC) else Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = if (isChecked) "✓ ASISTIÓ" else "PENDIENTE",
            color = if (isChecked) Color(0xFF2D6A4F) else Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun QrCodeTicketDialog(appointment: Appointment, onDismiss: () -> Unit) {
    val qrData = if (appointment.qrCode.isNotEmpty()) appointment.qrCode else appointment.id
    val qrBitmap = remember(qrData) { BarcodeUtils.generateQrCode(qrData) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { 
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { 
                Text("CERRAR", fontWeight = FontWeight.Bold) 
            } 
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Tu Pase Digital", fontWeight = FontWeight.ExtraBold)
                Text("Preséntalo en recepción", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                qrBitmap?.let {
                    Surface(
                        modifier = Modifier.size(220.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                    ) {
                        androidx.compose.foundation.Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "QR",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(appointment.appointmentTime, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        }
    )
}
