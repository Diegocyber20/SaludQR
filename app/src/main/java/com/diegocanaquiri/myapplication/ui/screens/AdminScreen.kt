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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import com.diegocanaquiri.myapplication.ui.screens.LoginTextField
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
                title = { Text("Panel Administrativo", fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp) },
                actions = {
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.padding(end = 8.dp).background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    label = { Text("Crear") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), selectedIconColor = MaterialTheme.colorScheme.primary)
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    label = { Text("Gestionar") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), selectedIconColor = MaterialTheme.colorScheme.primary)
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
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(androidx.compose.foundation.rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Nueva Consulta Médica", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                
                LoginTextField(value = title, onValueChange = { title = it }, label = "Especialidad (Título)", icon = Icons.Default.Info)
                LoginTextField(value = description, onValueChange = { description = it }, label = "Nombre del Doctor", icon = Icons.Default.Person)
                LoginTextField(value = startAt, onValueChange = { startAt = it }, label = "Fecha y Hora (ej: 20 Mar, 10:00 AM)", icon = Icons.Default.DateRange)
                LoginTextField(value = venueName, onValueChange = { venueName = it }, label = "Hospital / Clínica", icon = Icons.Default.LocationOn)
                LoginTextField(value = venueAddress, onValueChange = { venueAddress = it }, label = "Dirección Exacta", icon = Icons.Default.LocationOn)
                LoginTextField(value = capacity, onValueChange = { capacity = it }, label = "Cupos Disponibles", icon = Icons.Default.Info)

                Spacer(modifier = Modifier.height(8.dp))

                if (uiState is AdminUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    Button(
                        onClick = { 
                            viewModel.createMedicalService(
                                title = title,
                                description = description,
                                date = startAt, 
                                locationName = venueName,
                                locationAddress = venueAddress,
                                capacity = capacity.toIntOrNull() ?: 0
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("PUBLICAR CONSULTA", fontWeight = FontWeight.Bold)
                    }
                    
                    OutlinedButton(
                        onClick = onNavigateToReport,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Text("VER REPORTES DE ASISTENCIA", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (uiState is AdminUiState.Success) {
            Surface(
                color = Color(0xFFD8F3DC),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = (uiState as AdminUiState.Success).message, 
                    color = Color(0xFF2D6A4F),
                    modifier = Modifier.padding(16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ManageServicesList(viewModel: AdminViewModel) {
    val services by viewModel.services.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { 
            Text(
                "Gestión de Consultas", 
                style = MaterialTheme.typography.titleLarge, 
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            ) 
        }
        
        items(services) { service ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.Info, null, modifier = Modifier.padding(8.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = service.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        AdminStatItem("Cupos", service.capacity.toString())
                        AdminStatItem("Vendidos", service.sold.toString(), MaterialTheme.colorScheme.primary)
                        val remaining = service.capacity - service.sold
                        AdminStatItem("Libres", remaining.toString(), if (remaining > 0) Color(0xFF2D6A4F) else Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatItem(label: String, value: String, valueColor: Color = Color.Black) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = valueColor)
        Text(text = label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
    }
}
