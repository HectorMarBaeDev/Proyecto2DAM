package com.example.pokemonapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokemonapp.ui.login.LoginScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onUserSelected = { userId ->
                    navController.navigate("teams/$userId")
                }
            )
        }

        composable (
            route = "teams/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType}
            )
        ) {
            backStackEntry ->
            val userId = backStackEntry.arguments!!.getLong("teamId")
            TeamDetailScreen(teamId = teamId)
        }
    }
}