package dam.tfg.pokeplace.ui.detailsActivityFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import dam.tfg.pokeplace.adapters.MovesAdapter;
import dam.tfg.pokeplace.api.PokeApiDetailsResponse;
import dam.tfg.pokeplace.api.PokemonCallback;
import dam.tfg.pokeplace.databinding.FragmentMovesBinding;
import dam.tfg.pokeplace.models.Move;
import dam.tfg.pokeplace.models.Pokemon;

public class MovesFragment extends Fragment {

    private FragmentMovesBinding binding;
    private MovesAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMovesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.movesList.setLayoutManager(new GridLayoutManager(getContext(),1));
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PokemonViewModel viewModel = new ViewModelProvider(requireActivity()).get(PokemonViewModel.class);
        viewModel.getPokemon().observe(getViewLifecycleOwner(), pokemon -> { //Obtenemos el Pokemon del viewmodel
            binding.txtMovesTitle.setText("MOVIMIENTOS DE "+pokemon.getName().toUpperCase());
            adapter=new MovesAdapter(pokemon.getMoves());
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
                                    adapter.notifyItemChanged(index);
                                });
                            }
                        }
                    }, getContext());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}