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
    private val secretClientId = "bb18192ef20e4ef9bf39158e88a18c02"
    private var api: SpotifyAppApi? = null

    suspend fun buildSearchAPI() {
        api = spotifyAppApi(clientId, secretClientId).build()
    }

    suspend fun search(searchQuery: String): SpotifySearchResult {
        return api!!.search.searchAllTypes(searchQuery, 50, 1, market = Market.ES)
    }

    suspend fun getTrack(trackId: String): Track {
        return api!!.tracks.getTrack(trackId)!!
    }

    suspend fun getArtist(artistId: String): Artist {
        return api!!.artists.getArtist(artistId)!!
    }

    suspend fun getRecommendations(artist: Artist): List<Artist>{
        return api!!.artists.getRelatedArtists(artist.id)
    }

    suspend fun getAlbum(albumId: String): Album {
        return api!!.albums.getAlbum(albumId)!!
    }

    suspend fun getAlbums(artistId: String): List<SimpleAlbum> {
        return api!!.artists.getArtistAlbums(artistId).items
    }

    suspend fun getTopTracks(artistId: String): List<Track> {
        return api!!.artists.getArtistTopTracks(artistId)
    }
}