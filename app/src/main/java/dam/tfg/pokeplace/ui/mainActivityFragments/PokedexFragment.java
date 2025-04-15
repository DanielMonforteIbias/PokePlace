package dam.tfg.pokeplace.ui.mainActivityFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.tfg.pokeplace.adapters.PokemonAdapter;
import dam.tfg.pokeplace.api.PokeApiPokemonResponse;
import dam.tfg.pokeplace.api.PokeApiTypeResponse;
import dam.tfg.pokeplace.api.PokemonCallback;
import dam.tfg.pokeplace.api.TypeCallback;
import dam.tfg.pokeplace.data.Data;
import dam.tfg.pokeplace.databinding.FragmentPokedexBinding;
import dam.tfg.pokeplace.models.Pokemon;
import dam.tfg.pokeplace.models.Type;

public class PokedexFragment extends Fragment {

    private FragmentPokedexBinding binding;
    private PokemonAdapter adapter;
    private Data data;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPokedexBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        data=Data.getInstance();
        RecyclerView recyclerView = binding.pokemonList;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),4));
        adapter=new PokemonAdapter(data.getPokemonList());
        recyclerView.setAdapter(adapter);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("HOLA");
        loadTypes();
    }

    public void loadTypes(){
        if (data.getTypeList().isEmpty()) { //Buscamos los tipos solo si la lista está vacía
            PokeApiTypeResponse.getAllTypes(new TypeCallback() {
                @Override
                public void onTypeListReceived(List<Type> typeList) {
                    data.getTypeList().clear();
                    data.getTypeList().addAll(typeList);
                    loadPokemon();
                }

                @Override
                public void onTypeReceived(Type type) {

                }
            }, getContext());
        }
        else loadPokemon();
    }
    public void loadPokemon(){
        PokeApiPokemonResponse.getAllPokemons(new PokemonCallback() {
            @Override
            public void onPokemonListReceived(List<Pokemon> list) {
                data.getPokemonList().addAll(list);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onPokemonReceived(Pokemon pokemon) {

            }
        }, getContext(),data.getPokemonList().size()); //Le pasamos el numero de Pokemon actual por si la carga se quedo a medias, para retomar donde estaba
        System.out.println("Actual: "+data.getPokemonList().size());
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}