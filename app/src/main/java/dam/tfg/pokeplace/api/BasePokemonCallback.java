package dam.tfg.pokeplace.api;

import java.util.List;

import dam.tfg.pokeplace.models.BasePokemon;

public interface BasePokemonCallback {
    public void onBasePokemonListReceived(List<BasePokemon> pokemonList);
    public void onBasePokemonReceived(BasePokemon pokemon);
}
