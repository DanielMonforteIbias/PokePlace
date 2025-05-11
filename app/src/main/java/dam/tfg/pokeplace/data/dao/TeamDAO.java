package dam.tfg.pokeplace.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dam.tfg.pokeplace.data.DatabaseHelper;
import dam.tfg.pokeplace.data.DatabaseManager;
import dam.tfg.pokeplace.models.Team;

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
    public void updateTeam(Team team){
        values=new ContentValues();
        values.put(DatabaseHelper.TEAM_NAME_COLUMN,team.getName());
        String where=DatabaseHelper.TEAM_USER_ID_COLUMN+"=? AND "+DatabaseHelper.TEAM_ID_COLUMN+"=?";
        db.update(DatabaseHelper.TEAMS_TABLE_NAME,values,where,new String[]{team.getUserId(),String.valueOf(team.getTeamId())});
    }
    public void removeTeam(String teamId){
        String condition = DatabaseHelper.TEAM_ID_COLUMN+"=?"; //Condicion para el borrado
        String[] conditionArgs = {teamId}; //Ponemos los parámetros recibidos en los ? de la condicion anterior
        db.delete(DatabaseHelper.TEAMS_TABLE_NAME, condition,conditionArgs);
    }
    public boolean teamExists(String teamId){
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TEAMS_TABLE_NAME + " WHERE "+DatabaseHelper.TEAM_ID_COLUMN+"=?", new String[]{teamId});
        boolean exists = false;
        if(cursor.moveToFirst()) {
            int count=cursor.getInt(0);
            if(count>0) exists=true;
        }
        cursor.close(); //Cerramos el cursor
        return exists; //Devolvemos si existe o no
    }
    public Team getTeam(String teamId){
        Team team=new Team();
        Cursor cursor=db.rawQuery("SELECT * FROM "+DatabaseHelper.TEAMS_TABLE_NAME+" WHERE "+DatabaseHelper.TEAM_ID_COLUMN+"=?",new String[]{teamId});
        if(cursor.moveToNext()){
            team.setUserId(cursor.getString(0));
            team.setTeamId(cursor.getString(1));
            team.setName(cursor.getString(2));
        }
        cursor.close();
        return team;
    }
    public List<Team> getAllTeams(String userId){
        List<Team>teams=new ArrayList<>();
        Cursor cursor=db.rawQuery("SELECT * FROM "+DatabaseHelper.TEAMS_TABLE_NAME+" WHERE "+DatabaseHelper.TEAM_USER_ID_COLUMN+"=?",new String[]{userId});
        while (cursor.moveToNext()) { //Recorremos el cursor
            String teamId=cursor.getString(1);
            String teamName=cursor.getString(2);
            teams.add(new Team(userId,teamId,teamName));
        }
        cursor.close(); //Cerramos el cursor
        return teams;
    }
    public String getNewTeamId(){
        return UUID.randomUUID().toString();
    }
}
