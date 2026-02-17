package com.example.pokemonapp.ui.teams

import android.util.Log
import com.example.pokemonapp.data.repository.PokemonRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokemonapp.data.model.PokemonDto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(teamId: Long) {
    val repository = remember { PokemonRepository() }
    val scope = rememberCoroutineScope()

    var pokemonList by remember { mutableStateOf<List<PokemonDto>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

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
                        Text(
                            "+",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Añadir")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = remember { SnackbarHostState() }.apply {
                    LaunchedEffect(errorMessage) {
                        errorMessage?.let {
                            showSnackbar(
                                message = it,
                                duration = SnackbarDuration.Short
                            )
                            errorMessage = null
                        }
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Contador de Pokémon
            if (pokemonList.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (pokemonList.size >= 6)
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
                            text = "Pokémon en el equipo:",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${pokemonList.size}/6",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            }

            // Lista de Pokémon o estado vacío
            if (pokemonList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "No hay Pokémon en este equipo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Toca el botón + para añadir uno",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(pokemonList) { pokemon ->
                        PokemonCard(
                            pokemon = pokemon,
                            onDelete = {
                                scope.launch {
                                    try {
                                        isLoading = true
                                        repository.deletePokemon(pokemon.id)
                                        pokemonList = repository.getPokemonByTeam(teamId)
                                    } catch (e: Exception) {
                                        Log.e("TEAM_DETAIL", "Error deleting: ${e.message}")
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

            // Indicador de carga
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showDialog) {
        AddPokemonDialog(
            onDismiss = { showDialog = false },
            onAdd = { identifier ->
                scope.launch {
                    try {
                        isLoading = true

                        // Verificar límite antes de llamar al backend
                        if (pokemonList.size >= 6) {
                            errorMessage = "Ya tienes 6 Pokémon en el equipo (máximo permitido)"
                            showDialog = false
                            return@launch
                        }

                        repository.addPokemon(teamId, identifier)
                        pokemonList = repository.getPokemonByTeam(teamId)
                        showDialog = false

                    } catch (e: retrofit2.HttpException) {
                        // Manejar errores HTTP específicos
                        when (e.code()) {
                            400 -> errorMessage = "Pokémon no encontrado. Verifica el nombre o número"
                            404 -> errorMessage = "Equipo no encontrado"
                            else -> errorMessage = "Error al añadir Pokémon: ${e.message()}"
                        }
                        showDialog = false

                    } catch (e: Exception) {
                        Log.e("TEAM_DETAIL", "Error adding pokemon: ${e.message}")
                        errorMessage = "Error de conexión. Verifica tu internet"
                        showDialog = false

                    } finally {
                        isLoading = false
                    }
                }
            }
        )
    }
}