package dam.tfg.pokeplace.ui.detailsActivityFragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.adapters.MovesAdapter;
import dam.tfg.pokeplace.api.PokeApiDetailsResponse;
import dam.tfg.pokeplace.api.PokemonCallback;
import dam.tfg.pokeplace.data.Data;
import dam.tfg.pokeplace.databinding.FragmentMovesBinding;
import dam.tfg.pokeplace.interfaces.OnTypeSelectedListener;
import dam.tfg.pokeplace.models.BasePokemon;
import dam.tfg.pokeplace.models.Move;
import dam.tfg.pokeplace.models.Pokemon;
import dam.tfg.pokeplace.models.Type;
import dam.tfg.pokeplace.ui.mainActivityFragments.pokedex.TypeFilterBottomSheetFragment;

public class MovesFragment extends Fragment implements OnTypeSelectedListener {
    private FragmentMovesBinding binding;
    private MovesAdapter adapter;
    private Data data;
    private Pokemon pokemon;
    private List<Type> filterTypes=new ArrayList<>();
    private List<Move>filteredList=new ArrayList<>();
    private String currentTypeFilter="";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMovesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        data= Data.getInstance();
        filterTypes=new ArrayList<>(data.getTypeList());
        filterTypes.add(0,new Type(getString(R.string.all_types),null)); //Añadimos el tipo para mostrar todos
        binding.movesList.setLayoutManager(new GridLayoutManager(getContext(),1));
        binding.btnTypeFilterMoves.setOnClickListener(new View.OnClickListener() {
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
        PokemonViewModel viewModel = new ViewModelProvider(requireActivity()).get(PokemonViewModel.class);
        viewModel.getPokemon().observe(getViewLifecycleOwner(), pokemon -> { //Obtenemos el Pokemon del viewmodel
            this.pokemon=pokemon;
            filteredList=new ArrayList<>();
            binding.txtMovesTitle.setText(getString(R.string.moves_title,pokemon.getName().toUpperCase()));
            adapter=new MovesAdapter(filteredList);
            binding.movesList.setAdapter(adapter);
            for (int i = 0; i <pokemon.getMoves().size(); i++) {
                Move m = pokemon.getMoves().get(i);
                if (m.getName() == null && m.getUrl()!=null) { //Cuando no hay nombre y si URL, es que el movimiento no esta relleno
                    final int index = i;
                    PokeApiDetailsResponse.getMove(m.getUrl(),new PokemonCallback() {
                        @Override
                        public void onPokemonReceived(Pokemon pokemon) {

                        }

                        @Override
                        public void onMoveReceived(Move move) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    pokemon.getMoves().set(index, move);
                                    filterList(currentTypeFilter);
                                });
                            }
                        }
                    }, getContext());
                }
                else filterList(currentTypeFilter);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void filterList(String typeFilter) {
        filteredList.clear();
        if(typeFilter == null || typeFilter.isEmpty() || typeFilter.equals(getString(R.string.all_types))) filteredList.addAll((pokemon.getMoves()));
        else for (Move m:pokemon.getMoves()) {
            if(m!=null){
                if(m.getType().getName().equals(typeFilter))filteredList.add(m);
            }
        }
        adapter.notifyDataSetChanged();
        if(binding!=null){ //Actualizamos el TextView solo si está disponible
            if(filteredList.isEmpty()){
                binding.txtNoMovesForType.setVisibility(View.VISIBLE);
                binding.txtNoMovesForType.setText(getString(R.string.no_moves_for_type,currentTypeFilter));
            }
            else binding.txtNoMovesForType.setVisibility(View.GONE);
        }
    }
    @Override
    public void onTypeSelected(Type type) {
        currentTypeFilter=type.getName();
        int colorId=getResources().getIdentifier(type.getName(), "color", getContext().getPackageName());
        if (colorId != 0) {
            int color = ContextCompat.getColor(getContext(), colorId);
            binding.btnTypeFilterMoves.setBackgroundTintList(ColorStateList.valueOf(color));
        }else {
            binding.btnTypeFilterMoves.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_500)));
        }
        filterList(currentTypeFilter);
    }
}