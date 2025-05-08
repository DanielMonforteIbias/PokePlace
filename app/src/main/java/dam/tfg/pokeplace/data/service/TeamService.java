package dam.tfg.pokeplace.data.service;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import dam.tfg.pokeplace.data.DatabaseHelper;
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

    public TeamService(TeamDAO teamDAO, TeamPokemonDAO teamPokemonDAO, BasePokemonDAO basePokemonDAO) {
        this.teamDAO = teamDAO;
        this.teamPokemonDAO = teamPokemonDAO;
        this.basePokemonDAO=basePokemonDAO;
    }

    public Team getTeamWithMembers(String userId, int teamId) {
        Team team = teamDAO.getTeam(userId, teamId);
        ArrayList<TeamPokemon> teamMembers = (ArrayList<TeamPokemon>) teamPokemonDAO.getTeamMembers(userId, teamId);
        for(TeamPokemon teamPokemon:teamMembers){ //Se completa la informacion en esta capa para no mezclar los DAOs uno dentro del otro
            teamPokemon.completeBaseData(basePokemonDAO.getBasePokemon(Integer.parseInt(teamPokemon.getPokedexNumber())));
        }
        team.setTeamMembers(teamMembers);
        return team;
    }
    public List<Team> getAllTeams(String userId) {
        List<Team> teams = teamDAO.getAllTeams(userId);
        for (Team team : teams) {
            ArrayList<TeamPokemon> members = (ArrayList<TeamPokemon>) teamPokemonDAO.getTeamMembers(userId, team.getTeamId());
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

    public int getNewTeamId(String userId) {
        return teamDAO.getNewTeamId(userId);
    }
    public void changeTeamName(Team team){
        teamDAO.changeTeamName(team);
    }
    public void removeTeam(String userId, int teamId){
        teamDAO.removeTeam(userId,teamId);
    }
    public int getTeamSize(String userId, int teamId){
        return teamPokemonDAO.getTeamSize(userId,teamId);
    }
    public BasePokemon getBasePokemon(int pokedexNumber){
        return basePokemonDAO.getBasePokemon(pokedexNumber);
    }
    public long addTeamPokemon(TeamPokemon pokemon) {
        return teamPokemonDAO.addTeamPokemon(pokemon);
    }
}
