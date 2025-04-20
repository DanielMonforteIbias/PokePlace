package dam.tfg.pokeplace.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import dam.tfg.pokeplace.data.DatabaseHelper;
import dam.tfg.pokeplace.data.DatabaseManager;
import dam.tfg.pokeplace.models.BasePokemon;
import dam.tfg.pokeplace.models.TeamPokemon;

public class TeamPokemonDAO {
    private final SQLiteDatabase db;
    private ContentValues values=null;

    private BasePokemonDAO basePokemonDAO; //Ya que TeamPokemon hereda de BasePokemon, necesitaremos recuperar datos de BasePokemon

    public TeamPokemonDAO(Context context) {
        db= DatabaseManager.getInstance(context).openDatabase();
        basePokemonDAO=new BasePokemonDAO(context);
    }
    public void addTeamPokemon(TeamPokemon pokemon) {
        values=new ContentValues();
        values.put(DatabaseHelper.TEAM_POKEMON_USER_ID_COLUMN, pokemon.getUserId());
        values.put(DatabaseHelper.TEAM_POKEMON_TEAM_ID_COLUMN, pokemon.getTeamId());
        values.put(DatabaseHelper.TEAM_POKEMON_POKEDEX_NUMBER_COLUMN, pokemon.getPokedexNumber());
        values.put(DatabaseHelper.TEAM_POKEMON_CUSTOM_NAME_COLUMN, pokemon.getCustomName());
        values.put(DatabaseHelper.TEAM_POKEMON_CUSTOM_SPRITE_COLUMN, pokemon.getCustomSprite());
        db.insert(DatabaseHelper.TEAM_POKEMON_TABLE_NAME, null, values);
    }
    public List<TeamPokemon> getTeamMembers(String userId, int teamId){
        List<TeamPokemon> teamMembers=new ArrayList<>();
        Cursor cursor=db.rawQuery("SELECT * FROM "+DatabaseHelper.TEAM_POKEMON_TABLE_NAME+" WHERE "+DatabaseHelper.TEAM_POKEMON_USER_ID_COLUMN+"=? AND "+DatabaseHelper.TEAM_POKEMON_TEAM_ID_COLUMN+"=?",new String[]{userId,String.valueOf(teamId)});
        while (cursor.moveToNext()) { //Recorremos el cursor
            int pokemonId=cursor.getInt(0);
            String pokedexNumber=String.valueOf(cursor.getInt(3));
            String customName=cursor.getString(4);
            String customSprite=cursor.getString(5);
            BasePokemon basePokemon=basePokemonDAO.getBasePokemon(Integer.parseInt(pokedexNumber));
            teamMembers.add(new TeamPokemon(pokedexNumber,basePokemon.getName(),basePokemon.getSprite(),basePokemon.getUrl(),basePokemon.getType1(),basePokemon.getType2(),pokemonId,userId,teamId,customName,customSprite));
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
}
