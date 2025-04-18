package dam.tfg.pokeplace.ui.mainActivityFragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.adapters.TeamsAdapter;
import dam.tfg.pokeplace.databinding.FragmentTeamsBinding;
import dam.tfg.pokeplace.models.Team;

public class TeamsFragment extends Fragment {
    private FragmentTeamsBinding binding;
    private TeamsAdapter adapter;
    private List<Team> teams=new ArrayList<>();
    private int teamSizeLimit;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTeamsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        adapter=new TeamsAdapter(teams);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        teamSizeLimit = getResources().getInteger(R.integer.teams_limit);
        binding.teamsList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.teamsList.setAdapter(adapter);
        binding.imgBtnAddTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(teams.size()<teamSizeLimit) displayAddTeamDialog();
                else Toast.makeText(getContext(),getResources().getText(R.string.limite_equipos),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void displayAddTeamDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_team, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        EditText input = dialogView.findViewById(R.id.editTextTeamName);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelTeam);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button btnAccept=dialogView.findViewById(R.id.btnAcceptTeam);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String teamName = input.getText().toString().trim();
                if (!teamName.isEmpty()) {
                    teams.add(new Team(teamName));
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), getResources().getText(R.string.nombre_vacio), Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}