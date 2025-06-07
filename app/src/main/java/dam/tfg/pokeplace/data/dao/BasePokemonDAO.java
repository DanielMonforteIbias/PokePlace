package dam.tfg.pokeplace.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dam.tfg.pokeplace.data.DatabaseHelper;
import dam.tfg.pokeplace.data.DatabaseManager;
import dam.tfg.pokeplace.models.BasePokemon;

public class BasePokemonDAO {
    private final SQLiteDatabase db;

    public BasePokemonDAO(Context context) {
        db= DatabaseManager.getInstance(context).openDatabase();
    }

    public void addBasePokemon(BasePokemon pokemon) {
        if(!pokemonExists(pokemon.getPokedexNumber())){
            ContentValues values=new ContentValues(); //En vez de ser atributo de clase se crea una por insercion porque se hacen muchas a la vez y podria dar ConcurrentModification al intentar usar la misma variable
            values.put(DatabaseHelper.POKEMON_POKEDEX_NUMBER_COLUMN,pokemon.getPokedexNumber());
            values.put(DatabaseHelper.POKEMON_NAME_COLUMN,pokemon.getName());
            values.put(DatabaseHelper.POKEMON_SPRITE_COLUMN,pokemon.getSprite());
            values.put(DatabaseHelper.POKEMON_URL_COLUMN,pokemon.getUrl());
            values.put(DatabaseHelper.POKEMON_TYPE1_COLUMN,pokemon.getType1());
            values.put(DatabaseHelper.POKEMON_TYPE2_COLUMN,pokemon.getType2());
            db.insert(DatabaseHelper.POKEMON_TABLE_NAME, null, values);
        }
    }
    public boolean pokemonExists(String pokedexNumber){
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.POKEMON_TABLE_NAME + " WHERE "+DatabaseHelper.POKEMON_POKEDEX_NUMBER_COLUMN+" = ?", new String[]{pokedexNumber});
        boolean exists = false;
        if(cursor.moveToFirst()) { //Si hay resultado en la consulta
            int count=cursor.getInt(0); //Obtenemos el primer dato que hay (solo habrá uno ya que es un count)
            if(count>0) exists=true; //Si el valor es mayor que 0, es que el usuario existe, así que ponemos la variable a true
        }
        cursor.close(); //Cerramos el cursor
        return exists; //Devolvemos si existe o no
    }
    public List<BasePokemon> getBasePokemonListWithOffset(int limit, int offset) {
        List<BasePokemon> basePokemonList = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.POKEMON_TABLE_NAME, null, null, null, null, null, DatabaseHelper.POKEMON_POKEDEX_NUMBER_COLUMN+" ASC", offset + "," + limit);
        if (cursor.moveToFirst()) {
            do {
                String pokedexNumber=String.format(Locale.US,"%03d",cursor.getInt(0));
                String name=cursor.getString(1);
                String sprite=cursor.getString(2);
                String url=cursor.getString(3);
                String type1=cursor.getString(4);
                String type2=cursor.getString(5);
                basePokemonList.add(new BasePokemon(pokedexNumber,name,sprite,url,type1,type2));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return basePokemonList;
    }
    public List<BasePokemon> getBasePokemonList() {
        List<BasePokemon> basePokemonList = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.POKEMON_TABLE_NAME, null, null, null, null, null, DatabaseHelper.POKEMON_POKEDEX_NUMBER_COLUMN+" ASC", null);
        if (cursor.moveToFirst()) {
            do {
                String pokedexNumber=String.format(Locale.US,"%03d",cursor.getInt(0));
                String name=cursor.getString(1);
                String sprite=cursor.getString(2);
                String url=cursor.getString(3);
                String type1=cursor.getString(4);
                String type2=cursor.getString(5);
                basePokemonList.add(new BasePokemon(pokedexNumber,name,sprite,url,type1,type2));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return basePokemonList;
    }
    public BasePokemon getBasePokemon(int pokedexNumber){
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.POKEMON_TABLE_NAME + " WHERE "+DatabaseHelper.POKEMON_POKEDEX_NUMBER_COLUMN+" = ?", new String[]{String.valueOf(pokedexNumber)});
        if(cursor.moveToFirst()) { //Si hay resultado en la consulta
            String number=String.format(Locale.US,"%03d",cursor.getInt(0));
            String name=cursor.getString(1);
            String sprite=cursor.getString(2);
            String url=cursor.getString(3);
            String type1=cursor.getString(4);
            String type2=cursor.getString(5);
            return new BasePokemon(number,name,sprite,url,type1,type2);
        }
        cursor.close(); //Cerramos el cursor
        return null;
    }
}
