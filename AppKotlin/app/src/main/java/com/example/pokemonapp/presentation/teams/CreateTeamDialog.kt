package com.example.pokemonapp.presentation.teams

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateTeamDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {

    var name by remember { mutableStateOf("") }
    var format by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo equipo") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del equipo") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = format,
                    onValueChange = { format = it },
                    label = { Text("Formato (OU, UU, etc.)") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && format.isNotBlank()) {
                        onCreate(name, format)
                    }
                }
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}