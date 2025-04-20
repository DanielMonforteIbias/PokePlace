package dam.tfg.pokeplace.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dam.tfg.pokeplace.PokemonDetailsActivity;
import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.api.PokeApiBasePokemonResponse;
import dam.tfg.pokeplace.api.PokeApiDetailsResponse;
import dam.tfg.pokeplace.api.PokemonCallback;
import dam.tfg.pokeplace.models.BasePokemon;
import dam.tfg.pokeplace.models.Move;
import dam.tfg.pokeplace.models.Pokemon;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.ViewHolder>{
    private List<BasePokemon> pokemonList;

    public PokemonAdapter(List<BasePokemon>pokemonList){
        this.pokemonList=pokemonList;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView sprite;
        private final TextView pokedexNumber;
        public ViewHolder(View itemView) {
            super(itemView);
            sprite = itemView.findViewById(R.id.sprite);
            pokedexNumber=itemView.findViewById(R.id.pokedexNumber);
        }

        public ImageView getPortada() {
            return sprite;
        }
        public TextView getPokedexNumber(){return pokedexNumber;}
    }

    @NonNull
    @Override
    public PokemonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pokemon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonAdapter.ViewHolder holder, int position) {
        BasePokemon pokemon=pokemonList.get(position);
        Context context=holder.itemView.getContext();
        Glide.with(context).load(pokemon.getSprite()).into(holder.sprite);
        holder.pokedexNumber.setText(pokemon.getPokedexNumber());
        holder.sprite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PokeApiDetailsResponse.getPokemon(pokemon.getUrl(), new PokemonCallback() {
                    @Override
                    public void onPokemonReceived(Pokemon pokemon) {
                        Intent intent=new Intent(context, PokemonDetailsActivity.class);
                        intent.putExtra("Pokemon",pokemon);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onMoveReceived(Move move) {

                    }
                },context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }
}
