package dam.tfg.pokeplace.ui.mainActivityFragments.typeCalculator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.adapters.TypeSpinnerAdapter;
import dam.tfg.pokeplace.data.Data;
import dam.tfg.pokeplace.databinding.FragmentDefenderBinding;
import dam.tfg.pokeplace.models.Type;
import dam.tfg.pokeplace.utils.StringFormatter;

public class DefenderFragment extends Fragment {
    private FragmentDefenderBinding binding;
    private Data data;
    private TypeSpinnerAdapter adapter;
    private boolean advancedEnabled=false;
    private Type currentType=null;

    public DefenderFragment() {
        super(R.layout.fragment_defender);
    }
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDefenderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        data=Data.getInstance();
        adapter=new TypeSpinnerAdapter(data.getTypeList(),getContext());
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState!=null) currentType=savedInstanceState.getParcelable("currentType");
        binding.spinnerDefenderTypes.setAdapter(adapter);
        binding.spinnerDefenderTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Type type=adapter.getItem(position);
                if(type!=null){
                    currentType=type;
                    updateTypeLayouts(type);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.switchAdvancedMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.switchAdvancedMode.setText(getResources().getText(R.string.advanced));
                    advancedEnabled=true;
                }else{
                    binding.switchAdvancedMode.setText(getResources().getText(R.string.simple));
                    advancedEnabled=false;
                }
                if(currentType!=null)updateTypeLayouts(currentType);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("currentType",currentType);
    }

    private void updateTypeLayouts(Type type){
        resetViews();//Reseteamos todas las vistas
        if(!advancedEnabled){
            addTypeSpritesToLayout(binding.defenderEffectiveLayout,binding.defenderEffectiveTypesLayout, binding.defenderEffective,getResources().getString(R.string.defender_x2,type.getName()),type.getDoubleDamageFrom());
            addTypeSpritesToLayout(binding.defenderNormalLayout,binding.defenderNormalTypesLayout, binding.defenderNormal,getResources().getString(R.string.defender_x1,type.getName()),data.getNormalDamageFrom(type));
            addTypeSpritesToLayout(binding.defenderNotEffectiveLayout,binding.defenderNotEffectiveTypesLayout, binding.defenderNotEffective,getResources().getString(R.string.defender_x0_5,type.getName()),type.getHalfDamageFrom());
            addTypeSpritesToLayout(binding.defenderNoEffectLayout,binding.defenderNoEffectTypesLayout, binding.defenderNoEffect,getResources().getString(R.string.defender_x0,type.getName()),type.getNoDamageFrom());
        }else{
            String mode = "defender";
            addTypeCombinationRowsToLayout(binding.defenderVeryEffectiveLayout,binding.defenderVeryEffectiveTypesAdvancedLayout,binding.defenderVeryEffective,getResources().getString(R.string.defender_x4,type.getName()),data.getTypeCombinationsWithMultiplier(type, mode,4));
            addTypeCombinationRowsToLayout(binding.defenderEffectiveLayout,binding.defenderEffectiveTypesAdvancedLayout,binding.defenderEffective,getResources().getString(R.string.defender_x2,type.getName()),data.getTypeCombinationsWithMultiplier(type, mode,2));
            addTypeCombinationRowsToLayout(binding.defenderNormalLayout,binding.defenderNormalTypesAdvancedLayout,binding.defenderNormal,getResources().getString(R.string.defender_x1,type.getName()),data.getTypeCombinationsWithMultiplier(type, mode,1));
            addTypeCombinationRowsToLayout(binding.defenderNotEffectiveLayout,binding.defenderNotEffectiveTypesAdvancedLayout,binding.defenderNotEffective,getResources().getString(R.string.defender_x0_5,type.getName()),data.getTypeCombinationsWithMultiplier(type, mode,0.5));
            addTypeCombinationRowsToLayout(binding.defenderNotVeryEffectiveLayout,binding.defenderNotVeryEffectiveTypesAdvancedLayout,binding.defenderNotVeryEffective,getResources().getString(R.string.defender_x0_25,type.getName()),data.getTypeCombinationsWithMultiplier(type, mode,0.25));
            addTypeCombinationRowsToLayout(binding.defenderNoEffectLayout,binding.defenderNoEffectTypesAdvancedLayout,binding.defenderNoEffect,getResources().getString(R.string.defender_x0,type.getName()),data.getTypeCombinationsWithMultiplier(type, mode,0));
        }
    }
    private void addTypeSpritesToLayout(ViewGroup layout,ViewGroup typesLayout, TextView textView, String message, List<String> typeNames) {
        if(!typeNames.isEmpty()){
            layout.setVisibility(View.VISIBLE);
            textView.setText(message);
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics());
            int horizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            int verticalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT); //75dp width, wrap_content height
            params.setMargins(horizontalMargin,verticalMargin,horizontalMargin,verticalMargin);
            for (String typeName : typeNames) {
                ImageView img = new ImageView(getContext());
                img.setLayoutParams(params);
                if(getContext()!=null)Glide.with(getContext()).load(data.getTypeByName(typeName).getSprite()).into(img);
                typesLayout.addView(img);
            }
        }
    }

    private void addTypeCombinationRowsToLayout(ViewGroup layout, ViewGroup typesLayout, TextView textView, String message, List<Pair<String,String>> combinations) {
        if(!combinations.isEmpty()) {
            layout.setVisibility(View.VISIBLE);
            textView.setText(message);int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics());
            int horizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            int verticalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
            for (Pair<String,String> combination:combinations) {
                LinearLayout row = new LinearLayout(getContext());
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setPadding(0,verticalMargin,0,verticalMargin);
                String firstType = combination.first;
                String secondType = combination.second;
                for (String typeName : secondType != null ? Arrays.asList(firstType, secondType) : Collections.singletonList(firstType)) {
                    ImageView typeIcon = new ImageView(getContext());
                    LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(width,ViewGroup.LayoutParams.WRAP_CONTENT);
                    iconParams.setMargins(horizontalMargin,0,horizontalMargin,0);
                    typeIcon.setLayoutParams(iconParams);
                    if(getContext()!=null)Glide.with(getContext()).load(data.getTypeByName(typeName).getSprite()).into(typeIcon);
                    row.addView(typeIcon);
                }
                typesLayout.addView(row);
            }

        }
    }

    private void resetViews(){
        binding.defenderVeryEffectiveTypesAdvancedLayout.removeAllViews();
        binding.defenderEffectiveTypesLayout.removeAllViews();
        binding.defenderEffectiveTypesAdvancedLayout.removeAllViews();
        binding.defenderNormalTypesLayout.removeAllViews();
        binding.defenderNormalTypesAdvancedLayout.removeAllViews();
        binding.defenderNotEffectiveTypesLayout.removeAllViews();
        binding.defenderNotEffectiveTypesAdvancedLayout.removeAllViews();
        binding.defenderNotVeryEffectiveTypesAdvancedLayout.removeAllViews();
        binding.defenderNoEffectTypesLayout.removeAllViews();
        binding.defenderNoEffectTypesAdvancedLayout.removeAllViews();

        binding.defenderVeryEffectiveLayout.setVisibility(View.GONE);
        binding.defenderEffectiveLayout.setVisibility(View.GONE);
        binding.defenderNormalLayout.setVisibility(View.GONE);
        binding.defenderNotEffectiveLayout.setVisibility(View.GONE);
        binding.defenderNotVeryEffectiveLayout.setVisibility(View.GONE);
        binding.defenderNoEffectLayout.setVisibility(View.GONE);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}