package dam.tfg.pokeplace.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import dam.tfg.pokeplace.ui.mainActivityFragments.typeCalculator.AttackerFragment;
import dam.tfg.pokeplace.ui.mainActivityFragments.typeCalculator.DefenderFragment;
import dam.tfg.pokeplace.ui.mainActivityFragments.typeCalculator.TypeChartFragment;

public class TypeCalculatorAdapter extends FragmentStateAdapter {
    public TypeCalculatorAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TypeChartFragment();
            case 1:
                return new AttackerFragment();
            case 2:
                return new DefenderFragment();
            default:
                return new TypeChartFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
