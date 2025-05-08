package dam.tfg.pokeplace.interfaces;

import dam.tfg.pokeplace.models.TeamPokemon;

public interface OnTeamPokemonActionListener {
    public void onRenameClick(TeamPokemon pokemon);
    public void onDeleteClick(TeamPokemon pokemon);
}
