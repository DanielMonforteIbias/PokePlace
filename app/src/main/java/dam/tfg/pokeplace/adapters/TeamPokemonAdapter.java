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
import dam.tfg.pokeplace.data.Data;
import dam.tfg.pokeplace.models.Move;
import dam.tfg.pokeplace.models.Pokemon;
import dam.tfg.pokeplace.models.TeamPokemon;
import dam.tfg.pokeplace.models.Type;
import dam.tfg.pokeplace.utils.PokemonClickHandler;
import dam.tfg.pokeplace.utils.ViewUtils;

public class TeamPokemonAdapter extends RecyclerView.Adapter<TeamPokemonAdapter.ViewHolder>{
    private List<TeamPokemon> pokemonList;

    public TeamPokemonAdapter(List<TeamPokemon>pokemonList){
        this.pokemonList=pokemonList;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView sprite;
        private final TextView pokemonCustomName;
        private final TextView pokemonName;
        private final ImageView type1;
        private final ImageView type2;
        public ViewHolder(View itemView) {
            super(itemView);
            sprite = itemView.findViewById(R.id.teamPokemonSprite);
            pokemonCustomName =itemView.findViewById(R.id.teamPokemonCustomName);
            pokemonName =itemView.findViewById(R.id.teamPokemonName);
            type1=itemView.findViewById(R.id.teamPokemonType1);
            type2=itemView.findViewById(R.id.teamPokemonType2);
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
        ViewUtils.setPokemonTypeBackground(context,holder.itemView,pokemon.getType1(),50,12);
        if(pokemon.getType1()!=null) {
            Type type1=Data.getInstance().getTypeByName(pokemon.getType1());
            if(type1!=null) Glide.with(context).load(type1.getSprite()).into(holder.type1);
        }
        if(pokemon.getType2()!=null) {
            Type type2=Data.getInstance().getTypeByName(pokemon.getType2());
            if(type2!=null) Glide.with(context).load(type2.getSprite()).into(holder.type2);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PokemonClickHandler.handlePokemonClick(context,pokemon);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }
}
