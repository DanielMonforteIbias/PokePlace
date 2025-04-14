package dam.tfg.pokeplace.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Pokemon implements Parcelable {
    private String pokedexNumber;
    private String name;
    private ArrayList<String>sprites;
    private String sound;
    private ArrayList<String> abilities;
    private Map<String, Integer> stats;
    private Type types[]=new Type[2];
    private float height, weight;

    public Pokemon(){

    }
    public Pokemon(String pokedexNumber, String name, ArrayList<String> sprites) {
        this.pokedexNumber = pokedexNumber;
        this.name = name;
        this.sprites = sprites;
    }

    protected Pokemon(Parcel in) {
        pokedexNumber = in.readString();
        name = in.readString();
        sprites = in.createStringArrayList();
        sound = in.readString();
        abilities = in.createStringArrayList();
        types = in.createTypedArray(Type.CREATOR);
        height = in.readFloat();
        weight = in.readFloat();
        //Leemos el map de stats, primero el tamaño y luego cada entrada
        int size = in.readInt();
        stats = new LinkedHashMap<>(); //Debe ser Linked para mantener el orden de las stats
        for (int i = 0; i < size; i++) {
            String key = in.readString();
            int value = in.readInt();
            stats.put(key, value);
        }
    }

    public static final Creator<Pokemon> CREATOR = new Creator<Pokemon>() {
        @Override
        public Pokemon createFromParcel(Parcel in) {
            return new Pokemon(in);
        }

        @Override
        public Pokemon[] newArray(int size) {
            return new Pokemon[size];
        }
    };

    public String getPokedexNumber() {
        return pokedexNumber;
    }

    public void setPokedexNumber(String pokedexNumber) {
        this.pokedexNumber = pokedexNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getSprites() {
        return sprites;
    }
    public void setSprites(ArrayList<String> sprites) {
        this.sprites = sprites;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public ArrayList<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(ArrayList<String> abilities) {
        this.abilities = abilities;
    }

    public Map<String, Integer> getStats() {
        return stats;
    }

    public void setStats(Map<String, Integer> stats) {
        this.stats = stats;
    }

    public Type[] getTypes() {
        return types;
    }

    public void setTypes(Type[] types) {
        this.types = types;
    }
    public float getHeight() {
        return height;
    }
    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(pokedexNumber);
        dest.writeString(name);
        dest.writeStringList(sprites);
        dest.writeString(sound);
        dest.writeStringList(abilities);
        dest.writeTypedArray(types, flags);
        dest.writeFloat(height);
        dest.writeFloat(weight);
        //Ponemos en el parcel el map, primero el tamaño y luego cada par
        dest.writeInt(stats.size());
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeInt(entry.getValue());
        }
    }
}
