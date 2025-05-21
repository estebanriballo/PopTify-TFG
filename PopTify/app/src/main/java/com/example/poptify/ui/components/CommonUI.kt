package com.example.poptify.ui.components

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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TrackCard(track: Track){
    Card(
        modifier = Modifier
            .fillMaxWidth()
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
                Text(
                    text = track.name,
                )
                Text(
                    text = "${track.artists[0].name}"
                )
            }
        }
    }
}