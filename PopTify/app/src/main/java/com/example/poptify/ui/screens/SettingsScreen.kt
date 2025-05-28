package com.example.poptify.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun SettingsScreen(
    navController: NavController? = null,
    navController1: NavController? = null
) {
    val auth = Firebase.auth

    Text(
        text = "Poptify",
        style = MaterialTheme.typography.bodyLarge
    )
    TextButton(
        onClick = {
            auth.signOut()
            navController?.navigate("login") {
                popUpTo(0)
            }
        }
    ) {
        Text("Cerrar sesión")
    }
}