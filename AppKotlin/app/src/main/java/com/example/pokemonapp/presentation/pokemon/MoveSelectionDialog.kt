package com.example.pokemonapp.presentation.pokemon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokemonapp.model.MoveDto

@Composable
fun MoveSelectionDialog(
    moves: List<MoveDto>,
    onDismiss: () -> Unit,
    onMoveSelected: (MoveDto) -> Unit
)
 {

    var search by remember { mutableStateOf("") }

     val filteredMoves = remember(search, moves) {
         moves.filter {
             it.name.contains(search, ignoreCase = true)
         }
     }


     AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        text = {
            Column {

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Buscar movimiento") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                ) {
                    items(filteredMoves) { move ->

                        TextButton(
                            onClick = {
                                onMoveSelected(move)
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(modifier = Modifier.fillMaxWidth()) {

                                // Nombre
                                Text(
                                    text = move.name.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {

                                    // Icono tipo
                                    PokemonTypeIcon(
                                        type = move.type,
                                        modifier = Modifier.size(32.dp)
                                    )

                                    // Categoría
                                    Text(
                                        text = move.category.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    // Potencia
                                    Text(
                                        text = "Power: ${move.power ?: "--"}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    )
}