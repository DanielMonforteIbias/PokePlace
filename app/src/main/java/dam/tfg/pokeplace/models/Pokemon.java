package dam.tfg.pokeplace.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Pokemon implements Parcelable {
    private String pokedexNumber;
    private String name;
    private ArrayList<String>sprites;
    private String sound;
    private ArrayList<String> abilities;
    private ArrayList<Move> moves;
    private Map<String, Integer> stats;
    private Type types[]=new Type[2];
    private float height, weight;
    private List<Pair<String,String>> descriptions; //Lista de parejas (first la version del juego, second la descripcion de ese juego)

    public Pokemon(){
        this.sprites = new ArrayList<>();
        this.moves=new ArrayList<>();
        this.descriptions=new ArrayList<>();
    }
    public Pokemon(String pokedexNumber, String name, ArrayList<String> sprites) {
        this.pokedexNumber = pokedexNumber;
        this.name = name;
        this.sprites = sprites;
        this.moves=new ArrayList<>();
        this.descriptions=new ArrayList<>();
    }

    protected Pokemon(Parcel in) {
        pokedexNumber = in.readString();
        name = in.readString();
        sprites = in.createStringArrayList();
        sound = in.readString();
        abilities = in.createStringArrayList();
        moves = in.createTypedArrayList(Move.CREATOR);
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
        //Leemos el tamaño de la lista y los valores de la pareja en orden (ya que Pair no es Parcelable de por si)
        int descriptionsSize = in.readInt();
        descriptions = new ArrayList<>();
        for (int i = 0; i < descriptionsSize; i++) {
            String descriptionGame = in.readString();
            String descriptionText = in.readString();
            descriptions.add(new Pair<>(descriptionGame, descriptionText));
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pokedexNumber);
        dest.writeString(name);
        dest.writeStringList(sprites);
        dest.writeString(sound);
        dest.writeStringList(abilities);
        dest.writeTypedList(moves);
        dest.writeTypedArray(types, flags);
        dest.writeFloat(height);
        dest.writeFloat(weight);
        //Ponemos en el parcel el map, primero el tamaño y luego cada par
        dest.writeInt(stats.size());
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeInt(entry.getValue());
        }
        dest.writeInt(descriptions.size()); //Ponemos tambien el tamaño de la lista
        for (Pair<String, String> description : descriptions) {
            dest.writeString(description.first);
            dest.writeString(description.second);
        }
    }

    @Override
    public int describeContents() {
        return 0;
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

    public ArrayList<Move> getMoves() {
        return moves;
    }

    public void setMoves(ArrayList<Move> moves) {
        this.moves = moves;
    }

    public List<Pair<String, String>> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<Pair<String, String>> descriptions) {
        this.descriptions = descriptions;
    }
}
