package com.example.poptify.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun MainScreen(navController: NavController? = null) {
    val navController1 = rememberNavController()
    val auth = Firebase.auth

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            Row(
                Modifier
                    .selectableGroup()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Poptify",
                    style = MaterialTheme.typography.bodyLarge
                )
                TextButton(
                    onClick = {
                        auth.signOut()
                        navController?.navigate("login") {
                            popUpTo(0)
                        }
                    }
                ) {
                    Text("Cerrar sesión")
                }
            }
        },
        bottomBar = {
            Row(
                Modifier
                    .selectableGroup()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { navController1.navigate("home") },
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Home"
                    )
                }
                IconButton(
                    onClick = { navController1.navigate("search") },
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search"
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController1,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = "home") {
                HomeScreen()
            }
            composable(route = "search") {
                SearchScreen(navController = navController1)
            }
            composable(
                route = "detail-track/{trackId}",
                arguments = listOf(navArgument("trackId") { type = NavType.StringType })
            ) { backStackEntry ->
                val trackId = backStackEntry.arguments?.getString("trackId") ?: ""
                DetailTrack(trackId = trackId, navController = navController1)
            }

            composable(
                route = "detail-artist/{artistId}",
                arguments = listOf(navArgument("artistId") { type = NavType.StringType })
            ) { backStackEntry ->
                val artistId = backStackEntry.arguments?.getString("artistId") ?: ""
                DetailArtist(artistId = artistId, navController = navController1)
            }

            composable(
                route = "detail-album/{albumId}",
                arguments = listOf(navArgument("albumId") { type = NavType.StringType })
            ) { backStackEntry ->
                val albumId = backStackEntry.arguments?.getString("albumId") ?: ""
                DetailAlbum(albumId = albumId, navController = navController1)
            }
        }
    }
}