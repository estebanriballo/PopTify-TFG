package com.example.poptify.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adamratzman.spotify.models.Album
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.Track
import com.example.poptify.FavoritesRepository
import com.example.poptify.SpotifyApiRequest
import com.example.poptify.ui.components.AlbumCard
import com.example.poptify.ui.components.ArtistCard
import com.example.poptify.ui.components.TrackCard
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun FavoriteTracksScreen(
    favoritesRepository: FavoritesRepository = remember { FavoritesRepository() },
    navController: NavController? = null,
    spotifyApi: SpotifyApiRequest = remember { SpotifyApiRequest() }
) {
    val scope = rememberCoroutineScope()

    // Estados simplificados
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

                // 3. Obtener los objetos completos
                val loadedTracks = favTracks.mapNotNull {
                    runCatching { spotifyApi.getTrack(it.id) }.getOrNull()
                }


                // 4. Actualizar estado
                tracks = (loadedTracks)
            } catch (e: Exception) {
                Log.e("FavoriteTracksScreen", "Error loading favorites", e)
            } finally {
                isLoading = (false)
            }
        }
    }

    val onFavoriteClick: (Any, Boolean) -> Unit = { item, isFavorite ->
        scope.launch {
            when (item) {
                is Track -> {
                    if (isFavorite) {
                        favoritesRepository.addFavoriteTrack(item)
                        if (tracks.none { it.id == item.id }) {
                            tracks = tracks + item
                        }
                    } else {
                        favoritesRepository.removeFavoriteTrack(item.id)
                        tracks = tracks.filterNot { it.id == item.id }
                    }
                }
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn {
                items(tracks) { track ->
                    TrackCard(
                        track = track,
                        isFavorite = true,
                        onFavoriteClick = onFavoriteClick,
                        onClick = { navController?.navigate("detail-track/${track.id}") }
                    )
                }
            }
        }
    }
}