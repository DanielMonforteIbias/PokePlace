package dam.tfg.pokeplace.api;

import android.util.Pair;

import java.util.List;

public interface PokemonSpeciesCallback {
    public void onDetailsReceived(List<Pair<String,String>> descriptions);
}
