package dam.tfg.pokeplace.api;

import android.util.Pair;

import java.util.List;

import dam.tfg.pokeplace.models.Pokemon;

public interface PokemonSpeciesCallback {
    public void onDescriptionsReceived(List<Pair<String,String>> descriptions);
}
