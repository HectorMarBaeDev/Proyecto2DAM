package com.example.pokemonapp.ui.teams

import PokemonRepository
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokemonapp.data.model.PokemonDto
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

    LaunchedEffect(Unit) {
        pokemonList = repository.getPokemonByTeam(teamId)
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

        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 🔥 2 columnas
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(pokemonList) { pokemon ->
                PokemonCard(
                    pokemon = pokemon,
                    onDelete = {
                        scope.launch {
                            repository.deletePokemon(pokemon.id)
                            pokemonList = repository.getPokemonByTeam(teamId)
                        }
                    }
                )
            }
        }
    }

    if (showDialog) {
        AddPokemonDialog(
            onDismiss = { showDialog = false },
            onAdd = { identifier ->
                scope.launch {
                    repository.addPokemon(teamId, identifier)
                    pokemonList = repository.getPokemonByTeam(teamId)
                    showDialog = false
                }
            }
        )
    }
}
