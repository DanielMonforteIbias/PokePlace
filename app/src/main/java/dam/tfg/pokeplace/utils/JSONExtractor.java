package dam.tfg.pokeplace.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dam.tfg.pokeplace.data.Data;
import dam.tfg.pokeplace.models.BasePokemon;
import dam.tfg.pokeplace.models.Move;
import dam.tfg.pokeplace.models.Pokemon;
import dam.tfg.pokeplace.models.Type;

public class JSONExtractor {
    public static List<String> extractUrls(String jsonResponse) {
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

    public static BasePokemon extractBasePokemon(String jsonResponse){
        BasePokemon pokemon = new BasePokemon();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            int id = jsonObject.getInt("id");
            String pokedexNumber=String.format(Locale.US,"%03d",id);
            String name=jsonObject.getString("name");
            ArrayList<String> sprites=new ArrayList<>();
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
    }
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
                if (sprite!=null && !sprite.isEmpty() && !sprite.equals("null")) { //No todos los pokemon tienen los 4 sprites, algunos son nulos, en ese caso no lo añadimos
                    sprites.add(sprite);
                }
            }
            String sound=jsonObject.getJSONObject("cries").getString("latest");
            float height= (float) jsonObject.getDouble("height")/10; //Viene en decimetros, se pasa a metros
            float weight= (float) jsonObject.getDouble("weight")/10; //Viene en hectogramos, se pasa a kilogramos
            JSONArray typesArray=jsonObject.getJSONArray("types");
            Type types[]=new Type[2];
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
            e.printStackTrace();
        }
        return pokemon;
    }

    public static Type extractType(String jsonResponse){
        Type type=new Type();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            int id = jsonObject.getInt("id");
            String name=jsonObject.getString("name");
            String sprite=jsonObject.getJSONObject("sprites").getJSONObject("generation-vi").getJSONObject("x-y").getString("name_icon"); //Se cogen los de gen6 porque estan todos menos stellar y unknown
            JSONObject damageRelations=jsonObject.getJSONObject("damage_relations");
            Iterator<String> keys = damageRelations.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONArray types = damageRelations.getJSONArray(key);
                ArrayList<String> typeNames = getTypeNames(types);
                Collections.sort(typeNames,(t1, t2) -> t1.compareTo(t2));
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
            type.setId(id);
            type.setName(name);
            type.setSprite(sprite);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }
    private static ArrayList<String> getTypeNames(JSONArray jsonArray) throws JSONException {
        ArrayList<String> typeNames = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject typeObject = jsonArray.getJSONObject(i);
            typeNames.add(typeObject.getString("name"));
        }
        return typeNames;
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
            e.printStackTrace();
            System.out.println("Move: "+move.getId());
        }
        return move;
    }
}
