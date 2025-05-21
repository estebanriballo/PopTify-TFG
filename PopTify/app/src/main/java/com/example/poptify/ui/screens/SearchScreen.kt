package com.example.poptify.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.adamratzman.spotify.models.Track
import com.example.poptify.SpotifyApiRequest
import com.example.poptify.ui.components.TrackCard
import kotlinx.coroutines.launch

@Composable
fun SearchScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    val searchResults = remember { mutableStateListOf<Track>() } // Cambia esto por tu modelo de datos real
    val coroutineScope = rememberCoroutineScope()
    val spotifyApi = remember { SpotifyApiRequest() }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = {
                coroutineScope.launch {
                    try {
                        spotifyApi.buildSearchAPI()
                        val result = spotifyApi.search(searchQuery)
                        // Procesa los resultados aquí
                        // Esto es un ejemplo, debes adaptarlo a tu modelo de datos
                        searchResults.clear()
                        result.tracks?.items?.forEach { track ->
                            searchResults.add(track)
                        }
                    } catch (e: Exception) {
                        // Maneja el error
                        searchResults.clear()
                    }
                }
            },
            isSearching = isSearching,
            onActiveChange = { isSearching = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de resultados
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(searchResults) { item ->
                TrackCard(item)
                Divider()
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