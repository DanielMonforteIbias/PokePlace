package dam.tfg.pokeplace.adapters;

import android.content.Context;
import android.util.Log;
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
    private final Context context;
    private final List<BasePokemon> pokemonList;
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
                List<BasePokemon> filteredList = new ArrayList<>(); //Usamos otra lista y no suggestions directamente porque esto está en un hilo secundario pero a suggestions se accede eesde el principal
                if (constraint != null && constraint.length() > 0) {
                    String prefix = constraint.toString().toLowerCase();
                    for (BasePokemon p : pokemonList) {
                        if (p.getName().toLowerCase().startsWith(prefix)) filteredList.add(p);
                    }
                }else filteredList.addAll(pokemonList);
                FilterResults results = new FilterResults();
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.values != null) {
                    @SuppressWarnings("unchecked") //Sabemos que la lista es de este tipo
                    List<BasePokemon> list = (List<BasePokemon>) results.values;
                    suggestions = new ArrayList<>(list); //Creamos una nueva lista en base a los resultados, esta vez en el hilo principal, pues suggestions es la relacionada con el adpater
                }
                else suggestions = new ArrayList<>();
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
        if (position >= 0 && position < suggestions.size()) {
            return suggestions.get(position);
        } else {
            Log.e("PokemonAdapter", "ERROR: Position" + position + "/" + suggestions.size() + ")");
            return null;
        }
    }
}
