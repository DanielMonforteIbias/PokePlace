package dam.tfg.pokeplace.ui.mainActivityFragments;

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

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.ui.activities.TeamDetailsActivity;
import dam.tfg.pokeplace.adapters.TeamsAdapter;
import dam.tfg.pokeplace.data.dao.UserDAO;
import dam.tfg.pokeplace.data.service.TeamService;
import dam.tfg.pokeplace.databinding.FragmentTeamsBinding;
import dam.tfg.pokeplace.interfaces.OnTeamClickListener;
import dam.tfg.pokeplace.models.Team;
import dam.tfg.pokeplace.sync.UserSync;
import dam.tfg.pokeplace.ui.activities.BaseActivity;
import dam.tfg.pokeplace.utils.ToastUtil;

public class TeamsFragment extends Fragment {
    private FragmentTeamsBinding binding;
    private TeamsAdapter adapter;
    private List<Team> teams;
    private int teamSizeLimit;

    private TeamService teamService;
    private String userId;
    private UserDAO userDAO;
    private UserSync userSync;

    private ActivityResultLauncher<Intent>teamDetailsActivityLauncher;
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTeamsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        userDAO=new UserDAO(getContext());
        userSync=new UserSync(getContext());
        teamService=new TeamService(getContext());
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) userId=userDAO.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).getUserId();
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
        if (getActivity() != null) {
            ((BaseActivity)getActivity()).showCustomDialog(R.layout.dialog_create_team, false, (dialog, dialogView) -> {
                EditText input = dialogView.findViewById(R.id.editTextTeamName);
                Button btnCancel = dialogView.findViewById(R.id.btnCancelTeam);
                Button btnAccept = dialogView.findViewById(R.id.btnAcceptTeam);
                btnCancel.setOnClickListener(v -> dialog.dismiss());
                btnAccept.setOnClickListener(v -> {
                    String teamName = input.getText().toString().trim();
                    if (!teamName.isEmpty()) {
                        Team newTeam = new Team(userId, teamService.getNewTeamId(), teamName);
                        teamService.addTeam(newTeam);
                        userSync.addTeam(newTeam);
                        teams.add(newTeam);
                        updateUI();
                        adapter.notifyItemInserted(teams.size()-1);
                        dialog.dismiss();
                    } else {
                        ToastUtil.showToast(getContext(), getString(R.string.error_empty_name));
                    }
                });
            });
        }
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