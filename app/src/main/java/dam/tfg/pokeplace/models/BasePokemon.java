package dam.tfg.pokeplace.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class BasePokemon implements Parcelable {
    private String pokedexNumber;
    private String name;
    private String sprite;
    private String url;
    private String type1;
    private String type2;
    public BasePokemon(){}
    public BasePokemon(String pokedexNumber, String name, String sprite, String url,String type1, String type2) {
        this.pokedexNumber = pokedexNumber;
        this.name = name;
        this.sprite = sprite;
        this.url = url;
        this.type1=type1;
        this.type2=type2;
    }

    protected BasePokemon(Parcel in) {
        pokedexNumber = in.readString();
        name = in.readString();
        sprite = in.readString();
        url = in.readString();
        type1 = in.readString();
        type2 = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pokedexNumber);
        dest.writeString(name);
        dest.writeString(sprite);
        dest.writeString(url);
        dest.writeString(type1);
        dest.writeString(type2);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BasePokemon> CREATOR = new Creator<BasePokemon>() {
        @Override
        public BasePokemon createFromParcel(Parcel in) {
            return new BasePokemon(in);
        }

        @Override
        public BasePokemon[] newArray(int size) {
            return new BasePokemon[size];
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

    public String getSprite() {
        return sprite;
    }

    public void setSprite(String sprite) {
        this.sprite = sprite;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

}
