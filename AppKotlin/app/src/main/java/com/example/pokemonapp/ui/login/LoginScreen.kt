package com.example.pokemonapp.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokemonapp.data.model.UserDto
import com.example.pokemonapp.data.repository.PokemonRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen( onUserSelected: (Long) -> Unit ) {
    val repository = remember { PokemonRepository() }
    val scope = rememberCoroutineScope()

    var users by remember {
        mutableStateOf(emptyList<UserDto>())
    }

    LaunchedEffect(Unit) {
        users = repository.getUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Selecciona usuario") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            users.forEach { users ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            onUserSelected(user.id)
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(user.username, style = MaterialTheme.typography.titleMedium)
                        Text(user.email, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}