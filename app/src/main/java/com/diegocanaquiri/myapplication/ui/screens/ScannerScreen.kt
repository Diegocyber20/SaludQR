package com.diegocanaquiri.myapplication.ui.screens

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.diegocanaquiri.myapplication.ui.scanner.QrCodeAnalyzer
import com.diegocanaquiri.myapplication.viewmodel.ScannerUiState
import com.diegocanaquiri.myapplication.viewmodel.ScannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(viewModel: ScannerViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.scanState.collectAsState()

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    Scaffold(
        topBar = { 
            CenterAlignedTopAppBar(
                title = { Text("Escáner de Asistencia", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            ) 
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Cámara
            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context)
                    val preview = Preview.Builder().build()
                    val selector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                    
                    imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(context),
                        QrCodeAnalyzer { result -> viewModel.onQrScanned(result) }
                    )
                    
                    try {
                        cameraProviderFuture.get().bindToLifecycle(lifecycleOwner, selector, preview, imageAnalysis)
                    } catch (e: Exception) { e.printStackTrace() }
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            // Superposición de escaneo (Mira)
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .align(Alignment.Center)
                    .background(Color.Transparent)
            ) {
                // Dibujar esquinas o mira aquí si se desea
            }

            // Estados de la UI
            when (val state = uiState) {
                is ScannerUiState.Processing -> {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f))) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center), color = Color.White)
                    }
                }
                is ScannerUiState.Success -> {
                    ResultDialog(
                        title = state.message,
                        appointment = state.appointment,
                        onDismiss = { viewModel.resetScanner() }
                    )
                }
                is ScannerUiState.Error -> {
                    AlertDialog(
                        onDismissRequest = { viewModel.resetScanner() },
                        confirmButton = { Button(onClick = { viewModel.resetScanner() }) { Text("Reintentar") } },
                        title = { Text("Error de Validación", color = MaterialTheme.colorScheme.error) },
                        text = { Text(state.message) }
                    )
                }
                else -> {
                    Surface(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Black.copy(alpha = 0.7f)
                    ) {
                        Text(
                            "Enfoque el código QR del paciente",
                            color = Color.White,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultDialog(title: String, appointment: com.diegocanaquiri.myapplication.domain.model.Appointment?, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { 
            Button(
                onClick = onDismiss, 
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("CONTINUAR") } 
        },
        icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2D6A4F), modifier = Modifier.size(48.dp)) },
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                if (appointment != null) {
                    Text(text = "Cita Reservada para:", fontSize = 12.sp, color = Color.Gray)
                    Text(text = appointment.appointmentTime, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Estado: CONFIRMADO", color = Color(0xFF2D6A4F), fontWeight = FontWeight.Medium)
                } else {
                    Text("Cita procesada correctamente.")
                }
            }
        },
        shape = RoundedCornerShape(28.dp)
    )
}
