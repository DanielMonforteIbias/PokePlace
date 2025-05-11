package dam.tfg.pokeplace.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Team implements Parcelable {
    private String userId;
    private String teamId;
    private String name;
    private ArrayList<TeamPokemon> teamMembers;

    public Team(){}

    public Team(String userId, String teamId, String name){
        this.userId=userId;
        this.teamId=teamId;
        this.name=name;
        teamMembers=new ArrayList<>();
    }

    protected Team(Parcel in) {
        userId = in.readString();
        teamId = in.readString();
        name = in.readString();
        teamMembers = in.createTypedArrayList(TeamPokemon.CREATOR);
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

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<TeamPokemon> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(ArrayList<TeamPokemon> teamMembers) {
        this.teamMembers = teamMembers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(teamId);
        dest.writeString(name);
        dest.writeTypedList(teamMembers);
    }
}
