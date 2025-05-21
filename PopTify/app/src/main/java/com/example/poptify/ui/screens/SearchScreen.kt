package com.example.poptify.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.adamratzman.spotify.models.Album
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.SimpleAlbum
import com.adamratzman.spotify.models.Track
import com.example.poptify.SpotifyApiRequest
import com.example.poptify.ui.components.AlbumCard
import com.example.poptify.ui.components.ArtistCard
import com.example.poptify.ui.components.TrackCard
import kotlinx.coroutines.launch

@Composable
fun SearchScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    val searchTracksResults = remember { mutableStateListOf<Track>() }
    val searchArtistsResults = remember { mutableStateListOf<Artist>() }
    val searchAlbumsResults = remember { mutableStateListOf<SimpleAlbum>() }
    val coroutineScope = rememberCoroutineScope()
    val spotifyApi = remember { SpotifyApiRequest() }
    val radioOptions = listOf("Tracks", "Artists", "Albums")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = {
                coroutineScope.launch {
                    try {
                        spotifyApi.buildSearchAPI()
                        val result = spotifyApi.search(searchQuery)
                        searchTracksResults.clear()
                        searchArtistsResults.clear()
                        searchAlbumsResults.clear()
                        result.tracks?.items?.forEach { track ->
                            searchTracksResults.add(track)
                        }
                        result.artists?.items?.forEach { artist ->
                            searchArtistsResults.add(artist)
                        }
                        result.albums?.items?.forEach { album ->
                            searchAlbumsResults.add(album)
                        }
                    } catch (e: Exception) {
                        // Maneja el error
                        searchTracksResults.clear()
                    }
                }
            },
            isSearching = isSearching,
            onActiveChange = { isSearching = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Opciones de radio button mejoradas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            radioOptions.forEach { text ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = { onOptionSelected(text) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 8.dp)
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de resultados
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            if (selectedOption == "Tracks"){
                items(searchTracksResults) { item ->
                    TrackCard(item)
                    Divider()
                }
            } else if (selectedOption == "Artists"){
                items(searchArtistsResults) { item ->
                    ArtistCard(item)
                    Divider()
                }
            } else if (selectedOption == "Albums") {
                items(searchAlbumsResults) { item ->
                    AlbumCard(item)
                    Divider()
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    isSearching: Boolean,
    onActiveChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        modifier = modifier,
        elevation = AppBarDefaults.TopAppBarElevation,
        color = MaterialTheme.colors.primary
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar en Spotify...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar"
                )
            },
            trailingIcon = {
                if (isSearching) {
                    IconButton(
                        onClick = {
                            onQueryChange("")
                            onActiveChange(false)
                            keyboardController?.hide()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar búsqueda"
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch()
                    keyboardController?.hide()
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.surface,
                textColor = MaterialTheme.colors.onSurface,
                cursorColor = MaterialTheme.colors.onSurface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}