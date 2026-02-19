package com.example.pokemonapp.presentation.teams

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pokemonapp.model.PokemonDto
import com.example.pokemonapp.presentation.pokemon.PokemonTypeIcon

@Composable
fun PokemonCard(
    pokemon: PokemonDto,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {

    val spriteUrl =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${pokemon.pokedexNumber}.png"

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.80f),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ---------- SPRITE ----------
            AsyncImage(
                model = spriteUrl,
                contentDescription = pokemon.name,
                modifier = Modifier
                    .size(90.dp)
            )

            Spacer(Modifier.height(10.dp))

            // ---------- NOMBRE ----------
            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Nº ${String.format("%03d", pokemon.pokedexNumber)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(10.dp))

            // ---------- TIPOS ----------
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PokemonTypeIcon(pokemon.primaryType)

                pokemon.secondaryType?.let {
                    PokemonTypeIcon(it)
                }
            }

            Spacer(Modifier.weight(1f))

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 0.5.dp
            )

            // ---------- ELIMINAR ----------
            TextButton(
                onClick = onDelete,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "Eliminar",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
