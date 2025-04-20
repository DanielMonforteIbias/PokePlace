package dam.tfg.pokeplace.api;

import dam.tfg.pokeplace.models.Pokemon;

public interface PokemonCallback {
    public void onPokemonReceived(Pokemon pokemon);
}
