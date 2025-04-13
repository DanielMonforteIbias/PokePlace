package dam.tfg.pokeplace.ui.pokedex;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dam.tfg.pokeplace.adapters.PokemonAdapter;
import dam.tfg.pokeplace.api.PokeApiResponse;
import dam.tfg.pokeplace.api.PokemonCallback;
import dam.tfg.pokeplace.databinding.FragmentPokedexBinding;
import dam.tfg.pokeplace.models.Pokemon;

public class PokedexFragment extends Fragment {

    private FragmentPokedexBinding binding;
    private List<Pokemon> pokemonList=new ArrayList<Pokemon>();
    private PokemonAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPokedexBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        RecyclerView recyclerView = binding.pokemonList;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),4));
        adapter=new PokemonAdapter(pokemonList);
        recyclerView.setAdapter(adapter);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(pokemonList.isEmpty()){ //Buscamos los Pokemon solo si la lista está vacía
            PokeApiResponse.getAllPokemons(new PokemonCallback() {
                @Override
                public void onPokemonListReceived(List<Pokemon> list) {
                    pokemonList.clear();
                    pokemonList.addAll(list);
                    System.out.println("FIN");
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
            },getContext());
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}