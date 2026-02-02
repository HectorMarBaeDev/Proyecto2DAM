package com.example.pokemonapp.ui.pokemon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pokemonapp.R

@Composable
fun PokemonTypeIcon(
    type: String,
    modifier: Modifier = Modifier
) {
    val iconRes = when (type.lowercase()) {
        "fire" -> R.drawable.type_fire
        "water" -> R.drawable.type_water
        "grass" -> R.drawable.type_grass
        "electric" -> R.drawable.type_electric
        "psychic" -> R.drawable.type_psychic
        "fairy" -> R.drawable.type_fairy
        "dark" -> R.drawable.type_dark
        "ghost" -> R.drawable.type_ghost
        "dragon" -> R.drawable.type_dragon
        "ice" -> R.drawable.type_ice
        "steel" -> R.drawable.type_steel
        "rock" -> R.drawable.type_rock
        "ground" -> R.drawable.type_ground
        "fighting" -> R.drawable.type_fighting
        "poison" -> R.drawable.type_poison
        "bug" -> R.drawable.type_bug
        "flying" -> R.drawable.type_flying
        "normal" -> R.drawable.type_normal
        else -> R.drawable.type_normal
    }

    Image(
        painter = painterResource(id = iconRes),
        contentDescription = type,
        modifier = modifier.size(32.dp)
    )
}
