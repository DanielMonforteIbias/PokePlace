package dam.tfg.pokeplace.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.models.Move;
import dam.tfg.pokeplace.utils.StringFormatter;

public class DescriptionAdapter extends RecyclerView.Adapter<DescriptionAdapter.ViewHolder>{
    private List<Pair<String,String>> descriptions;
    public DescriptionAdapter(List<Pair<String,String>>descriptions){
        this.descriptions =descriptions;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView version;
        private final TextView descriptionText;
        public ViewHolder(View itemView) {
            super(itemView);
            version = itemView.findViewById(R.id.txtDescriptionVersion);
            descriptionText =itemView.findViewById(R.id.txtDescriptionText);
        }

    }

    @NonNull
    @Override
    public DescriptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_description, parent, false);
        return new DescriptionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DescriptionAdapter.ViewHolder holder, int position) {
        Pair<String,String>description= descriptions.get(position);
        String descriptionText=StringFormatter.formatName(description.second);
        holder.version.setText(StringFormatter.formatName(description.first));
        holder.descriptionText.setText(StringFormatter.removeLineBreaks(descriptionText));
    }

    @Override
    public int getItemCount() {
        return descriptions.size();
    }
}
