package dam.tfg.pokeplace.ui.mainActivityFragments.typeCalculator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;
import java.util.stream.Collectors;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.adapters.TypeAdapter;
import dam.tfg.pokeplace.adapters.TypeCalculatorAdapter;
import dam.tfg.pokeplace.data.Data;
import dam.tfg.pokeplace.databinding.FragmentDefenderBinding;
import dam.tfg.pokeplace.databinding.FragmentTypeCalculatorBinding;
import dam.tfg.pokeplace.models.Type;
import dam.tfg.pokeplace.utils.StringFormatter;

public class DefenderFragment extends Fragment {
    private FragmentDefenderBinding binding;
    private Data data;
    private TypeAdapter adapter;
    private List<String>allTypeNames;
    public DefenderFragment() {
        super(R.layout.fragment_defender);
    }
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDefenderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        data=Data.getInstance();
        allTypeNames=data.getTypeList().stream().map(Type::getName).collect(Collectors.toList());
        adapter=new TypeAdapter(data.getTypeList(),getContext());
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.spinnerDefenderTypes.setAdapter(adapter);
        binding.spinnerDefenderTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                resetViews();
                Type type=adapter.getItem(position);
                addTypeSpritesToLayout(binding.defenderEffectiveLayout,binding.defenderEffectiveTypesLayout, binding.defenderEffective,getResources().getString(R.string.defender_x2),type.getDoubleDamageFrom(),type.getName());
                addTypeSpritesToLayout(binding.defenderNormalLayout,binding.defenderNormalTypesLayout, binding.defenderNormal,getResources().getString(R.string.defender_x1),data.getNormalDamageFrom(type),type.getName());
                addTypeSpritesToLayout(binding.defenderNotEffectiveLayout,binding.defenderNotEffectiveTypesLayout, binding.defenderNotEffective,getResources().getString(R.string.defender_x0_5),type.getHalfDamageFrom(),type.getName());
                addTypeSpritesToLayout(binding.defenderNoEffectLayout,binding.defenderNoEffectTypesLayout, binding.defenderNoEffect,getResources().getString(R.string.defender_x0),type.getNoDamageFrom(),type.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void addTypeSpritesToLayout(ViewGroup layout,ViewGroup typesLayout, TextView textView, String message, List<String> typeNames, String defenderName) {
        if(!typeNames.isEmpty()){
            layout.setVisibility(View.VISIBLE);
            textView.setText(StringFormatter.formatName(defenderName) +" "+message);
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics());
            int horizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            int verticalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT); //75dp width, wrap_content height
            params.setMargins(horizontalMargin,verticalMargin,horizontalMargin,verticalMargin);
            for (String typeName : typeNames) {
                ImageView img = new ImageView(getContext());
                img.setLayoutParams(params);
                Glide.with(getContext()).load(data.getTypeByName(typeName).getSprite()).into(img);
                typesLayout.addView(img);
            }
        }
    }
    private void resetViews(){
        //Reseteamos todas las vistas
        binding.defenderEffectiveTypesLayout.removeAllViews();
        binding.defenderNormalTypesLayout.removeAllViews();
        binding.defenderNotEffectiveTypesLayout.removeAllViews();
        binding.defenderNoEffectTypesLayout.removeAllViews();
        binding.defenderEffectiveLayout.setVisibility(View.GONE);
        binding.defenderNormalLayout.setVisibility(View.GONE);
        binding.defenderNotEffectiveLayout.setVisibility(View.GONE);
        binding.defenderNoEffectLayout.setVisibility(View.GONE);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}