package dam.tfg.pokeplace;

import static android.view.View.GONE;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;
import java.util.Map;

import dam.tfg.pokeplace.databinding.ActivityPokemonDetailsBinding;
import dam.tfg.pokeplace.models.Pokemon;
import dam.tfg.pokeplace.ui.detailsActivityFragments.PokemonViewModel;

public class PokemonDetailsActivity extends AppCompatActivity {
    private ActivityPokemonDetailsBinding binding;
    private Pokemon pokemon=new Pokemon();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPokemonDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_pokemon_details);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        Intent intent=getIntent();
        pokemon=intent.getParcelableExtra("Pokemon");
        PokemonViewModel viewModel = new ViewModelProvider(this).get(PokemonViewModel.class);
        viewModel.setPokemon(pokemon);

    }

}