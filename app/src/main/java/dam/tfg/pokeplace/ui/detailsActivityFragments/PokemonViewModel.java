package dam.tfg.pokeplace.ui.detailsActivityFragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import dam.tfg.pokeplace.models.Pokemon;

public class PokemonViewModel extends ViewModel {
    private final MutableLiveData<Pokemon> pokemon = new MutableLiveData<>(); //Pokemon al que podran acceder los fragmentos

    public void setPokemon(Pokemon pokemon) {
        this.pokemon.setValue(pokemon);
    }

    public LiveData<Pokemon> getPokemon() {
        return pokemon;
    }

    private MutableLiveData<Integer> currentSpriteIndex = new MutableLiveData<>();
    public void setCurrentSpriteIndex(int index) {
        currentSpriteIndex.setValue(index);
    }

    public LiveData<Integer> getCurrentSpriteIndex() {
        return currentSpriteIndex;
    }
}