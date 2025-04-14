package dam.tfg.pokeplace;

import static android.view.View.GONE;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.io.IOException;

import dam.tfg.pokeplace.databinding.ActivityPokemonDetailsBinding;
import dam.tfg.pokeplace.models.Pokemon;

public class PokemonDetailsActivity extends AppCompatActivity {
    private ActivityPokemonDetailsBinding binding;
    private Pokemon pokemon=new Pokemon();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int currentSpriteIndex = 0; // 0 = normal, 1 = shiny

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPokemonDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        pokemon=intent.getParcelableExtra("Pokemon");
        binding.txtNameDetails.setText(pokemon.getName());
        binding.txtNumberDetails.setText(pokemon.getPokedexNumber());
        binding.txtHeightDetails.setText(getString(R.string.altura)+" "+pokemon.getHeight()+" m");
        binding.txtWeightDetails.setText(getString(R.string.peso)+" "+pokemon.getWeight()+ "kg");
        Glide.with(this).load(pokemon.getSprites().get(currentSpriteIndex)).into(binding.spriteDetails);
        if(pokemon.getTypes()[0]!=null) Glide.with(this).load(pokemon.getTypes()[0].getSprite()).into(binding.imgType1);
        else binding.imgType1.setVisibility(GONE);
        if(pokemon.getTypes()[1]!=null)Glide.with(this).load(pokemon.getTypes()[1].getSprite()).into(binding.imgType2);
        else binding.imgType2.setVisibility(GONE);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(pokemon.getSound());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        binding.btnSoundDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
            }
        });


        binding.btnVolverDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                finish();
            }
        });
        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 50;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            currentSpriteIndex = (currentSpriteIndex - 1 + pokemon.getSprites().size()) % pokemon.getSprites().size();
                        } else {
                            currentSpriteIndex = (currentSpriteIndex + 1 ) % pokemon.getSprites().size();
                        }
                        Glide.with(getApplicationContext()).load(pokemon.getSprites().get(currentSpriteIndex)).into(binding.spriteDetails);
                        return true;
                    }
                }
                return false;
            }
        });
        binding.spriteDetails.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });


    }
}