package com.example.pokemonapp.presentation.teams

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pokemonapp.data.auth.AuthManager
import com.example.pokemonapp.data.repository.PokemonRepository
import com.example.pokemonapp.model.TeamDto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(
    repository: PokemonRepository,
    onTeamClick: (Long) -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }

    var showCreateSection by remember { mutableStateOf(false) }



    var teams by remember { mutableStateOf<List<TeamDto>>(emptyList()) }
    var newTeamName by remember { mutableStateOf("") }
    var newTeamFormat by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var imageVersion by remember { mutableLongStateOf(0L) }
    var teamToDelete by remember { mutableStateOf<TeamDto?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val userId = AuthManager.userId

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                try {
                    repository.uploadProfilePicture(it, context)
                    imageVersion = System.currentTimeMillis()
                } catch (e: Exception) {
                    Log.e("Upload", "Error subiendo imagen", e)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        teams = try {
            repository.getTeamsByUser()
        } catch (e: Exception) {
            errorMessage = "Error cargando equipos"
            emptyList()
        }
        imageVersion = System.currentTimeMillis()
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
                            "Mis Equipos",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${teams.size} equipos",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = { showCreateSection = !showCreateSection }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir equipo")
                    }

                    IconButton(onClick = {
                        imagePickerLauncher.launch("image/*")
                    }) {
                        if (userId != null) {
                            val imageUrl =
                                "${RetrofitInstance.BASE_URL}users/$userId/profile-picture?v=$imageVersion"

                            AsyncImage(
                                model = imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Icon(Icons.Default.AccountCircle, null)
                        }
                    }
                }
            )

        }
    )
 { padding ->

        Column(
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ---------- CREAR EQUIPO ----------

            AnimatedVisibility(
                visible = showCreateSection
            ) {

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                )
                {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Nuevo Equipo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        OutlinedTextField(
                            value = newTeamName,
                            onValueChange = { newTeamName = it },
                            label = { Text("Nombre del equipo") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newTeamFormat,
                            onValueChange = { newTeamFormat = it },
                            label = { Text("Formato") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        val createdTeam = repository.createTeam(
                                            name = newTeamName,
                                            format = newTeamFormat
                                        )

                                        teams = repository.getTeamsByUser()
                                        onTeamClick(createdTeam.id)

                                        newTeamName = ""
                                        newTeamFormat = ""

                                        showCreateSection = false // 🔥 se cierra al crear

                                    } catch (e: Exception) {
                                        errorMessage = "Error creando equipo"
                                    }
                                }
                            },
                            enabled = newTeamName.isNotBlank() &&
                                    newTeamFormat.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Crear equipo")
                        }
                    }
                }
            }


            // ---------- ERROR ----------

            errorMessage?.let {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // ---------- LISTA ----------
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Text(
                "Tus Equipos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (teams.isEmpty()) {

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CatchingPokemon,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No tienes equipos todavía",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Pulsa + para crear tu primer equipo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

            } else {


                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    items(teams) { team ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(2.dp)
                        )
                        {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            onTeamClick(team.id)
                                        }
                                ) {
                                    Text(
                                        text = team.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = team.format,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row {
                                    IconButton(
                                        onClick = {
                                            teamToDelete = team
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }

                                }

                                }
                            }
                        }
                    }
                }
            }
        }

        // ---------- DIALOGO CONFIRMACION ----------

        teamToDelete?.let { team ->

            AlertDialog(
                onDismissRequest = { teamToDelete = null },
                title = { Text("Eliminar equipo") },
                text = {
                    Text(
                        "¿Seguro que quieres eliminar '${team.name}'? Esta acción no se puede deshacer."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                try {
                                    repository.deleteTeam(team.id)
                                    teams = repository.getTeamsByUser()
                                } catch (e: Exception) {
                                    errorMessage = "Error eliminando equipo"
                                } finally {
                                    teamToDelete = null
                                }
                            }
                        }
                    ) {
                        Text(
                            "Eliminar",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { teamToDelete = null }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    teamToDelete?.let { team ->

        AlertDialog(
            onDismissRequest = { teamToDelete = null },
            title = {
                Text("Eliminar equipo")
            },
            text = {
                Text(
                    "¿Seguro que quieres eliminar '${team.name}'? Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {

                        val removedTeam = team
                        teamToDelete = null   // 🔥 cerrar diálogo inmediatamente

                        scope.launch {

                            teams = teams.filter { it.id != removedTeam.id }

                            val result = snackbarHostState.showSnackbar(
                                message = "Equipo eliminado",
                                actionLabel = "Deshacer",
                                duration = SnackbarDuration.Short
                            )

                            if (result == SnackbarResult.ActionPerformed) {
                                teams = teams + removedTeam
                            } else {
                                try {
                                    repository.deleteTeam(removedTeam.id)
                                } catch (e: Exception) {
                                    errorMessage = "Error eliminando equipo"
                                    teams = teams + removedTeam
                                }
                            }
                        }
                    }

                ) {
                    Text(
                        "Eliminar",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { teamToDelete = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

}

