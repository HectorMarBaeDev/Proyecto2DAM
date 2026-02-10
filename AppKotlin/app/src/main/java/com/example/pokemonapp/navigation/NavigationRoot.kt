package com.example.pokemonapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.pokemonapp.ui.login.LoginScreen
import com.example.pokemonapp.ui.login.RegisterScreen
import com.example.pokemonapp.ui.teams.TeamDetailScreen
import com.example.pokemonapp.ui.teams.TeamListScreen
import kotlinx.serialization.Serializable

@Serializable
data object LoginScreenKey : NavKey

@Serializable
data object RegisterScreenKey : NavKey

@Serializable
data class TeamListScreenKey(val userId: Long) : NavKey

@Serializable
data class TeamDetailScreenKey(val teamId: Long) : NavKey

@Composable
fun NavigationRoot() {

    val navBackStack = rememberNavBackStack(LoginScreenKey)

    NavDisplay(
        backStack = navBackStack,
        onBack = { if (navBackStack.size > 1) navBackStack.removeLastOrNull() },
        entryProvider = entryProvider {

            // -------------------- LOGIN --------------------
            entry<LoginScreenKey> {
                LoginScreen(
                    onLoginSuccess = { userId -> navBackStack.add(TeamListScreenKey(userId)) },
                    onGoToRegister = { navBackStack.add(RegisterScreenKey) }
                )
            }

            // -------------------- REGISTER --------------------
            entry<RegisterScreenKey> {
                RegisterScreen(
                    onRegisterSuccess = { navBackStack.add(LoginScreenKey) },
                    onGoToLogin = { navBackStack.add(LoginScreenKey) }
                )
            }

            // -------------------- TEAM LIST --------------------
            entry<TeamListScreenKey> { key ->
                TeamListScreen(
                    userId = key.userId,
                    onTeamClick = { teamId ->
                        navBackStack.add(TeamDetailScreenKey(teamId))
                    }
                )
            }

            // -------------------- TEAM DETAIL --------------------
            entry<TeamDetailScreenKey> { key ->
                TeamDetailScreen(teamId = key.teamId)
            }

        }
    )
}
