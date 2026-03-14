# 🏥 SaludQR - Sistema de Gestión de Citas Médicas con QR

SaludQR es una aplicación Android moderna construida con **Jetpack Compose** y **Firebase** que digitaliza el acceso a servicios de salud. Permite gestionar roles de Administrador, Staff y Paciente, asegurando un control de acceso eficiente mediante códigos QR.

## 🚀 Funcionalidades Principales

### 🔐 Roles de Usuario
- **Administrador**: Crea y gestiona servicios médicos, define cupos y visualiza reportes de asistencia en tiempo real.
- **Staff**: Escanea códigos QR para validar la entrada de pacientes y registra el ingreso automáticamente.
- **Paciente**: Explora especialidades, reserva citas con control de disponibilidad y obtiene tickets digitales.

### 📱 Características Técnicas
- **Tickets Digitales**: Generación de códigos QR únicos por cita.
- **Control de Cupos**: Transacciones seguras en Firestore para evitar sobrecupos.
- **Integración con Wallet**: Preparado para Google Wallet.
- **Diseño Moderno**: UI/UX profesional con Material 3, degradados y carga de imágenes con Coil.

## 🛠️ Stack Tecnológico
- **Lenguaje**: Kotlin
- **UI**: Jetpack Compose
- **Backend**: Firebase (Auth, Firestore, Cloud Functions)
- **Cámara**: CameraX + Google ML Kit (Barcode Scanning)
- **Imagen**: Coil
- **QR**: ZXing Core

## 📦 Instalación y Configuración
1. Clona el repositorio:
   ```bash
   git clone https://github.com/TU_USUARIO/SaludQR.git
   ```
2. Crea un proyecto en [Firebase Console](https://console.firebase.google.com/).
3. Añade una App Android con el paquete `com.diegocanaquiri.myapplication`.
4. Descarga el archivo `google-services.json` y colócalo en la carpeta `app/`.
5. Habilita **Email/Password Auth** y **Firestore** en Firebase.
6. ¡Compila y disfruta!

---
Desarrollado por [Diego Canaquiri]
