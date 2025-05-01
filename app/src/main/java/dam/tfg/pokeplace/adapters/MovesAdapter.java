package dam.tfg.pokeplace.adapters;

import android.content.Context;
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
import dam.tfg.pokeplace.models.Pokemon;
import dam.tfg.pokeplace.utils.StringFormatter;
import dam.tfg.pokeplace.utils.ViewUtils;

public class MovesAdapter extends RecyclerView.Adapter<MovesAdapter.ViewHolder>{
    private List<Move> movesList;
    public MovesAdapter(List<Move>movesList){
        this.movesList=movesList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView typeSprite;
        private final TextView name;
        private final TextView description;
        private final TextView category;
        private final TextView power;
        private final TextView accuracy;
        private final TextView pp;
        public ViewHolder(View itemView) {
            super(itemView);
            typeSprite = itemView.findViewById(R.id.imgMoveType);
            name =itemView.findViewById(R.id.txtMoveName);
            description =itemView.findViewById(R.id.txtMoveDescription);
            category =itemView.findViewById(R.id.txtCategoryValue);
            power=itemView.findViewById(R.id.txtPowerValue);
            accuracy =itemView.findViewById(R.id.txtAccuracyValue);
            pp =itemView.findViewById(R.id.txtPPValue);
        }

        public ImageView getTypeSprite() {
            return typeSprite;
        }
        public TextView getName(){return name;}
    }

    @NonNull
    @Override
    public MovesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_move, parent, false);
        return new MovesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovesAdapter.ViewHolder holder, int position) {
        Move move=movesList.get(position);
        Context context=holder.itemView.getContext();
        if(move.getName()!=null){ //Solo si el movimiento esta relleno, es decir, cuando tiene nombre
            if(move.getType()!=null) {
                Glide.with(context).load(move.getType().getSprite()).into(holder.typeSprite);
                ViewUtils.setPokemonTypeBackground(context,holder.itemView,move.getType().getName(),20,12);
            }
            holder.name.setText(StringFormatter.formatName(move.getName()));
            holder.description.setText(move.getDescription());
            int categoryResId = context.getResources().getIdentifier(move.getDamageClass(), "string", context.getPackageName()); //Obtenemos el id del nombre de la categoria de strings
            holder.category.setText(context.getString(categoryResId));
            holder.power.setText(String.valueOf(move.getPower()));
            holder.accuracy.setText(String.valueOf(move.getAccuracy()));
            holder.pp.setText(String.valueOf(move.getPp()));
        }
    }

    @Override
    public int getItemCount() {
        return movesList.size();
    }
}
