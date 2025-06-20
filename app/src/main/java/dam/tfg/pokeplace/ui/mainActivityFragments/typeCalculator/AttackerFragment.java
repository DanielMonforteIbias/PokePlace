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
import dam.tfg.pokeplace.databinding.FragmentAttackerBinding;
import dam.tfg.pokeplace.models.Type;
import dam.tfg.pokeplace.utils.StringFormatter;

public class AttackerFragment extends Fragment {
    private FragmentAttackerBinding binding;
    private Data data;
    private TypeSpinnerAdapter adapter;
    private boolean advancedEnabled=false;
    private Type currentType=null;

    public AttackerFragment() {
        super(R.layout.fragment_attacker);
    }
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAttackerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        data=Data.getInstance();
        adapter=new TypeSpinnerAdapter(data.getTypeList(),getContext());
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState!=null) currentType=savedInstanceState.getParcelable("currentType");
        binding.spinnerAttackerTypes.setAdapter(adapter);
        binding.spinnerAttackerTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
    private void updateTypeLayouts(Type type){
        resetViews();//Reseteamos todas las vistas
        String typeName=StringFormatter.formatName(type.getName()); //Hacemos esto en vez de setName al nombre de type para mantener los datos logicos y que el nombre formaetado solo actue en la interfaz, no en la logica
        if(!advancedEnabled){
            addTypeSpritesToLayout(binding.attackerEffectiveLayout,binding.attackerEffectiveTypesLayout, binding.attackerEffective,getResources().getString(R.string.attacker_x2,typeName),type.getDoubleDamageTo());
            addTypeSpritesToLayout(binding.attackerNormalLayout,binding.attackerNormalTypesLayout, binding.attackerNormal,getResources().getString(R.string.attacker_x1,typeName),data.getNormalDamageTo(type));
            addTypeSpritesToLayout(binding.attackerNotEffectiveLayout,binding.attackerNotEffectiveTypesLayout, binding.attackerNotEffective,getResources().getString(R.string.attacker_x0_5,typeName),type.getHalfDamageTo());
            addTypeSpritesToLayout(binding.attackerNoEffectLayout,binding.attackerNoEffectTypesLayout, binding.attackerNoEffect,getResources().getString(R.string.attacker_x0,typeName),type.getNoDamageTo());
        }else{
            String mode = "attacker";
            addTypeCombinationRowsToLayout(binding.attackerVeryEffectiveLayout,binding.attackerVeryEffectiveTypesAdvancedLayout,binding.attackerVeryEffective,getResources().getString(R.string.attacker_x4,typeName),data.getTypeCombinationsWithMultiplier(type, mode,4));
            addTypeCombinationRowsToLayout(binding.attackerEffectiveLayout,binding.attackerEffectiveTypesAdvancedLayout,binding.attackerEffective,getResources().getString(R.string.attacker_x2,typeName),data.getTypeCombinationsWithMultiplier(type, mode,2));
            addTypeCombinationRowsToLayout(binding.attackerNormalLayout,binding.attackerNormalTypesAdvancedLayout,binding.attackerNormal,getResources().getString(R.string.attacker_x1,typeName),data.getTypeCombinationsWithMultiplier(type, mode,1));
            addTypeCombinationRowsToLayout(binding.attackerNotEffectiveLayout,binding.attackerNotEffectiveTypesAdvancedLayout,binding.attackerNotEffective,getResources().getString(R.string.attacker_x0_5,typeName),data.getTypeCombinationsWithMultiplier(type, mode,0.5));
            addTypeCombinationRowsToLayout(binding.attackerNotVeryEffectiveLayout,binding.attackerNotVeryEffectiveTypesAdvancedLayout,binding.attackerNotVeryEffective,getResources().getString(R.string.attacker_x0_25,typeName),data.getTypeCombinationsWithMultiplier(type, mode,0.25));
            addTypeCombinationRowsToLayout(binding.attackerNoEffectLayout,binding.attackerNoEffectTypesAdvancedLayout,binding.attackerNoEffect,getResources().getString(R.string.attacker_x0,typeName),data.getTypeCombinationsWithMultiplier(type, mode,0));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("currentType",currentType);
    }

    private void addTypeSpritesToLayout(ViewGroup layout, ViewGroup typesLayout, TextView textView, String message, List<String> typeNames) {
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
    private void addTypeCombinationRowsToLayout(ViewGroup layout,ViewGroup typesLayout, TextView textView, String message,List<Pair<String,String>> combinations) {
        if(!combinations.isEmpty()) {
            layout.setVisibility(View.VISIBLE);
            textView.setText(message);
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics());
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
        binding.attackerVeryEffectiveTypesAdvancedLayout.removeAllViews();
        binding.attackerEffectiveTypesLayout.removeAllViews();
        binding.attackerEffectiveTypesAdvancedLayout.removeAllViews();
        binding.attackerNormalTypesLayout.removeAllViews();
        binding.attackerNormalTypesAdvancedLayout.removeAllViews();
        binding.attackerNotEffectiveTypesLayout.removeAllViews();
        binding.attackerNotEffectiveTypesAdvancedLayout.removeAllViews();
        binding.attackerNotVeryEffectiveTypesAdvancedLayout.removeAllViews();
        binding.attackerNoEffectTypesLayout.removeAllViews();
        binding.attackerNoEffectTypesAdvancedLayout.removeAllViews();

        binding.attackerVeryEffectiveLayout.setVisibility(View.GONE);
        binding.attackerEffectiveLayout.setVisibility(View.GONE);
        binding.attackerNormalLayout.setVisibility(View.GONE);
        binding.attackerNotEffectiveLayout.setVisibility(View.GONE);
        binding.attackerNotVeryEffectiveLayout.setVisibility(View.GONE);
        binding.attackerNoEffectLayout.setVisibility(View.GONE);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}