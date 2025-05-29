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
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    favoritesRepository: FavoritesRepository = remember { FavoritesRepository() },
    navController: NavController? = null,
    spotifyApi: SpotifyApiRequest = remember { SpotifyApiRequest() }
) {
    val coroutineScope = rememberCoroutineScope()

    // Estados para los favoritos
    val favoriteTracks = remember { mutableStateListOf<Track>() }
    val favoriteArtists = remember { mutableStateListOf<Artist>() }
    val favoriteAlbums = remember { mutableStateListOf<Album>() }

    // Estados de carga
    var isLoadingTracks by remember { mutableStateOf(true) }
    var isLoadingArtists by remember { mutableStateOf(true) }
    var isLoadingAlbums by remember { mutableStateOf(true) }

    // Cargar favoritos al iniciar
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                spotifyApi.buildSearchAPI()

                // Cargar todos los favoritos en paralelo
                val tracksDeferred = async { loadFavoriteTracks(favoritesRepository, spotifyApi, favoriteTracks) }
                val artistsDeferred = async { loadFavoriteArtists(favoritesRepository, spotifyApi, favoriteArtists) }
                val albumsDeferred = async { loadFavoriteAlbums(favoritesRepository, spotifyApi, favoriteAlbums) }

                Log.e("ee", favoriteTracks[0].name)

                // Esperar a que todos completen
                tracksDeferred.await().also { isLoadingTracks = false }
                artistsDeferred.await().also { isLoadingArtists = false }
                albumsDeferred.await().also { isLoadingAlbums = false }
            } catch (e: Exception) {
                e.printStackTrace()
                isLoadingTracks = false
                isLoadingArtists = false
                isLoadingAlbums = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Sección de Tracks
        FavoriteSection(
            title = "Favorite Tracks",
            isLoading = isLoadingTracks,
            items = favoriteTracks,
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
            isLoading = isLoadingArtists,
            items = favoriteArtists,
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
            isLoading = isLoadingAlbums,
            items = favoriteAlbums,
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

private suspend fun loadFavoriteTracks(
    favoritesRepository: FavoritesRepository,
    spotifyApi: SpotifyApiRequest,
    favoriteTracks: MutableList<Track>
): Boolean {
    return try {
        favoritesRepository.getFavoriteTracks().collect { favTracks ->
            favTracks.take(6).forEach { favTrack ->
                try {
                    val track = spotifyApi.getTrack(favTrack.id)
                    if (!favoriteTracks.any { it.id == track.id }) {
                        favoriteTracks.add(track)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

private suspend fun loadFavoriteArtists(
    favoritesRepository: FavoritesRepository,
    spotifyApi: SpotifyApiRequest,
    favoriteArtists: MutableList<Artist>
): Boolean {
    return try {
        favoritesRepository.getFavoriteArtists().collect { favArtists ->
            favArtists.take(6).forEach { favArtist ->
                try {
                    val artist = spotifyApi.getArtist(favArtist.id)
                    if (!favoriteArtists.any { it.id == artist.id }) {
                        favoriteArtists.add(artist)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

private suspend fun loadFavoriteAlbums(
    favoritesRepository: FavoritesRepository,
    spotifyApi: SpotifyApiRequest,
    favoriteAlbums: MutableList<Album>
): Boolean {
    return try {
        favoritesRepository.getFavoriteAlbums().collect { favAlbums ->
            favAlbums.take(6).forEach { favAlbum ->
                try {
                    val album = spotifyApi.getAlbum(favAlbum.id)
                    if (!favoriteAlbums.any { it.id == album.id }) {
                        favoriteAlbums.add(album)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}