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
    //TABLA TEAM_POKEMON
    public static final String TEAM_POKEMON_TABLE_NAME="TEAM_POKEMON";
    public static final String TEAM_POKEMON_ID_COLUMN="teamPokemonId";
    public static final String TEAM_POKEMON_USER_ID_COLUMN="userId";
    public static final String TEAM_POKEMON_TEAM_ID_COLUMN="teamId";
    public static final String TEAM_POKEMON_POKEDEX_NUMBER_COLUMN="pokedexNumber";
    public static final String TEAM_POKEMON_CUSTOM_NAME_COLUMN="customName";
    public static final String TEAM_POKEMON_CUSTOM_SPRITE_COLUMN="customSprite";
    //TABLA TYPES
    public static final String TYPES_TABLE_NAME="TYPES";
    public static final String TYPES_NAME_COLUMN="name";
    public static final String TYPES_SPRITE_COLUMN="sprite";
    //TABLA TYPE_RELATIONS
    public static final String TYPE_RELATIONS_TABLE_NAME="TYPE_RELATIONS";
    public static final String TYPE_RELATIONS_SOURCE_TYPE="source_type";
    public static final String TYPE_RELATIONS_TARGET_TYPE="target_type";
    public static final String TYPE_RELATIONS_RELATION="relation";

    /*VERSION
    Version 1: Tabla Users
    Version 2: Tabla Teams
    Version 3: Tabla Pokemon
    Version 4: Tabla Team_Pokemon
    Version 5: Tabla Types y Type_Relations
    */
    private static final int databaseVersion=5;

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
        //Creacion tabla Team_Pokemon
        String createTableTeamPokemon="CREATE TABLE "+TEAM_POKEMON_TABLE_NAME+"("+TEAM_POKEMON_ID_COLUMN+" INTEGER PRIMARY KEY AUTOINCREMENT, "+TEAM_POKEMON_USER_ID_COLUMN+" TEXT, "+TEAM_POKEMON_TEAM_ID_COLUMN+" INTEGER, "+TEAM_POKEMON_POKEDEX_NUMBER_COLUMN+" INTEGER, "+TEAM_POKEMON_CUSTOM_NAME_COLUMN+" TEXT, "+TEAM_POKEMON_CUSTOM_SPRITE_COLUMN+" TEXT, FOREIGN KEY("+TEAM_POKEMON_USER_ID_COLUMN+", "+TEAM_POKEMON_TEAM_ID_COLUMN+") REFERENCES "+TEAMS_TABLE_NAME+"("+TEAM_USER_ID_COLUMN+", "+TEAM_ID_COLUMN+") ON DELETE CASCADE, FOREIGN KEY ("+TEAM_POKEMON_POKEDEX_NUMBER_COLUMN+") REFERENCES "+POKEMON_TABLE_NAME+"("+POKEMON_POKEDEX_NUMBER_COLUMN+"))";
        db.execSQL(createTableTeamPokemon);
        //Creacion tabla Types
        String createTableTypes="CREATE TABLE "+TYPES_TABLE_NAME+" ("+TYPES_NAME_COLUMN+" TEXT PRIMARY KEY,"+TYPES_SPRITE_COLUMN+" TEXT NOT NULL)";
        db.execSQL(createTableTypes);
        //Creacion tabla Type_Relations
        String createTableTypeRelations="CREATE TABLE "+TYPE_RELATIONS_TABLE_NAME+" ("+TYPE_RELATIONS_SOURCE_TYPE+" TEXT,"+TYPE_RELATIONS_TARGET_TYPE+" TEXT,"+TYPE_RELATIONS_RELATION+" TEXT CHECK("+TYPE_RELATIONS_RELATION+" IN ('double_damage_from', 'double_damage_to','half_damage_from', 'half_damage_to','no_damage_from', 'no_damage_to')),  PRIMARY KEY ("+TYPE_RELATIONS_SOURCE_TYPE+","+TYPE_RELATIONS_TARGET_TYPE+","+TYPE_RELATIONS_RELATION+"), FOREIGN KEY ("+TYPE_RELATIONS_SOURCE_TYPE+") REFERENCES "+TYPES_TABLE_NAME+"("+TYPES_NAME_COLUMN+"),FOREIGN KEY ("+TYPE_RELATIONS_TARGET_TYPE+") REFERENCES "+TYPES_TABLE_NAME+"("+TYPES_NAME_COLUMN+"))";
        db.execSQL(createTableTypeRelations);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<2) db.execSQL("CREATE TABLE "+TEAMS_TABLE_NAME+" ("+TEAM_USER_ID_COLUMN+" TEXT,"+TEAM_ID_COLUMN+" INTEGER, "+TEAM_NAME_COLUMN+" TEXT, PRIMARY KEY("+TEAM_USER_ID_COLUMN+","+TEAM_ID_COLUMN+"), FOREIGN KEY("+TEAM_USER_ID_COLUMN+") REFERENCES "+USERS_TABLE_NAME+"("+USER_ID_COLUMN+"))");
        if(oldVersion<3) db.execSQL("CREATE TABLE "+POKEMON_TABLE_NAME+" ("+POKEMON_POKEDEX_NUMBER_COLUMN+" INTEGER PRIMARY KEY,"+POKEMON_NAME_COLUMN+" TEXT, "+POKEMON_SPRITE_COLUMN+" TEXT, "+POKEMON_URL_COLUMN+" TEXT, "+POKEMON_TYPE1_COLUMN+" TEXT, "+POKEMON_TYPE2_COLUMN+" TEXT)");
        if(oldVersion<4) db.execSQL("CREATE TABLE "+TEAM_POKEMON_TABLE_NAME+"("+TEAM_POKEMON_ID_COLUMN+" INTEGER PRIMARY KEY AUTOINCREMENT, "+TEAM_POKEMON_USER_ID_COLUMN+" TEXT, "+TEAM_POKEMON_TEAM_ID_COLUMN+" INTEGER, "+TEAM_POKEMON_POKEDEX_NUMBER_COLUMN+" INTEGER, "+TEAM_POKEMON_CUSTOM_NAME_COLUMN+" TEXT, "+TEAM_POKEMON_CUSTOM_SPRITE_COLUMN+" TEXT, FOREIGN KEY("+TEAM_POKEMON_USER_ID_COLUMN+", "+TEAM_POKEMON_TEAM_ID_COLUMN+") REFERENCES "+TEAMS_TABLE_NAME+"("+TEAM_USER_ID_COLUMN+", "+TEAM_ID_COLUMN+") ON DELETE CASCADE, FOREIGN KEY ("+TEAM_POKEMON_POKEDEX_NUMBER_COLUMN+") REFERENCES "+POKEMON_TABLE_NAME+"("+POKEMON_POKEDEX_NUMBER_COLUMN+"))");
        if(oldVersion<5){
            db.execSQL("CREATE TABLE "+TYPES_TABLE_NAME+" ("+TYPES_NAME_COLUMN+" TEXT PRIMARY KEY,"+TYPES_SPRITE_COLUMN+" TEXT NOT NULL)");
            db.execSQL("CREATE TABLE "+TYPE_RELATIONS_TABLE_NAME+" ("+TYPE_RELATIONS_SOURCE_TYPE+" TEXT,"+TYPE_RELATIONS_TARGET_TYPE+" TEXT,"+TYPE_RELATIONS_RELATION+" TEXT CHECK("+TYPE_RELATIONS_RELATION+" IN ('double_damage_from', 'double_damage_to','half_damage_from', 'half_damage_to','no_damage_from', 'no_damage_to')),  PRIMARY KEY ("+TYPE_RELATIONS_SOURCE_TYPE+","+TYPE_RELATIONS_TARGET_TYPE+","+TYPE_RELATIONS_RELATION+"), FOREIGN KEY ("+TYPE_RELATIONS_SOURCE_TYPE+") REFERENCES "+TYPES_TABLE_NAME+"("+TYPES_NAME_COLUMN+"),FOREIGN KEY ("+TYPE_RELATIONS_TARGET_TYPE+") REFERENCES "+TYPES_TABLE_NAME+"("+TYPES_NAME_COLUMN+"))");
        }
    }
}
