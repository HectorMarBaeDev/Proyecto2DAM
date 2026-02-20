package com.example.pokemonapp.model

data class PokemonDto(
    val id: Long,
    val pokedexNumber: Int,
    val name: String,
    val image: String,
    val primaryType: String,
    val secondaryType: String?,


val item: String?,
val ability: String?,

val move1: String?,
val move2: String?,
val move3: String?,
val move4: String?,

val hpIv: Int?,
val atkIv: Int?,
val defIv: Int?,
val spAtkIv: Int?,
val spDefIv: Int?,
val speedIv: Int?,

val hpEv: Int?,
val atkEv: Int?,
val defEv: Int?,
val spAtkEv: Int?,
val spDefEv: Int?,
val speedEv: Int?,

    val teraType: String?,

    val cry: String?,

)