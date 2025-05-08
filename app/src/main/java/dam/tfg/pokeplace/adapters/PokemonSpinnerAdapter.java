package dam.tfg.pokeplace.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.models.BasePokemon;
import dam.tfg.pokeplace.utils.StringFormatter;

public class PokemonSpinnerAdapter extends ArrayAdapter<BasePokemon> {
    private Context context;
    private List<BasePokemon> pokemonList;
    private List<BasePokemon> suggestions;

    public PokemonSpinnerAdapter(List<BasePokemon> pokemonList, Context context) {
        super(context,0,pokemonList);
        this.context=context;
        this.pokemonList =pokemonList;
        this.suggestions=new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) { //Vista normal de lo seleccionado en el spinner
        return getItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) { //Vista con el spinner desplegado
        return getItemView(position, convertView, parent);
    }
    private View getItemView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pokemon_spinner, parent, false);
        BasePokemon pokemon= getItem(position);
        ImageView imageView = view.findViewById(R.id.imgPokemonSpinner);
        TextView textView=view.findViewById(R.id.txtPokemonNameSpinner);
        if(pokemon!=null){
            Glide.with(context).load(pokemon.getSprite()).into(imageView);
            textView.setText(StringFormatter.formatName(pokemon.getName()));
        }
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                suggestions.clear();
                if (constraint != null && constraint.length() > 0) {
                    String prefix = constraint.toString().toLowerCase();
                    for (BasePokemon p : pokemonList) {
                        if (p.getName().toLowerCase().startsWith(prefix)) {
                            suggestions.add(p);
                        }
                    }
                } else {
                    suggestions.addAll(pokemonList);
                }

                FilterResults results = new FilterResults();
                results.values = suggestions;
                results.count = suggestions.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((BasePokemon) resultValue).getName();
            }
        };
    }
    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public BasePokemon getItem(int position) {
        return suggestions.get(position);
    }
}
