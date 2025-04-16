package dam.tfg.pokeplace.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<String> getTypeNamesList() {
        return typeList.stream().map(Type::getName).collect(Collectors.toList());
    }
    public List<String>getNormalDamageFrom(Type type){
        List<String>normalDamageFrom=getTypeNamesList();
        normalDamageFrom.removeAll(type.getDoubleDamageFrom());
        normalDamageFrom.removeAll(type.getHalfDamageFrom());
        normalDamageFrom.removeAll(type.getNoDamageFrom());
        return normalDamageFrom;
    }
    public List<String>getNormalDamageTo(Type type){
        List<String>normalDamageTo=getTypeNamesList();
        normalDamageTo.removeAll(type.getDoubleDamageTo());
        normalDamageTo.removeAll(type.getHalfDamageTo());
        normalDamageTo.removeAll(type.getNoDamageTo());
        return normalDamageTo;
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
