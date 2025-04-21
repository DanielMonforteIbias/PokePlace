package dam.tfg.pokeplace.ui.detailsActivityFragments;

import static android.view.View.GONE;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.Map;

import dam.tfg.pokeplace.PokemonDetailsActivity;
import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.databinding.FragmentInfoBinding;
import dam.tfg.pokeplace.models.Pokemon;
import dam.tfg.pokeplace.utils.StringFormatter;

public class InfoFragment extends Fragment {
    private FragmentInfoBinding binding;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int currentSpriteIndex = 0;

    private Pokemon pokemon;
    private PokemonViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PokemonViewModel.class);
        viewModel.getPokemon().observe(getViewLifecycleOwner(), pokemon -> { //Obtenemos el Pokemon del viewmodel
            this.pokemon=pokemon;
            binding.txtNameDetails.setText(StringFormatter.formatName(pokemon.getName()));
            binding.txtNumberDetails.setText(pokemon.getPokedexNumber());
            binding.txtHeightDetails.setText(getString(R.string.altura)+" "+pokemon.getHeight()+" m");
            binding.txtWeightDetails.setText(getString(R.string.peso)+" "+pokemon.getWeight()+ "kg");
            Glide.with(this).load(pokemon.getSprites().get(currentSpriteIndex)).into(binding.spriteDetails);
            viewModel.setCurrentSpriteIndex(currentSpriteIndex);
            if(pokemon.getTypes()[0]!=null) Glide.with(this).load(pokemon.getTypes()[0].getSprite()).into(binding.imgType1);
            else binding.imgType1.setVisibility(GONE);
            if(pokemon.getTypes()[1]!=null)Glide.with(this).load(pokemon.getTypes()[1].getSprite()).into(binding.imgType2);
            else binding.imgType2.setVisibility(GONE);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(pokemon.getSound());
                mediaPlayer.prepareAsync(); //prepareAsync hace que no bloquee el hilo principal. Por ejemplo, si accedes sin wifi se quedaria mucho tiempo esperando y pareceria bloqueada la app con prepare, con prepareAsync no pasa
            } catch (IOException e) {
                e.printStackTrace();
            }
            binding.btnSoundDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPlayer.start();
                }
            });
            //Controles de botones para cambiar el sprite
            binding.spriteLeftArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changePokemonSprite(-1);
                }
            });
            binding.spriteRightArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changePokemonSprite(1);
                }
            });
            //Controles de deslizamiento para cambiar el sprite
            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                private static final int SWIPE_THRESHOLD = 100;
                private static final int SWIPE_VELOCITY_THRESHOLD = 50;

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    float diffX = e2.getX() - e1.getX();
                    float diffY = e2.getY() - e1.getY();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) changePokemonSprite(-1); //Cambiamos el sprite hacia la izquierda
                            else changePokemonSprite(1); //Cambiamos el sprite hacia la derecha
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
        });
    }

    private void changePokemonSprite(int direction){
        if(direction==-1)currentSpriteIndex = (currentSpriteIndex - 1 + pokemon.getSprites().size()) % pokemon.getSprites().size();
        else if (direction==1)currentSpriteIndex = (currentSpriteIndex + 1 ) % pokemon.getSprites().size();
        Glide.with(getContext()).load(pokemon.getSprites().get(currentSpriteIndex)).into(binding.spriteDetails);
        viewModel.setCurrentSpriteIndex(currentSpriteIndex);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        binding = null;
    }
}