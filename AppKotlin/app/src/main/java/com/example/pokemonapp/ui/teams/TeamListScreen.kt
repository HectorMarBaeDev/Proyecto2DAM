package com.example.pokemonapp.ui.teams

import PokemonRepository
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokemonapp.data.model.TeamDto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamListScreen(
    userId: Long,
    onTeamClick: (Long) -> Unit
) {

    val repository = remember { PokemonRepository() }
    val scope = rememberCoroutineScope()

    var teams by remember { mutableStateOf<List<TeamDto>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        teams = repository.getTeamsByUser(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis equipos") },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Text("+")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(teams) { team ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            onTeamClick(team.id)
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(team.name, style = MaterialTheme.typography.titleMedium)
                        Text("Formato: ${team.format}")
                    }
                }
            }
        }
    }

    if (showDialog) {
        CreateTeamDialog(
            onDismiss = { showDialog = false },
            onCreate = { name, format ->
                scope.launch {
                    repository.createTeam(userId, name, format)
                    teams = repository.getTeamsByUser(userId)
                    showDialog = false
                }
            }
        )
    }
}