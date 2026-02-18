package com.example.pokemonapp.ui.teams

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokemonapp.data.model.PokemonDto
import com.example.pokemonapp.data.model.PokemonListItemDto
import com.example.pokemonapp.data.repository.PokemonRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(
    teamId: Long,
    onPokemonClick: (Long) -> Unit
) {

    val repository = remember { PokemonRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    var pokemonList by remember { mutableStateOf<List<PokemonDto>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Función para copiar al portapapeles
    fun handleExportTeam(pokemons: List<PokemonDto>) {
        val texto = pokemons.joinToString("\n\n") { pokemon ->
            // Nombre + item (solo si existe)
            val itemText = if (!pokemon.item.isNullOrEmpty()) " @ ${pokemon.item}" else ""
            val nameLine = "${pokemon.name}$itemText"

            // Ability (si existe)
            val abilityLine = if (!pokemon.ability.isNullOrEmpty()) "Ability: ${pokemon.ability}" else null

            // Tera Type → solo si lo añades a tu DTO; por ahora puedes usar null
            val teraLine = "Tera Type: Normal"

            // EVs → solo si al menos uno está configurado
            val evsList = listOfNotNull(
                pokemon.hpEv?.takeIf { it > 0 }?.let { "$it HP" },
                pokemon.atkEv?.takeIf { it > 0 }?.let { "$it Atk" },
                pokemon.defEv?.takeIf { it > 0 }?.let { "$it Def" },
                pokemon.spAtkEv?.takeIf { it > 0 }?.let { "$it SpA" },
                pokemon.spDefEv?.takeIf { it > 0 }?.let { "$it SpD" },
                pokemon.speedEv?.takeIf { it > 0 }?.let { "$it Spe" }
            )
            val evsLine = if (evsList.isNotEmpty()) "EVs: ${evsList.joinToString(" / ")}" else null

            // Movimientos → solo los no nulos
            val moves = listOfNotNull(pokemon.move1, pokemon.move2, pokemon.move3, pokemon.move4)
                .takeIf { it.isNotEmpty() }
                ?.joinToString("\n- ", prefix = "- ")

            // Construir bloque de Pokémon
            listOfNotNull(nameLine, abilityLine, teraLine, evsLine, moves)
                .joinToString("\n")
        }

        clipboardManager.setText(AnnotatedString(texto))
        Toast.makeText(context, "Equipo copiado al portapapeles", Toast.LENGTH_SHORT).show()
    }



    LaunchedEffect(Unit) {
        pokemonList = try {
            repository.getPokemonByTeam(teamId)
        } catch (e: Exception) {
            Log.e("TEAM_DETAIL", "Error loading pokemon: ${e.message}")
            emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mi Equipo Pokémon",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                actions = {
                    FilledTonalButton(
                        onClick = { showDialog = true },
                        modifier = Modifier.padding(end = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = pokemonList.size < 6
                    ) {
                        Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(4.dp))
                        Text("Añadir")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp) // deja espacio para el botón
            ) {

                // Contador
                if (pokemonList.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                if (pokemonList.size >= 6)
                                    MaterialTheme.colorScheme.errorContainer
                                else
                                    MaterialTheme.colorScheme.secondaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Pokémon en el equipo:",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "${pokemonList.size}/6",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    }
                }

                // Lista
                if (pokemonList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "No hay Pokémon en este equipo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Toca el botón + para añadir uno",
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(pokemonList) { pokemon ->
                            PokemonCard(
                                pokemon = pokemon,
                                onClick = { onPokemonClick(pokemon.id) },
                                onDelete = {
                                    scope.launch {
                                        try {
                                            isLoading = true
                                            repository.deletePokemon(pokemon.id)
                                            pokemonList = repository.getPokemonByTeam(teamId)
                                        } catch (e: Exception) {
                                            errorMessage = "Error al eliminar el Pokémon"
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }

            // Botón fijo abajo
            Button(
                onClick = { handleExportTeam(pokemonList) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Exportar equipo")
            }
        }
    }

    // NUEVO DIALOGO DE SELECCIÓN
    if (showDialog) {
        SelectPokemonDialog(
            repository = repository,
            onDismiss = { showDialog = false },
            onPokemonSelected = { pokemon: PokemonListItemDto ->

                scope.launch {
                    try {
                        isLoading = true

                        if (pokemonList.size >= 6) {
                            errorMessage = "Ya tienes 6 Pokémon en el equipo"
                            showDialog = false
                            return@launch
                        }

                        repository.addPokemon(
                            teamId,
                            pokemon.pokedexNumber.toString()
                        )

                        pokemonList = repository.getPokemonByTeam(teamId)
                        showDialog = false

                    } catch (e: Exception) {
                        errorMessage = "Error al añadir el Pokémon"
                        showDialog = false
                    } finally {
                        isLoading = false
                    }
                }
            }
        )
    }
}
