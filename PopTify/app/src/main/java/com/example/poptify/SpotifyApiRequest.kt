package com.example.poptify

import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.Album
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.PagingObject
import com.adamratzman.spotify.models.SimpleAlbum
import com.adamratzman.spotify.models.SpotifySearchResult
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.spotifyAppApi
import com.adamratzman.spotify.utils.Market

class SpotifyApiRequest {
    private val clientId = "6cdf9a0a06a44e648b2f3b820eaa5104"
    private val secretClientId = "bb18192ef20e4ef9bf39158e88a18c02" // Credenciales app Spotify for Developers
    private var api: SpotifyAppApi? = null

    suspend fun buildSearchAPI() {
        api = spotifyAppApi(clientId, secretClientId).build() // Construccion API
    }

    suspend fun search(searchQuery: String): SpotifySearchResult {
        return api!!.search.searchAllTypes(searchQuery, 50, 1, market = Market.ES) // Buscar resultados de todo tipo en españa con un maximo de 50 elementos
    }

    suspend fun getTrack(trackId: String): Track {
        return api!!.tracks.getTrack(trackId)!! // Conseguir el track a partir de su ID
    }

    suspend fun getArtist(artistId: String): Artist {
        return api!!.artists.getArtist(artistId)!! // Conseguir el artista a partir de su ID
    }

    suspend fun getRecommendations(artist: Artist): List<Artist>{
        return api!!.artists.getRelatedArtists(artist.id) // (Deprecado) Recomendaciones de artistas en base a un artista
    }

    suspend fun getAlbum(albumId: String): Album {
        return api!!.albums.getAlbum(albumId)!!  // Obtener album a partir de su ID
    }

    suspend fun getAlbums(artistId: String): List<SimpleAlbum> {
        return api!!.artists.getArtistAlbums(artistId).items    // Obtener albumes pertenecientes a un artista en base a su ID
    }

    suspend fun getTopTracks(artistId: String): List<Track> {
        return api!!.artists.getArtistTopTracks(artistId) // Obtener los tracks mas populares de un artista
    }
}