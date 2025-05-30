package com.example.poptify.ui.screens

import android.util.Log
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import kotlinx.coroutines.flow.first

@Composable
fun PersonalScreen(
    favoritesRepository: FavoritesRepository = remember { FavoritesRepository() },
    spotifyApi: SpotifyApiRequest = remember { SpotifyApiRequest() },
    navController: NavController? = null
) {
    val scope = rememberCoroutineScope()

    // Estados simplificados
    val (tracks, setTracks) = remember { mutableStateOf<List<Track>>(emptyList()) }
    val (artists, setArtists) = remember { mutableStateOf<List<Artist>>(emptyList()) }
    val (albums, setAlbums) = remember { mutableStateOf<List<Album>>(emptyList()) }
    val (isLoading, setLoading) = remember { mutableStateOf(true) }

    // Cargar datos una sola vez al entrar
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                // 1. Inicializar API si es necesario
                spotifyApi.buildSearchAPI()

                // 2. Cargar IDs de favoritos
                val favTracks = favoritesRepository.getFavoriteTracks().first()
                val favArtists = favoritesRepository.getFavoriteArtists().first()
                val favAlbums = favoritesRepository.getFavoriteAlbums().first()

                // 3. Obtener los objetos completos
                val loadedTracks = favTracks.mapNotNull {
                    runCatching { spotifyApi.getTrack(it.id) }.getOrNull()
                }

                val loadedArtists = favArtists.mapNotNull {
                    runCatching { spotifyApi.getArtist(it.id) }.getOrNull()
                }

                val loadedAlbums = favAlbums.mapNotNull {
                    runCatching { spotifyApi.getAlbum(it.id) }.getOrNull()
                }

                // 4. Actualizar estado
                setTracks(loadedTracks)
                setArtists(loadedArtists)
                setAlbums(loadedAlbums)

            } catch (e: Exception) {
                Log.e("PersonalScreen", "Error loading favorites", e)
            } finally {
                setLoading(false)
            }
        }
    }

    // UI simple
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
            // Sección de canciones
            if (tracks.isNotEmpty()) {
                Text("Tus canciones", style = MaterialTheme.typography.titleLarge)
                LazyColumn {
                    items(tracks) { track ->
                        TrackCard(
                            track = track,
                            isFavorite = true,
                            onFavoriteClick = { _, _ -> },
                            onClick = { navController?.navigate("detail-track/${track.id}") }
                        )
                    }
                }
            }

            // Sección de artistas
            if (artists.isNotEmpty()) {
                Text("Tus artistas", style = MaterialTheme.typography.titleLarge)
                LazyColumn {
                    items(artists) { artist ->
                        ArtistCard(
                            artist = artist,
                            isFavorite = true,
                            onFavoriteClick = { _, _ -> },
                            onClick = { navController?.navigate("detail-artist/${artist.id}") }
                        )
                    }
                }
            }

            // Sección de álbumes
            if (albums.isNotEmpty()) {
                Text("Tus álbumes", style = MaterialTheme.typography.titleLarge)
                LazyColumn {
                    items(albums) { album ->
                        AlbumCard(
                            album = album,
                            isFavorite = true,
                            onFavoriteClick = { _, _ -> },
                            onClick = { navController?.navigate("detail-album/${album.id}") }
                        )
                    }
                }
            }

            if (tracks.isEmpty() && artists.isEmpty() && albums.isEmpty()) {
                Text("Añade algunos favoritos para verlos aquí")
            }
        }
    }
}