package com.example.poptify.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adamratzman.spotify.models.Album
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.SimpleAlbum
import com.adamratzman.spotify.models.Track
import com.example.poptify.FavoritesRepository
import com.example.poptify.SpotifyApiRequest
import com.example.poptify.ui.components.AlbumCard
import com.example.poptify.ui.components.ArtistCard
import com.example.poptify.ui.components.TrackCard
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    favoritesRepository: FavoritesRepository = remember { FavoritesRepository() },
    navController: NavController? = null,
    spotifyApi: SpotifyApiRequest = remember { SpotifyApiRequest() }
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
                Log.e("HomeScreen", "Error loading favorites", e)
            } finally {
                setLoading(false)
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
            // Sección de Tracks
            FavoriteSection(
                title = "Favorite Tracks",
                isLoading = isLoading,
                items = if (tracks.size > 3) tracks.take(4) else tracks,
                onSeeAllClick = { navController?.navigate("personal") },
                itemContent = { track ->
                    TrackCard(
                        track = track,
                        isFavorite = true,
                        onFavoriteClick = { _, _ -> },
                        onClick = { navController?.navigate("detail-track/${track.id}") }
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sección de Artistas
            FavoriteSection(
                title = "Favorite Artists",
                isLoading = isLoading,
                items = if (artists.size > 3) artists.take(4) else artists,
                onSeeAllClick = { navController?.navigate("personal") },
                itemContent = { artist ->
                    ArtistCard(
                        artist = artist,
                        isFavorite = true,
                        onFavoriteClick = { _, _ -> },
                        onClick = { navController?.navigate("detail-artist/${artist.id}") }
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sección de Álbumes
            FavoriteSection(
                title = "Favorite Albums",
                isLoading = isLoading,
                items = if (albums.size > 3) albums.take(4) else albums,
                onSeeAllClick = { navController?.navigate("personal") },
                itemContent = { album ->
                    AlbumCard(
                        album = album,
                        isFavorite = true,
                        onFavoriteClick = { _, _ -> },
                        onClick = { navController?.navigate("detail-album/${album.id}") }
                    )
                }
            )
        }
    }
}

@Composable
fun <T> FavoriteSection(
    title: String,
    isLoading: Boolean,
    items: List<T>,
    onSeeAllClick: () -> Unit,
    itemContent: @Composable (T) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )

            if (items.isNotEmpty()) {
                TextButton(onClick = onSeeAllClick) {
                    Text("See All")
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "See All",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (items.isEmpty()) {
            Text(
                text = "No favorites yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(items) { item ->
                    Box(modifier = Modifier.width(150.dp)) {
                        itemContent(item)
                    }
                }
            }
        }
    }
}