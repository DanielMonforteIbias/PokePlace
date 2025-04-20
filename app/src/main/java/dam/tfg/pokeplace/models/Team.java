package dam.tfg.pokeplace.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Team implements Parcelable {
    private String userId;
    private int teamId;
    private String name;
    private ArrayList<Pokemon> pokemons;
    public Team(){}
    public Team(String userId, int teamId, String name){
        this.userId=userId;
        this.teamId=teamId;
        this.name=name;
    }

    protected Team(Parcel in) {
        userId = in.readString();
        teamId = in.readInt();
        name = in.readString();
        pokemons = in.createTypedArrayList(Pokemon.CREATOR);
    }

    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        @Override
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Pokemon> getPokemons() {
        return pokemons;
    }

    public void setPokemons(ArrayList<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeInt(teamId);
        dest.writeString(name);
        dest.writeTypedList(pokemons);
    }
}
