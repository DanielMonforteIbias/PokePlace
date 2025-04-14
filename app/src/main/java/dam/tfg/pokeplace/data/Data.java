package dam.tfg.pokeplace.data;

import java.util.ArrayList;
import java.util.List;

import dam.tfg.pokeplace.models.Pokemon;
import dam.tfg.pokeplace.models.Type;

public class Data {
    private static Data instance;

    private List<Pokemon> pokemonList = new ArrayList<>();
    private List<Type> typeList = new ArrayList<>();

    private Data() {}

    public static synchronized Data getInstance() {
        if (instance == null) {
            instance = new Data();
        }
        return instance;
    }

    public void setPokemonList(List<Pokemon> list) {
        this.pokemonList = list;
    }

    public List<Pokemon> getPokemonList() {
        return pokemonList;
    }

    public void setTypeList(List<Type> list) {
        this.typeList = list;
    }

    public List<Type> getTypeList() {
        return typeList;
    }

    public Type getTypeByName(String name) {
        for (Type type : typeList) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
