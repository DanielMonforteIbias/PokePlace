package dam.tfg.pokeplace.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="database.db"; //Nombre de la base de datos
    //TABLA USERS
    public static final String USERS_TABLE_NAME="USERS";
    public static final String USER_ID_COLUMN="userId";
    public static final String USER_EMAIL_COLUMN="email";
    public static final String USER_NAME_COLUMN="name";
    public static final String USER_IMAGE_COLUMN="image";
    //TABLA TEAMS
    public static final String TEAMS_TABLE_NAME="TEAMS";
    public static final String TEAM_USER_ID_COLUMN="userId";
    public static final String TEAM_ID_COLUMN="teamId";
    public static final String TEAM_NAME_COLUMN="name";
    //TABLA POKEMON - Datos basicos cargados de la API para no saturarla a peticiones cada vez que se abra la app y cogerlos de aqui
    public static final String POKEMON_TABLE_NAME="POKEMON";
    public static final String POKEMON_POKEDEX_NUMBER_COLUMN="pokedexNumber";
    public static final String POKEMON_NAME_COLUMN="name";
    public static final String POKEMON_SPRITE_COLUMN="sprite";
    public static final String POKEMON_URL_COLUMN="url";
    public static final String POKEMON_TYPE1_COLUMN="type1";
    public static final String POKEMON_TYPE2_COLUMN="type2";

    /*VERSION
    Version 1: Tabla Users
    Version 2: Tabla Teams
    Version 3: Tabla Pokemon
    */
    private static final int databaseVersion=3;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME,null,databaseVersion); //Pasamos el nombre y la version
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creacion tabla usuarios
        String createTableUsers="CREATE TABLE "+USERS_TABLE_NAME+" ("+USER_ID_COLUMN+" TEXT PRIMARY KEY,"+USER_EMAIL_COLUMN+" TEXT NOT NULL, "+USER_NAME_COLUMN+" TEXT, "+USER_IMAGE_COLUMN+" TEXT)";
        db.execSQL(createTableUsers);
        //Creacion tabla equipos
        String createTableTeams="CREATE TABLE "+TEAMS_TABLE_NAME+" ("+TEAM_USER_ID_COLUMN+" TEXT,"+TEAM_ID_COLUMN+" INTEGER, "+TEAM_NAME_COLUMN+" TEXT, PRIMARY KEY("+TEAM_USER_ID_COLUMN+","+TEAM_ID_COLUMN+"), FOREIGN KEY("+TEAM_USER_ID_COLUMN+") REFERENCES "+USERS_TABLE_NAME+"("+USER_ID_COLUMN+"))";
        db.execSQL(createTableTeams);
        //Creacion tabla pokemon
        String createTablePokemon="CREATE TABLE "+POKEMON_TABLE_NAME+" ("+POKEMON_POKEDEX_NUMBER_COLUMN+" INTEGER PRIMARY KEY,"+POKEMON_NAME_COLUMN+" TEXT, "+POKEMON_SPRITE_COLUMN+" TEXT, "+POKEMON_URL_COLUMN+" TEXT, "+POKEMON_TYPE1_COLUMN+" TEXT, "+POKEMON_TYPE2_COLUMN+" TEXT)";
        db.execSQL(createTablePokemon);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<2) db.execSQL("CREATE TABLE "+TEAMS_TABLE_NAME+" ("+TEAM_USER_ID_COLUMN+" TEXT,"+TEAM_ID_COLUMN+" INTEGER, "+TEAM_NAME_COLUMN+" TEXT, PRIMARY KEY("+TEAM_USER_ID_COLUMN+","+TEAM_ID_COLUMN+"), FOREIGN KEY("+TEAM_USER_ID_COLUMN+") REFERENCES "+USERS_TABLE_NAME+"("+USER_ID_COLUMN+"))");
        if(oldVersion<3) db.execSQL("CREATE TABLE "+POKEMON_TABLE_NAME+" ("+POKEMON_POKEDEX_NUMBER_COLUMN+" INTEGER PRIMARY KEY,"+POKEMON_NAME_COLUMN+" TEXT, "+POKEMON_SPRITE_COLUMN+" TEXT, "+POKEMON_URL_COLUMN+" TEXT, "+POKEMON_TYPE1_COLUMN+" TEXT, "+POKEMON_TYPE2_COLUMN+" TEXT)");
    }
}
