package com.example.poptify.models

data class FavoriteArtist(
    val id: String = "",
    val name: String = "",
    val artistImage: String = "",
    val addedAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)
