package dam.tfg.pokeplace.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Type implements Parcelable {
    private int id;
    private String name;
    private String sprite;
    private ArrayList<String> doubleDamageFrom=new ArrayList<>();
    private ArrayList<String> doubleDamageTo=new ArrayList<>();
    private ArrayList<String> halfDamageFrom=new ArrayList<>();
    private ArrayList<String> halfDamageTo=new ArrayList<>();
    private ArrayList<String> noDamageFrom=new ArrayList<>();
    private ArrayList<String> noDamageTo=new ArrayList<>();
    public Type(){}
    public Type(int id, String name, String sprite) {
        this.id=id;
        this.name = name;
        this.sprite = sprite;
    }

    protected Type(Parcel in) {
        id = in.readInt();
        name = in.readString();
        sprite = in.readString();
        doubleDamageFrom = in.createStringArrayList();
        doubleDamageTo = in.createStringArrayList();
        halfDamageFrom = in.createStringArrayList();
        halfDamageTo = in.createStringArrayList();
        noDamageFrom = in.createStringArrayList();
        noDamageTo = in.createStringArrayList();
    }

    public static final Creator<Type> CREATOR = new Creator<Type>() {
        @Override
        public Type createFromParcel(Parcel in) {
            return new Type(in);
        }

        @Override
        public Type[] newArray(int size) {
            return new Type[size];
        }
    };

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getDoubleDamageFrom() {
        return doubleDamageFrom;
    }

    public void setDoubleDamageFrom(ArrayList<String> doubleDamageFrom) {
        this.doubleDamageFrom = doubleDamageFrom;
    }

    public ArrayList<String> getDoubleDamageTo() {
        return doubleDamageTo;
    }

    public void setDoubleDamageTo(ArrayList<String> doubleDamageTo) {
        this.doubleDamageTo = doubleDamageTo;
    }

    public ArrayList<String> getHalfDamageFrom() {
        return halfDamageFrom;
    }

    public void setHalfDamageFrom(ArrayList<String> halfDamageFrom) {
        this.halfDamageFrom = halfDamageFrom;
    }

    public ArrayList<String> getHalfDamageTo() {
        return halfDamageTo;
    }

    public void setHalfDamageTo(ArrayList<String> halfDamageTo) {
        this.halfDamageTo = halfDamageTo;
    }

    public ArrayList<String> getNoDamageFrom() {
        return noDamageFrom;
    }

    public void setNoDamageFrom(ArrayList<String> noDamageFrom) {
        this.noDamageFrom = noDamageFrom;
    }

    public ArrayList<String> getNoDamageTo() {
        return noDamageTo;
    }

    public void setNoDamageTo(ArrayList<String> noDamageTo) {
        this.noDamageTo = noDamageTo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(sprite);
        dest.writeStringList(doubleDamageFrom);
        dest.writeStringList(doubleDamageTo);
        dest.writeStringList(halfDamageFrom);
        dest.writeStringList(halfDamageTo);
        dest.writeStringList(noDamageFrom);
        dest.writeStringList(noDamageTo);
    }
}
