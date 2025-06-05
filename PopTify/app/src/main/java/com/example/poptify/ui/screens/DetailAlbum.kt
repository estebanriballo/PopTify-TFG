package com.example.poptify.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adamratzman.spotify.models.Album
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.SimpleAlbum
import com.adamratzman.spotify.models.Track
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.poptify.FavoritesRepository
import com.example.poptify.R
import com.example.poptify.SpotifyApiRequest
import com.example.poptify.ui.components.ArtistCard
import com.example.poptify.ui.components.TrackCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun DetailAlbum(
    favoritesRepository: FavoritesRepository = remember { FavoritesRepository() },
    albumId: String,
    navController: NavController? = null,
    spotifyApi: SpotifyApiRequest = remember { SpotifyApiRequest() }
) {
    var album by remember { mutableStateOf<Album?>(null) } //Inicialización variables que se rellenarán al recibir un Album
    val artists = remember { mutableStateListOf<Artist>() }
    val tracks = remember { mutableStateListOf<Track>() }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val favoriteTracks = remember { mutableStateListOf<String>() }
    val favoriteArtists = remember { mutableStateListOf<String>() }
    val favoriteAlbums = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        favoritesRepository.getFavoriteTracks().collect { tracks ->
            favoriteTracks.clear()                                              // Carga de listas con los favoritos sacados de Firebase
            favoriteTracks.addAll(tracks.map { it.id })
        }
    }

    LaunchedEffect(Unit) {
        favoritesRepository.getFavoriteArtists().collect { artists ->
            favoriteArtists.clear()
            favoriteArtists.addAll(artists.map { it.id })
        }
    }

    LaunchedEffect(Unit) {
        favoritesRepository.getFavoriteAlbums().collect { albums ->
            favoriteAlbums.clear()
            favoriteAlbums.addAll(albums.map { it.id })
        }
    }

    fun onFavoriteClick(item: Any, isFavorite: Boolean) {
        coroutineScope.launch {
            when (item) {                               // Accion de añadir o eliminar de favoritos
                is Track -> {
                    if (isFavorite) {
                        favoritesRepository.addFavoriteTrack(item)
                        favoriteTracks.add(item.id)
                    } else {
                        favoritesRepository.removeFavoriteTrack(item.id)
                        favoriteTracks.remove(item.id)
                    }
                }
                is Artist -> {
                    if (isFavorite) {
                        favoritesRepository.addFavoriteArtist(item)
                        favoriteArtists.add(item.id)
                    } else {
                        favoritesRepository.removeFavoriteArtist(item.id)
                        favoriteArtists.remove(item.id)
                    }
                }
                is SimpleAlbum -> {
                    if (isFavorite) {
                        favoritesRepository.addFavoriteAlbum(item)
                        favoriteAlbums.add(item.id)
                    } else {
                        favoritesRepository.removeFavoriteAlbum(item.id)
                        favoriteAlbums.remove(item.id)
                    }
                }
                is Album -> {
                    if (isFavorite) {
                        favoritesRepository.addFavoriteAlbum(item)
                        favoriteAlbums.add(item.id)
                    } else {
                        favoritesRepository.removeFavoriteAlbum(item.id)
                        favoriteAlbums.remove(item.id)
                    }
                }
            }
        }
    }

    LaunchedEffect(albumId) {
        try {
            spotifyApi.buildSearchAPI()
            album = spotifyApi.getAlbum(albumId)
        } catch (e: Exception) {
            error = "Error al cargar el artista"
        } finally {
            isLoading = false
        }

        try {
            spotifyApi.buildSearchAPI()
            var art: Artist
            for (artist in album!!.artists) {
                art = spotifyApi.getArtist(artist.id)
                artists.add(art)
            }
        } catch (e: Exception) {
            error = "Error al cargar los albumes"
        } finally {
            isLoading = false
        }

        try {
            spotifyApi.buildSearchAPI()
            var tra: Track
            for (track in album!!.tracks) {
                tra = spotifyApi.getTrack(track.id)
                tracks.add(tra)
            }
        } catch (e: Exception) {
            error = "Error al cargar las canciones"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles Album") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {                           // Volver a la pantalla anterior
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {                                                 // Añadir a favorito desde el detalle
                    if (album != null) {
                        val isFavorite = favoriteAlbums.contains(album!!.id)
                        IconButton(onClick = {
                            onFavoriteClick(album!!, !isFavorite)
                        }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error ?: "Error desconocido")
                }
            }
            album != null -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val context = LocalContext.current

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(                                                               // Titulo del Album
                        text = album?.name ?: "Título desconocido",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GlideImage(                                                             // Caratula del album, si no tiene, una imagen por defecto
                        model = album?.images?.firstOrNull()?.url ?: R.drawable.ic_music_note,
                        contentDescription = "Portada del álbum",
                        modifier = Modifier
                            .size(300.dp)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop,
                        colorFilter = if (album?.images?.firstOrNull()?.url == null) {
                            ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                        } else null
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Popularidad",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(                                // Barra de popularidad
                        progress = (album?.popularity ?: 0) / 100f,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(50)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${album?.popularity}/100",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(                                           // Relación con Spotify (Si está instalado)
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(album!!.externalUrls.spotify))
                                context.startActivity(intent)
                            },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Text(
                            text = "Tocar para abrir en Spotify",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Artists",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column {                                            // Lista de Artistas dueños del Album
                        artists.forEach{ artist ->
                            ArtistCard(
                                artist = artist,
                                isFavorite = favoriteArtists.contains(artist.id),
                                onFavoriteClick = { t, fav -> onFavoriteClick(t, fav) },
                                onClick = {
                                    navController?.navigate("detail-artist/${artist.id}")
                                }
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Tracks",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column {                                        // Lista de canciones del Album
                        tracks.forEach{ track ->
                            TrackCard(
                                track = track,
                                isFavorite = favoriteTracks.contains(track.id),
                                onFavoriteClick = {t, fav -> onFavoriteClick(t, fav)},
                                onClick = {
                                    navController?.navigate("detail-track/${track.id}")
                                }
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}