package dam.tfg.pokeplace.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class TeamPokemon extends BasePokemon implements Parcelable {
    private int id;
    private String userId;
    private int teamId;
    private String customName;
    private String customSprite;
    public TeamPokemon(){}
    public TeamPokemon(int id, String userId, int teamId, String customName, String customSprite) {
        this.id = id;
        this.userId = userId;
        this.teamId = teamId;
        this.customName = customName;
        this.customSprite = customSprite;
    }

    public TeamPokemon(String pokedexNumber, String name, String sprite, String url, String type1, String type2, int id, String userId, int teamId, String customName, String customSprite) {
        super(pokedexNumber, name, sprite, url, type1, type2);
        this.id = id;
        this.userId = userId;
        this.teamId = teamId;
        this.customName = customName;
        this.customSprite = customSprite;
    }

    protected TeamPokemon(Parcel in) {
        super(in);
        id = in.readInt();
        userId = in.readString();
        teamId = in.readInt();
        customName = in.readString();
        customSprite = in.readString();
    }

    public static final Creator<TeamPokemon> CREATOR = new Creator<TeamPokemon>() {
        @Override
        public TeamPokemon createFromParcel(Parcel in) {
            return new TeamPokemon(in);
        }

        @Override
        public TeamPokemon[] newArray(int size) {
            return new TeamPokemon[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String getCustomSprite() {
        return customSprite;
    }

    public void setCustomSprite(String customSprite) {
        this.customSprite = customSprite;
    }

    public void completeBaseData(BasePokemon base) {
        this.setName(base.getName());
        this.setSprite(base.getSprite());
        this.setUrl(base.getUrl());
        this.setType1(base.getType1());
        this.setType2(base.getType2());
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(id);
        dest.writeString(userId);
        dest.writeInt(teamId);
        dest.writeString(customName);
        dest.writeString(customSprite);
    }
}
