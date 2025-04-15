package dam.tfg.pokeplace.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Move implements Parcelable {
    private int id;
    private String url;
    private String name;
    private String damageClass;
    private String description;
    private Type type;
    private int power;
    private int pp;
    private int accuracy;

    public Move(int id, String name, String damageClass, String description, Type type, int power, int pp, int accuracy) {
        this.id = id;
        this.name = name;
        this.damageClass = damageClass;
        this.description = description;
        this.type = type;
        this.power = power;
        this.pp = pp;
        this.accuracy = accuracy;
    }
    public Move(){}
    public Move (String url){
        this.url=url;
    }

    protected Move(Parcel in) {
        id = in.readInt();
        url = in.readString();
        name = in.readString();
        damageClass = in.readString();
        description = in.readString();
        type = in.readParcelable(Type.class.getClassLoader());
        power = in.readInt();
        pp = in.readInt();
        accuracy = in.readInt();
    }

    public static final Creator<Move> CREATOR = new Creator<Move>() {
        @Override
        public Move createFromParcel(Parcel in) {
            return new Move(in);
        }

        @Override
        public Move[] newArray(int size) {
            return new Move[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDamageClass() {
        return damageClass;
    }

    public void setDamageClass(String damageClass) {
        this.damageClass = damageClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getPp() {
        return pp;
    }

    public void setPp(int pp) {
        this.pp = pp;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(url);
        dest.writeString(name);
        dest.writeString(damageClass);
        dest.writeString(description);
        dest.writeParcelable(type, flags);
        dest.writeInt(power);
        dest.writeInt(pp);
        dest.writeInt(accuracy);
    }
}
