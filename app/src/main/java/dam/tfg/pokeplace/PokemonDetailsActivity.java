package dam.tfg.pokeplace;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.tfg.pokeplace.adapters.TeamsAdapter;
import dam.tfg.pokeplace.data.dao.TeamDAO;
import dam.tfg.pokeplace.data.dao.TeamPokemonDAO;
import dam.tfg.pokeplace.data.dao.UserDAO;
import dam.tfg.pokeplace.data.service.TeamService;
import dam.tfg.pokeplace.databinding.ActivityPokemonDetailsBinding;
import dam.tfg.pokeplace.interfaces.DialogConfigurator;
import dam.tfg.pokeplace.interfaces.OnTeamClickListener;
import dam.tfg.pokeplace.models.Pokemon;
import dam.tfg.pokeplace.models.Team;
import dam.tfg.pokeplace.models.TeamPokemon;
import dam.tfg.pokeplace.models.User;
import dam.tfg.pokeplace.ui.detailsActivityFragments.PokemonViewModel;
import dam.tfg.pokeplace.utils.BaseActivity;
import dam.tfg.pokeplace.utils.StringFormatter;
import dam.tfg.pokeplace.utils.ToastUtil;

public class PokemonDetailsActivity extends BaseActivity {
    private ActivityPokemonDetailsBinding binding;
    private PokemonViewModel viewModel;
    private Pokemon pokemon=new Pokemon();

    private int teamSizeLimit;

    private UserDAO userDAO;
    public static User user;
    private TeamService teamService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPokemonDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.pokemonDetailsLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setSupportActionBar(binding.toolbarPokemonDetails);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_info, R.id.navigation_stats, R.id.navigation_moves).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_pokemon_details);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        userDAO=new UserDAO(this);
        teamService=new TeamService(new TeamDAO(getApplicationContext()),new TeamPokemonDAO(getApplicationContext()));
        user=userDAO.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
        teamSizeLimit= getResources().getInteger(R.integer.team_size_limit);

        Intent intent=getIntent();
        pokemon=intent.getParcelableExtra("Pokemon");
        viewModel = new ViewModelProvider(this).get(PokemonViewModel.class);
        viewModel.setPokemon(pokemon);

        updateFavoriteIcon(); //Actualizamos el icono de favorito cuando tenemos el user y el Pokemon, asegurando que este marcado o desmarcado segun toque
    }

    private void displayAddPokemonToTeamDialog(List<Team>userTeams){
        showCustomDialog(R.layout.dialog_add_pokemon_to_team, false, new DialogConfigurator() {
            @Override
            public void configure(AlertDialog dialog, View dialogView) {
                Button btnCancel = dialogView.findViewById(R.id.btnCancelAddToTeam);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                RecyclerView recyclerView = dialogView.findViewById(R.id.availableTeamsList);
                recyclerView.setLayoutManager(new LinearLayoutManager(PokemonDetailsActivity.this));
                recyclerView.setAdapter(new TeamsAdapter(userTeams, new OnTeamClickListener() {
                    @Override
                    public void onTeamClick(Team team) {
                        if(teamService.getTeamSize(user.getUserId(),team.getTeamId())<teamSizeLimit){
                            if(addPokemonToTeam(team)) ToastUtil.showToast(getApplicationContext(), StringFormatter.formatName(pokemon.getName()) +" "+getResources().getText(R.string.added_to)+" "+team.getName());
                            else ToastUtil.showToast(getApplicationContext(),getString(R.string.error_add_to_team));
                            dialog.dismiss();
                        }
                        else ToastUtil.showToast(getApplicationContext(),getString(R.string.team_size_limit));
                    }
                }));
            }
        });
    }

    private boolean addPokemonToTeam(Team team){
        Integer currentSpriteIndex = viewModel.getCurrentSpriteIndex().getValue();
        if (currentSpriteIndex != null) {
            TeamPokemon teamPokemon=new TeamPokemon();
            teamPokemon.setUserId(user.getUserId());
            teamPokemon.setTeamId(team.getTeamId());
            teamPokemon.setPokedexNumber(pokemon.getPokedexNumber());
            teamPokemon.setCustomName(pokemon.getName()); //El nombre por defecto es el mismo nombre del Pokemon
            teamPokemon.setCustomSprite(pokemon.getSprites().get(currentSpriteIndex)); //El sprite por defecto es el que esté viendo el usuario
            teamService.addTeamPokemon(teamPokemon);
            return true;
        } else {
            return false;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pokemon_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_mark_as_favorite);
        if(pokemon!=null  && user!=null){
            if (pokemon.getPokedexNumber().equals(user.getFavPokemon())) item.setIcon(R.drawable.staron);
            else item.setIcon(R.drawable.staroff);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void updateFavoriteIcon() {
        invalidateOptionsMenu();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_to_team){
            List<Team> userTeams=teamService.getAllTeams(user.getUserId());
            if(!userTeams.isEmpty()) displayAddPokemonToTeamDialog(userTeams);
            else ToastUtil.showToast(PokemonDetailsActivity.this,getString(R.string.error_no_teams));
        }
        else if(id==R.id.action_mark_as_favorite){
            if(pokemon.getPokedexNumber().equals(user.getFavPokemon())){
                user.setFavPokemon(null);
                item.setIcon(R.drawable.staroff);
                ToastUtil.showToast(PokemonDetailsActivity.this,getString(R.string.removed_from_favorite,StringFormatter.formatName(pokemon.getName())));
            }
            else{
                user.setFavPokemon(pokemon.getPokedexNumber());
                item.setIcon(R.drawable.staron);
                ToastUtil.showToast(PokemonDetailsActivity.this,getString(R.string.marked_as_fav,StringFormatter.formatName(pokemon.getName())));
            }
            userDAO.updateUser(user); //Ponemos el cambio en la base de datos
        }
        return super.onOptionsItemSelected(item);
    }
}