package com.example.pokemonapp.presentation.teams

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
                    Text("Mis Equipos", fontWeight = FontWeight.Bold)
                },
                actions = {
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
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null
                            )
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

            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
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
                        leadingIcon = {
                            Icon(Icons.Default.Group, null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = newTeamFormat,
                        onValueChange = { newTeamFormat = it },
                        label = { Text("Formato (OU, VGC, etc)") },
                        leadingIcon = {
                            Icon(Icons.Default.Settings, null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
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
                                } catch (e: Exception) {
                                    errorMessage = "Error creando equipo"
                                }
                            }
                        },
                        enabled = newTeamName.isNotBlank() &&
                                newTeamFormat.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Crear equipo")
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

            Text(
                "Tus Equipos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (teams.isEmpty()) {

                Card(shape = RoundedCornerShape(20.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CatchingPokemon,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text("Aún no tienes equipos")
                    }
                }

            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    items(teams) { team ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
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

                                            scope.launch {

                                                val removedTeam = team
                                                teams = teams.filter { it.id != removedTeam.id }

                                                val result = snackbarHostState.showSnackbar(
                                                    message = "Equipo eliminado",
                                                    actionLabel = "Deshacer",
                                                    duration = SnackbarDuration.Short
                                                )

                                                if (result == SnackbarResult.ActionPerformed) {

                                                    // RESTAURAR
                                                    teams = teams + removedTeam

                                                } else {

                                                    // CONFIRMAR EN BACKEND
                                                    try {
                                                        repository.deleteTeam(removedTeam.id)
                                                    } catch (e: Exception) {
                                                        errorMessage = "Error eliminando equipo"
                                                        teams = teams + removedTeam
                                                    }
                                                }
                                            }
                                        }
                                    )
                                    {
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
    }
}
