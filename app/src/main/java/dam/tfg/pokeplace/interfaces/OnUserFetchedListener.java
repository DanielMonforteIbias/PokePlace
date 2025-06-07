package dam.tfg.pokeplace.interfaces;

import java.util.List;
import java.util.Map;

import dam.tfg.pokeplace.models.Team;
import dam.tfg.pokeplace.models.TeamPokemon;
import dam.tfg.pokeplace.models.User;

public interface OnUserFetchedListener {
    public void onUserFetched(User user);
    public void onTeamsFetched(List<Team> teams, Map<String, List<TeamPokemon>> teamMembersMap);
}