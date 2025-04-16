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

import java.util.List;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.adapters.TypeAdapter;
import dam.tfg.pokeplace.data.Data;
import dam.tfg.pokeplace.databinding.FragmentAttackerBinding;
import dam.tfg.pokeplace.databinding.FragmentDefenderBinding;
import dam.tfg.pokeplace.models.Type;
import dam.tfg.pokeplace.utils.StringFormatter;

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
        binding.spinnerAttackerTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Reseteamos todas las vistas
                resetViews();
                Type type=adapter.getItem(position);
                addTypeSpritesToLayout(binding.attackerEffectiveLayout,binding.attackerEffectiveTypesLayout, binding.attackerEffective,getResources().getString(R.string.attacker_x2),type.getDoubleDamageTo(),type.getName());
                addTypeSpritesToLayout(binding.attackerNormalLayout,binding.attackerNormalTypesLayout, binding.attackerNormal,getResources().getString(R.string.attacker_x1),data.getNormalDamageTo(type),type.getName());
                addTypeSpritesToLayout(binding.attackerNotEffectiveLayout,binding.attackerNotEffectiveTypesLayout, binding.attackerNotEffective,getResources().getString(R.string.attacker_x0_5),type.getHalfDamageTo(),type.getName());
                addTypeSpritesToLayout(binding.attackerNoEffectLayout,binding.attackerNoEffectTypesLayout, binding.attackerNoEffect,getResources().getString(R.string.attacker_x0),type.getNoDamageTo(),type.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void addTypeSpritesToLayout(ViewGroup layout,ViewGroup typesLayout, TextView textView, String message,List<String> typeNames, String attackerName) {
        if(!typeNames.isEmpty()){
            layout.setVisibility(View.VISIBLE);
            textView.setText(StringFormatter.formatName(attackerName) +" "+message);
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
        binding.attackerEffectiveTypesLayout.removeAllViews();
        binding.attackerNormalTypesLayout.removeAllViews();
        binding.attackerNotEffectiveTypesLayout.removeAllViews();
        binding.attackerNoEffectTypesLayout.removeAllViews();
        binding.attackerEffectiveLayout.setVisibility(View.GONE);
        binding.attackerNormalLayout.setVisibility(View.GONE);
        binding.attackerNotEffectiveLayout.setVisibility(View.GONE);
        binding.attackerNoEffectLayout.setVisibility(View.GONE);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}