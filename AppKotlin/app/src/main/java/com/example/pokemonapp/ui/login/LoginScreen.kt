package com.example.pokemonapp.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
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
            TopAppBar(title = {
                Text("Selecciona usuario")
            })
        }
    ) {
        padding -> Column()
    }
}