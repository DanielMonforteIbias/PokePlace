package dam.tfg.pokeplace.models;

import java.util.ArrayList;

public class Team {
    private String userId;
    private int teamId;
    private String name;
    private ArrayList<Pokemon> pokemons;
    public Team(){}
    public Team(String userId, int teamId, String name){
        this.userId=userId;
        this.teamId=teamId;
        this.name=name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Pokemon> getPokemons() {
        return pokemons;
    }

    public void setPokemons(ArrayList<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }
}
