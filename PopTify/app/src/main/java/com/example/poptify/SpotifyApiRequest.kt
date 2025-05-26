package com.example.poptify

import com.adamratzman.spotify.SpotifyAppApi
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
}