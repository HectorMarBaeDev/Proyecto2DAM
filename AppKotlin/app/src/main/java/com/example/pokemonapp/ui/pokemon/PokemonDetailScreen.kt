package com.example.pokemonapp.ui.pokemon

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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

    // -------- CARGA INICIAL --------
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {

            when {
                isLoading -> CircularProgressIndicator()
                error != null -> Text(error!!)
                pokemon != null -> {

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

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = pokemon!!.name.replaceFirstChar { it.uppercase() },
                            fontSize = 22.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

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

                        Spacer(modifier = Modifier.height(8.dp))

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

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Movimientos", style = MaterialTheme.typography.titleMedium)

                        @Composable
                        fun moveButton(text: String, onClick: () -> Unit) {
                            Button(
                                onClick = onClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
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

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        isLoading = true

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

                                        pokemon = repository.updatePokemon(updated)
                                        error = null

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
