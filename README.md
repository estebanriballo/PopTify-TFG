# PopTify-TFG
Trabajo Fin de Grado Desarrollo de Aplicaciones Multiplataforma en Android usando Kotlin y Jetpack Compose basado en la API de Spotify

# Descripción de la aplicación

**PopTify** es una aplicación cuyo propósito es ofrecerte datos sobre música y artistas que Spotify no muestra directamente en su plataforma.

Para ello, cuenta con las siguientes funcionalidades:

- Acceso completo a la base de datos de Spotify, mostrando información no accesible desde la aplicación oficial.
- Administración de tus canciones, álbumes y artistas favoritos.
- Visualización de estadísticas personalizadas sobre tus gustos musicales.
- Conexión directa con Spotify para reproducir, guardar o compartir canciones.

---

# Requisitos hardware y software

- **Sistema operativo:** Android 7 o superior  
- **Hardware mínimo:** 8 GB de almacenamiento y 1 GB de RAM  
- **Software necesario:** Android Studio, JDK 17, Gradle 8 o superior  
- **Dependencias/librerías:**
  - `com.adamratzman:spotify-api-kotlin-core:3.8.6`
  - `com.google.firebase:firebase-bom:33.13.0`
  - `com.google.firebase:firebase-analytics`
  - `com.google.firebase:firebase-auth-ktx`
  - `com.github.bumptech.glide:compose:1.0.0-beta01`
  - `androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0`
  - `androidx.navigation:navigation-compose:2.9.0`
  - `androidx.datastore:datastore-preferences:1.0.0`
  - `androidx.lifecycle:lifecycle-runtime-ktx:2.6.0`
  - `com.google.firebase:firebase-firestore-ktx`
  - `com.google.android.gms:play-services-auth:20.7.0`
  - `com.google.android.gms:play-services-base:18.2.0`
  - `androidx.core:core-splashscreen:1.0.1`

---

# Guía de instalación del entorno de desarrollo

1. Ve a [https://developer.android.com/studio](https://developer.android.com/studio)
2. Haz clic en **Download Android Studio**.
3. Ejecuta el archivo `.exe` descargado.
4. Sigue los pasos del asistente de instalación.
5. Asegúrate de seleccionar:
   - Android Studio
   - Android SDK
   - Android Virtual Device (AVD)
6. Espera a que finalice la instalación.
7. Al abrir por primera vez, selecciona **Do not import settings** si es tu primera instalación.
8. Configura el entorno:
   - Elige un tema (Claro u Oscuro).
   - Instala el SDK y las herramientas necesarias.
   - Crea un dispositivo virtual (emulador), si lo deseas.
9. Espera a que se descarguen todos los componentes.

---

# Guía de puesta en marcha de la aplicación

Para ejecutar la aplicación tras clonar el repositorio o configurarla en tu entorno de desarrollo:

- Clonar el repositorio:  
  ```bash
  git clone https://github.com/estebanriballo/PopTify_TFG

- Sincronizar el gradle.app para que las dependencias y librerias se instalen

- Ejecución de la aplicación: Pulsar ejecutar en el botón 'run app' o pulsar Máyus + F10
