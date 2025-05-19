package dam.tfg.pokeplace.data;

import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import dam.tfg.pokeplace.data.dao.BasePokemonDAO;
import dam.tfg.pokeplace.data.service.TypeService;
import dam.tfg.pokeplace.models.BasePokemon;
import dam.tfg.pokeplace.models.Type;
import dam.tfg.pokeplace.utils.JSONExtractor;

public class Data {
    private static Data instance;

    private List<BasePokemon> pokemonList = new ArrayList<>();
    private List<Type> typeList = new ArrayList<>();

    private Data() {}

    public static synchronized Data getInstance() {
        if (instance == null) {
            instance = new Data();
        }
        return instance;
    }
    public void loadTypes(Context context){
        if(this.typeList.isEmpty()){ //Si la lista está vacia
            TypeService typeService=new TypeService(context);
            this.typeList.addAll(typeService.getAllTypes()); //Cogemos los tipos de la base de datos
            if (this.typeList.isEmpty()){ //Si sigue estando vacia
                this.typeList=JSONExtractor.extractTypeList(context); //Los cogemos del JSON
                typeService.addAllTypes(this.typeList); //Los añadimos a la BD
            }
            //Si estaban en la base de datos ya se habran añadido
        }
        //Si no está vacia no los añadimos para no duplicar
    }
    public void loadPokemon(Context context){
        if(this.pokemonList.isEmpty()){ //Si la lista de Pokémon está vacía
            BasePokemonDAO basePokemonDAO=new BasePokemonDAO(context);
            this.pokemonList.addAll(basePokemonDAO.getBasePokemonList()); //Cogemos los Pokémon de la BD
            if(this.pokemonList.isEmpty()){ //Si sigue estando vacia
                this.pokemonList= JSONExtractor.extractBasePokemonList(context); //Cogemos los Pokémon del archivo
                this.pokemonList.forEach(basePokemonDAO::addBasePokemon); //Añadimos los Pokémon a la BD
            }//Si estaban en la base de datos ya se habran añadido
        }
        //Si no está vacia no los añadimos para no duplicar
    }

    public List<BasePokemon> getPokemonList() {
        return pokemonList;
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
    public Type getPokemonByName(String name) {
        for (Type type : typeList) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
    public List<Pair<String, String>> getTypeCombinationsWithMultiplier(Type type, String mode,double targetMultiplier) {
        if(type!=null){
            List<Pair<String, String>> result = new ArrayList<>();
            BiFunction<Type,Type,Double> effectivenessFunction = null; //Una funcion que recibe dos Types y devuelve un double. Nuestras funciones cumplen eso, y asi elegimos la funcion a usar en base a mode
            boolean validMode=true;
            if (mode.equalsIgnoreCase("attacker")) {
                effectivenessFunction = this::getTypeEffectivenessTo;
            }else if (mode.equalsIgnoreCase("defender")) {
                effectivenessFunction = this::getTypeEffectivenessFrom;
            }else{
                validMode=false;
            }
            if(validMode){
                for (int i=0;i<typeList.size();i++) {
                    Type t1=typeList.get(i);
                    double multiplier1 = effectivenessFunction.apply(type, t1);
                    for (int j=i;j<typeList.size();j++) {
                        Type t2=typeList.get(j);
                        if(t1.getName().equals(t2.getName())){ //Si los dos tipos son iguales, es solo un tipo
                            if(multiplier1==targetMultiplier)result.add(new Pair<>(t1.getName(), null)); //Añadimos una Pair con un solo tipo si el multiplicador es el esperado
                        }else{
                            double multiplier2=effectivenessFunction.apply(type, t2);
                            double combinedMultiplier=multiplier1 * multiplier2;
                            if (combinedMultiplier==targetMultiplier)result.add(new Pair<>(t1.getName(), t2.getName()));
                        }
                    }
                }
                return result;
            }
            else return new ArrayList<Pair<String,String>>();
        }
        else return new ArrayList<Pair<String,String>>();
    }
    private double getTypeEffectivenessTo(Type t1, Type t2) {
        if(t1.getDoubleDamageTo().contains(t2.getName())) return DamageMultiplier.VERY_EFFECTIVE.multiplier;
        else if(t1.getHalfDamageTo().contains(t2.getName())) return DamageMultiplier.NOT_EFFECTIVE.multiplier;
        else if(t1.getNoDamageTo().contains(t2.getName())) return DamageMultiplier.NO_EFFECT.multiplier;
        else return DamageMultiplier.NEUTRAL.multiplier;
    }
    private double getTypeEffectivenessFrom(Type t1, Type t2) {
        if(t1.getDoubleDamageFrom().contains(t2.getName())) return DamageMultiplier.VERY_EFFECTIVE.multiplier;
        else if(t1.getHalfDamageFrom().contains(t2.getName())) return DamageMultiplier.NOT_EFFECTIVE.multiplier;
        else if(t1.getNoDamageFrom().contains(t2.getName())) return DamageMultiplier.NO_EFFECT.multiplier;
        else return DamageMultiplier.NEUTRAL.multiplier;
    }
}
