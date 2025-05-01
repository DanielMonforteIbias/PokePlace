package dam.tfg.pokeplace.ui.mainActivityFragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import dam.tfg.pokeplace.MainActivity;
import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.TeamDetailsActivity;
import dam.tfg.pokeplace.adapters.TeamsAdapter;
import dam.tfg.pokeplace.data.dao.TeamDAO;
import dam.tfg.pokeplace.data.dao.TeamPokemonDAO;
import dam.tfg.pokeplace.data.dao.UserDAO;
import dam.tfg.pokeplace.data.service.TeamService;
import dam.tfg.pokeplace.databinding.FragmentTeamsBinding;
import dam.tfg.pokeplace.interfaces.OnTeamClickListener;
import dam.tfg.pokeplace.models.Team;
import dam.tfg.pokeplace.utils.ToastUtil;

public class TeamsFragment extends Fragment {
    private FragmentTeamsBinding binding;
    private TeamsAdapter adapter;
    private List<Team> teams;
    private int teamSizeLimit;

    private TeamService teamService;
    private String userId;
    private UserDAO userDAO;

    private ActivityResultLauncher<Intent>teamDetailsActivityLauncher;
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTeamsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        userDAO=new UserDAO(getContext());
        teamService=new TeamService(new TeamDAO(getContext()),new TeamPokemonDAO(getContext()));
        userId=userDAO.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).getUserId();
        teams=teamService.getAllTeams(userId);
        teamDetailsActivityLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        updateData();
                        updateUI();
                    }
                });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter=new TeamsAdapter(teams, new OnTeamClickListener() {
            @Override
            public void onTeamClick(Team team) {
                Context context=getContext();
                Intent intent=new Intent(context, TeamDetailsActivity.class);
                intent.putExtra("Team",team);
                teamDetailsActivityLauncher.launch(intent);
            }
        });
        updateUI();
        teamSizeLimit = getResources().getInteger(R.integer.teams_limit);
        binding.teamsList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.teamsList.setAdapter(adapter);
        binding.btnAddTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(teams.size()<teamSizeLimit) displayAddTeamDialog();
                else ToastUtil.showToast(getContext(),getResources().getText(R.string.teams_limit).toString());
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
                    Team newTeam=new Team(userId,teamService.getNewTeamId(userId),teamName);
                    teamService.addTeam(newTeam);
                    teams.add(newTeam);
                    updateUI();
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    ToastUtil.showToast(getContext(), getResources().getText(R.string.error_empty_name).toString());
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
    private void updateUI(){
        if(teamService.getAllTeams(userId).isEmpty())binding.txtNoTeams.setVisibility(View.VISIBLE);
        else binding.txtNoTeams.setVisibility(View.GONE);
    }
    private void updateData(){
        teams=teamService.getAllTeams(userId);
        adapter.updateTeams(teams); //Notificamos al adaptador para tener la nueva lista
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}