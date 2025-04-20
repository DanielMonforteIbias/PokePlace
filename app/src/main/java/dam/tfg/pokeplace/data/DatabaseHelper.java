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
    /*VERSION
    Version 1: Tabla Users
    Version 2: Tabla Teams
    */
    private static final int databaseVersion=2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME,null,databaseVersion); //Pasamos el nombre y la version
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creacion tabla usuarios
        String createTableUsers="CREATE TABLE "+USERS_TABLE_NAME+" ("+USER_ID_COLUMN+" TEXT PRIMARY KEY,"+USER_EMAIL_COLUMN+" TEXT NOT NULL, "+USER_NAME_COLUMN+" TEXT, "+USER_IMAGE_COLUMN+"TEXT)";
        db.execSQL(createTableUsers);
        //Creacion tabla equipos
        String createTableTeams="CREATE TABLE "+TEAMS_TABLE_NAME+" ("+TEAM_USER_ID_COLUMN+" TEXT,"+TEAM_ID_COLUMN+" INTEGER, "+TEAM_NAME_COLUMN+" TEXT, PRIMARY KEY("+TEAM_USER_ID_COLUMN+","+TEAM_ID_COLUMN+"), FOREIGN KEY("+TEAM_USER_ID_COLUMN+") REFERENCES "+USERS_TABLE_NAME+"("+USER_ID_COLUMN+"))";
        db.execSQL(createTableTeams);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<2) db.execSQL("CREATE TABLE "+TEAMS_TABLE_NAME+" ("+TEAM_USER_ID_COLUMN+" TEXT,"+TEAM_ID_COLUMN+" INTEGER, "+TEAM_NAME_COLUMN+" TEXT, PRIMARY KEY("+TEAM_USER_ID_COLUMN+","+TEAM_ID_COLUMN+"), FOREIGN KEY("+TEAM_USER_ID_COLUMN+") REFERENCES "+USERS_TABLE_NAME+"("+USER_ID_COLUMN+"))");
    }
}
