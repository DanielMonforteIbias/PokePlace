package dam.tfg.pokeplace.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import dam.tfg.pokeplace.data.DatabaseHelper;
import dam.tfg.pokeplace.data.DatabaseManager;
import dam.tfg.pokeplace.models.Team;
import dam.tfg.pokeplace.models.User;

public class TeamDAO {
    private final SQLiteDatabase db;
    private ContentValues values=null;

    public TeamDAO(Context context) {
        db= DatabaseManager.getInstance(context).openDatabase();
    }

    public void addTeam(Team team) {
        values=new ContentValues();
        values.put(DatabaseHelper.TEAM_USER_ID_COLUMN, team.getUserId());
        values.put(DatabaseHelper.TEAM_ID_COLUMN, team.getTeamId());
        values.put(DatabaseHelper.TEAM_NAME_COLUMN, team.getName());
        db.insert(DatabaseHelper.TEAMS_TABLE_NAME, null, values);
    }
    public void changeTeamName(Team team){
        values=new ContentValues();
        values.put(DatabaseHelper.TEAM_NAME_COLUMN,team.getName());
        String where=DatabaseHelper.TEAM_USER_ID_COLUMN+"=? AND "+DatabaseHelper.TEAM_ID_COLUMN+"=?";
        db.update(DatabaseHelper.TEAMS_TABLE_NAME,values,where,new String[]{team.getUserId(),String.valueOf(team.getTeamId())});
    }
    public void removeTeam(String userId, int teamId){
        String condition = DatabaseHelper.TEAM_USER_ID_COLUMN+"=? AND "+DatabaseHelper.TEAM_ID_COLUMN+"=?"; //Condicion para el borrado
        String conditionArgs[] = { userId, String.valueOf(teamId) }; //Ponemos los parámetros recibidos en los ? de la condicion anterior
        db.delete(DatabaseHelper.TEAMS_TABLE_NAME, condition,conditionArgs);
    }

    public Team getTeam(String userId, int teamId){
        Team team=new Team();
        Cursor cursor=db.rawQuery("SELECT * FROM "+DatabaseHelper.TEAMS_TABLE_NAME+" WHERE "+DatabaseHelper.TEAM_USER_ID_COLUMN+"=? AND "+DatabaseHelper.TEAM_ID_COLUMN+"=?",new String[]{userId,String.valueOf(teamId)});
        if(cursor.moveToNext()){
            team.setUserId(cursor.getString(0));
            team.setTeamId(cursor.getInt(1));
            team.setName(cursor.getString(2));
        }
        cursor.close();
        return team;
    }
    public List<Team> getAllTeams(String userId){
        List<Team>teams=new ArrayList<>();
        Cursor cursor=db.rawQuery("SELECT * FROM "+DatabaseHelper.TEAMS_TABLE_NAME+" WHERE "+DatabaseHelper.TEAM_USER_ID_COLUMN+"=?",new String[]{userId});
        while (cursor.moveToNext()) { //Recorremos el cursor
            int teamId=cursor.getInt(1);
            String teamName=cursor.getString(2);
            teams.add(new Team(userId,teamId,teamName));
        }
        cursor.close(); //Cerramos el cursor
        return teams;
    }
    public int getNewTeamId(String userId){
        int newId=1;
        Cursor cursor=db.rawQuery("SELECT MAX("+DatabaseHelper.TEAM_ID_COLUMN+") FROM "+DatabaseHelper.TEAMS_TABLE_NAME+" WHERE "+DatabaseHelper.TEAM_USER_ID_COLUMN+"=?",new String[]{userId});
        if(cursor.moveToFirst()) { //Si hay resultado en la consulta
            newId=cursor.getInt(0)+1; //El nuevo id sera el del maximo +1
        }
        cursor.close(); //Cerramos el cursor
        return newId;
    }
}
