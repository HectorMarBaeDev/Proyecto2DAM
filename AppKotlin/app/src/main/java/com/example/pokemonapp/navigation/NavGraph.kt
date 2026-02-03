package com.example.pokemonapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokemonapp.ui.login.LoginScreen
import com.example.pokemonapp.ui.teams.TeamListScreen
import com.example.pokemonapp.ui.teams.TeamDetailScreen

@Composable
fun NavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // -------- LOGIN REAL --------
        composable("login") {
            LoginScreen(
                onLoginSuccess = { userId ->
                    navController.navigate("teams/$userId") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // -------- LISTA DE EQUIPOS --------
        composable(
            route = "teams/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType }
            )
        ) { backStackEntry ->

            val userId = backStackEntry.arguments!!.getLong("userId")

            TeamListScreen(
                userId = userId,
                onTeamClick = { teamId ->
                    navController.navigate("team/$teamId")
                }
            )
        }

        // -------- DETALLE DE EQUIPO --------
        composable(
            route = "team/{teamId}",
            arguments = listOf(
                navArgument("teamId") { type = NavType.LongType }
            )
        ) { backStackEntry ->

            val teamId = backStackEntry.arguments!!.getLong("teamId")
            TeamDetailScreen(teamId = teamId)
        }
    }
}

