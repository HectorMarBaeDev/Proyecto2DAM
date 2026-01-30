package com.example.pokemonapp.ui.teams

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokemonapp.data.model.PokemonDto
import com.example.pokemonapp.data.repository.PokemonRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(
    teamId: Long
) {

    val repository = remember { PokemonRepository() }
    val scope = rememberCoroutineScope()

    var pokemonList by remember { mutableStateOf<List<PokemonDto>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // CARGA INICIAL
    LaunchedEffect(Unit) {
        try {
            pokemonList = repository.getPokemonByTeam(teamId)
        } catch (e: Exception) {
            errorMessage = "Error cargando los Pokémon del equipo"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Equipo Pokémon") },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Text("+")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            // MENSAJE DE ERROR (si existe)
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(pokemonList) { pokemon ->
                    PokemonCard(
                        pokemon = pokemon,
                        onDelete = {
                            scope.launch {
                                try {
                                    repository.deletePokemon(pokemon.id)
                                    pokemonList = repository.getPokemonByTeam(teamId)
                                } catch (e: Exception) {
                                    errorMessage = "No se pudo eliminar el Pokémon"
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    // DIÁLOGO DE AÑADIR POKÉMON
    if (showDialog) {
        AddPokemonDialog(
            onDismiss = { showDialog = false },
            onAdd = { identifier ->
                scope.launch {
                    try {
                        repository.addPokemon(teamId, identifier)
                        pokemonList = repository.getPokemonByTeam(teamId)
                        showDialog = false
                    } catch (e: Exception) {
                        errorMessage = "No se pudo añadir el Pokémon"
                    }
                }
            }
        )
    }
}