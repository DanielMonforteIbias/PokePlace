package dam.tfg.pokeplace.ui.mainActivityFragments.pokedex;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.adapters.TypeAdapter;
import dam.tfg.pokeplace.databinding.BottomSheetTypesBinding;
import dam.tfg.pokeplace.models.Type;

public class TypeFilterBottomSheetFragment extends BottomSheetDialogFragment {
    private BottomSheetTypesBinding binding;
    private TypeAdapter adapter;
    private List<Type> types;
    private TypeAdapter.OnTypeSelectedListener listener;
    public TypeFilterBottomSheetFragment(){
        types=new ArrayList<>();
    } //Constructor vacio por defecto. Util por ejemplo si se reconstruye la app con uno abierto
    public TypeFilterBottomSheetFragment(List<Type> types, TypeAdapter.OnTypeSelectedListener listener) {
        this.types = types;
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = BottomSheetTypesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.typesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TypeAdapter(types, getContext(), new TypeAdapter.OnTypeSelectedListener() {
            @Override
            public void onTypeSelected(Type type) {
                if(listener!=null) listener.onTypeSelected(type);
                dismiss();
            }
        });
        binding.typesRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}