package dam.tfg.pokeplace.ui.mainActivityFragments.pokedex;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.adapters.PokemonAdapter;
import dam.tfg.pokeplace.api.PokeApiBasePokemonResponse;
import dam.tfg.pokeplace.api.PokeApiTypeResponse;
import dam.tfg.pokeplace.api.BasePokemonCallback;
import dam.tfg.pokeplace.api.TypeCallback;
import dam.tfg.pokeplace.data.Data;
import dam.tfg.pokeplace.data.dao.BasePokemonDAO;
import dam.tfg.pokeplace.data.dao.TypeDAO;
import dam.tfg.pokeplace.data.dao.TypeRelationDAO;
import dam.tfg.pokeplace.data.service.TypeService;
import dam.tfg.pokeplace.databinding.FragmentPokedexBinding;
import dam.tfg.pokeplace.interfaces.OnTypeSelectedListener;
import dam.tfg.pokeplace.models.BasePokemon;
import dam.tfg.pokeplace.models.Type;

public class PokedexFragment extends Fragment implements OnTypeSelectedListener {

    private FragmentPokedexBinding binding;
    private PokemonAdapter adapter;
    private Data data;
    private List<Type>filterTypes=new ArrayList<>();
    private List<BasePokemon>filteredList=new ArrayList<>();
    private String currentNameFilter ="";
    private String currentTypeFilter="";
    //private BasePokemonDAO basePokemonDAO;
    //private TypeService typeService;

