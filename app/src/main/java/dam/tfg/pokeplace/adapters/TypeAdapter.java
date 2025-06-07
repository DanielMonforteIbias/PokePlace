package dam.tfg.pokeplace.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.interfaces.OnTypeSelectedListener;
import dam.tfg.pokeplace.models.Type;
import dam.tfg.pokeplace.utils.StringFormatter;
import dam.tfg.pokeplace.utils.ViewUtils;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.TypeViewHolder> {
    private final List<Type> types;
    private final OnTypeSelectedListener listener;

    public TypeAdapter(List<Type> types, Context context, OnTypeSelectedListener listener) {
        this.types =(types!=null) ? types : new ArrayList<>(); //Si la lista no es nula la guardamos, si es nula la inicialiamos para evitar NullPointers
        this.listener = listener;
    }

    public static class TypeViewHolder extends RecyclerView.ViewHolder {
        private final TextView typeName;
        public TypeViewHolder(View itemView) {
            super(itemView);
            typeName=itemView.findViewById(R.id.txtTypeName);
        }
    }

    @NonNull
    @Override
    public TypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type, parent, false);
        return new TypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TypeViewHolder holder, int position) {
        Type type = types.get(position);
        ViewUtils.setPokemonTypeBackground(holder.itemView.getContext(),holder.itemView,type.getName(),50,0);
        holder.typeName.setText(StringFormatter.formatName(type.getName()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTypeSelected(type);
            }
        });
    }

    @Override
    public int getItemCount() {
        return types.size();
    }
}
