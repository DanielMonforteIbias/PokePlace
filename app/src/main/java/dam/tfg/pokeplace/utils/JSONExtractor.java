package dam.tfg.pokeplace.utils;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.data.Data;
import dam.tfg.pokeplace.models.BasePokemon;
import dam.tfg.pokeplace.models.Move;
import dam.tfg.pokeplace.models.Pokemon;
import dam.tfg.pokeplace.models.Type;

public class JSONExtractor {
    private static final String TYPES_JSON_NAME="types.json";
    private static final String POKEMON_JSON_NAME="basepokemon.json";
    private static final String TAG="JSONEXTRACTOR";

    public static List<BasePokemon> extractBasePokemonList(Context c) {
        List<BasePokemon> basePokemonList=new ArrayList<>();
        try {
            InputStream is = c.getAssets().open(POKEMON_JSON_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray pokemonArray = new JSONArray(json);
            for (int i = 0; i < pokemonArray.length(); i++) {
                BasePokemon basePokemon=extractBasePokemon(pokemonArray.getJSONObject(i));
                if(basePokemon!=null) basePokemonList.add(basePokemon);
            }
        } catch (JSONException e)  {
            ToastUtil.showToast(c, c.getString(R.string.error_json_empty));
        }catch(IOException e){
            ToastUtil.showToast(c, c.getString(R.string.error_no_json));
        }
        return basePokemonList;
    }

    public static BasePokemon extractBasePokemon(JSONObject pokemonObject){
        try {
            int id = pokemonObject.getInt("id");
            String pokedexNumber=String.format(Locale.US,"%03d",id);
            String name=pokemonObject.getString("name");
            String sprite=pokemonObject.getString("sprite");
            String url=pokemonObject.getString("url");
            String type1=pokemonObject.getString("type_1");
            String type2=pokemonObject.optString("type_2",null); //opt porque puede no estar, en cuyo caso sera null
            return new BasePokemon(pokedexNumber,name,sprite,url,type1,type2);
        } catch (JSONException e) {
            Log.e(TAG,"Error: "+e.getMessage());
        }
        return null;
    }
    /*public static List<String> extractUrls(String jsonResponse) {
        List<String> urlList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray results = jsonObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject pokemonObject = results.getJSONObject(i);
                String pokemonUrl=pokemonObject.getString("url");
                urlList.add(pokemonUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlList;
    }
    public static BasePokemon extractBasePokemonAPI(String jsonResponse){
        BasePokemon pokemon = new BasePokemon();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            int id = jsonObject.getInt("id");
            String pokedexNumber=String.format(Locale.US,"%03d",id);
            String name=jsonObject.getString("name");
            String sprite=jsonObject.getJSONObject("sprites").getString("front_default");
            JSONArray typesArray=jsonObject.getJSONArray("types");
            String type1=typesArray.getJSONObject(0).getJSONObject("type").getString("name");
            String type2=null;
            if(typesArray.length()>1)type2=typesArray.getJSONObject(1).getJSONObject("type").getString("name");
            pokemon.setPokedexNumber(pokedexNumber);
            pokemon.setName(name);
            pokemon.setSprite(sprite);
            pokemon.setType1(type1);
            pokemon.setType2(type2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pokemon;
    }*/
    public static Pokemon extractPokemon(String jsonResponse){
        Pokemon pokemon = new Pokemon();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            int id = jsonObject.getInt("id");
            String pokedexNumber=String.format(Locale.US,"%03d",id);
            String name=jsonObject.getString("name");
            ArrayList<String> sprites=new ArrayList<>();
            JSONObject spritesObject=jsonObject.getJSONObject("sprites");
            String[] keys = {"front_default", "front_shiny", "back_default", "back_shiny"};//Los sprites que queremos
            for(String s:keys){
                String sprite = spritesObject.getString(s);
                if (!sprite.isEmpty() && !sprite.equals("null")) { //No todos los pokemon tienen los 4 sprites, algunos son nulos, en ese caso no lo añadimos
                    sprites.add(sprite);
                }
            }
            String sound=jsonObject.getJSONObject("cries").getString("latest");
            float height= (float) jsonObject.getDouble("height")/10; //Viene en decimetros, se pasa a metros
            float weight= (float) jsonObject.getDouble("weight")/10; //Viene en hectogramos, se pasa a kilogramos
            JSONArray typesArray=jsonObject.getJSONArray("types");
            Type[] types =new Type[2];
            for(int i=0;i<typesArray.length();i++){
                types[i]= Data.getInstance().getTypeByName(typesArray.getJSONObject(i).getJSONObject("type").getString("name"));
            }
            Map<String, Integer> stats=new LinkedHashMap<>(); //Debe ser Linked para mantener el orden de las stats
            JSONArray statsArray=jsonObject.getJSONArray("stats");
            for(int i=0;i<statsArray.length();i++){
                stats.put(statsArray.getJSONObject(i).getJSONObject("stat").getString("name"),statsArray.getJSONObject(i).getInt("base_stat"));
            }
            ArrayList<Move>moves=new ArrayList<Move>();
            JSONArray movesArray=jsonObject.getJSONArray("moves");
            for(int i=0;i<movesArray.length();i++){
                String url=movesArray.getJSONObject(i).getJSONObject("move").getString("url");
                moves.add(new Move(url));
            }
            pokemon.setPokedexNumber(pokedexNumber);
            pokemon.setName(name);
            pokemon.setSprites(sprites);
            pokemon.setSound(sound);
            pokemon.setHeight(height);
            pokemon.setWeight(weight);
            pokemon.setTypes(types);
            pokemon.setStats(stats);
            pokemon.setMoves(moves);
        } catch (Exception e) {
            Log.e(TAG,"Error: "+e.getMessage());
        }
        return pokemon;
    }

