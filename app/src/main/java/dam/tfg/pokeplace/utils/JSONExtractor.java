package dam.tfg.pokeplace.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dam.tfg.pokeplace.data.Data;
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
    public static Pokemon extractPokemon(String jsonResponse){
        Pokemon pokemon = new Pokemon();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            int id = jsonObject.getInt("id");
            String pokedexNumber=String.format(Locale.US,"%03d",id);
            String name=jsonObject.getString("name");
            name=name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase(); //Ponemos la primera en mayuscula
            ArrayList<String> sprites=new ArrayList<>();
            sprites.add(jsonObject.getJSONObject("sprites").getString("front_default"));
            sprites.add(jsonObject.getJSONObject("sprites").getString("front_shiny"));
            sprites.add(jsonObject.getJSONObject("sprites").getString("back_default"));
            sprites.add(jsonObject.getJSONObject("sprites").getString("back_shiny"));
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
            pokemon.setPokedexNumber(pokedexNumber);
            pokemon.setName(name);
            pokemon.setSprites(sprites);
            pokemon.setSound(sound);
            pokemon.setHeight(height);
            pokemon.setWeight(weight);
            pokemon.setTypes(types);
            pokemon.setStats(stats);
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
            type.setId(id);
            type.setName(name);
            type.setSprite(sprite);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }
}
