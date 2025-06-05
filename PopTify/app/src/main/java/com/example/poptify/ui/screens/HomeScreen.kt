package com.example.poptify.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.google.firebase.auth.FirebaseAuth
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

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    // Estados simplificados
    var tracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var artists by remember { mutableStateOf<List<Artist>>(emptyList()) }
    var albums by remember { mutableStateOf<List<Album>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar datos una sola vez al entrar
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                // Inicializar API si es necesario
                spotifyApi.buildSearchAPI()

                // Cargar IDs de favoritos
                val favTracks = favoritesRepository.getFavoriteTracks().first()
                val favArtists = favoritesRepository.getFavoriteArtists().first()
                val favAlbums = favoritesRepository.getFavoriteAlbums().first()

                // Obtener los objetos completos
                val loadedTracks = favTracks.mapNotNull {
                    runCatching { spotifyApi.getTrack(it.id) }.getOrNull()
                }

                val loadedArtists = favArtists.mapNotNull {
                    runCatching { spotifyApi.getArtist(it.id) }.getOrNull()
                }

                val loadedAlbums = favAlbums.mapNotNull {
                    runCatching { spotifyApi.getAlbum(it.id) }.getOrNull()
                }

                // Actualizar estado
                tracks = (loadedTracks)
                artists = (loadedArtists)
                albums =(loadedAlbums)
            } catch (e: Exception) {
                Log.e("HomeScreen", "Error loading favorites", e)
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

                is Artist -> {
                    if (isFavorite) {
                        favoritesRepository.addFavoriteArtist(item)
                        if (artists.none { it.id == item.id }) {
                            artists = artists + item
                        }
                    } else {
                        favoritesRepository.removeFavoriteArtist(item.id)
                        artists = artists.filterNot { it.id == item.id }
                    }
                }

                is Album -> {
                    if (isFavorite) {
                        favoritesRepository.addFavoriteAlbum(item)
                        if (albums.none { it.id == item.id }) {
                            albums = albums + item
                        }
                    } else {
                        favoritesRepository.removeFavoriteAlbum(item.id)
                        albums = albums.filterNot { it.id == item.id }
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
            // Sección de Tracks
            FavoriteSection(
                title = "Tracks Favoritos",
                isLoading = isLoading,
                items = if (tracks.size > 3) tracks.take(4) else tracks,
                onSeeAllClick = { navController?.navigate("personal/favoriteTracks") },
                itemContent = { track ->
                    TrackCard(
                        track = track,
                        isFavorite = true,
                        onFavoriteClick = onFavoriteClick,
                        onClick = { navController?.navigate("detail-track/${track.id}") }
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sección de Artistas
            FavoriteSection(
                title = "Artistas Favoritos",
                isLoading = isLoading,
                items = if (artists.size > 3) artists.take(4) else artists,
                onSeeAllClick = { navController?.navigate("personal/favoriteArtists") },
                itemContent = { artist ->
                    ArtistCard(
                        artist = artist,
                        isFavorite = true,
                        onFavoriteClick = onFavoriteClick,
                        onClick = { navController?.navigate("detail-artist/${artist.id}") }
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sección de Álbumes
            FavoriteSection(
                title = "Albumes Favoritos",
                isLoading = isLoading,
                items = if (albums.size > 3) albums.take(4) else albums,
                onSeeAllClick = { navController?.navigate("personal/favoriteAlbums") },
                itemContent = { album ->
                    AlbumCard(
                        album = album,
                        isFavorite = true,
                        onFavoriteClick = onFavoriteClick,
                        onClick = { navController?.navigate("detail-album/${album.id}") }
                    )
                }
            )
        }
    }
}

//Seccion que muestra
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
                TextButton(onClick = onSeeAllClick) {               //Botón que te manda a la lista completa de favoritos de su tipo
                    Text("Ver Todos")
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
                text = "No hay favoritos aún",
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