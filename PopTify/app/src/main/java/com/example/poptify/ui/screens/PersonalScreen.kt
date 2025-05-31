package com.example.poptify.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.first
import com.example.poptify.FavoritesRepository
import com.example.poptify.ui.components.SectionCard

@Composable
fun PersonalScreen(
    navController: NavController? = null,
    favoritesRepository: FavoritesRepository = remember { FavoritesRepository() }
) {
    var tracksCount by remember { mutableStateOf(0) }
    var artistsCount by remember { mutableStateOf(0) }
    var albumsCount by remember { mutableStateOf(0) }

    // Cargar datos rápidos
    LaunchedEffect(Unit) {
        tracksCount = favoritesRepository.getFavoriteTracks().first().size
        artistsCount = favoritesRepository.getFavoriteArtists().first().size
        albumsCount = favoritesRepository.getFavoriteAlbums().first().size
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("👋 Bienvenido a tu espacio", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Consulta tu actividad favorita y navega a tus contenidos guardados.", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(32.dp))

        SectionCard(
            title = "Canciones favoritas",
            description = "Has marcado $tracksCount canciones",
            onClick = { navController?.navigate("personal/favoriteTracks") },
            color = Color(0xFFBBDEFB)
        )

        SectionCard(
            title = "Artistas favoritos",
            description = "Has marcado $artistsCount artistas",
            onClick = { navController?.navigate("personal/favoriteArtists") },
            color = Color(0xFFC8E6C9)
        )

        SectionCard(
            title = "Álbumes favoritos",
            description = "Has marcado $albumsCount álbumes",
            onClick = { navController?.navigate("personal/favoriteAlbums") },
            color = Color(0xFFFFF9C4)
        )
    }
}
