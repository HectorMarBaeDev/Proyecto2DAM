package com.example.pokemonapp.ui.teams

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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pokemonapp.data.auth.AuthManager
import com.example.pokemonapp.data.model.TeamDto
import com.example.pokemonapp.data.repository.PokemonRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(
    repository: PokemonRepository,
    onTeamClick: (Long) -> Unit
) {
    var teams by remember { mutableStateOf<List<TeamDto>>(emptyList()) }
    var newTeamName by remember { mutableStateOf("") }
    var newTeamFormat by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var imageVersion by remember { mutableLongStateOf(0L) }

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

                    // 🔥 Forzar recarga inmediata
                    imageVersion = System.currentTimeMillis()

                } catch (e: Exception) {
                    Log.e("Upload", "Error subiendo imagen", e)
                }
            }
        }
    }

    // Cargar equipos al entrar
    LaunchedEffect(Unit) {
        teams = try {
            repository.getTeamsByUser()
        } catch (e: Exception) {
            errorMessage = "Error cargando equipos"
            emptyList()
        }

        // 🔥 Inicializar versión para que cargue imagen
        imageVersion = System.currentTimeMillis()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Equipos", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        imagePickerLauncher.launch("image/*")
                    }) {

                        if (userId != null) {

                            val imageUrl =
                                "${RetrofitInstance.BASE_URL}users/$userId/profile-picture?v=$imageVersion"

                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                            )

                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Subir foto de perfil"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ---------- CREAR EQUIPO ----------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        "Crear Nuevo Equipo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

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
                                } catch (e: Exception) {
                                    errorMessage = "Error creando equipo"
                                }
                            }
                        },
                        enabled = newTeamName.isNotBlank() && newTeamFormat.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Crear equipo")
                    }
                }
            }

            // ---------- ERROR ----------
            errorMessage?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // ---------- LISTA ----------
            Text(
                "Equipos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (teams.isEmpty()) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay equipos aún")
                    }
                }

            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(teams) { team ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTeamClick(team.id) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = team.name,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(text = team.format)
                                }
                                Text("→", fontSize = 24.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}