package com.example.pokemonapp.ui.teams

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pokemonapp.data.model.PokemonDto
import com.example.pokemonapp.ui.pokemon.PokemonTypeIcon

@Composable
fun PokemonCard(
    pokemon: PokemonDto,
    onDelete: () -> Unit
) {
    val spriteUrl =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${pokemon.pokedexNumber}.png"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // IZQUIERDA: sprite + info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = spriteUrl,
                    contentDescription = pokemon.name,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(end = 12.dp)
                )

                Column {
                    Text(
                        text = pokemon.name.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PokemonTypeIcon(pokemon.primaryType)

                        pokemon.secondaryType?.let {
                            PokemonTypeIcon(it)
                        }
                    }
                }
            }

            // DERECHA: eliminar
            TextButton(onClick = onDelete) {
                Text("Eliminar")
            }
        }
    }
}
