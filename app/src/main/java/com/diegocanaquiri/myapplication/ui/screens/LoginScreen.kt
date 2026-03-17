package com.diegocanaquiri.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diegocanaquiri.myapplication.viewmodel.AuthUiState
import com.diegocanaquiri.myapplication.viewmodel.AuthViewModel

@Composable
fun LoginScreen(viewModel: AuthViewModel, onLoginSuccess: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("PATIENT") }
    var isRegisterMode by remember { mutableStateOf(false) }
    
    val uiState by viewModel.authState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess((uiState as AuthUiState.Success).role)
        }
    }

    val vibrantGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0077B6), // Azul profundo
            Color(0xFF00B4D8), // Azul vibrante
            Color(0xFF90E0EF)  // Celeste suave
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(vibrantGradient)
    ) {
        // Burbujas decorativas de colores para dar dinamismo
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = (-50).dp, y = (-50).dp)
                .background(Color.White.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Contenedor Blanco Elevado para el Formulario
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(28.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo / Imagen Médica (La personita) con fondo de color vivo
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .background(Color(0xFFD8F3DC), androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        coil.compose.AsyncImage(
                            model = "https://cdn-icons-png.flaticon.com/512/3774/3774299.png",
                            contentDescription = null,
                            modifier = Modifier.size(80.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (isRegisterMode) "Crear Cuenta" else "SaludQR",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color(0xFF0077B6),
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-1).sp
                        )
                    )
                    
                    Text(
                        text = "Tu salud en colores",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Formulario con iconos de colores
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (isRegisterMode) {
                            LoginTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = "Nombre Completo",
                                icon = Icons.Default.Person
                            )
                        }

                        LoginTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Correo Electrónico",
                            icon = Icons.Default.Email
                        )

                        LoginTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Contraseña",
                            icon = Icons.Default.Lock,
                            isPassword = true
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (uiState is AuthUiState.Loading) {
                        CircularProgressIndicator(color = Color(0xFF00B4D8))
                    } else {
                        Button(
                            onClick = { 
                                if (isRegisterMode) viewModel.register(email, password, name, role)
                                else viewModel.login(email, password)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0077B6)
                            )
                        ) {
                            Text(
                                if (isRegisterMode) "¡EMPEZAR AHORA!" else "ENTRAR",
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        
                        TextButton(
                            onClick = { 
                                isRegisterMode = !isRegisterMode 
                                viewModel.resetState()
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                if (isRegisterMode) "¿Ya tienes cuenta? Ingresa" else "¿No tienes cuenta? Regístrate",
                                color = Color(0xFF00B4D8),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            if (uiState is AuthUiState.Error) {
                Surface(
                    color = Color(0xFFFFE5E5),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    Text(
                        text = (uiState as AuthUiState.Error).message,
                        color = Color(0xFFD90429),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color(0xFFF8F9FA)
        ),
        singleLine = true
    )
}
