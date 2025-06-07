package dam.tfg.pokeplace.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.data.Data;
import dam.tfg.pokeplace.interfaces.OnTeamPokemonActionListener;
import dam.tfg.pokeplace.models.TeamPokemon;
import dam.tfg.pokeplace.models.Type;
import dam.tfg.pokeplace.utils.PokemonClickHandler;
import dam.tfg.pokeplace.utils.ViewUtils;

public class TeamPokemonAdapter extends RecyclerView.Adapter<TeamPokemonAdapter.ViewHolder>{
    private final List<TeamPokemon> pokemonList;
    private final OnTeamPokemonActionListener listener;
    private boolean editing=false; //Variable para saber si mostrar o no los botones de edición

    public TeamPokemonAdapter(List<TeamPokemon>pokemonList, OnTeamPokemonActionListener listener){
        this.pokemonList=pokemonList;
        this.listener=listener;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView sprite;
        private final TextView pokemonCustomName;
        private final TextView pokemonName;
        private final ImageView type1;
        private final ImageView type2;
        private final ImageView renameButton;
        private final ImageView removeButton;
        public ViewHolder(View itemView) {
            super(itemView);
            sprite = itemView.findViewById(R.id.teamPokemonSprite);
            pokemonCustomName =itemView.findViewById(R.id.teamPokemonCustomName);
            pokemonName =itemView.findViewById(R.id.teamPokemonName);
            type1=itemView.findViewById(R.id.teamPokemonType1);
            type2=itemView.findViewById(R.id.teamPokemonType2);
            renameButton=itemView.findViewById(R.id.teamPokemonRename);
            removeButton=itemView.findViewById(R.id.teamPokemonDelete);
        }
    }

    public boolean isEditing() {
        return editing;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setEditing(boolean editing) {
        this.editing = editing;
        notifyDataSetChanged(); //Lo usamos porque cambian todos los items, mostrando u ocultando las opciones de renombrar y borrar
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
        if(isEditing()){
            holder.renameButton.setVisibility(View.VISIBLE);
            holder.removeButton.setVisibility(View.VISIBLE);
            holder.renameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null) listener.onRenameClick(pokemon);
                }
            });
            holder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null) listener.onDeleteClick(pokemon);
                }
            });
        }
        else{ //Si no estamos editando, quitamos los botones
            holder.renameButton.setVisibility(View.GONE);
            holder.removeButton.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }
}
