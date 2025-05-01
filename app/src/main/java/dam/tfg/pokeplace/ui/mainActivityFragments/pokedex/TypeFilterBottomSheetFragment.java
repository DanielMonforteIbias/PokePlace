package dam.tfg.pokeplace.ui.mainActivityFragments.pokedex;

import android.content.Context;
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
import dam.tfg.pokeplace.interfaces.OnTypeSelectedListener;
import dam.tfg.pokeplace.models.Type;

public class TypeFilterBottomSheetFragment extends BottomSheetDialogFragment {
    private BottomSheetTypesBinding binding;
    private TypeAdapter adapter;
    private ArrayList<Type> types;
    private OnTypeSelectedListener listener;
    public TypeFilterBottomSheetFragment(){//Constructor vacio por defecto. Util por ejemplo si se reconstruye la app con uno abierto
        types=new ArrayList<>();
    }

    public static TypeFilterBottomSheetFragment newInstance(List<Type> types) {
        TypeFilterBottomSheetFragment fragment = new TypeFilterBottomSheetFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("Types", new ArrayList<>(types));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = BottomSheetTypesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) types = savedInstanceState.getParcelableArrayList("Types");
        else if (getArguments() != null) types = getArguments().getParcelableArrayList("Types");
        binding.typesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TypeAdapter(types, getContext(), new OnTypeSelectedListener() {
            @Override
            public void onTypeSelected(Type type) {
                if(listener!=null) listener.onTypeSelected(type);
                dismiss();
            }
        });
        binding.typesRecyclerView.setAdapter(adapter);
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("Types", types);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //De esta forma aseguramos que el fragment siga teniendo el listener aunque se reconstruya, por ejemplo al rotarlo
        if (getParentFragment() instanceof OnTypeSelectedListener) {
            listener = (OnTypeSelectedListener) getParentFragment();
        } else if (context instanceof OnTypeSelectedListener) {
            listener = (OnTypeSelectedListener) context;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}