    //private int loadLimit;
    //private int totalPokemon;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPokedexBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //basePokemonDAO=new BasePokemonDAO(getContext());
        //typeService=new TypeService(new TypeDAO(getContext()),new TypeRelationDAO(getContext()));
        data=Data.getInstance();
        filterTypes=new ArrayList<>(data.getTypeList());
        filterTypes.add(0,new Type(getString(R.string.all_types),null)); //Añadimos el tipo para mostrar todos
        filteredList=new ArrayList<>(data.getPokemonList());
        binding.pokemonList.setLayoutManager(new GridLayoutManager(getContext(),4));
        adapter=new PokemonAdapter(filteredList);
        binding.pokemonList.setAdapter(adapter);
        binding.btnTypeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypeFilterBottomSheetFragment filtersFragment = TypeFilterBottomSheetFragment.newInstance(filterTypes);
                filtersFragment.show(getChildFragmentManager(), filtersFragment.getTag());
            }
        });
        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*if(savedInstanceState!=null){
            currentNameFilter=savedInstanceState.getString("currentNameFilter");
            currentTypeFilter=savedInstanceState.getString("currentTypeFilter");
        }*/
        /*loadLimit = getResources().getInteger(R.integer.load_limit);
        totalPokemon=getResources().getInteger(R.integer.total_pokemon);*/
        requireActivity().addMenuProvider(new MenuProvider() { //Añadimos el menu con la SearchView solo a este fragment
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_pokedex, menu);
                SearchView searchView = (SearchView) menu.findItem(R.id.action_search_name).getActionView();
                if(searchView!=null){
                    searchView.setQueryHint(getString(R.string.search_name));
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            currentNameFilter =newText;
                            filterList(currentNameFilter,currentTypeFilter);
                            return true;
                        }
                    });
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        },getViewLifecycleOwner()); //Importante añadir para destruir junto al fragmento
        /*if(!loadTypes()){
            if(isAdded() && getActivity()!=null){
                getActivity().runOnUiThread(()->binding.btnTypeFilter.setVisibility(View.VISIBLE));
                loadPokemon();
            }
        }*/
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentNameFilter",currentNameFilter);
        outState.putString("currenTypeFilter",currentTypeFilter);
    }

    /*public boolean loadTypes(){
        if (data.getTypeList().isEmpty()) { //Buscamos los tipos solo si la lista está vacía
            binding.btnTypeFilter.setVisibility(View.GONE);
            data.getTypeList().addAll(typeService.getAllTypes());
            if (data.getTypeList().isEmpty()){ //Comprobamos de nuevo si esta vacia por si no estan en la BD
                PokeApiTypeResponse.getAllTypes(new TypeCallback() {
                    @Override
                    public void onTypeListReceived(List<Type> typeList) {
                        data.getTypeList().clear();
                        data.getTypeList().addAll(typeList);
                        typeService.addAllTypes(typeList); //Añadimos los tipos a la BD
                        filterTypes.addAll(typeList);
                        if (isAdded() && getActivity() != null) {
                            getActivity().runOnUiThread(() -> binding.btnTypeFilter.setVisibility(View.VISIBLE));
                            loadPokemon(); //Empezamos a cargar los Pokemon cuando terminen los tipos, ya que los necesitan
                        }
                    }

                    @Override
                    public void onTypeReceived(Type type) {

                    }
                }, getContext());
                return true;
            }else { //Si estaban en la base de datos
                filterTypes.addAll(data.getTypeList()); //Añadimos los tipos a la lista de filtros
                if (isAdded() && getActivity() != null) getActivity().runOnUiThread(() -> binding.btnTypeFilter.setVisibility(View.VISIBLE)); //Volvemos a hacer visible el boton
                return false;
            }
        }
        else {
            return false;
        }
    }
    public void loadPokemon(){
        int current = data.getPokemonList().size();
        System.out.println("current: "+current);
        List<BasePokemon> localBlock = basePokemonDAO.getBasePokemonList(loadLimit,current);
        if (!localBlock.isEmpty()) {
            data.getPokemonList().addAll(localBlock);
            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    filterList(currentNameFilter,currentTypeFilter);
                    loadPokemon();//Se llama a si mismo para seguir cargando de 60 en 60
                });
            }
        }
        else if(current<totalPokemon){
            PokeApiBasePokemonResponse.getAllPokemon(new BasePokemonCallback() {
                @Override
                public void onBasePokemonListReceived(List<BasePokemon> pokemonList) {
                    data.getPokemonList().addAll(pokemonList);
                    pokemonList.forEach(basePokemonDAO::addBasePokemon); //Añadimos todos los pokemon recibidos a la BD. No se añade uno por uno durante la carga para evitar saltarse Pokemon al interrumpirla a mitades
                    System.out.println("AÑADIDOS NUEVOS POKEMON A LA BD");
                    if(getActivity()!=null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                filterList(currentNameFilter,currentTypeFilter);
                            }
                        });
                    }
                }
                @Override
                public void onBasePokemonReceived(BasePokemon pokemon) {

                }
            }, getContext(),data.getPokemonList().size()); //Le pasamos el numero de Pokemon actual por si la carga se quedo a medias, para retomar donde estaba
        }
    }*/
    private void filterList(String nameFilter, String typeFilter) {
        filteredList.clear();
        for (BasePokemon p : data.getPokemonList()) {
            boolean nameMatches = p.getName().toLowerCase().contains(nameFilter.trim().toLowerCase());
            String type1=p.getType1();
            String type2=p.getType2();
            boolean noTypeFilter=typeFilter == null || typeFilter.isEmpty() || typeFilter.equals(getString(R.string.all_types));
            boolean typeMatches=(type1 != null && type1.equalsIgnoreCase(typeFilter)) || (type2 != null && type2.equalsIgnoreCase(typeFilter));
            if (nameMatches && (typeMatches||noTypeFilter)) filteredList.add(p);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onTypeSelected(Type type) {
        currentTypeFilter=type.getName();
        int colorId=getResources().getIdentifier(type.getName(), "color", getContext().getPackageName());
        if (colorId != 0) {
            int color = ContextCompat.getColor(getContext(), colorId);
            binding.btnTypeFilter.setBackgroundTintList(ColorStateList.valueOf(color)); //Cambiamos el color del boton por el tipo seleccionado en el filtro
        }else {
            binding.btnTypeFilter.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_500)));
        }
        filterList(currentNameFilter,currentTypeFilter);
    }
}