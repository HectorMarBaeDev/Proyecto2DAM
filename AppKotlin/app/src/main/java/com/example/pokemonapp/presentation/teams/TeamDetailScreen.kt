package com.example.pokemonapp.presentation.teams

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokemonapp.model.PokemonDto
import com.example.pokemonapp.model.PokemonListItemDto
import com.example.pokemonapp.data.repository.PokemonRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(
    teamId: Long,
    onPokemonClick: (Long) -> Unit

) {
    val snackbarHostState = remember { SnackbarHostState() }

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

            val itemText =
                if (!pokemon.item.isNullOrEmpty())
                    " @ ${pokemon.item}"
                else ""

            val nameLine = "${pokemon.name}$itemText"

            val abilityLine =
                if (!pokemon.ability.isNullOrEmpty())
                    "Ability: ${pokemon.ability}"
                else null

            val teraLine = "Tera Type: Normal"

            val evsList = listOfNotNull(
                pokemon.hpEv?.takeIf { it > 0 }?.let { "$it HP" },
                pokemon.atkEv?.takeIf { it > 0 }?.let { "$it Atk" },
                pokemon.defEv?.takeIf { it > 0 }?.let { "$it Def" },
                pokemon.spAtkEv?.takeIf { it > 0 }?.let { "$it SpA" },
                pokemon.spDefEv?.takeIf { it > 0 }?.let { "$it SpD" },
                pokemon.speedEv?.takeIf { it > 0 }?.let { "$it Spe" }
            )

            val evsLine =
                if (evsList.isNotEmpty())
                    "EVs: ${evsList.joinToString(" / ")}"
                else null

            val moves = listOfNotNull(
                pokemon.move1,
                pokemon.move2,
                pokemon.move3,
                pokemon.move4
            )
                .takeIf { it.isNotEmpty() }
                ?.joinToString("\n- ", prefix = "- ")

            listOfNotNull(
                nameLine,
                abilityLine,
                teraLine,
                evsLine,
                moves
            ).joinToString("\n")
        }

        clipboardManager.setText(AnnotatedString(texto))

        // 🔥 Snackbar en vez de Toast
        scope.launch {
            snackbarHostState.showSnackbar(
                message = "Equipo copiado al portapapeles"
            )
        }
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {

                        Text(
                            "Mi Equipo",
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                "${pokemonList.size}/6 Pokémon",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            if (pokemonList.size >= 6) {

                                Spacer(Modifier.width(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = MaterialTheme.colorScheme.error,
                                    tonalElevation = 2.dp
                                ) {
                                    Text(
                                        "COMPLETO",
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 2.dp
                                        ),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onError,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    FilledTonalIconButton(
                        onClick = { showDialog = true },
                        enabled = pokemonList.size < 6
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir")
                    }
                }
            )

        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
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
                // Lista
                if (pokemonList.isEmpty()) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.CatchingPokemon,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    "Tu equipo está vacío",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "Pulsa + para empezar a construirlo",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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
            ExtendedFloatingActionButton(
                onClick = { handleExportTeam(pokemonList) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                icon = {
                    Icon(Icons.Default.Share, contentDescription = null)
                },
                text = {
                    Text("Exportar")
                }
            )

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
