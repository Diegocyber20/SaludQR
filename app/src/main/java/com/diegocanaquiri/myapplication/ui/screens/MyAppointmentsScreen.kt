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
                title = { Text("Mis Tickets", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            ) 
        },
        containerColor = Color.Transparent
    ) { padding ->
        MedicalBackground {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                when (val state = uiState) {
                    is AppointmentsUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    is AppointmentsUiState.Empty -> {
                        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Aún no tienes citas agendadas.", color = Color.Gray)
                        }
                    }
                    is AppointmentsUiState.Success -> AppointmentsList(state.appointments, viewModel)
                    is AppointmentsUiState.Error -> Text("Error: ${state.message}", Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun AppointmentsList(appointments: List<Appointment>, viewModel: AppointmentsViewModel) {
    LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(appointments) { appointment ->
            var walletUrl by remember { mutableStateOf<String?>(null) }
            var showQrDialog by remember { mutableStateOf(false) }
            
            LaunchedEffect(appointment.id) {
                viewModel.getWalletUrl(appointment.id) { url ->
                    walletUrl = url
                }
            }

            if (showQrDialog) {
                val qrData = if (appointment.qrCode.isNotEmpty()) appointment.qrCode else appointment.id
                val qrBitmap = remember(qrData) { BarcodeUtils.generateQrCode(qrData) }
                AlertDialog(
                    onDismissRequest = { showQrDialog = false },
                    confirmButton = { TextButton(onClick = { showQrDialog = false }) { Text("Cerrar") } },
                    title = { Text("Escanea este código al llegar") },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            qrBitmap?.let {
                                androidx.compose.foundation.Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "Código QR",
                                    modifier = Modifier.size(240.dp).background(Color.White).padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "ID Ticket: $qrData", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    },
                    shape = RoundedCornerShape(28.dp)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(text = "CONSULTA MÉDICA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(text = appointment.appointmentTime, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                        if (appointment.status.name == "CHECKED_IN") {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2D6A4F))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { showQrDialog = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("MOSTRAR CÓDIGO QR", fontWeight = FontWeight.Bold)
                        }
                        
                        AddToWalletButton(
                            saveUrl = walletUrl,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    if (appointment.status.name == "CHECKED_IN") {
                        Text(
                            text = "✓ ASISTENCIA CONFIRMADA", 
                            color = Color(0xFF2D6A4F), 
                            modifier = Modifier.padding(top = 12.dp).align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
