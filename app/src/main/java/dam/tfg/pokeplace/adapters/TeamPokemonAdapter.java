package dam.tfg.pokeplace.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
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
import dam.tfg.pokeplace.api.PokeApiDetailsResponse;
import dam.tfg.pokeplace.api.PokemonCallback;
import dam.tfg.pokeplace.api.PokemonSpeciesCallback;
import dam.tfg.pokeplace.models.BasePokemon;
import dam.tfg.pokeplace.models.Move;
import dam.tfg.pokeplace.models.Pokemon;
import dam.tfg.pokeplace.models.TeamPokemon;

public class TeamPokemonAdapter extends RecyclerView.Adapter<TeamPokemonAdapter.ViewHolder>{
    private List<TeamPokemon> pokemonList;

    public TeamPokemonAdapter(List<TeamPokemon>pokemonList){
        this.pokemonList=pokemonList;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView sprite;
        private final TextView pokemonCustomName;
        private final TextView pokemonName;
        public ViewHolder(View itemView) {
            super(itemView);
            sprite = itemView.findViewById(R.id.teamPokemonSprite);
            pokemonCustomName =itemView.findViewById(R.id.teamPokemonCustomName);
            pokemonName =itemView.findViewById(R.id.teamPokemonName);
        }

        public ImageView getPortada() {return sprite;}
        public TextView getPokemonCustomName(){return pokemonCustomName;}
        public TextView getPokemonName(){return pokemonName;}
    }

    @NonNull
    @Override
    public TeamPokemonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_pokemon, parent, false);
        return new TeamPokemonAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamPokemonAdapter.ViewHolder holder, int position) {
        TeamPokemon pokemon=pokemonList.get(position);
        Context context=holder.itemView.getContext();
        Glide.with(context).load(pokemon.getCustomSprite()).into(holder.sprite);
        holder.pokemonCustomName.setText(pokemon.getCustomName());
        holder.pokemonName.setText(pokemon.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pokemonUrl=pokemon.getUrl();
                PokeApiDetailsResponse.getPokemon(pokemonUrl, new PokemonCallback() {
                    @Override
                    public void onPokemonReceived(Pokemon pokemon) {
                        String urlSpecies=pokemonUrl.replace("pokemon","pokemon-species"); //Creamos la URL para conseguir la especie
                        PokeApiDetailsResponse.getDescriptions(urlSpecies, new PokemonSpeciesCallback() {
                            @Override
                            public void onDescriptionsReceived(List<Pair<String, String>> descriptions) {
                                pokemon.setDescriptions(descriptions);
                                Intent intent=new Intent(context, PokemonDetailsActivity.class);
                                intent.putExtra("Pokemon",pokemon);
                                context.startActivity(intent);
                            }
                        },context);
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
