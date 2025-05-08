package dam.tfg.pokeplace.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import dam.tfg.pokeplace.data.DatabaseHelper;
import dam.tfg.pokeplace.data.DatabaseManager;
import dam.tfg.pokeplace.models.Type;

public class TypeRelationDAO {
    private final SQLiteDatabase db;
    private ContentValues values=null;

    public TypeRelationDAO(Context context) {
        db= DatabaseManager.getInstance(context).openDatabase();
    }

    public void addTypeRelation(String sourceType, String targetType, String relation) {
        String complementaryRelation=getComplementaryRelation(relation);
        /*Evitamos relaciones duplicadas. No serian el mismo registro exactamente, pero representarian lo mismo
        Por ejemplo, si existe source: fire, target: grass, relation: double_damage_to, podemos ahorrarnos el registro
        que seria source: grass, target: fire, relation:double_damage_from
        */
        if(!relationExists(targetType,sourceType,complementaryRelation)) {
            values=new ContentValues();
            values.put(DatabaseHelper.TYPE_RELATIONS_SOURCE_TYPE, sourceType);
            values.put(DatabaseHelper.TYPE_RELATIONS_TARGET_TYPE, targetType);
            values.put(DatabaseHelper.TYPE_RELATIONS_RELATION, relation);
            db.insert(DatabaseHelper.TYPE_RELATIONS_TABLE_NAME, null, values);
        }
    }
    public Type loadRelationsForType(Type type) {
        String typeName = type.getName();
        Cursor cursor = db.query(DatabaseHelper.TYPE_RELATIONS_TABLE_NAME, null, DatabaseHelper.TYPE_RELATIONS_SOURCE_TYPE+" = ? OR "+DatabaseHelper.TYPE_RELATIONS_TARGET_TYPE +"= ?", new String[]{typeName, typeName}, null, null, null);
        while (cursor.moveToNext()) {
            String source = cursor.getString(0);
            String target = cursor.getString(1);
            String relation = cursor.getString(2);
            if (source.equals(typeName)) {
                assignRelation(type, target, relation, false);
            } else {
                assignRelation(type, source, relation, true);
            }
        }
        cursor.close();
        return type;
    }
    private void assignRelation(Type type, String targetType, String relation, boolean isTarget) {
        boolean isSameType = type.getName().equalsIgnoreCase(targetType); //Para controlar que se añada en ambas listas si la relacion es contra si mismo
        switch (relation) {
            case "double_damage_from":
                if (isSameType || !isTarget) type.getDoubleDamageFrom().add(targetType);
                if (isSameType || isTarget)  type.getDoubleDamageTo().add(targetType);
                break;
            case "double_damage_to":
                if (isSameType || !isTarget) type.getDoubleDamageTo().add(targetType);
                if (isSameType || isTarget)  type.getDoubleDamageFrom().add(targetType);
                break;
            case "half_damage_from":
                if (isSameType || !isTarget) type.getHalfDamageFrom().add(targetType);
                if (isSameType || isTarget)  type.getHalfDamageTo().add(targetType);
                break;
            case "half_damage_to":
                if (isSameType || !isTarget) type.getHalfDamageTo().add(targetType);
                if (isSameType || isTarget)  type.getHalfDamageFrom().add(targetType);
                break;
            case "no_damage_from":
                if (isSameType || !isTarget) type.getNoDamageFrom().add(targetType);
                if (isSameType || isTarget)  type.getNoDamageTo().add(targetType);
                break;
            case "no_damage_to":
                if (isSameType || !isTarget) type.getNoDamageTo().add(targetType);
                if (isSameType || isTarget)  type.getNoDamageFrom().add(targetType);
                break;
        }
    }
    private String getComplementaryRelation(String relation) {
        switch (relation) {
            case "double_damage_to":
                return "double_damage_from";
            case "double_damage_from":
                return "double_damage_to";
            case "half_damage_to":
                return "half_damage_from";
            case "half_damage_from":
                return "half_damage_to";
            case "no_damage_to":
                return "no_damage_from";
            case "no_damage_from":
                return "no_damage_to";
            default:
                return "";
        }
    }

    private boolean relationExists(String sourceType, String targetType, String relation) {
        Cursor cursor = db.query(DatabaseHelper.TYPE_RELATIONS_TABLE_NAME, null, DatabaseHelper.TYPE_RELATIONS_SOURCE_TYPE+" = ? AND "+DatabaseHelper.TYPE_RELATIONS_TARGET_TYPE+" = ? AND "+DatabaseHelper.TYPE_RELATIONS_RELATION+" = ?", new String[]{sourceType, targetType, relation}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }
}
