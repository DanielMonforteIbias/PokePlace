package dam.tfg.pokeplace.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.models.Type;
import dam.tfg.pokeplace.utils.StringFormatter;

public class TypeAdapter extends ArrayAdapter<Type> {
    private Context context;
    private List<Type> types;

    public TypeAdapter(List<Type> types,Context context) {
        super(context,0,types);
        this.context=context;
        this.types=types;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) { //Vista normal de lo seleccionado en el spinner
        return getItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) { //Vista con el spinner desplegado
        return getItemView(position, convertView, parent);
    }
    private View getItemView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_type, parent, false);
        Type type = types.get(position);
        ImageView imageView = view.findViewById(R.id.imgType);
        TextView textView=view.findViewById(R.id.txtTypeName);
        Glide.with(context).load(type.getSprite()).into(imageView);
        textView.setText(StringFormatter.formatName(type.getName()));
        return view;
    }
}
