package dam.tfg.pokeplace.data;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
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
    public List<Pair<String, String>> getTypeCombinationsWithMultiplier(Type type, String mode,double targetMultiplier) {
        List<Pair<String, String>> result = new ArrayList<>();
        BiFunction<Type,Type,Double> effectivenessFunction; //Una funcion que recibe dos Types y devuelve un double. Nuestras funciones cumplen eso, y asi elegimos la funcion a usar en base a mode
        if (mode.equalsIgnoreCase("attacker")) {
            effectivenessFunction = this::getTypeEffectivenessTo;
        }else if (mode.equalsIgnoreCase("defender")) {
            effectivenessFunction = this::getTypeEffectivenessFrom;
        }else{
            throw new IllegalArgumentException("Modo inválido: debe ser attacker o defender");
        }
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
