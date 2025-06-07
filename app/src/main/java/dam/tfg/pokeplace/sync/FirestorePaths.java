package dam.tfg.pokeplace.sync;

public class FirestorePaths {
    //Collections
    public static final String USERS_COLLECTION="users";
    public static final String TEAMS_COLLECTION="teams";
    public static final String MEMBERS_COLLECTION="members";
    //User attributes
    public static final String USER_ID="userId";
    public static final String USER_EMAIL="email";
    public static final String USER_NAME="name";
    public static final String USER_IMAGE="image";
    public static final String USER_FAV_TYPE="favType";
    public static final String USER_FAV_POKEMON="favPokemon";
    //Team attributes
    public static final String TEAM_ID="teamId";
    public static final String TEAM_NAME="name";
    //Members attributes
    public static final String TEAM_POKEMON_ID="teamPokemonId";
    public static final String TEAM_POKEMON_POKEDEX_NUMBER="pokedexNumber";
    public static final String TEAM_POKEMON_CUSTOM_NAME="customName";
    public static final String TEAM_POKEMON_CUSTOM_SPRITE="customSprite";
}
