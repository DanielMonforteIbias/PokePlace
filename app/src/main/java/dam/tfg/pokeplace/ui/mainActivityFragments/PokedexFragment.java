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

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.adapters.PokemonAdapter;
import dam.tfg.pokeplace.api.PokeApiBasePokemonResponse;
import dam.tfg.pokeplace.api.PokeApiTypeResponse;
import dam.tfg.pokeplace.api.BasePokemonCallback;
import dam.tfg.pokeplace.api.TypeCallback;
import dam.tfg.pokeplace.data.Data;
import dam.tfg.pokeplace.data.dao.BasePokemonDAO;
import dam.tfg.pokeplace.databinding.FragmentPokedexBinding;
import dam.tfg.pokeplace.models.BasePokemon;
import dam.tfg.pokeplace.models.Type;

public class PokedexFragment extends Fragment {

    private FragmentPokedexBinding binding;
    private PokemonAdapter adapter;
    private Data data;
    private BasePokemonDAO basePokemonDAO;

    private int loadLimit;
    private int totalPokemon;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPokedexBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        basePokemonDAO=new BasePokemonDAO(getContext());
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
        loadLimit = getResources().getInteger(R.integer.load_limit);
        totalPokemon=getResources().getInteger(R.integer.total_pokemon);
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
        int current = data.getPokemonList().size();
        System.out.println("current: "+current);
        List<BasePokemon> localBlock = basePokemonDAO.getBasePokemon(loadLimit,current);
        if (!localBlock.isEmpty()) {
            data.getPokemonList().addAll(localBlock);
            getActivity().runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                loadPokemon();//Se llama a si mismo para seguir cargando de 60 en 60
            });
        }
        else if(current<totalPokemon){
            PokeApiBasePokemonResponse.getAllPokemons(new BasePokemonCallback() {
                @Override
                public void onBasePokemonListReceived(List<BasePokemon> pokemonList) {
                    data.getPokemonList().addAll(pokemonList);
                    pokemonList.forEach(basePokemonDAO::addBasePokemon); //Añadimos todos los pokemon recibidos a la BD. No se añade uno por uno durante la carga para evitar saltarse Pokemon al interrumpirla a mitades
                    System.out.println("AÑADIDOS NUEVOS POKEMON A LA BD");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onBasePokemonReceived(BasePokemon pokemon) {

                }
            }, getContext(),data.getPokemonList().size()); //Le pasamos el numero de Pokemon actual por si la carga se quedo a medias, para retomar donde estaba
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}