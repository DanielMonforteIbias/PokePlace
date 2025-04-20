package dam.tfg.pokeplace;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import dam.tfg.pokeplace.adapters.TeamPokemonAdapter;
import dam.tfg.pokeplace.data.dao.TeamDAO;
import dam.tfg.pokeplace.data.dao.TeamPokemonDAO;
import dam.tfg.pokeplace.data.dao.UserDAO;
import dam.tfg.pokeplace.data.service.TeamService;
import dam.tfg.pokeplace.databinding.ActivityTeamDetailsBinding;
import dam.tfg.pokeplace.databinding.FragmentTeamsBinding;
import dam.tfg.pokeplace.models.Team;
import dam.tfg.pokeplace.models.TeamPokemon;
import dam.tfg.pokeplace.models.User;
import dam.tfg.pokeplace.utils.ToastUtil;

public class TeamDetailsActivity extends AppCompatActivity {
    private Team team;
    private ActivityTeamDetailsBinding binding;
    private TeamPokemonAdapter adapter;

    private UserDAO userDAO;
    public static User user;
    private TeamService teamService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding= ActivityTeamDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setSupportActionBar(binding.toolbarTeamDetails);

        userDAO=new UserDAO(this);
        teamService=new TeamService(new TeamDAO(getApplicationContext()),new TeamPokemonDAO(getApplicationContext()));
        user=userDAO.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Intent intent=getIntent();
        team=intent.getParcelableExtra("Team");
        adapter=new TeamPokemonAdapter(team.getTeamMembers());
        binding.teamPokemonList.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        binding.teamPokemonList.setAdapter(adapter);
        updateUI();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_team_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_change_name){
            displayModifyTeamDialog();
        }else if (id==R.id.action_remove_team){
            displayRemoveTeamDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayModifyTeamDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_team, null); //Reusamos el dialogo de añadir
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        EditText input = dialogView.findViewById(R.id.editTextTeamName);
        input.setText(team.getName());
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
                    Team newTeam=new Team(team.getUserId(),team.getTeamId(),teamName);
                    teamService.changeTeamName(newTeam);
                    team.setName(teamName); //Cambiamos el nombre del team actual de la actividad
                    updateUI();
                    dialog.dismiss();
                } else {
                    ToastUtil.showToast(getApplicationContext(), getText(R.string.nombre_vacio).toString());
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
    private void displayRemoveTeamDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_remove_team, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        Button btnCancel = dialogView.findViewById(R.id.btnCancelRemoveTeam);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button btnAccept=dialogView.findViewById(R.id.btnAcceptRemoveTeam);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamService.removeTeam(user.getUserId(),team.getTeamId());
                ToastUtil.showToast(getApplicationContext(),team.getName()+" "+getString(R.string.team_removed));
                dialog.dismiss();
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void updateUI(){
        binding.toolbarTeamDetails.setTitle(team.getName());
        binding.txtTeamNameDetails.setText(team.getName());
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }
}