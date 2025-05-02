package dam.tfg.pokeplace.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {
    private String userId;
    private String email;
    private String name;
    private String image;
    private String favType;
    private String favPokemon;
    public User(){

    }
    public User(String userId, String email, String name, String image) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.image = image;
    }

    protected User(Parcel in) {
        userId = in.readString();
        email = in.readString();
        name = in.readString();
        image = in.readString();
        favType=in.readString();
        favPokemon=in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFavType() {
        return favType;
    }

    public void setFavType(String favType) {
        this.favType = favType;
    }

    public String getFavPokemon() {
        return favPokemon;
    }

    public void setFavPokemon(String favPokemon) {
        this.favPokemon = favPokemon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(favType);
        dest.writeString(favPokemon);
    }
}
