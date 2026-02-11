import com.example.pokemonapp.data.api.RetrofitInstance
import com.example.pokemonapp.data.auth.AuthManager
import com.example.pokemonapp.data.model.PokemonDto
import com.example.pokemonapp.data.model.TeamDto

class PokemonRepository {

    // ======================
    // AUTH
    // ======================

    suspend fun login(username: String, password: String): Long {
        val api = RetrofitInstance.create(null)

        val response = api.login(
            mapOf(
                "username" to username,
                "password" to password
            )
        )

        AuthManager.jwt = response.token

        return response.userId
    }

    suspend fun register(
        username: String,
        email: String,
        password: String
    ) {
        RetrofitInstance.create(null).register(
            mapOf(
                "username" to username,
                "email" to email,
                "password" to password
            )
        )
    }

    // ======================
    // TEAMS
    // ======================

    suspend fun getTeamsByUser(userId: Long): List<TeamDto> {
        val api = RetrofitInstance.create(AuthManager.jwt)
        return api.getTeamsByUser(userId)
    }

    suspend fun createTeam(
        userId: Long,
        name: String,
        format: String
    ): TeamDto {
        val api = RetrofitInstance.create(AuthManager.jwt)

        return api.createTeam(
            userId,
            mapOf(
                "name" to name,
                "format" to format
            )
        )
    }

    // ======================
    // POKEMON
    // ======================

    suspend fun getPokemonByTeam(teamId: Long): List<PokemonDto> {
        val api = RetrofitInstance.create(AuthManager.jwt)
        return api.getPokemonByTeam(teamId)
    }

    suspend fun addPokemon(
        teamId: Long,
        identifier: String
    ): PokemonDto {
        val api = RetrofitInstance.create(AuthManager.jwt)

        return api.addPokemon(
            teamId,
            mapOf("identifier" to identifier)
        )
    }

    suspend fun deletePokemon(pokemonId: Long) {
        val api = RetrofitInstance.create(AuthManager.jwt)
        api.deletePokemon(pokemonId)
    }
}
