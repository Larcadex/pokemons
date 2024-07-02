package com.example.pokemon.network

import com.example.pokemon.model.Pokemon
import com.example.pokemon.model.PokemonResponse
import com.example.pokemon.model.PokemonSpecies
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApiService {
    @GET("pokemon/{name}")
    fun getPokemon(@Path("name") name: String): Call<Pokemon>

    @GET("pokemon/{name}")
    fun getPokemonDetail(@Path("name") name: String): Call<Pokemon>

    @GET("pokemon?limit=30")
    fun getPokemonList(): Call<PokemonResponse>

    @GET("pokemon-species/{id}")
    fun getPokemonSpecies(@Path("id") id: Int): Call<PokemonSpecies>

}

