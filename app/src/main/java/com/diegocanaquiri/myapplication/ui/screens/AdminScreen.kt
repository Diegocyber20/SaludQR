package com.diegocanaquiri.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diegocanaquiri.myapplication.domain.model.MedicalService
import com.diegocanaquiri.myapplication.viewmodel.AdminUiState
import com.diegocanaquiri.myapplication.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel, 
    onLogout: () -> Unit,
    onNavigateToReport: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Panel Administrador", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    label = { Text("Crear") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    label = { Text("Gestionar") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF8F9FA))) {
            if (selectedTab == 0) {
                CreateServiceForm(viewModel, onNavigateToReport)
            } else {
                ManageServicesList(viewModel)
            }
        }
    }
}

@Composable
fun CreateServiceForm(viewModel: AdminViewModel, onNavigateToReport: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startAt by remember { mutableStateOf("") }
    var venueName by remember { mutableStateOf("") }
    var venueAddress by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).background(Color.White, RoundedCornerShape(28.dp)).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Publicar Nueva Consulta", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Especialidad (Título)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Doctor y Detalles") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        OutlinedTextField(value = startAt, onValueChange = { startAt = it }, label = { Text("Fecha y Hora (ej: 15 Mar, 10:00 AM)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        OutlinedTextField(value = venueName, onValueChange = { venueName = it }, label = { Text("Hospital / Clínica") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        OutlinedTextField(value = venueAddress, onValueChange = { venueAddress = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        OutlinedTextField(value = capacity, onValueChange = { capacity = it }, label = { Text("Cupos Totales") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState is AdminUiState.Loading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { 
                    viewModel.createMedicalService(
                        title = title,
                        description = description,
                        date = startAt, 
                        location = "$venueName - $venueAddress",
                        capacity = capacity.toIntOrNull() ?: 0
                    ) 
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("CREAR CITA MÉDICA")
            }
            
            OutlinedButton(
                onClick = onNavigateToReport,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("VER REPORTE ASISTENCIA")
            }
        }

        if (uiState is AdminUiState.Success) {
            Text((uiState as AdminUiState.Success).message, color = Color(0xFF2D6A4F))
        }
    }
}

@Composable
fun ManageServicesList(viewModel: AdminViewModel) {
    val services by viewModel.services.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Gestión de Consultas Activas", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }
        items(services) { service ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = service.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    Text(text = "Doctor: ${service.description}", fontSize = 14.sp)
                    Text(text = "Lugar: ${service.venueName}", color = Color.Gray, fontSize = 12.sp)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatBox(label = "Cupos", value = service.capacity.toString())
                        StatBox(label = "Reservas", value = service.sold.toString(), color = MaterialTheme.colorScheme.primary)
                        StatBox(label = "Libres", value = (service.capacity - service.sold).toString(), color = Color(0xFF2D6A4F))
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String, color: Color = Color.Black) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}
