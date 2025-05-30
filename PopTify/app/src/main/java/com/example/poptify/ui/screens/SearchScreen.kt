package com.example.poptify.ui.screens

import SearchHistoryPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.SimpleAlbum
import com.adamratzman.spotify.models.Track
import com.example.poptify.FavoritesRepository
import com.example.poptify.SpotifyApiRequest
import com.example.poptify.ui.components.AlbumCard
import com.example.poptify.ui.components.ArtistCard
import com.example.poptify.ui.components.TrackCard
import kotlinx.coroutines.launch
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.adamratzman.spotify.models.Album

@Composable
fun SearchScreen(
    favoritesRepository: FavoritesRepository = remember { FavoritesRepository() },
    navController: NavController? = null,
    context: Context = LocalContext.current
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    val searchTracksResults = remember { mutableStateListOf<Track>() }
    val searchArtistsResults = remember { mutableStateListOf<Artist>() }
    val searchAlbumsResults = remember { mutableStateListOf<SimpleAlbum>() }
    val coroutineScope = rememberCoroutineScope()
    val spotifyApi = remember { SpotifyApiRequest() }
    val radioOptions = listOf("Tracks", "Artists", "Albums")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    var hasSearched by remember { mutableStateOf(false) }

    val favoriteTracks = remember { mutableStateListOf<String>() }
    val favoriteArtists = remember { mutableStateListOf<String>() }
    val favoriteAlbums = remember { mutableStateListOf<String>() }

    val searchHistoryPreferences = remember { SearchHistoryPreferences(context) }
    val searchHistory by searchHistoryPreferences.searchHistory.collectAsState(initial = emptyList())
    var showHistory by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // Listener para tracks (que funciona)
        favoritesRepository.getFavoriteTracks().collect { tracks ->
            favoriteTracks.clear()
            favoriteTracks.addAll(tracks.map { it.id })
        }
    }

    LaunchedEffect(Unit) {
        // Listener para artistas
        favoritesRepository.getFavoriteArtists().collect { artists ->
            favoriteArtists.clear()
            favoriteArtists.addAll(artists.map { it.id })
        }
    }

    LaunchedEffect(Unit) {
        // Listener para álbumes
        favoritesRepository.getFavoriteAlbums().collect { albums ->
            favoriteAlbums.clear()
            favoriteAlbums.addAll(albums.map { it.id })
        }
    }

    fun onFavoriteClick(item: Any, isFavorite: Boolean) {
        coroutineScope.launch {
            when (item) {
                is Track -> {
                    if (isFavorite) {
                        favoritesRepository.addFavoriteTrack(item)
                        favoriteTracks.add(item.id) // Actualización local inmediata
                    } else {
                        favoritesRepository.removeFavoriteTrack(item.id)
                        favoriteTracks.remove(item.id) // Actualización local inmediata
                    }
                }
                is Artist -> {
                    if (isFavorite) {
                        favoritesRepository.addFavoriteArtist(item)
                        favoriteArtists.add(item.id) // Actualización local inmediata
                    } else {
                        favoritesRepository.removeFavoriteArtist(item.id)
                        favoriteArtists.remove(item.id) // Actualización local inmediata
                    }
                }
                is SimpleAlbum -> {
                    if (isFavorite) {
                        favoritesRepository.addFavoriteAlbum(item)
                        favoriteAlbums.add(item.id) // Actualización local inmediata
                    } else {
                        favoritesRepository.removeFavoriteAlbum(item.id)
                        favoriteAlbums.remove(item.id) // Actualización local inmediata
                    }
                }
                is Album -> {
                    if (isFavorite) {
                        favoritesRepository.addFavoriteAlbum(item)
                        favoriteAlbums.add(item.id) // Actualización local inmediata
                    } else {
                        favoritesRepository.removeFavoriteAlbum(item.id)
                        favoriteAlbums.remove(item.id) // Actualización local inmediata
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = {
                searchQuery = it
                showHistory = it.isEmpty()
            },
            onSearch = {
                if (searchQuery.isNotBlank()) {
                    coroutineScope.launch {
                        searchHistoryPreferences.addSearchQuery(searchQuery)
                    }
                    isSearching = true
                    hasSearched = true
                    showHistory = false

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
                            searchTracksResults.clear()
                            searchArtistsResults.clear()
                            searchAlbumsResults.clear()
                        } finally {
                            isSearching = false
                        }
                    }
                }
            },
            isSearching = isSearching,
            onActiveChange = { isSearching = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (showHistory && searchHistory.isNotEmpty()) {
            Text(
                text = "Historial de búsqueda",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp, 8.dp)
            )
            LazyColumn {
                items(searchHistory) { historyItem ->
                    Surface(
                        onClick = {
                            searchQuery = historyItem
                            showHistory = false
                            if (historyItem.isNotBlank()) {
                                coroutineScope.launch {
                                    searchHistoryPreferences.addSearchQuery(historyItem)
                                }
                                isSearching = true
                                hasSearched = true
                                showHistory = false

                                coroutineScope.launch {
                                    try {
                                        spotifyApi.buildSearchAPI()
                                        val result = spotifyApi.search(historyItem) // Usamos historyItem en lugar de searchQuery
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
                                        searchTracksResults.clear()
                                        searchArtistsResults.clear()
                                        searchAlbumsResults.clear()
                                    } finally {
                                        isSearching = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = historyItem,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        } else {
            if (hasSearched && (searchTracksResults.isNotEmpty() ||
                        searchArtistsResults.isNotEmpty() ||
                        searchAlbumsResults.isNotEmpty())) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (searchTracksResults.isNotEmpty()){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .selectable(
                                    selected = (radioOptions[0] == selectedOption),
                                    onClick = { onOptionSelected(radioOptions[0]) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 8.dp)
                        ) {
                            RadioButton(
                                selected = (radioOptions[0] == selectedOption),
                                onClick = null
                            )
                            Text(
                                text = radioOptions[0],
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                    if (searchArtistsResults.isNotEmpty()){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .selectable(
                                    selected = (radioOptions[1] == selectedOption),
                                    onClick = { onOptionSelected(radioOptions[1]) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 8.dp)
                        ) {
                            RadioButton(
                                selected = (radioOptions[1] == selectedOption),
                                onClick = null
                            )
                            Text(
                                text = radioOptions[1],
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                    if (searchAlbumsResults.isNotEmpty()){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .selectable(
                                    selected = (radioOptions[2] == selectedOption),
                                    onClick = { onOptionSelected(radioOptions[2]) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 8.dp)
                        ) {
                            RadioButton(
                                selected = (radioOptions[2] == selectedOption),
                                onClick = null
                            )
                            Text(
                                text = radioOptions[2],
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (selectedOption == "Tracks" && searchTracksResults.isNotEmpty()) {
                    items(searchTracksResults) { item ->
                        TrackCard(
                            track = item,
                            isFavorite = favoriteTracks.contains(item.id),
                            onFavoriteClick = { t, fav -> onFavoriteClick(t, fav) },
                            onClick = {
                                navController?.navigate("detail-track/${item.id}") // Pasa el ID aquí
                            }
                        )
                        Spacer(
                            Modifier.height(3.dp)
                        )
                    }
                } else if (selectedOption == "Artists" && searchArtistsResults.isNotEmpty()) {
                    items(searchArtistsResults) { item ->
                        ArtistCard(
                            artist = item,
                            isFavorite = favoriteArtists.contains(item.id),
                            onFavoriteClick = { t, fav -> onFavoriteClick(t, fav) },
                            onClick = {
                                navController?.navigate("detail-artist/${item.id}") // Pasa el ID aquí
                            }
                        )
                        Spacer(
                            Modifier.height(3.dp)
                        )
                    }
                } else if (selectedOption == "Albums" && searchAlbumsResults.isNotEmpty()) {
                    items(searchAlbumsResults) { item ->
                        AlbumCard(
                            album = item,
                            isFavorite = favoriteAlbums.contains(item.id),
                            onFavoriteClick = { t, fav -> onFavoriteClick(t, fav) },
                            onClick = {
                                navController?.navigate("detail-album/${item.id}") // Pasa el ID aquí
                            }
                        )
                        Spacer(
                            Modifier.height(3.dp)
                        )
                    }
                } else if (hasSearched) {
                    item {
                        Text(
                            text = "No se encontraron resultados",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
        color = MaterialTheme.colorScheme.primary
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
                cursorColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}