package dam.tfg.pokeplace.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.models.Type;
import dam.tfg.pokeplace.utils.StringFormatter;

public class IconAdapter extends BaseAdapter {
    private List<String> iconNames;
    private Context context;

    public IconAdapter(List<String>iconNames,Context context) {
        this.iconNames=iconNames;
        this.context=context;
    }

    @Override
    public int getCount() {
        return iconNames.size();
    }

    @Override
    public Object getItem(int position) {
        return iconNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,200 ));
            imageView.setPadding(10,10,10,10);
        }
        else imageView = (ImageView) convertView;
        String iconName = iconNames.get(position);
        int resId = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
        imageView.setImageResource(resId);
        return imageView;
    }
}