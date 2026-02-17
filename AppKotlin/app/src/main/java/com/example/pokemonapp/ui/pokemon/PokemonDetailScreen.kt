package com.example.pokemonapp.ui.pokemon

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokemonapp.data.model.PokemonDto
import com.example.pokemonapp.data.repository.PokemonRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    pokemonId: Long
) {

    val repository = remember { PokemonRepository() }
    val scope = rememberCoroutineScope()

    var pokemon by remember { mutableStateOf<PokemonDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pokemonId) {
        try {
            pokemon = repository.getPokemonById(pokemonId)
        } catch (e: Exception) {
            error = "Error cargando Pokémon"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle Pokémon") }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {

            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                error != null -> {
                    Text(error!!)
                }

                pokemon != null -> {

                    var item by remember { mutableStateOf(pokemon!!.item ?: "") }
                    var ability by remember { mutableStateOf(pokemon!!.ability ?: "") }
                    var hpIv by remember { mutableStateOf(pokemon!!.hpIv?.toString() ?: "31") }
                    var hpEv by remember { mutableStateOf(pokemon!!.hpEv?.toString() ?: "0") }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {

                        Text(
                            text = pokemon!!.name.replaceFirstChar { it.uppercase() },
                            fontSize = 22.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = item,
                            onValueChange = { item = it },
                            label = { Text("Item") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = ability,
                            onValueChange = { ability = it },
                            label = { Text("Ability") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = hpIv,
                            onValueChange = { hpIv = it },
                            label = { Text("HP IV") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = hpEv,
                            onValueChange = { hpEv = it },
                            label = { Text("HP EV") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        isLoading = true

                                        val updatedPokemon = pokemon!!.copy(
                                            item = item.ifBlank { null },
                                            ability = ability.ifBlank { null },
                                            hpIv = hpIv.toIntOrNull() ?: 31,
                                            hpEv = hpEv.toIntOrNull() ?: 0
                                        )

                                        pokemon = repository.updatePokemon(updatedPokemon)

                                    } catch (e: Exception) {
                                        error = "Error guardando cambios"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}
