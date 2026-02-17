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
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pokemonId) {
        try {
            pokemon = repository.getPokemonById(pokemonId)
        } catch (e: Exception) {
            error = "Error cargando Pokémon"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle Pokémon") }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {

            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                error != null -> {
                    Text(error!!)
                }

                pokemon != null -> {

                    var item by remember { mutableStateOf(pokemon!!.item ?: "") }
                    var ability by remember { mutableStateOf(pokemon!!.ability ?: "") }

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

                        OutlinedTextField(
                            value = item,
                            onValueChange = { item = it },
                            label = { Text("Item") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = ability,
                            onValueChange = { ability = it },
                            label = { Text("Ability") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("IVs", style = MaterialTheme.typography.titleMedium)

                        @Composable
                        fun numberField(value: String, onChange: (String) -> Unit, label: String) {
                            OutlinedTextField(
                                value = value,
                                onValueChange = { onChange(it) },
                                label = { Text(label) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )
                        }

                        numberField(hpIv, { hpIv = it }, "HP IV")
                        numberField(atkIv, { atkIv = it }, "Atk IV")
                        numberField(defIv, { defIv = it }, "Def IV")
                        numberField(spAtkIv, { spAtkIv = it }, "SpAtk IV")
                        numberField(spDefIv, { spDefIv = it }, "SpDef IV")
                        numberField(speedIv, { speedIv = it }, "Speed IV")

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("EVs", style = MaterialTheme.typography.titleMedium)

                        numberField(hpEv, { hpEv = it }, "HP EV")
                        numberField(atkEv, { atkEv = it }, "Atk EV")
                        numberField(defEv, { defEv = it }, "Def EV")
                        numberField(spAtkEv, { spAtkEv = it }, "SpAtk EV")
                        numberField(spDefEv, { spDefEv = it }, "SpDef EV")
                        numberField(speedEv, { speedEv = it }, "Speed EV")

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        isLoading = true

                                        val hpEvInt = hpEv.toIntOrNull() ?: 0
                                        val atkEvInt = atkEv.toIntOrNull() ?: 0
                                        val defEvInt = defEv.toIntOrNull() ?: 0
                                        val spAtkEvInt = spAtkEv.toIntOrNull() ?: 0
                                        val spDefEvInt = spDefEv.toIntOrNull() ?: 0
                                        val speedEvInt = speedEv.toIntOrNull() ?: 0

                                        val totalEv = hpEvInt + atkEvInt + defEvInt +
                                                spAtkEvInt + spDefEvInt + speedEvInt

                                        // ---- VALIDACIONES ----

                                        if (listOf(
                                                hpEvInt, atkEvInt, defEvInt,
                                                spAtkEvInt, spDefEvInt, speedEvInt
                                            ).any { it > 252 || it < 0 }
                                        ) {
                                            error = "Cada EV debe estar entre 0 y 252"
                                            isLoading = false
                                            return@launch
                                        }

                                        if (totalEv > 510) {
                                            error = "El total de EV no puede superar 510"
                                            isLoading = false
                                            return@launch
                                        }

                                        val hpIvInt = hpIv.toIntOrNull() ?: 31
                                        val atkIvInt = atkIv.toIntOrNull() ?: 31
                                        val defIvInt = defIv.toIntOrNull() ?: 31
                                        val spAtkIvInt = spAtkIv.toIntOrNull() ?: 31
                                        val spDefIvInt = spDefIv.toIntOrNull() ?: 31
                                        val speedIvInt = speedIv.toIntOrNull() ?: 31

                                        if (listOf(
                                                hpIvInt, atkIvInt, defIvInt,
                                                spAtkIvInt, spDefIvInt, speedIvInt
                                            ).any { it > 31 || it < 0 }
                                        ) {
                                            error = "Cada IV debe estar entre 0 y 31"
                                            isLoading = false
                                            return@launch
                                        }

                                        val updatedPokemon = pokemon!!.copy(
                                            item = item.ifBlank { null },
                                            ability = ability.ifBlank { null },

                                            hpIv = hpIvInt,
                                            atkIv = atkIvInt,
                                            defIv = defIvInt,
                                            spAtkIv = spAtkIvInt,
                                            spDefIv = spDefIvInt,
                                            speedIv = speedIvInt,

                                            hpEv = hpEvInt,
                                            atkEv = atkEvInt,
                                            defEv = defEvInt,
                                            spAtkEv = spAtkEvInt,
                                            spDefEv = spDefEvInt,
                                            speedEv = speedEvInt
                                        )

                                        pokemon = repository.updatePokemon(updatedPokemon)

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
                }
            }
        }
    }
}
