package com.example.poptify

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.adamratzman.spotify.models.Artist
import com.example.poptify.ui.components.PoptifyTabRow
import com.example.poptify.ui.screens.HomeScreen
import com.example.poptify.ui.screens.PersonalScreen
import com.example.poptify.ui.screens.RankingsScreen
import com.example.poptify.ui.screens.SearchScreen
import com.example.poptify.ui.screens.SettingsScreen
import com.example.poptify.ui.theme.PopTifyTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PoptifyApp()
        }
    }
}

@Composable
fun PoptifyApp() {
    PopTifyTheme {
        val navController = rememberNavController()

        val currentBackStack by navController.currentBackStackEntryAsState()

        val currentDestination = currentBackStack?.destination

        val currentScreen = poptifyTabRowScreens.find { it.route == currentDestination?.route } ?: Home

        Scaffold(
            modifier = Modifier.systemBarsPadding(),
            topBar = {
                Text(
                    text = "PopTify",
                    modifier = Modifier.padding(top = 8.dp)
                )
            },
            bottomBar = {
                PoptifyTabRow(
                    allScreens = poptifyTabRowScreens,
                    onTabSelected = { newScreen ->
                        navController
                            .navigateSingleTopTo(newScreen.route)
                    },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = Home.route) {
                    HomeScreen()
                }
                composable(route = Search.route) {
                    SearchScreen()
                }
                composable(route = Personal.route) {
                    PersonalScreen()
                }
                composable(route = Rankings.route) {
                    RankingsScreen()
                }
                composable(route = Settings.route) {
                    SettingsScreen()
                }
            }
        }
    }

//    val artistsState = remember { mutableStateOf<List<Artist>?>(null) }
//
//    // Llama a la función suspend desde Compose
//    LaunchedEffect(Unit) {
//        val api = SpotifyApiRequest()
//        api.buildSearchAPI()
//        val searchResult = api.search("8belial")
//        artistsState.value = searchResult.artists?.items
//    }
//
//    if (artistsState.value == null) {
//        Text("Buscando...")
//    } else {
//        Column(
//            modifier = Modifier
//        ) {
//            for (artist in artistsState.value!!) {
//                Text(artist.name)
//            }
//        }
//    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop  = true
    }
//
//private fun NavHostController.navigateToSimgleAccount(accountType: String) {
//    this.navigateSingleTopTo("${SingleAccount.route}/$accountType")
//}
