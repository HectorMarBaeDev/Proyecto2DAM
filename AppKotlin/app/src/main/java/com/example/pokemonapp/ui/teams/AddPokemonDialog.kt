package com.example.pokemonapp.ui.teams

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddPokemonDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {

    var identifier by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Pokémon") },
        text = {
            OutlinedTextField(
                value = identifier,
                onValueChange = { identifier = it },
                label = { Text("Nombre o ID del Pokémon") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (identifier.isNotBlank()) {
                        onAdd(identifier)
                    }
                }
            ) {
                Text("Añadir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
