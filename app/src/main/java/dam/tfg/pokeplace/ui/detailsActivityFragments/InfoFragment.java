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

public class InfoFragment extends Fragment {
    private FragmentInfoBinding binding;
    private Pokemon pokemon;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int currentSpriteIndex = 0; // 0 = normal, 1 = shiny

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PokemonViewModel viewModel = new ViewModelProvider(requireActivity()).get(PokemonViewModel.class);
        viewModel.getPokemon().observe(getViewLifecycleOwner(), pokemon -> { //Obtenemos el Pokemon del viewmodel
            binding.txtNameDetails.setText(pokemon.getName());
            binding.txtNumberDetails.setText(pokemon.getPokedexNumber());
            binding.txtHeightDetails.setText(getString(R.string.altura)+" "+pokemon.getHeight()+" m");
            binding.txtWeightDetails.setText(getString(R.string.peso)+" "+pokemon.getWeight()+ "kg");
            //Cargar stats
            binding.statsDetailsLayout.removeAllViews(); //Las vistas que tiene son solo para visualizarlo en el XML, las quitamos
            for (Map.Entry<String,Integer>stat:pokemon.getStats().entrySet()) {
                View statItem = getLayoutInflater().inflate(R.layout.item_stat, binding.statsDetailsLayout, false);
                TextView txtStatName = statItem.findViewById(R.id.txtStatDetails);
                String resourceKey =stat.getKey().replace("-", "_"); //Los nombres de la stat estan en strings y la clave es el nombre que viene de la api y esta en el map. No se aceptan guiones en el fichero strings, se reemplazan por _
                int resId = getContext().getResources().getIdentifier(resourceKey, "string", getContext().getPackageName());
                txtStatName.setText(getString(resId));
                TextView txtStatValue = statItem.findViewById(R.id.txtStatValueDetails);
                txtStatValue.setText(String.valueOf(stat.getValue()));
                View statBar = statItem.findViewById(R.id.statBar);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) statBar.getLayoutParams();
                int statValue = stat.getValue();
                float scale = getContext().getResources().getDisplayMetrics().density;
                int color = getStatBarColor(statValue);
                GradientDrawable drawable = (GradientDrawable) statBar.getBackground(); //Para no perder el borde redondeado al aplicar fondo
                drawable.setColor(color);
                layoutParams.width = (int) (statValue * scale + 0.5f);
                statBar.setLayoutParams(layoutParams);
                binding.statsDetailsLayout.addView(statItem);
            }
            Glide.with(this).load(pokemon.getSprites().get(currentSpriteIndex)).into(binding.spriteDetails);
            if(pokemon.getTypes()[0]!=null) Glide.with(this).load(pokemon.getTypes()[0].getSprite()).into(binding.imgType1);
            else binding.imgType1.setVisibility(GONE);
            if(pokemon.getTypes()[1]!=null)Glide.with(this).load(pokemon.getTypes()[1].getSprite()).into(binding.imgType2);
            else binding.imgType2.setVisibility(GONE);
            mediaPlayer = new MediaPlayer();
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
            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
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
                            Glide.with(getContext()).load(pokemon.getSprites().get(currentSpriteIndex)).into(binding.spriteDetails);
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

        binding.btnVolverDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                getActivity().finish();
            }
        });
    }
    public int getStatBarColor(int statValue){
        int color;
        if (statValue <= 25) {
            color = Color.RED;
        } else if (statValue <= 50) {
            color = Color.parseColor("#FFA500");
        } else if (statValue <= 90) {
            color = Color.YELLOW;
        } else if (statValue <= 120) {
            color = Color.parseColor("#99FF2F");
        } else {
            color = Color.GREEN;
        }
        return color;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}