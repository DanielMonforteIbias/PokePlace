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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.models.Type;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.TypeViewHolder> {
    private List<Type> types;
    private Context context;
    private OnTypeSelectedListener listener;

    public TypeAdapter(List<Type> types, Context context, OnTypeSelectedListener listener) {
        this.types = types;
        this.context=context;
        this.listener = listener;
    }

    public static class TypeViewHolder extends RecyclerView.ViewHolder {
        private final TextView typeName;
        public TypeViewHolder(View itemView) {
            super(itemView);
            typeName=itemView.findViewById(R.id.txtTypeName);
        }
    }

    @Override
    public TypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type, parent, false);
        return new TypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TypeViewHolder holder, int position) {
        Type type = types.get(position);
        /*Cogemos el color del tipo de colors.
        Se ha intentado obtener dinamicamente de varias formas, con la libreria Palette o manualmente recorriendo los bitmaps,
        pero como son imagenes pequeñas algunas tienen resultados incoherentes y no hay otro modo de que sea el color que queremos*/
        int colorId=context.getResources().getIdentifier(type.getName(), "color", context.getPackageName());
        int color=0;
        if (colorId != 0) color = ContextCompat.getColor(context, colorId);
        else color=ContextCompat.getColor(context, R.color.gray_500);
        GradientDrawable background = new GradientDrawable(); //Background para dar color y borde redondeado
        background.setColor(color);
        background.setCornerRadius(50f);
        holder.itemView.setBackground(background);
        holder.typeName.setText(type.getName());
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

    public interface OnTypeSelectedListener {
        void onTypeSelected(Type type);
    }
}
