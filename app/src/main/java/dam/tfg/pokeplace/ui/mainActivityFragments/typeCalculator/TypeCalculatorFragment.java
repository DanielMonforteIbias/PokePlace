package dam.tfg.pokeplace.ui.mainActivityFragments.typeCalculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.adapters.TypeCalculatorAdapter;
import dam.tfg.pokeplace.databinding.FragmentTypeCalculatorBinding;

public class TypeCalculatorFragment extends Fragment {

    private FragmentTypeCalculatorBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTypeCalculatorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureViewPager();
    }
    public void configureViewPager(){
        TypeCalculatorAdapter adapter = new TypeCalculatorAdapter(this);
        binding.viewPager.setAdapter(adapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(getResources().getString(R.string.type_chart));
                    break;
                case 1:
                    tab.setText(getResources().getString(R.string.attacker));
                    break;
                case 2:
                    tab.setText(getResources().getString(R.string.defender));
                    break;
            }
        }).attach();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}