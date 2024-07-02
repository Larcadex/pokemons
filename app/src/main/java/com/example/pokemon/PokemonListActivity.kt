package com.example.pokemon

import PokemonAdapter
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokemon.model.Pokemon
import com.example.pokemon.model.PokemonResponse
import com.example.pokemon.network.PokeApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PokemonListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PokemonAdapter
    private lateinit var retrofit: Retrofit
    private lateinit var service: PokeApiService
    private lateinit var searchEditText: EditText
    private lateinit var sortSpinner: Spinner
    private var pokemonList = mutableListOf<Pokemon>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        adapter = PokemonAdapter { pokemon ->
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("pokemon_name", pokemon.name)
            startActivity(intent)
        }


        recyclerView.adapter = adapter

        searchEditText = findViewById(R.id.searchEditText)
        sortSpinner = findViewById(R.id.sortSpinner)

        retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(PokeApiService::class.java)

        setupSearch()
        setupSort()
        fetchPokemonList()
        sortByNumber()

    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                val filteredList = pokemonList.filter { pokemon ->
                    pokemon.name.contains(searchText, ignoreCase = true) ||
                            pokemon.id.toString().contains(searchText)
                }
                adapter.submitList(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupSort() {
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> sortByName()
                    1 -> sortByNumber()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun sortByName() {
        val sortedList = pokemonList.sortedBy { it.name }
        adapter.submitList(sortedList)
    }

    private fun sortByNumber() {
        val sortedList = pokemonList.sortedBy { it.id }
        adapter.submitList(sortedList)
    }

    private fun fetchPokemonList() {
        service.getPokemonList().enqueue(object : Callback<PokemonResponse> {
            override fun onResponse(call: Call<PokemonResponse>, response: Response<PokemonResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { pokemonResponse ->
                        val pokemonNames = pokemonResponse.results.map { it.name }
                        fetchPokemonDetails(pokemonNames)
                    }
                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun fetchPokemonDetails(pokemonNames: List<String>) {
        pokemonNames.forEach { name ->
            service.getPokemon(name).enqueue(object : Callback<Pokemon> {
                override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                    if (response.isSuccessful) {
                        val pokemon = response.body()
                        pokemon?.let {
                            pokemonList.add(it)
                            adapter.submitList(pokemonList)
                        }
                    } else {
                        // Handle error response
                    }
                }

                override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                    // Handle failure
                }
            })
        }
    }
}
