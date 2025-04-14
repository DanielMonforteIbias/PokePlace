package dam.tfg.pokeplace.ui.detailsActivityFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import dam.tfg.pokeplace.databinding.FragmentStatsBinding;
import dam.tfg.pokeplace.models.Pokemon;

public class StatsFragment extends Fragment {
    private FragmentStatsBinding binding;
    private Pokemon pokemon;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PokemonViewModel viewModel = new ViewModelProvider(requireActivity()).get(PokemonViewModel.class);
        viewModel.getPokemon().observe(getViewLifecycleOwner(), pokemon -> { //Obtenemos el Pokemon del viewmodel

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}