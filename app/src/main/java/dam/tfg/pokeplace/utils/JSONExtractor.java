package dam.tfg.pokeplace.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dam.tfg.pokeplace.models.Pokemon;

public class JSONExtractor {
    public static List<String> extractPokemonUrls(String jsonResponse) {
        List<String> pokemonUrlList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray results = jsonObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject pokemonObject = results.getJSONObject(i);
                String pokemonUrl=pokemonObject.getString("url");
                pokemonUrlList.add(pokemonUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pokemonUrlList;
    }
    public static Pokemon extractPokemon(String jsonResponse){
        Pokemon pokemon = new Pokemon();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            int id = jsonObject.getInt("id");
            String pokedexNumber=String.format(Locale.US,"%03d",id);
            String name=jsonObject.getString("name");
            String sprite=jsonObject.getJSONObject("sprites").getString("front_default");
            pokemon.setPokedexNumber(pokedexNumber);
            pokemon.setName(name);
            pokemon.setSprite(sprite);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pokemon;
    }
}
