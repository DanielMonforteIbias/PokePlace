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
import dam.tfg.pokeplace.models.Type;
import dam.tfg.pokeplace.models.User;

public class TypeDAO {
    private final SQLiteDatabase db;
    private ContentValues values=null;

    public TypeDAO(Context context) {
        db= DatabaseManager.getInstance(context).openDatabase();
    }

    public void addType(Type type) {
        values=new ContentValues();
        values.put(DatabaseHelper.TYPES_NAME_COLUMN, type.getName());
        values.put(DatabaseHelper.TYPES_SPRITE_COLUMN, type.getSprite());
        db.insert(DatabaseHelper.TYPES_TABLE_NAME, null, values);
    }
    public Type getType(String typeName){
        Type type=new Type();
        Cursor cursor=db.rawQuery("SELECT * FROM "+DatabaseHelper.TYPES_TABLE_NAME+" WHERE "+DatabaseHelper.TYPES_NAME_COLUMN+"=?",new String[]{typeName});
        if(cursor.moveToNext()){
            type.setName(cursor.getString(0));
            type.setSprite(cursor.getString(1));
        }
        cursor.close();
        return type;
    }
    public List<Type> getTypes() {
        List<Type>types=new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TYPES_TABLE_NAME, null, null, null, null, null, DatabaseHelper.TYPES_NAME_COLUMN+" ASC");
        if (cursor.moveToFirst()) {
            do {
                String name=cursor.getString(0);
                String sprite=cursor.getString(1);
                types.add(new Type(name,sprite));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return types;
    }
}
