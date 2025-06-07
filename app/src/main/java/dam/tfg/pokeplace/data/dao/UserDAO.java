package dam.tfg.pokeplace.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import dam.tfg.pokeplace.data.DatabaseHelper;
import dam.tfg.pokeplace.data.DatabaseManager;
import dam.tfg.pokeplace.models.User;

public class UserDAO {
    private final SQLiteDatabase db;
    private ContentValues values=null;

    public UserDAO(Context context) {
        db=DatabaseManager.getInstance(context).openDatabase();
    }

    public void addUser(User user) {
        values=new ContentValues();
        values.put(DatabaseHelper.USER_ID_COLUMN, user.getUserId());
        values.put(DatabaseHelper.USER_EMAIL_COLUMN, user.getEmail());
        values.put(DatabaseHelper.USER_NAME_COLUMN, user.getName());
        values.put(DatabaseHelper.USER_IMAGE_COLUMN, user.getImage());
        values.put(DatabaseHelper.USER_FAV_TYPE_COLUMN,user.getFavType());
        values.put(DatabaseHelper.USER_FAV_POKEMON_COLUMN,user.getFavPokemon());
        db.insert(DatabaseHelper.USERS_TABLE_NAME, null, values);
    }
    public void deleteUser(String userId) {
        String condition = DatabaseHelper.USER_ID_COLUMN+"=?";
        String[] conditionArgs = {userId};
        db.delete(DatabaseHelper.USERS_TABLE_NAME, condition,conditionArgs);
    }
    public void updateUser(User user){
        values = new ContentValues();
        values.put(DatabaseHelper.USER_NAME_COLUMN, user.getName());
        values.put(DatabaseHelper.USER_IMAGE_COLUMN,user.getImage());
        values.put(DatabaseHelper.USER_FAV_TYPE_COLUMN,user.getFavType());
        values.put(DatabaseHelper.USER_FAV_POKEMON_COLUMN,user.getFavPokemon());
        String where=DatabaseHelper.USER_ID_COLUMN+"=?";
        db.update(DatabaseHelper.USERS_TABLE_NAME,values,where,new String[]{user.getUserId()});
    }
    public boolean userExists(String userId){
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.USERS_TABLE_NAME + " WHERE "+DatabaseHelper.USER_ID_COLUMN+" = ?", new String[]{userId});
        boolean exists = false;
        if(cursor.moveToFirst()) { //Si hay resultado en la consulta
            int count=cursor.getInt(0); //Obtenemos el primer dato que hay (solo habrá uno ya que es un count)
            if(count>0) exists=true; //Si el valor es mayor que 0, es que el usuario existe, así que ponemos la variable a true
        }
        cursor.close(); //Cerramos el cursor
        return exists; //Devolvemos si existe o no
    }
    public User getUser(String userId){
        User user=new User();
        Cursor cursor=db.rawQuery("SELECT * FROM "+DatabaseHelper.USERS_TABLE_NAME+" WHERE "+DatabaseHelper.USER_ID_COLUMN+"=?",new String[]{userId});
        if(cursor.moveToNext()){
            user.setUserId(cursor.getString(0));
            user.setEmail(cursor.getString(1));
            user.setName(cursor.getString(2));
            user.setImage(cursor.getString(3));
            user.setFavType(cursor.getString(4));
            user.setFavPokemon(cursor.getString(5));
        }
        cursor.close();
        return user;
    }
}