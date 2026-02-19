package com.example.pokemonapp.presentation.pokemon

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pokemonapp.model.PokemonDto
import com.example.pokemonapp.data.repository.PokemonRepository
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    pokemonId: Long,
    onPokemonUpdated: () -> Unit
) {




    val repository = remember { PokemonRepository() }
    val scope = rememberCoroutineScope()

    var pokemon by remember { mutableStateOf<PokemonDto?>(null) }

    var abilityOptions by remember { mutableStateOf<List<String>>(emptyList()) }
    var itemOptions by remember { mutableStateOf<List<String>>(emptyList()) }
    var moveOptions by remember { mutableStateOf<List<String>>(emptyList()) }

    var abilityExpanded by remember { mutableStateOf(false) }
    var itemExpanded by remember { mutableStateOf(false) }

    var showMoveDialog1 by remember { mutableStateOf(false) }
    var showMoveDialog2 by remember { mutableStateOf(false) }
    var showMoveDialog3 by remember { mutableStateOf(false) }
    var showMoveDialog4 by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pokemonId) {
        try {
            pokemon = repository.getPokemonById(pokemonId)
            abilityOptions = repository.getPokemonAbilities(pokemonId)
            itemOptions = repository.getCompetitiveItems()
            moveOptions = repository.getPokemonMoves(pokemonId)
        } catch (e: Exception) {
            error = "Error cargando Pokémon"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle Pokémon") }) }
    ) { padding ->
        val backgroundBrush = if (pokemon != null) {
            val primaryColor = typeColor(pokemon!!.primaryType)

            if (pokemon!!.secondaryType != null) {
                val secondaryColor = typeColor(pokemon!!.secondaryType!!)
                Brush.verticalGradient(
                    colors = listOf(primaryColor, secondaryColor)
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(primaryColor, primaryColor.copy(alpha = 0.7f))
                )
            }
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.background,
                    MaterialTheme.colorScheme.background
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(padding),

            contentAlignment = Alignment.Center
        ) {

            when {
                isLoading -> CircularProgressIndicator()
                error != null -> Text(error!!)
                pokemon != null -> {

                    val primaryColor = typeColor(pokemon!!.primaryType)


                    var item by remember { mutableStateOf(pokemon!!.item ?: "") }
                    var ability by remember { mutableStateOf(pokemon!!.ability ?: "") }

                    var move1 by remember { mutableStateOf(pokemon!!.move1 ?: "") }
                    var move2 by remember { mutableStateOf(pokemon!!.move2 ?: "") }
                    var move3 by remember { mutableStateOf(pokemon!!.move3 ?: "") }
                    var move4 by remember { mutableStateOf(pokemon!!.move4 ?: "") }

                    var hpIv by remember { mutableStateOf(pokemon!!.hpIv?.toString() ?: "31") }
                    var atkIv by remember { mutableStateOf(pokemon!!.atkIv?.toString() ?: "31") }
                    var defIv by remember { mutableStateOf(pokemon!!.defIv?.toString() ?: "31") }
                    var spAtkIv by remember { mutableStateOf(pokemon!!.spAtkIv?.toString() ?: "31") }
                    var spDefIv by remember { mutableStateOf(pokemon!!.spDefIv?.toString() ?: "31") }
                    var speedIv by remember { mutableStateOf(pokemon!!.speedIv?.toString() ?: "31") }

                    var hpEv by remember { mutableStateOf(pokemon!!.hpEv?.toString() ?: "0") }
                    var atkEv by remember { mutableStateOf(pokemon!!.atkEv?.toString() ?: "0") }
                    var defEv by remember { mutableStateOf(pokemon!!.defEv?.toString() ?: "0") }
                    var spAtkEv by remember { mutableStateOf(pokemon!!.spAtkEv?.toString() ?: "0") }
                    var spDefEv by remember { mutableStateOf(pokemon!!.spDefEv?.toString() ?: "0") }
                    var speedEv by remember { mutableStateOf(pokemon!!.speedEv?.toString() ?: "0") }

                    val totalEv = listOf(
                        hpEv.toIntOrNull() ?: 0,
                        atkEv.toIntOrNull() ?: 0,
                        defEv.toIntOrNull() ?: 0,
                        spAtkEv.toIntOrNull() ?: 0,
                        spDefEv.toIntOrNull() ?: 0,
                        speedEv.toIntOrNull() ?: 0
                    ).sum()

                    val isEvValid = totalEv <= 510


                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // -------- IMAGEN --------
                        // -------- IMAGEN --------
                        // -------- HEADER --------
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(600)) +
                                    slideInVertically(
                                        initialOffsetY = { -200 },
                                        animationSpec = tween(600)
                                    )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                // Animación flotante infinita
                                val infiniteTransition = rememberInfiniteTransition(label = "floating")

                                val offsetY by infiniteTransition.animateFloat(
                                    initialValue = -6f,
                                    targetValue = 6f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(2000, easing = EaseInOut),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "offsetY"
                                )

                                Box(
                                    modifier = Modifier
                                        .size(240.dp)
                                        .offset { IntOffset(0, offsetY.roundToInt()) },
                                    contentAlignment = Alignment.Center
                                ) {

                                    // Glow circular detrás
                                    Box(
                                        modifier = Modifier
                                            .size(200.dp)
                                            .background(
                                                brush = Brush.radialGradient(
                                                    colors = listOf(
                                                        primaryColor.copy(alpha = 0.6f),
                                                        primaryColor.copy(alpha = 0.25f),
                                                        Color.Transparent
                                                    )
                                                ),
                                                shape = CircleShape
                                            )
                                    )

                                    // Imagen Pokémon
                                    Box(
                                        modifier = Modifier
                                            .size(200.dp)
                                            .shadow(16.dp, CircleShape)
                                            .clip(CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = pokemon!!.image,
                                            contentDescription = pokemon!!.name,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }



                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = pokemon!!.name.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    PokemonTypeIcon(pokemon!!.primaryType)

                                    pokemon!!.secondaryType?.let {
                                        Spacer(modifier = Modifier.width(12.dp))
                                        PokemonTypeIcon(it)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "ID: ${pokemon!!.id}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }




                        // -------- CONFIGURACIÓN --------
                        Card(shape = RoundedCornerShape(16.dp)) {
                            Column(Modifier.padding(16.dp)) {

                                Text("Configuración", fontWeight = FontWeight.SemiBold)

                                Spacer(Modifier.height(12.dp))

                                // ITEM
                                ExposedDropdownMenuBox(
                                    expanded = itemExpanded,
                                    onExpandedChange = { itemExpanded = !itemExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = item.replaceFirstChar { it.uppercase() },
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Item") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(itemExpanded)
                                        },
                                        modifier = Modifier.menuAnchor().fillMaxWidth()
                                    )

                                    ExposedDropdownMenu(
                                        expanded = itemExpanded,
                                        onDismissRequest = { itemExpanded = false }
                                    ) {
                                        itemOptions.forEach { option ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(option.replaceFirstChar { it.uppercase() })
                                                },
                                                onClick = {
                                                    item = option
                                                    itemExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(8.dp))

                                // ABILITY
                                ExposedDropdownMenuBox(
                                    expanded = abilityExpanded,
                                    onExpandedChange = { abilityExpanded = !abilityExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = ability.replaceFirstChar { it.uppercase() },
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Ability") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(abilityExpanded)
                                        },
                                        modifier = Modifier.menuAnchor().fillMaxWidth()
                                    )

                                    ExposedDropdownMenu(
                                        expanded = abilityExpanded,
                                        onDismissRequest = { abilityExpanded = false }
                                    ) {
                                        abilityOptions.forEach { option ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(option.replaceFirstChar { it.uppercase() })
                                                },
                                                onClick = {
                                                    ability = option
                                                    abilityExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // -------- MOVIMIENTOS --------
                        Card(shape = RoundedCornerShape(16.dp)) {
                            Column(Modifier.padding(16.dp)) {

                                Text("Movimientos", fontWeight = FontWeight.SemiBold)

                                Spacer(Modifier.height(12.dp))

                                @Composable
                                fun moveButton(text: String, onClick: () -> Unit) {
                                    OutlinedButton(
                                        onClick = onClick,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            if (text.isBlank()) "Seleccionar movimiento"
                                            else text.replaceFirstChar { it.uppercase() }
                                        )
                                    }
                                }

                                moveButton(move1) { showMoveDialog1 = true }
                                moveButton(move2) { showMoveDialog2 = true }
                                moveButton(move3) { showMoveDialog3 = true }
                                moveButton(move4) { showMoveDialog4 = true }
                            }
                        }

                        // -------- IVs --------
                        Card(shape = RoundedCornerShape(16.dp)) {
                            Column(Modifier.padding(16.dp)) {
                                Text("IVs (0-31)", fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(12.dp))
                                StatInputGrid(
                                    hpIv, { hpIv = it },
                                    atkIv, { atkIv = it },
                                    defIv, { defIv = it },
                                    spAtkIv, { spAtkIv = it },
                                    spDefIv, { spDefIv = it },
                                    speedIv, { speedIv = it },
                                    maxValue = 31
                                )

                            }
                        }

                        // -------- EVs --------
                        Card(shape = RoundedCornerShape(16.dp)) {
                            Column(Modifier.padding(16.dp)) {

                                Text("EVs (0-252)", fontWeight = FontWeight.SemiBold)

                                Spacer(Modifier.height(8.dp))

                                // Contador dinámico
                                Text(
                                    text = "Total EV: $totalEv / 510",
                                    color = if (isEvValid)
                                        MaterialTheme.colorScheme.onSurface
                                    else
                                        MaterialTheme.colorScheme.error
                                )

                                Spacer(Modifier.height(4.dp))

                                LinearProgressIndicator(
                                    progress = (totalEv / 510f).coerceAtMost(1f),
                                    modifier = Modifier.fillMaxWidth(),
                                    color = if (isEvValid)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.error
                                )

                                Spacer(Modifier.height(16.dp))

                                StatInputGrid(
                                    hpEv, { hpEv = it },
                                    atkEv, { atkEv = it },
                                    defEv, { defEv = it },
                                    spAtkEv, { spAtkEv = it },
                                    spDefEv, { spDefEv = it },
                                    speedEv, { speedEv = it },
                                    maxValue = 252
                                )


                                if (!isEvValid) {
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = "El total de EV no puede superar 510",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }


                        Button(
                            onClick = {
                                scope.launch {
                                    val updated = pokemon!!.copy(
                                        item = item.ifBlank { null },
                                        ability = ability.ifBlank { null },
                                        move1 = move1.ifBlank { null },
                                        move2 = move2.ifBlank { null },
                                        move3 = move3.ifBlank { null },
                                        move4 = move4.ifBlank { null },
                                        hpIv = hpIv.toIntOrNull() ?: 31,
                                        atkIv = atkIv.toIntOrNull() ?: 31,
                                        defIv = defIv.toIntOrNull() ?: 31,
                                        spAtkIv = spAtkIv.toIntOrNull() ?: 31,
                                        spDefIv = spDefIv.toIntOrNull() ?: 31,
                                        speedIv = speedIv.toIntOrNull() ?: 31,
                                        hpEv = hpEv.toIntOrNull() ?: 0,
                                        atkEv = atkEv.toIntOrNull() ?: 0,
                                        defEv = defEv.toIntOrNull() ?: 0,
                                        spAtkEv = spAtkEv.toIntOrNull() ?: 0,
                                        spDefEv = spDefEv.toIntOrNull() ?: 0,
                                        speedEv = speedEv.toIntOrNull() ?: 0
                                    )

                                    repository.updatePokemon(updated)

                                    onPokemonUpdated()

                                }
                            },
                            enabled = isEvValid,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Guardar cambios")
                        }

                    }

                    if (showMoveDialog1)
                        MoveSelectionDialog(moveOptions, { showMoveDialog1 = false }) { move1 = it }
                    if (showMoveDialog2)
                        MoveSelectionDialog(moveOptions, { showMoveDialog2 = false }) { move2 = it }
                    if (showMoveDialog3)
                        MoveSelectionDialog(moveOptions, { showMoveDialog3 = false }) { move3 = it }
                    if (showMoveDialog4)
                        MoveSelectionDialog(moveOptions, { showMoveDialog4 = false }) { move4 = it }
                }
            }
        }
    }
}

@Composable
fun StatInputGrid(
    hp: String, onHpChange: (String) -> Unit,
    atk: String, onAtkChange: (String) -> Unit,
    def: String, onDefChange: (String) -> Unit,
    spAtk: String, onSpAtkChange: (String) -> Unit,
    spDef: String, onSpDefChange: (String) -> Unit,
    speed: String, onSpeedChange: (String) -> Unit,
    maxValue: Int
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatField("HP", hp, onHpChange, maxValue, Modifier.weight(1f))
            StatField("Atk", atk, onAtkChange, maxValue, Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatField("Def", def, onDefChange, maxValue, Modifier.weight(1f))
            StatField("SpA", spAtk, onSpAtkChange, maxValue, Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatField("SpD", spDef, onSpDefChange, maxValue, Modifier.weight(1f))
            StatField("Spe", speed, onSpeedChange, maxValue, Modifier.weight(1f))
        }
    }
}



@Composable
fun StatField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    maxValue: Int,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.all { c -> c.isDigit() }) {
                val intValue = it.toIntOrNull() ?: 0
                if (intValue <= maxValue) {
                    onValueChange(it)
                }
            }
        },
        label = { Text(label) },
        singleLine = true,
        modifier = modifier
    )
}



fun typeColor(type: String): Color {
    return when (type.lowercase()) {
        "fire" -> Color(0xFFFF7043)
        "water" -> Color(0xFF42A5F5)
        "grass" -> Color(0xFF66BB6A)
        "electric" -> Color(0xFFFFEE58)
        "psychic" -> Color(0xFFEC407A)
        "ice" -> Color(0xFF81D4FA)
        "dragon" -> Color(0xFF7E57C2)
        "dark" -> Color(0xFF616161)
        "fairy" -> Color(0xFFF48FB1)
        "fighting" -> Color(0xFF8D6E63)
        "poison" -> Color(0xFFAB47BC)
        "ground" -> Color(0xFFBCAAA4)
        "flying" -> Color(0xFF90CAF9)
        "bug" -> Color(0xFF9CCC65)
        "rock" -> Color(0xFFA1887F)
        "ghost" -> Color(0xFF7986CB)
        "steel" -> Color(0xFFB0BEC5)
        else -> Color(0xFFBDBDBD)
    }
}




