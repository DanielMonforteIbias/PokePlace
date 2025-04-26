package dam.tfg.pokeplace.ui.detailsActivityFragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

import dam.tfg.pokeplace.R;
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
            binding.txtStatsTitle.setText(getString(R.string.stats_title,pokemon.getName().toUpperCase())); //Le pasamos como parametro al string el nombre del pokemon
            //Cargar stats
            binding.statsDetailsLayout.removeAllViews(); //Las vistas que tiene son solo para visualizarlo en el XML, las quitamos
            int statTotalValue=0;
            for (Map.Entry<String,Integer>stat:pokemon.getStats().entrySet()) {
                View statItem = getLayoutInflater().inflate(R.layout.item_stat, binding.statsDetailsLayout, false);
                TextView txtStatName = statItem.findViewById(R.id.txtStatDetails);
                String resourceKey =stat.getKey().replace("-", "_"); //Los nombres de la stat estan en strings y la clave es el nombre que viene de la api y esta en el map. No se aceptan guiones en el fichero strings, se reemplazan por _
                int resId = getContext().getResources().getIdentifier(resourceKey, "string", getContext().getPackageName());
                txtStatName.setText(getString(resId));
                TextView txtStatValue = statItem.findViewById(R.id.txtStatValueDetails);
                txtStatValue.setText(String.valueOf(stat.getValue()));
                View statBar = statItem.findViewById(R.id.statBar);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) statBar.getLayoutParams();
                int statValue = stat.getValue();
                statTotalValue+=statValue;
                float scale = getContext().getResources().getDisplayMetrics().density;
                int color = getStatBarColor(statValue);
                GradientDrawable drawable = (GradientDrawable) statBar.getBackground(); //Para no perder el borde redondeado al aplicar fondo
                drawable.setColor(color);
                layoutParams.width = (int) (statValue * scale + 0.5f);
                statBar.setLayoutParams(layoutParams);
                binding.statsDetailsLayout.addView(statItem);
            }
            binding.txtStatsDetailsTotal.setText(getString(R.string.total,statTotalValue));
        });
    }

    public int getStatBarColor(int statValue){
        int color;
        if (statValue <= 25) {
            color = Color.RED;
        } else if (statValue <= 50) {
            color = Color.parseColor("#FFA500");
        } else if (statValue <= 90) {
            color = Color.YELLOW;
        } else if (statValue <= 120) {
            color = Color.parseColor("#99FF2F");
        } else {
            color = Color.GREEN;
        }
        return color;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}