package com.example.poptify.ui.components

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adamratzman.spotify.models.Track
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.*
import androidx.compose.foundation.clickable
import com.adamratzman.spotify.models.Album
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.SimpleAlbum

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TrackCard(track: Track) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/track/${track.id}"))
                context.startActivity(intent)
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
                Text(text = "${track.artists[0].name}")
                Text(
                    text = "Tocar para abrir en Spotify",
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ArtistCard(artist: Artist) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/artist/${artist.id}"))
                context.startActivity(intent)
            }
    ) {
        Row {
            GlideImage(
                model = artist.images[0].url,
                contentDescription = "Nombre artista ${artist.name}",
                modifier = Modifier
                    .size(width = 90.dp, height = 90.dp)
                    .padding(all = 15.dp)
            )
            Column(
                modifier = Modifier
                    .padding(all = 15.dp)
            ) {
                Text(text = artist.name)
                Text(
                    text = "Tocar para abrir en Spotify",
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AlbumCard(album: SimpleAlbum) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/album/${album.id}"))
                context.startActivity(intent)
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
                Text(text = "${album.artists[0].name}")
                Text(
                    text = "Tocar para abrir en Spotify",
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

