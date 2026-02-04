package com.example.pokemonapp.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.pokemonapp.data.repository.PokemonRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (Long) -> Unit
) {
    val repository = remember { PokemonRepository() }
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Login") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        error = null
                        try {
                            val user = repository.login(username, password)
                            onLoginSuccess(user.id)
                        } catch (e: Exception) {
                            error = when (e) {
                                is retrofit2.HttpException -> {
                                    when (e.code()) {
                                        401 -> "Credenciales incorrectas (401)"
                                        403 -> "Acceso prohibido (403)"
                                        404 -> "Endpoint no encontrado (404)"
                                        else -> "Error HTTP ${e.code()}"
                                    }
                                }
                                is java.net.UnknownHostException ->
                                    "No se puede conectar con el servidor"

                                is java.net.SocketTimeoutException ->
                                    "Timeout de conexión"

                                else -> {
                                    e.printStackTrace()
                                    "Error inesperado: ${e.message}"
                                }
                            }
                        }
                        finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Entrando..." else "Entrar")
            }

            error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
