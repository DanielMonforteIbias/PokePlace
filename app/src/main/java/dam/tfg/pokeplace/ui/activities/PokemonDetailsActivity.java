package dam.tfg.pokeplace.ui.activities;

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

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.adapters.TeamsAdapter;
import dam.tfg.pokeplace.data.dao.UserDAO;
import dam.tfg.pokeplace.data.service.TeamService;
import dam.tfg.pokeplace.databinding.ActivityPokemonDetailsBinding;
import dam.tfg.pokeplace.interfaces.DialogConfigurator;
import dam.tfg.pokeplace.interfaces.OnTeamClickListener;
import dam.tfg.pokeplace.models.Pokemon;
import dam.tfg.pokeplace.models.Team;
import dam.tfg.pokeplace.models.TeamPokemon;
import dam.tfg.pokeplace.models.User;
import dam.tfg.pokeplace.sync.UserSync;
import dam.tfg.pokeplace.ui.detailsActivityFragments.PokemonViewModel;
import dam.tfg.pokeplace.utils.StringFormatter;

public class PokemonDetailsActivity extends BaseActivity {
    private ActivityPokemonDetailsBinding binding;
    private PokemonViewModel viewModel;
    private Pokemon pokemon=new Pokemon();

    private int teamSizeLimit;

    private UserDAO userDAO;
    private UserSync userSync;
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
        userSync=new UserSync(getApplicationContext());
        teamService=new TeamService(getApplicationContext());
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)user=userDAO.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
        teamSizeLimit= getResources().getInteger(R.integer.team_size_limit);

        Intent intent=getIntent();
        pokemon=intent.getParcelableExtra("Pokemon");
        viewModel = new ViewModelProvider(this).get(PokemonViewModel.class);
        viewModel.setPokemon(pokemon);

        updateFavoriteIcon(); //Actualizamos el icono de favorito cuando tenemos el user y el Pokemon, asegurando que este marcado o desmarcado segun toque
    }

    private void displayAddPokemonToTeamDialog(List<Team>userTeams){
        showCustomDialog(R.layout.dialog_add_pokemon_to_team_list, false, new DialogConfigurator() {
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
                        if(teamService.getTeamSize(team.getTeamId())<teamSizeLimit){
                            if(addPokemonToTeam(team)) showToast(StringFormatter.formatName(pokemon.getName()) +" "+getResources().getText(R.string.added_to)+" "+team.getName());
                            else showToast(getString(R.string.error_add_to_team));
                            dialog.dismiss();
                        }
                        else showToast(getString(R.string.team_size_limit));
                    }
                }));
            }
        });
    }

    private boolean addPokemonToTeam(Team team){
        Integer currentSpriteIndex = viewModel.getCurrentSpriteIndex().getValue();
        if (currentSpriteIndex != null) {
            TeamPokemon teamPokemon=new TeamPokemon(teamService.generateNewPokemonId(),team.getTeamId(),pokemon.getName(),pokemon.getSprites().get(currentSpriteIndex));
            teamPokemon.setPokedexNumber(pokemon.getPokedexNumber());
            teamService.addTeamPokemon(teamPokemon);
            userSync.addTeamPokemon(teamPokemon);
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
            if(user.getFavPokemon()!=null && pokemon.getPokedexNumber()!=null){
                if (Integer.parseInt(pokemon.getPokedexNumber())==Integer.parseInt(user.getFavPokemon())) item.setIcon(R.drawable.staron);
            }
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
            if(user.getUserId()!=null){
                List<Team> userTeams=teamService.getAllTeams(user.getUserId());
                if(!userTeams.isEmpty()) displayAddPokemonToTeamDialog(userTeams);
                else showToast(getString(R.string.error_no_teams));
            }
            else showToast(getString(R.string.user_error));
        }
        else if(id==R.id.action_mark_as_favorite){
            boolean isFavorite=false;
            if (user.getFavPokemon()!= null && pokemon.getPokedexNumber() != null) {
                try { //Necesario un try por si hay errores en el Integer.parseInt
                    isFavorite = Integer.parseInt(user.getFavPokemon()) == Integer.parseInt(pokemon.getPokedexNumber());
                } catch (NumberFormatException e) {
                    isFavorite = false;
                }
            }
            if (isFavorite) {
                user.setFavPokemon(null);
                item.setIcon(R.drawable.staroff);
                showToast(getString(R.string.removed_from_favorite, StringFormatter.formatName(pokemon.getName())));
            } else {
                user.setFavPokemon(pokemon.getPokedexNumber());
                item.setIcon(R.drawable.staron);
                showToast(getString(R.string.marked_as_fav, StringFormatter.formatName(pokemon.getName())));
            }
            userDAO.updateUser(user); //Ponemos el cambio en la base de datos
            userSync.updateUser(user); //En Firestore tambien
        }
        return super.onOptionsItemSelected(item);
    }
}