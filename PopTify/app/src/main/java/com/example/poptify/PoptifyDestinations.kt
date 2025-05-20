package com.example.poptify

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

interface PoptifyDestination {
    val icon: ImageVector
    val route: String
}

object Home : PoptifyDestination {
    override val icon = Icons.Filled.Home
    override val route = "home"
}

object Search : PoptifyDestination {
    override val icon = Icons.Filled.Search
    override val route = "search"
}

object Personal : PoptifyDestination {
    override val icon = Icons.Filled.Person
    override val route = "personal"
}

object Rankings : PoptifyDestination {
    override val icon = Icons.Filled.Star
    override val route = "rankings"
}

object Settings : PoptifyDestination {
    override val icon = Icons.Filled.Settings
    override val route = "settings"
}

val poptifyTabRowScreens = listOf(Home, Search, Personal, Rankings)