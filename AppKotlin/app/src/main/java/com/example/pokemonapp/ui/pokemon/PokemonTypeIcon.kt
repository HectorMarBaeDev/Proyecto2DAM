package com.example.pokemonapp.ui.pokemon

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest

@Composable
fun PokemonTypeIcon(
    type: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val iconUrl =
        "https://raw.githubusercontent.com/duiker101/pokemon-type-svg-icons/master/icons/${type.lowercase()}.svg"

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(iconUrl)
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        contentDescription = type,
        modifier = modifier.size(24.dp)
    )
}
