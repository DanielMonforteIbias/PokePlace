package dam.tfg.pokeplace.api;

import dam.tfg.pokeplace.models.Move;
import dam.tfg.pokeplace.models.Pokemon;

public interface DetailsCallback {
    public void onMoveReceived(Move move);
}
