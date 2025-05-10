package dam.tfg.pokeplace.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.data.DatabaseHelper;
import dam.tfg.pokeplace.data.DatabaseManager;
import dam.tfg.pokeplace.models.BasePokemon;
import dam.tfg.pokeplace.models.TeamPokemon;

public class TeamPokemonDAO {
    private final SQLiteDatabase db;
    private ContentValues values=null;
    private int teamSizeLimit=0;

    public TeamPokemonDAO(Context context) {
        db= DatabaseManager.getInstance(context).openDatabase();
        teamSizeLimit= context.getResources().getInteger(R.integer.team_size_limit);
    }
    public void addTeamPokemon(TeamPokemon pokemon) {
        values=new ContentValues();
        if(getTeamSize(pokemon.getUserId(),pokemon.getTeamId())<teamSizeLimit){
            values.put(DatabaseHelper.TEAM_POKEMON_ID_COLUMN, pokemon.getId());
            values.put(DatabaseHelper.TEAM_POKEMON_USER_ID_COLUMN, pokemon.getUserId());
            values.put(DatabaseHelper.TEAM_POKEMON_TEAM_ID_COLUMN, pokemon.getTeamId());
            values.put(DatabaseHelper.TEAM_POKEMON_POKEDEX_NUMBER_COLUMN, pokemon.getPokedexNumber());
            values.put(DatabaseHelper.TEAM_POKEMON_CUSTOM_NAME_COLUMN, pokemon.getCustomName());
            values.put(DatabaseHelper.TEAM_POKEMON_CUSTOM_SPRITE_COLUMN, pokemon.getCustomSprite());
            db.insert(DatabaseHelper.TEAM_POKEMON_TABLE_NAME, null, values);
        }
    }
    public List<TeamPokemon> getTeamMembers(String userId, int teamId){
        List<TeamPokemon> teamMembers=new ArrayList<>();
        Cursor cursor=db.rawQuery("SELECT * FROM "+DatabaseHelper.TEAM_POKEMON_TABLE_NAME+" WHERE "+DatabaseHelper.TEAM_POKEMON_USER_ID_COLUMN+"=? AND "+DatabaseHelper.TEAM_POKEMON_TEAM_ID_COLUMN+"=?",new String[]{userId,String.valueOf(teamId)});
        while (cursor.moveToNext()) { //Recorremos el cursor
            String pokemonId=cursor.getString(0);
            String pokedexNumber=String.valueOf(cursor.getInt(3));
            String customName=cursor.getString(4);
            String customSprite=cursor.getString(5);
            TeamPokemon teamPokemon=new TeamPokemon(pokemonId,userId,teamId,customName,customSprite);
            teamPokemon.setPokedexNumber(pokedexNumber);
            teamMembers.add(teamPokemon);
        }
        cursor.close(); //Cerramos el cursor
        return teamMembers;
    }
    public int getTeamSize(String userId, int teamId){
        int teamSize=0;
        Cursor cursor=db.rawQuery("SELECT COUNT(*) FROM "+DatabaseHelper.TEAM_POKEMON_TABLE_NAME+" WHERE "+DatabaseHelper.TEAM_POKEMON_USER_ID_COLUMN+"=? AND "+DatabaseHelper.TEAM_POKEMON_TEAM_ID_COLUMN+"=?",new String[]{userId,String.valueOf(teamId)});
        if(cursor.moveToFirst()) { //Si hay resultado en la consulta
            teamSize=cursor.getInt(0); //Obtenemos el primer dato que hay (solo habrá uno ya que es un count)
        }
        cursor.close(); //Cerramos el cursor
        return teamSize;
    }
    public void updateTeamPokemon(TeamPokemon teamPokemon){
        values = new ContentValues();
        values.put(DatabaseHelper.TEAM_POKEMON_CUSTOM_NAME_COLUMN, teamPokemon.getCustomName());
        String where=DatabaseHelper.TEAM_POKEMON_ID_COLUMN+"=?";
        db.update(DatabaseHelper.TEAM_POKEMON_TABLE_NAME,values,where,new String[]{teamPokemon.getId()});
    }
    public void removeTeamPokemon(String pokemonId){
        String condition = DatabaseHelper.TEAM_POKEMON_ID_COLUMN+"=?";
        String[] conditionArgs = {pokemonId};
        db.delete(DatabaseHelper.TEAM_POKEMON_TABLE_NAME, condition,conditionArgs);
    }

    public boolean teamPokemonExists(String teamPokemonId){
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TEAM_POKEMON_TABLE_NAME + " WHERE "+DatabaseHelper.TEAM_POKEMON_ID_COLUMN+" = ?", new String[]{teamPokemonId});
        boolean exists = false;
        if(cursor.moveToFirst()) {
            int count=cursor.getInt(0);
            if(count>0) exists=true;
        }
        cursor.close();
        return exists;
    }
    public String generateNewPokemonId() {
        return UUID.randomUUID().toString();
    }
}
