package dam.tfg.pokeplace.data.service;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.data.dao.BasePokemonDAO;
import dam.tfg.pokeplace.data.dao.TeamDAO;
import dam.tfg.pokeplace.data.dao.TeamPokemonDAO;
import dam.tfg.pokeplace.models.BasePokemon;
import dam.tfg.pokeplace.models.Team;
import dam.tfg.pokeplace.models.TeamPokemon;

public class TeamService {
    private TeamDAO teamDAO;
    private TeamPokemonDAO teamPokemonDAO;
    private BasePokemonDAO basePokemonDAO;
    public int teamsLimit =0;

    public TeamService(Context context) {
        this.teamDAO = new TeamDAO(context);
        this.teamPokemonDAO = new TeamPokemonDAO(context);
        this.basePokemonDAO=new BasePokemonDAO(context);
        this.teamsLimit =context.getResources().getInteger(R.integer.teams_limit);
    }

    public Team getTeamWithMembers(String teamId) {
        Team team = teamDAO.getTeam(teamId);
        ArrayList<TeamPokemon> teamMembers = (ArrayList<TeamPokemon>) teamPokemonDAO.getTeamMembers(teamId);
        for(TeamPokemon teamPokemon:teamMembers){ //Se completa la informacion en esta capa para no mezclar los DAOs uno dentro del otro
            teamPokemon.completeBaseData(basePokemonDAO.getBasePokemon(Integer.parseInt(teamPokemon.getPokedexNumber())));
        }
        team.setTeamMembers(teamMembers);
        return team;
    }
    public List<Team> getAllTeams(String userId) {
        List<Team> teams = teamDAO.getAllTeams(userId);
        for (Team team : teams) {
            ArrayList<TeamPokemon> members = (ArrayList<TeamPokemon>) teamPokemonDAO.getTeamMembers(team.getTeamId());
            for(TeamPokemon teamPokemon:members){ //Se completa la informacion en esta capa para no mezclar los DAOs uno dentro del otro
                teamPokemon.completeBaseData(basePokemonDAO.getBasePokemon(Integer.parseInt(teamPokemon.getPokedexNumber())));
            }
            team.setTeamMembers(members);
        }
        return teams;
    }

    public void addTeam(Team team) {
        teamDAO.addTeam(team);
    }

    public String getNewTeamId() {
        return teamDAO.getNewTeamId();
    }
    public void changeTeamName(Team team){
        teamDAO.updateTeam(team);
    }
    public void removeTeam(String teamId){
        teamDAO.removeTeam(teamId);
    }
    public int getTeamSize(String teamId){
        return teamPokemonDAO.getTeamSize(teamId);
    }
    public void updateTeam(Team team){
        teamDAO.updateTeam(team);
    }
    public boolean teamExists(String teamId){
        return teamDAO.teamExists(teamId);
    }
    public BasePokemon getBasePokemon(int pokedexNumber){
        return basePokemonDAO.getBasePokemon(pokedexNumber);
    }
    public void addTeamPokemon(TeamPokemon pokemon) {
        teamPokemonDAO.addTeamPokemon(pokemon);
    }
    public String generateNewPokemonId(){
        return teamPokemonDAO.generateNewPokemonId();
    }
    public void updateTeamPokemon(TeamPokemon pokemon){
        teamPokemonDAO.updateTeamPokemon(pokemon);
    }
    public void removeTeamPokemon(String pokemonId){
        teamPokemonDAO.removeTeamPokemon(pokemonId);
    }
    public boolean teamPokemonExists(String teamPokemonId){
        return teamPokemonDAO.teamPokemonExists(teamPokemonId);
    }
}
