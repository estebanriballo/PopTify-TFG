package com.example.poptify.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.adamratzman.spotify.models.Album
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.SimpleAlbum
import com.adamratzman.spotify.models.Track
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TrackCard(
    track: Track,
    isFavorite: Boolean,
    onFavoriteClick: (Track, Boolean) -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

            }
    ) {
        Row {
            GlideImage(
                model = track.album.images[0].url,
                contentDescription = "Cover album ${track.name}",
                modifier = Modifier
                    .size(width = 90.dp, height = 90.dp)
                    .padding(all = 15.dp)
            )
            Column(
                modifier = Modifier
                    .padding(all = 15.dp)
            ) {
                Text(text = track.name)
                Text(text = track.artists[0].name)
            }
            IconButton(
                onClick = { onFavoriteClick(track, !isFavorite) }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                    tint = if (isFavorite) Color.Red else Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ArtistCard(
    artist: Artist,
    isFavorite: Boolean,
    onFavoriteClick: (Artist, Boolean) -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

            }
    ) {
        Row {
            GlideImage(
                model = artist.images[0].url,
                contentDescription = "Imagen de ${artist.name}",
                modifier = Modifier
                    .size(width = 90.dp, height = 90.dp)
                    .padding(all = 15.dp)
            )
            Column(
                modifier = Modifier
                    .padding(all = 15.dp)
            ) {
                Text(text = artist.name)
            }
            IconButton(
                onClick = { onFavoriteClick(artist, !isFavorite) }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                    tint = if (isFavorite) Color.Red else Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AlbumCard(
    album: SimpleAlbum,
    isFavorite: Boolean,
    onFavoriteClick: (SimpleAlbum, Boolean) -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

            }
    ) {
        Row {
            GlideImage(
                model = album.images[0].url,
                contentDescription = "Cover album ${album.name}",
                modifier = Modifier
                    .size(width = 90.dp, height = 90.dp)
                    .padding(all = 15.dp)
            )
            Column(
                modifier = Modifier
                    .padding(all = 15.dp)
            ) {
                Text(text = album.name)
                Text(text = album.artists[0].name)
            }
            IconButton(
                onClick = { onFavoriteClick(album, !isFavorite) }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                    tint = if (isFavorite) Color.Red else Color.Gray
                )
            }
        }
    }
}