    /*public static Type extractTypeAPI(String jsonResponse){
        Type type=new Type();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            String name=jsonObject.getString("name");
            String sprite=jsonObject.getJSONObject("sprites").getJSONObject("generation-vi").getJSONObject("x-y").getString("name_icon"); //Se cogen los de gen6 porque estan todos menos stellar y unknown
            JSONObject damageRelations=jsonObject.getJSONObject("damage_relations");
            Iterator<String> keys = damageRelations.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONArray types = damageRelations.getJSONArray(key);
                ArrayList<String> typeNames = getEffectiveTypeNames(types);
                typeNames.sort(String::compareTo);
                switch (key) {
                    case "double_damage_to":
                        type.setDoubleDamageTo(typeNames);
                        break;
                    case "double_damage_from":
                        type.setDoubleDamageFrom(typeNames);
                        break;
                    case "half_damage_to":
                        type.setHalfDamageTo(typeNames);
                        break;
                    case "half_damage_from":
                        type.setHalfDamageFrom(typeNames);
                        break;
                    case "no_damage_to":
                        type.setNoDamageTo(typeNames);
                        break;
                    case "no_damage_from":
                        type.setNoDamageFrom(typeNames);
                        break;
                    default:
                        break;
                }
            }
            type.setName(name);
            type.setSprite(sprite);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }*/
    private static ArrayList<String> getEffectiveTypeNames(JSONArray jsonArray) throws JSONException {
        ArrayList<String> typeNames = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject typeObject = jsonArray.getJSONObject(i);
            typeNames.add(typeObject.getString("name"));
        }
        return typeNames;
    }
    public static List<Type> extractTypeList(Context context) {
        List<Type> typeList = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open(TYPES_JSON_NAME);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray typesArray = new JSONArray(json);
            for (int i = 0; i < typesArray.length(); i++) {
                Type type = extractType(typesArray.getJSONObject(i));
                if(type!=null) typeList.add(type);
            }

        } catch (IOException | JSONException e) {
            Log.e(TAG,"Error: "+e.getMessage());
        }
        return typeList;
    }
    public static Type extractType(JSONObject jsonObject) {
        try {
            Type type = new Type();
            String name = jsonObject.getString("name");
            String sprite = jsonObject.getString("sprite");
            JSONObject damageRelations = jsonObject.getJSONObject("damage_relations");
            Iterator<String> keys = damageRelations.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONArray types = damageRelations.getJSONArray(key);
                ArrayList<String> typeNames = getEffectiveTypeNames(types);
                typeNames.sort(String::compareTo);
                switch (key) {
                    case "double_damage_to":
                        type.setDoubleDamageTo(typeNames);
                        break;
                    case "double_damage_from":
                        type.setDoubleDamageFrom(typeNames);
                        break;
                    case "half_damage_to":
                        type.setHalfDamageTo(typeNames);
                        break;
                    case "half_damage_from":
                        type.setHalfDamageFrom(typeNames);
                        break;
                    case "no_damage_to":
                        type.setNoDamageTo(typeNames);
                        break;
                    case "no_damage_from":
                        type.setNoDamageFrom(typeNames);
                        break;
                }
            }
            type.setName(name);
            type.setSprite(sprite);
            return type;
        } catch (JSONException e) {
            Log.e(TAG,"Error: "+e.getMessage());
        }
        return null;
    }


    public static Move extractMove(String jsonResponse){
        Move move=new Move();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            int id=jsonObject.getInt("id");
            move.setId(id);
            String name=jsonObject.getString("name");
            //Usamos optInt en vez de getInt porque los valores pueden ser null, en ese caso devolvera 0
            int accuracy=jsonObject.optInt("accuracy");
            int power=jsonObject.optInt("power");
            int pp=jsonObject.optInt("pp");
            String damageClass=jsonObject.getJSONObject("damage_class").getString("name");
            Type type=Data.getInstance().getTypeByName(jsonObject.getJSONObject("type").getString("name"));
            JSONArray descriptions=jsonObject.getJSONArray("flavor_text_entries");
            String description="";
            for(int i=0;i<descriptions.length();i++){
                if(descriptions.getJSONObject(i).getJSONObject("language").getString("name").equals("en")){ //Cogemos la descripcion en ingles, que no  siempre esta en el mismo indice
                    description=descriptions.getJSONObject(i).getString("flavor_text");
                    break; //No seguimos recorriendo, ya tenemos la descripcion
                }
            }
            description=StringFormatter.removeLineBreaks(description); //Algunas descripciones tienen saltos de linea. Los quitamos
            move.setName(name);
            move.setAccuracy(accuracy);
            move.setPower(power);
            move.setPp(pp);
            move.setDamageClass(damageClass);
            move.setDescription(description);
            move.setType(type);
        } catch (Exception e) {
            Log.e(TAG,"Error: "+e.getMessage());
        }
        return move;
    }
    public static List<Pair<String,String>> extractDescriptions(String jsonResponse){
        List<Pair<String,String>> descriptions = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray descriptionsArray = jsonObject.getJSONArray("flavor_text_entries");
            for (int i = 0; i < descriptionsArray.length(); i++) {
                JSONObject description=descriptionsArray.getJSONObject(i);
                if(description.getJSONObject("language").getString("name").equals("en")){ //Cogemos la descripcion en ingles
                    String version=description.getJSONObject("version").getString("name");
                    String descriptionText=description.getString("flavor_text");
                    descriptions.add(new Pair<String,String>(version,descriptionText));
                }
            }
        } catch (Exception e) {
            Log.e(TAG,"Error: "+e.getMessage());
        }
        return descriptions;
    }
}
