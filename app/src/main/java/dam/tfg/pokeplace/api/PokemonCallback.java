package dam.tfg.pokeplace.api;

import java.util.List;

import dam.tfg.pokeplace.models.Pokemon;

public interface PokemonCallback {
    public void onPokemonListReceived(List<Pokemon> pokemonList);
    public void onPokemonReceived(Pokemon pokemon);
}
