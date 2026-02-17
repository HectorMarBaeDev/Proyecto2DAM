package com.example.pokemonapp.ui.teams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pokemonapp.data.model.PokemonListItemDto
import com.example.pokemonapp.data.repository.PokemonRepository
import com.example.pokemonapp.ui.pokemon.PokemonTypeIcon
import kotlinx.coroutines.launch

@Composable
fun SelectPokemonDialog(
    repository: PokemonRepository,
    onDismiss: () -> Unit,
    onPokemonSelected: (PokemonListItemDto) -> Unit
) {
    val scope = rememberCoroutineScope()
    var pokemonList by remember { mutableStateOf<List<PokemonListItemDto>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            pokemonList = repository.getAllPokemon()
            isLoading = false
        }
    }

    val filteredList = pokemonList.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text("Selecciona un Pokémon") },
        text = {

            Column {

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar Pokémon") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    LazyColumn(
                        modifier = Modifier.height(400.dp)
                    ) {
                        items(filteredList) { pokemon ->

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onPokemonSelected(pokemon)
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // Sprite desde GitHub
                                AsyncImage(
                                    model = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${pokemon.pokedexNumber}.png",
                                    contentDescription = pokemon.name,
                                    modifier = Modifier.size(60.dp)
                                )

                                Spacer(Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {

                                    Text(
                                        text = "#${String.format("%04d", pokemon.pokedexNumber)} ${pokemon.name.replaceFirstChar { it.uppercase() }}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        PokemonTypeIcon(pokemon.primaryType)

                                        pokemon.secondaryType?.let {
                                            PokemonTypeIcon(it)
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    )
}
