package com.example.pokemonapp.ui.teams

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pokemonapp.data.model.PokemonDto
import com.example.pokemonapp.ui.pokemon.PokemonTypeIcon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun PokemonCard(
    pokemon: PokemonDto,
    onDelete: () -> Unit
) {
    val spriteUrl =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${pokemon.pokedexNumber}.png"

    var displayName by remember { mutableStateOf("Cargando...") }

    // Obtener el nombre correcto desde PokéAPI usando el número de Pokédex
    LaunchedEffect(pokemon.pokedexNumber) {
        displayName = withContext(Dispatchers.IO) {
            try {
                val url = "https://pokeapi.co/api/v2/pokemon/${pokemon.pokedexNumber}"
                val json = URL(url).readText()
                // Extraer el nombre del JSON (formato: "name":"slowpoke")
                val nameMatch = """"name":"([a-z-]+)"""".toRegex().find(json)
                nameMatch?.groupValues?.get(1)?.replaceFirstChar { it.uppercase() } ?: "Pokémon"
            } catch (e: Exception) {
                "Pokémon"
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // SPRITE
            AsyncImage(
                model = spriteUrl,
                contentDescription = displayName,
                modifier = Modifier
                    .size(70.dp)
                    .weight(1f, fill = false)
            )

            // NOMBRE Y NÚMERO
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Nº ${String.format("%03d", pokemon.pokedexNumber)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            }

            // TIPOS - Ahora más grandes
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .height(32.dp)
            ) {
                PokemonTypeIcon(pokemon.primaryType)

                pokemon.secondaryType?.let {
                    PokemonTypeIcon(it)
                }
            }

            // BOTÓN ELIMINAR
            TextButton(
                onClick = onDelete,
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.height(28.dp)
            ) {
                Text(
                    text = "Eliminar",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}