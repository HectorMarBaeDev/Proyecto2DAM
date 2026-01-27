package com.example.pokemonapp.ui.teams

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokemonapp.data.model.PokemonDto

@Composable
fun PokemonCard(
    pokemon: PokemonDto,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    pokemon.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text("Tipo: ${pokemon.primaryType}")
                pokemon.secondaryType?.let {
                    Text("Secundario: $it")
                }
            }

            TextButton(onClick = onDelete) {
                Text("Eliminar")
            }
        }
    }
}
