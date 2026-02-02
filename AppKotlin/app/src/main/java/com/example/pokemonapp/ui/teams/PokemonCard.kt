package com.example.pokemonapp.ui.teams

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
            .aspectRatio(0.8f), // controla altura uniforme en grid
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // SPRITE
            AsyncImage(
                model = spriteUrl,
                contentDescription = pokemon.name,
                modifier = Modifier.size(72.dp)
            )

            // NOMBRE
            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // TIPOS
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PokemonTypeIcon(pokemon.primaryType)

                pokemon.secondaryType?.let {
                    PokemonTypeIcon(it)
                }
            }

            // BOTÓN ELIMINAR
            TextButton(
                onClick = onDelete,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Eliminar",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
