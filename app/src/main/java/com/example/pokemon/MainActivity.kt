package com.example.pokemon

import android.graphics.Color
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.example.pokemon.network.PokeApiService
import com.example.pokemon.model.Pokemon
import com.example.pokemon.network.RetrofitClient.service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var pokemonName: TextView
    private lateinit var pokemonNumber: TextView
    private lateinit var pokemonImage: ImageView
    private lateinit var pokemonDescription: TextView
    private lateinit var pokemonWeight: TextView
    private lateinit var pokemonHeight: TextView
    private lateinit var pokemonMoves: TextView
    private lateinit var hp: TextView
    private lateinit var atk: TextView
    private lateinit var def: TextView
    private lateinit var satk: TextView
    private lateinit var sdef: TextView
    private lateinit var spd: TextView
    private lateinit var titleHP: TextView
    private lateinit var titleATK: TextView
    private lateinit var titleDEF: TextView
    private lateinit var titleSATK: TextView
    private lateinit var titleSDEF: TextView
    private lateinit var titleSPD: TextView
    private lateinit var stats: TextView
    private lateinit var about: TextView
    private lateinit var frame: FrameLayout
    private lateinit var hpProgressBar: ProgressBar
    private lateinit var attackProgressBar: ProgressBar
    private lateinit var defenseProgressBar: ProgressBar
    private lateinit var specialAttackProgressBar: ProgressBar
    private lateinit var specialDefenseProgressBar: ProgressBar
    private lateinit var speedProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация View компонентов
        pokemonName = findViewById(R.id.pokemonName)
        pokemonNumber = findViewById(R.id.pokemonNumber)
        pokemonImage = findViewById(R.id.pokemonImage)
        pokemonDescription = findViewById(R.id.pokemonDescription)
        hp = findViewById(R.id.hp)
        atk = findViewById(R.id.atk)
        def = findViewById(R.id.def)
        satk = findViewById(R.id.satk)
        sdef = findViewById(R.id.sdef)
        spd = findViewById(R.id.spd)
        titleHP = findViewById(R.id.titleHP)
        titleATK = findViewById(R.id.titleATK)
        titleDEF = findViewById(R.id.titleDEF)
        titleSATK = findViewById(R.id.titleSATK)
        titleSDEF = findViewById(R.id.titleSDEF)
        titleSPD = findViewById(R.id.titleSPD)
        pokemonWeight = findViewById(R.id.pokemonWeight)
        pokemonHeight = findViewById(R.id.pokemonHeight)
        pokemonMoves = findViewById(R.id.pokemonMoves)
        stats = findViewById(R.id.stats)
        about = findViewById(R.id.about)
        frame = findViewById(R.id.frame)
        hpProgressBar = findViewById(R.id.hpProgressBar)
        attackProgressBar = findViewById(R.id.attackProgressBar)
        defenseProgressBar = findViewById(R.id.defenseProgressBar)
        specialAttackProgressBar = findViewById(R.id.specialAttackProgressBar)
        specialDefenseProgressBar = findViewById(R.id.specialDefenseProgressBar)
        speedProgressBar = findViewById(R.id.speedProgressBar)

        val backButton: ImageButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            onBackPressed() // Вызываем стандартное действие назад при нажатии кнопки
        }

        // Получение имени покемона из Intent
        val pokemonNameIntent = intent.getStringExtra("pokemon_name") ?: "gastly"


        service.getPokemon(pokemonNameIntent).enqueue(object : Callback<Pokemon> {
            override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                if (response.isSuccessful) {
                    val pokemon = response.body()
                    pokemon?.let {
                        updateUI(it)
                    }
                }
            }

            override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                // Обработка ошибок
            }
        })


    }

    private fun updateUI(pokemon: Pokemon) {
        pokemonName.text = pokemon.name.capitalize()
        pokemonNumber.text = "#${pokemon.id.toString().padStart(3, '0')}"
        pokemonWeight.text = "${pokemon.weight} kg"
        pokemonHeight.text = "${pokemon.height} m"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Получаем данные о виде покемона для получения описания
                val pokemonSpeciesCall = service.getPokemonSpecies(pokemon.id)
                val pokemonSpeciesResponse = pokemonSpeciesCall.execute()

                if (pokemonSpeciesResponse.isSuccessful) {
                    val pokemonSpecies = pokemonSpeciesResponse.body()
                    pokemonSpecies?.let {
                        // Извлекаем текст описания на английском языке, если есть
                        val flavorText = pokemonSpecies.flavorTextEntries
                            .firstOrNull { it.language.name == "en" }?.flavorText
                            ?.replace("\n", " ")?: "Description not available."

                        withContext(Dispatchers.Main) {
                            pokemonDescription.text = flavorText
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        pokemonDescription.text = "Failed to load description"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    pokemonDescription.text = "Failed to load description"
                }
            }
        }




        // Отображение списка ходов покемона
        val abilitiesStringBuilder = StringBuilder()
        for (ability in pokemon.abilities) {
            abilitiesStringBuilder.append("${ability.ability.name}\t")
        }
        pokemonMoves.text = abilitiesStringBuilder.toString()

        // Отображение изображения с использованием Glide, с учетом nullable Sprites
        pokemon.sprites?.other?.officialArtwork?.front_default?.let { artworkUrl ->
            Glide.with(this@MainActivity)
                .load(artworkUrl)
                .into(pokemonImage)
        }

        // Отображение типов покемона
        val typesContainer: LinearLayout = findViewById(R.id.typesContainer)
        typesContainer.removeAllViews() // Очистка контейнера перед добавлением типов
        val typeViews = arrayListOf<TextView>()

        for (type in pokemon.types) {
            val typeTextView = TextView(this)
            typeTextView.text = type.type.name.capitalize()
            typeTextView.setTextColor(Color.WHITE)
            typeTextView.setPadding(10, 0, 10, 0)
            typeTextView.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_bold))

            // Создание скругленного background
            val radius = resources.getDimension(R.dimen.corner_radius) // Загрузка радиуса из ресурсов
            val shapeDrawable = ShapeDrawable()
            shapeDrawable.shape = RoundRectShape(floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius), null, null)
            shapeDrawable.paint.color = getColorForType(type.type.name) // Цвет фона, зависящий от типа
            typeTextView.background = shapeDrawable

            typesContainer.addView(typeTextView)
        }

        if (typeViews.size == 1) {
            typesContainer.gravity = Gravity.CENTER
            typesContainer.addView(typeViews[0])
        } else {
            typesContainer.gravity = Gravity.CENTER_HORIZONTAL
            typeViews.forEach { typesContainer.addView(it) }
        }

        // Убеждаемся, что typeg не null перед использованием
        val typeg = pokemon.types.firstOrNull()?.type?.name?.capitalize()

        typeg?.let { type ->
            // Отображение статистики покемона
            for (stat in pokemon.stats) {
                when (stat.stat.name) {
                    "hp" -> hp.text = stat.base_stat.toString()
                    "attack" -> atk.text = stat.base_stat.toString()
                    "defense" -> def.text = stat.base_stat.toString()
                    "special-attack" -> satk.text = stat.base_stat.toString()
                    "special-defense" -> sdef.text = stat.base_stat.toString()
                    "speed" -> spd.text = stat.base_stat.toString()
                }
            }

            // Установка цвета для каждого заголовка статистики на основе typeg
            titleHP.setTextColor(getColorForType(type))
            titleATK.setTextColor(getColorForType(type))
            titleDEF.setTextColor(getColorForType(type))
            titleSATK.setTextColor(getColorForType(type))
            titleSDEF.setTextColor(getColorForType(type))
            titleSPD.setTextColor(getColorForType(type))
            stats.setTextColor(getColorForType(type))
            about.setTextColor(getColorForType(type))
            frame.setBackgroundColor(getColorForType(type))


            // Установка значений прогресс-баров
            hpProgressBar.progress =
                pokemon.stats.firstOrNull { it.stat.name == "hp" }?.base_stat ?: 0
            attackProgressBar.progress =
                pokemon.stats.firstOrNull { it.stat.name == "attack" }?.base_stat ?: 0
            defenseProgressBar.progress =
                pokemon.stats.firstOrNull { it.stat.name == "defense" }?.base_stat ?: 0
            specialAttackProgressBar.progress =
                pokemon.stats.firstOrNull { it.stat.name == "special-attack" }?.base_stat ?: 0
            specialDefenseProgressBar.progress =
                pokemon.stats.firstOrNull { it.stat.name == "special-defense" }?.base_stat ?: 0
            speedProgressBar.progress =
                pokemon.stats.firstOrNull { it.stat.name == "speed" }?.base_stat ?: 0

            // Установка цвета для каждого прогресс-бара на основе typeg
            setProgressBarColor(hpProgressBar, getColorForType(type))
            setProgressBarColor(attackProgressBar, getColorForType(type))
            setProgressBarColor(defenseProgressBar, getColorForType(type))
            setProgressBarColor(specialAttackProgressBar, getColorForType(type))
            setProgressBarColor(specialDefenseProgressBar, getColorForType(type))
            setProgressBarColor(speedProgressBar, getColorForType(type))
        }
    }

    private fun setProgressBarColor(progressBar: ProgressBar, color: Int) {
        val progressDrawable = progressBar.progressDrawable as LayerDrawable
        val clipDrawable =
            progressDrawable.findDrawableByLayerId(android.R.id.progress) as ClipDrawable
        DrawableCompat.setTint(clipDrawable, color)
    }

    private fun getColorForType(type: String): Int {
        return when (type.lowercase()) {
            "normal" -> Color.parseColor("#A8A77A")
            "fire" -> Color.parseColor("#EE8130")
            "water" -> Color.parseColor("#6390F0")
            "electric" -> Color.parseColor("#F7D02C")
            "grass" -> Color.parseColor("#7AC74C")
            "ice" -> Color.parseColor("#96D9D6")
            "fighting" -> Color.parseColor("#C22E28")
            "poison" -> Color.parseColor("#A33EA1")
            "ground" -> Color.parseColor("#E2BF65")
            "flying" -> Color.parseColor("#A98FF3")
            "psychic" -> Color.parseColor("#F95587")
            "bug" -> Color.parseColor("#A6B91A")
            "rock" -> Color.parseColor("#B6A136")
            "ghost" -> Color.parseColor("#735797")
            "dragon" -> Color.parseColor("#6F35FC")
            "dark" -> Color.parseColor("#705746")
            "steel" -> Color.parseColor("#B7B7CE")
            "fairy" -> Color.parseColor("#D685AD")
            else -> Color.BLACK
        }
    }

    private fun loadPokemonDetail(pokemonId: Int) {

    }

}
