package dam.tfg.pokeplace.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.interfaces.OnTeamClickListener;
import dam.tfg.pokeplace.models.Team;

public class TeamsAdapter extends RecyclerView.Adapter<TeamsAdapter.ViewHolder> {
    private List<Team> teamsList;
    private OnTeamClickListener onClickListener;

    public TeamsAdapter(List<Team>teamsList, OnTeamClickListener listener){
        this.teamsList=teamsList;
        this.onClickListener = listener;
    }
    public void updateTeams(List<Team> newTeams) {
        this.teamsList = newTeams;
        this.notifyDataSetChanged();
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
