import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokemon.R
import com.example.pokemon.model.Pokemon

class PokemonAdapter(private val onClick: (Pokemon) -> Unit) :
    RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    private val items = mutableListOf<Pokemon>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon, parent, false)
        return PokemonViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(pokemonList: List<Pokemon>) {
        items.clear()
        items.addAll(pokemonList)
        notifyDataSetChanged()
    }

    fun addPokemon(pokemon: Pokemon) {
        items.add(pokemon)
        notifyDataSetChanged()
    }

    class PokemonViewHolder(itemView: View, val onClick: (Pokemon) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val imagePokemon: ImageView = itemView.findViewById(R.id.imagePokemon)
        private val textPokemonName: TextView = itemView.findViewById(R.id.textPokemonName)
        private val textPokemonid: TextView = itemView.findViewById(R.id.textPokemonid )


        fun bind(pokemon: Pokemon) {
            textPokemonName.text = pokemon.name.capitalize()
            textPokemonid.text = "#${pokemon.id.toString().padStart(3, '0')}"



            // Загрузка изображения с использованием Glide
            pokemon.sprites?.other?.officialArtwork?.front_default?.let { imageUrl ->
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .into(imagePokemon)
            }

            // Обработка нажатия на элемент списка
            itemView.setOnClickListener {
                onClick(pokemon)
            }
        }
    }
}
