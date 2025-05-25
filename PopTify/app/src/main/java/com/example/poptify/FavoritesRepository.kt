package com.example.poptify

import android.util.Log
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.SimpleAlbum
import com.adamratzman.spotify.models.Track
import com.example.poptify.models.FavoriteAlbum
import com.example.poptify.models.FavoriteArtist
import com.example.poptify.models.FavoriteTrack
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FavoritesRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUserId: String?
        get() = auth.currentUser?.uid

    suspend fun addFavoriteTrack(track: Track) {
        if (currentUserId == null) return

        val favorite = FavoriteTrack(
            id = track.id,
            name = track.name,
            artistNames = track.artists.joinToString(", ") { it.name },
            albumName = track.album.name,
            albumCoverUrl = track.album.images.firstOrNull()?.url ?: "",
            durationMs = track.durationMs
        )

        db.collection("users")
            .document(currentUserId!!)
            .collection("favoriteTracks")
            .document(favorite.id)
            .set(favorite)
            .await()
    }

    suspend fun removeFavoriteTrack(trackId: String) {
        currentUserId?.let { uid ->
            db.collection("users")
                .document(uid)
                .collection("favoriteTracks")
                .document(trackId)
                .delete()
                .await()
        }
    }

    fun getFavoriteTracks(): Flow<List<FavoriteTrack>> = callbackFlow {
        if (currentUserId == null) {
            trySend(emptyList())
            return@callbackFlow
        }

        val listener = db.collection("users")
            .document(currentUserId!!)
            .collection("favoriteTracks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val tracks = snapshot?.toObjects(FavoriteTrack::class.java) ?: emptyList()

                trySend(tracks)
            }

        awaitClose { listener.remove() }
    }

    suspend fun addFavoriteArtist(artist: Artist) {
        if (currentUserId == null) return

        val favorite = FavoriteArtist(
            id = artist.id,
            name = artist.name,
            artistImage = artist.images[0].url
        )

        db.collection("users")
            .document(currentUserId!!)
            .collection("favoriteArtists")
            .document(favorite.id)
            .set(favorite)
            .await()
    }

    suspend fun removeFavoriteArtist(artistId: String) {
        currentUserId?.let { uid ->
            db.collection("users")
                .document(uid)
                .collection("favoriteArtists")
                .document(artistId)
                .delete()
                .await()
        }
    }

    fun getFavoriteArtists(): Flow<List<FavoriteArtist>> = callbackFlow {
        if (currentUserId == null) {
            trySend(emptyList())
            return@callbackFlow
        }

        Log.d("FavoritesRepo", "Iniciando listener de artistas") // Log de depuración

        val listener = db.collection("users")
            .document(currentUserId!!)
            .collection("favoriteArtists")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FavoritesRepo", "Error en listener de artistas", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val artists = snapshot?.toObjects(FavoriteArtist::class.java) ?: emptyList()
                Log.d("FavoritesRepo", "Artistas recibidos: ${artists.size}") // Log de depuración

                trySend(artists)
            }

        awaitClose {
            Log.d("FavoritesRepo", "Cerrando listener de artistas")
            listener.remove()
        }
    }

    suspend fun addFavoriteAlbum(album: SimpleAlbum) {
        if (currentUserId == null) return

        val favorite = FavoriteAlbum(
            id = album.id,
            name = album.name,
            artistNames = album.artists.joinToString(", ") { it.name },
            albumCoverUrl = album.images.firstOrNull()?.url ?: ""
        )

        db.collection("users")
            .document(currentUserId!!)
            .collection("favoriteAlbums")
            .document(favorite.id)
            .set(favorite)
            .await()
    }

    suspend fun removeFavoriteAlbum(albumId: String) {
        currentUserId?.let { uid ->
            db.collection("users")
                .document(uid)
                .collection("favoriteAlbums")
                .document(albumId)
                .delete()
                .await()
        }
    }

    fun getFavoriteAlbums(): Flow<List<FavoriteAlbum>> = callbackFlow {
        if (currentUserId == null) {
            trySend(emptyList())
            return@callbackFlow
        }

        val listener = db.collection("users")
            .document(currentUserId!!)
            .collection("favoriteAlbums")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val albums = snapshot?.toObjects(FavoriteAlbum::class.java) ?: emptyList()

                trySend(albums)
            }

        awaitClose { listener.remove() }
    }
}