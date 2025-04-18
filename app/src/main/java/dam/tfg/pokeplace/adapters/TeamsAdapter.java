package dam.tfg.pokeplace.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.models.Pokemon;
import dam.tfg.pokeplace.models.Team;

public class TeamsAdapter extends RecyclerView.Adapter<TeamsAdapter.ViewHolder> {
    private List<Team> teamsList;

    public TeamsAdapter(List<Team>teamsList){
        this.teamsList=teamsList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtName;
        public ViewHolder(View itemView) {
            super(itemView);
            txtName=itemView.findViewById(R.id.txtTeamName);
        }
        public TextView getTxtName() {return txtName;}
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team, parent, false);
        return new TeamsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Team team= teamsList.get(position);
        holder.txtName.setText(team.getName());
    }

    @Override
    public int getItemCount() {
        return teamsList.size();
    }
}
