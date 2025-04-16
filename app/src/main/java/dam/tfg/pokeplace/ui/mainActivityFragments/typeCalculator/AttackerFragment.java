package dam.tfg.pokeplace.ui.mainActivityFragments.typeCalculator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.adapters.TypeAdapter;
import dam.tfg.pokeplace.data.Data;
import dam.tfg.pokeplace.databinding.FragmentAttackerBinding;
import dam.tfg.pokeplace.databinding.FragmentDefenderBinding;

public class AttackerFragment extends Fragment {
    private FragmentAttackerBinding binding;
    private Data data;
    private TypeAdapter adapter;
    public AttackerFragment() {
        super(R.layout.fragment_attacker);
    }
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAttackerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        data=Data.getInstance();
        adapter=new TypeAdapter(data.getTypeList(),getContext());
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.spinnerAttackerTypes.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}