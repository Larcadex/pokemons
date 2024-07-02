package com.example.pokemon.model

import com.google.gson.annotations.SerializedName
import org.intellij.lang.annotations.Language

data class Pokemon(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("sprites") val sprites: Sprites?,
    @SerializedName("height") val height: Int,
    @SerializedName("weight") val weight: Int,
    @SerializedName("abilities") val abilities: List<Ability>,
    @SerializedName("types") val types: List<Type>,
    @SerializedName("stats") val stats: List<Stat>,
    @SerializedName("description") val description: String
)

data class Ability(
    @SerializedName("ability") val ability: AbilityDetails
)

data class AbilityDetails(
    @SerializedName("name") val name: String
)

data class Type(
    @SerializedName("type") val type: TypeDetails
)

data class TypeDetails(
    @SerializedName("name") val name: String
)

data class Stat(
    @SerializedName("stat") val stat: StatDetails,
    @SerializedName("base_stat") val base_stat: Int
)

data class StatDetails(
    @SerializedName("name") val name: String
)

data class Sprites(
    @SerializedName("other") val other: OtherSprites?
)

data class OtherSprites(
    @SerializedName("official-artwork") val officialArtwork: OfficialArtwork?
)

data class OfficialArtwork(
    @SerializedName("front_default") val front_default: String?
)

data class PokemonSpecies(
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>
)

data class FlavorTextEntry(
    @SerializedName("flavor_text") val flavorText: String,
    @SerializedName("language") val language: com.example.pokemon.model.Language
)

data class Language(
    @SerializedName("name") val name: String
)
