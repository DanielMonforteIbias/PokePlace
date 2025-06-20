package dam.tfg.pokeplace.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.interfaces.OnTeamClickListener;
import dam.tfg.pokeplace.models.Team;
import dam.tfg.pokeplace.models.TeamPokemon;

public class TeamsAdapter extends RecyclerView.Adapter<TeamsAdapter.ViewHolder> {
    private List<Team> teamsList;
    private final OnTeamClickListener onClickListener;

    public TeamsAdapter(List<Team> teamsList, OnTeamClickListener listener) {
        this.teamsList = teamsList;
        this.onClickListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateTeams(List<Team> newTeams) {
        this.teamsList = newTeams;
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtName;
        private GridLayout teamMembersLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtTeamName);
            teamMembersLayout = itemView.findViewById(R.id.teamMembersLayout);
        }

        public TextView getTxtName() {
            return txtName;
        }

        public GridLayout getTeamMembersLayout() {
            return teamMembersLayout;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team, parent, false);
        return new TeamsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Team team = teamsList.get(position);
        holder.txtName.setText(team.getName());
        holder.teamMembersLayout.removeAllViews(); //Limpiamos para evitar duplicados
        holder.itemView.post(() -> { //post hace que se ejecute lo definido cuando la vista ya ha sido medida, necesario para que getWidth y getHeight no sean 0
            //Controlamos errores
            if (holder.getBindingAdapterPosition() == RecyclerView.NO_POSITION) return;
            Context context = holder.itemView.getContext();
            if (!(context instanceof Activity) || ((Activity) context).isDestroyed()) return;

            int totalWidth = holder.teamMembersLayout.getWidth();
            int totalHeight = holder.teamMembersLayout.getHeight();
            int totalImagesPerRow = 3;
            int totalRows = 2;
            int width = totalWidth / totalImagesPerRow;
            int height = totalHeight / totalRows;
            for (TeamPokemon member : team.getTeamMembers()) {
                ImageView imageView = new ImageView(holder.itemView.getContext());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = width;
                params.height = height;
                imageView.setLayoutParams(params);
                Glide.with(holder.itemView).load(member.getCustomSprite()).into(imageView);
                holder.teamMembersLayout.addView(imageView);
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onTeamClick(team);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teamsList.size();
    }
}
