package com.example.poptify.models

data class FavoriteTrack(
    val id: String = "",
    val name: String = "",
    val artistNames: String = "",
    val albumName: String = "",
    val albumCoverUrl: String = "",
    val durationMs: Int = 0,
    val addedAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)
