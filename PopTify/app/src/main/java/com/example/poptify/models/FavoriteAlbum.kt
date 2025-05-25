package com.example.poptify.models

data class FavoriteAlbum(
    val id: String = "",
    val name: String = "",
    val artistNames: String = "",
    val albumCoverUrl: String = "",
    val addedAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)
