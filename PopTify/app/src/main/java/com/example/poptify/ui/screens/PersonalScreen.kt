package com.example.poptify.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.Track
import kotlinx.coroutines.flow.first
import com.example.poptify.FavoritesRepository
import com.example.poptify.SpotifyApiRequest
import com.example.poptify.ui.components.SectionCard
import kotlinx.coroutines.launch

@Composable
fun PersonalScreen(
    navController: NavController? = null,
    spotifyApi: SpotifyApiRequest = remember { SpotifyApiRequest() },
    favoritesRepository: FavoritesRepository = remember { FavoritesRepository() }
) {
    var tracksCount by remember { mutableStateOf(0) }
    var artistsCount by remember { mutableStateOf(0) }
    var albumsCount by remember { mutableStateOf(0) }
    var topGenre by remember { mutableStateOf("Desconocido") }
    var topArtist by remember { mutableStateOf("Desconocido") }
    var topAlbum by remember { mutableStateOf("Desconocido") }

    val scope = rememberCoroutineScope()

    // Estados simplificados
    var artists by remember { mutableStateOf<List<Artist>>(emptyList()) }
    var tracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar datos una sola vez al entrar
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                // 1. Inicializar API si es necesario
                spotifyApi.buildSearchAPI()

                // 2. Cargar IDs de favoritos
                val favTracks = favoritesRepository.getFavoriteTracks().first()
                val favArtists = favoritesRepository.getFavoriteArtists().first()

                val loadedArtists = favArtists.mapNotNull {
                    runCatching { spotifyApi.getArtist(it.id) }.getOrNull()
                }
                val loadedTracks = favTracks.mapNotNull {
                    runCatching { spotifyApi.getTrack(it.id) }.getOrNull()
                }

                // 4. Actualizar estado
                tracks = (loadedTracks)
                artists = (loadedArtists)

                tracksCount = favoritesRepository.getFavoriteTracks().first().size
                artistsCount = favoritesRepository.getFavoriteArtists().first().size
                albumsCount = favoritesRepository.getFavoriteAlbums().first().size

                tracksCount = favoritesRepository.getFavoriteTracks().first().size
                artistsCount = favoritesRepository.getFavoriteArtists().first().size
                albumsCount = favoritesRepository.getFavoriteAlbums().first().size

                if (tracks.isNotEmpty() || artists.isNotEmpty()) {

                    fun <T> mostCommon(list: List<T>): T? =
                        list.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key

                    topGenre = mostCommon(artists.flatMap { it.genres }) ?: "Desconocido"
                    topArtist = mostCommon(tracks.mapNotNull { it.artists.firstOrNull()?.name }) ?: "Desconocido"
                    topAlbum = mostCommon(tracks.mapNotNull { it.album.name }) ?: "Desconocido"
                }

            } catch (e: Exception) {
                Log.e("FavoriteTracksScreen", "Error loading favorites", e)
            } finally {
                isLoading = (false)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("👋 Bienvenido a tu espacio", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Consulta tu actividad favorita y navega a tus contenidos guardados.", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SectionCard(
                title = "Canciones favoritas",
                description = "Has marcado $tracksCount canciones",
                onClick = { navController?.navigate("personal/favoriteTracks") },
                color = Color(0xFFBBDEFB)
            )
        }

        item {
            SectionCard(
                title = "Artistas favoritos",
                description = "Has marcado $artistsCount artistas",
                onClick = { navController?.navigate("personal/favoriteArtists") },
                color = Color(0xFFC8E6C9)
            )
        }

        item {
            SectionCard(
                title = "Álbumes favoritos",
                description = "Has marcado $albumsCount álbumes",
                onClick = { navController?.navigate("personal/favoriteAlbums") },
                color = Color(0xFFFFF9C4)
            )
        }

        item {
            SectionCard(
                title = "Género más escuchado",
                description = "Tu género favorito es $topGenre",
                onClick = { navController?.navigate("personal/favoriteArtists") },
                color = Color(0xFFD1C4E9)
            )
        }

        item {
            SectionCard(
                title = "Artista más frecuente",
                description = "Has guardado más canciones de $topArtist",
                onClick = { navController?.navigate("personal/favoriteTracks") },
                color = Color(0xFFFFCCBC)
            )
        }

        item {
            SectionCard(
                title = "Álbum más frecuente",
                description = "Tienes más canciones del álbum $topAlbum",
                onClick = { navController?.navigate("personal/favoriteTracks") },
                color = Color(0xFFB2DFDB)
            )
        }
    }
}
