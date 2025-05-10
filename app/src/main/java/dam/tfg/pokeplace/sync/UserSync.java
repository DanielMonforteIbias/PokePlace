package dam.tfg.pokeplace.sync;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dam.tfg.pokeplace.interfaces.OnUserDataDeletedListener;
import dam.tfg.pokeplace.interfaces.OnUserFetchedListener;
import dam.tfg.pokeplace.models.Team;
import dam.tfg.pokeplace.models.TeamPokemon;
import dam.tfg.pokeplace.models.User;

public class UserSync {
    private FirebaseFirestore db;

    public UserSync(){
        db= FirebaseFirestore.getInstance(); //Inicializamos la variable para la base de datos de Firestore
    }
    public void addUser(User user) {
        DocumentReference userDocument = db.collection(FirestorePaths.USERS_COLLECTION).document(user.getUserId()); //La colección de usuarios tendrá un documento para cada uno, con su id como nombre
        Map<String, Object> userMap = new HashMap<>(); //Creamos un mapa para poner los datos del usuario
        userMap.put(FirestorePaths.USER_ID, user.getUserId());
        userMap.put(FirestorePaths.USER_EMAIL, user.getEmail());
        userMap.put(FirestorePaths.USER_NAME,user.getName());
        userMap.put(FirestorePaths.USER_IMAGE,user.getImage());
        userMap.put(FirestorePaths.USER_FAV_TYPE,user.getFavType());
        userMap.put(FirestorePaths.USER_FAV_POKEMON,user.getFavPokemon());
        userDocument.set(userMap);
    }
    public void updateUser(User user) {
        DocumentReference userDocument = db.collection(FirestorePaths.USERS_COLLECTION).document(user.getUserId());
        Map<String, Object> userMap = new HashMap<>();
        userMap.put(FirestorePaths.USER_NAME,user.getName());
        userMap.put(FirestorePaths.USER_IMAGE,user.getImage());
        userMap.put(FirestorePaths.USER_FAV_TYPE,user.getFavType());
        userMap.put(FirestorePaths.USER_FAV_POKEMON,user.getFavPokemon());
        userDocument.update(userMap);
    }
    public void deleteUser(String userId, OnUserDataDeletedListener listener){
        //Antes de borrar el usuario, obtenemos la subcoleccion de equipos y de ella las subcolecciones de miembros para borrarlas a mano, ya que Firestore no lo hace automaticamente
        DocumentReference userRef = db.collection(FirestorePaths.USERS_COLLECTION).document(userId);
        CollectionReference teamsRef = db.collection(FirestorePaths.USERS_COLLECTION).document(userId).collection(FirestorePaths.TEAMS_COLLECTION);
        teamsRef.get().addOnSuccessListener(teamSnapshot -> {
            final int totalTeams = teamSnapshot.size();
            if (totalTeams == 0) { //Si no tiene equipos borramos el user directamente
                userRef.delete();
                listener.onUserDataDeleted();
                return;
            }
            final int[] deletedTeams = {0};
            for (DocumentSnapshot teamDoc : teamSnapshot) {
                CollectionReference membersRef = teamDoc.getReference().collection(FirestorePaths.MEMBERS_COLLECTION);
                membersRef.get().addOnSuccessListener(membersSnapshot -> {
                    final int totalMembers=membersSnapshot.size();
                    if(totalMembers==0){ //Si no tiene miembros, borramos directamente el equipo
                        teamDoc.getReference().delete().addOnSuccessListener(unused -> {
                            deletedTeams[0]++;
                            if (deletedTeams[0] == totalTeams) { //Borramos el usuario solo cuando se hayan borrado todos sus equipos
                                userRef.delete();
                                listener.onUserDataDeleted();
                            }
                        });
                    }
                    else{
                        WriteBatch batch = db.batch();
                        for (DocumentSnapshot memberDoc : membersSnapshot) {
                            batch.delete(memberDoc.getReference());
                        }
                        batch.commit().addOnSuccessListener(unused -> { //Borramos el equipo solo cuando se hayan borrado todos sus miembros
                            teamDoc.getReference().delete().addOnSuccessListener(unused2 -> {
                                deletedTeams[0]++;
                                if (deletedTeams[0] == totalTeams) { //Borramos el usuario solo cuando se hayan borrado todos sus equipos
                                    userRef.delete();
                                    listener.onUserDataDeleted();
                                }
                            });
                        });
                    }
                });
            }
        });
    }
    public void userExists(String userId, OnCompleteListener<DocumentSnapshot> listener) {
        DocumentReference userDocument = db.collection(FirestorePaths.USERS_COLLECTION).document(userId);
        userDocument.get().addOnCompleteListener(listener);
    }
    public void getUser(String userId, OnUserFetchedListener listener) {
        db.collection(FirestorePaths.USERS_COLLECTION).document(userId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String email = doc.getString(FirestorePaths.USER_EMAIL);
                String name = doc.getString(FirestorePaths.USER_NAME);
                String image = doc.getString(FirestorePaths.USER_IMAGE);
                String favType = doc.getString(FirestorePaths.USER_FAV_TYPE);
                String favPokemon = doc.getString(FirestorePaths.USER_FAV_POKEMON);
                User user = new User(userId, email, name, image);
                user.setFavType(favType);
                user.setFavPokemon(favPokemon);
                listener.onUserFetched(user);
            } else {
                listener.onUserFetched(null);
            }
        }).addOnFailureListener(e -> listener.onUserFetched(null));
        getUserTeamsAndMembers(userId,listener);
    }

    public void getUserTeamsAndMembers(String userId, OnUserFetchedListener listener) {
        CollectionReference teamsRef = db.collection(FirestorePaths.USERS_COLLECTION).document(userId).collection(FirestorePaths.TEAMS_COLLECTION);
        teamsRef.get().addOnSuccessListener(teamSnapshot -> {
            List<Team> teams = new ArrayList<>();
            Map<String, List<TeamPokemon>> teamMembersMap = new HashMap<>();
            if (teamSnapshot.isEmpty()) {
                listener.onTeamsFetched(teams, teamMembersMap);
                return;
            }
            final int totalTeams = teamSnapshot.size();
            final int[] processedTeams = {0};
            for (DocumentSnapshot teamDoc : teamSnapshot) {
                Long teamIdLong = teamDoc.getLong(FirestorePaths.TEAM_ID); //Long y no long por si es nulo
                String teamName = teamDoc.getString(FirestorePaths.TEAM_NAME);
                if(teamIdLong!=null){
                    int teamId=teamIdLong.intValue();
                    Team team = new Team(userId,teamId,teamName);
                    teams.add(team);
                    teamDoc.getReference().collection(FirestorePaths.MEMBERS_COLLECTION).get().addOnSuccessListener(membersSnapshot -> {
                        List<TeamPokemon> members = new ArrayList<>();
                        for (DocumentSnapshot memberDoc : membersSnapshot) {
                            String memberId = memberDoc.getString(FirestorePaths.TEAM_POKEMON_ID);
                            String pokedexNumber = memberDoc.getString(FirestorePaths.TEAM_POKEMON_POKEDEX_NUMBER);
                            String customName = memberDoc.getString(FirestorePaths.TEAM_POKEMON_CUSTOM_NAME);
                            String customSprite = memberDoc.getString(FirestorePaths.TEAM_POKEMON_CUSTOM_SPRITE);
                            if(memberId!=null){
                                TeamPokemon pokemon=new TeamPokemon(memberId,userId,teamId,customName,customSprite);
                                pokemon.setPokedexNumber(pokedexNumber);
                                members.add(pokemon);
                            }
                        }
                        teamMembersMap.put(String.valueOf(teamId), members);
                        processedTeams[0]++;
                        if (processedTeams[0] == totalTeams) {
                            listener.onTeamsFetched(teams, teamMembersMap);
                        }
                    });
                }
            }
        }).addOnFailureListener(e -> {
            listener.onTeamsFetched(new ArrayList<>(), new HashMap<>());
        });
    }



    public void addTeam(Team team) {
        DocumentReference teamDocument = db.collection(FirestorePaths.USERS_COLLECTION).document(team.getUserId()).collection(FirestorePaths.TEAMS_COLLECTION).document(String.valueOf(team.getTeamId()));
        Map<String, Object> teamMap = new HashMap<>();
        teamMap.put(FirestorePaths.TEAM_ID, team.getTeamId());
        teamMap.put(FirestorePaths.TEAM_NAME, team.getName());
        teamDocument.set(teamMap);
    }
    public void updateTeam(Team team) {
        DocumentReference teamDocument = db.collection(FirestorePaths.USERS_COLLECTION).document(team.getUserId()).collection(FirestorePaths.TEAMS_COLLECTION).document(String.valueOf(team.getTeamId()));
        Map<String, Object> teamMap = new HashMap<>();
        teamMap.put(FirestorePaths.TEAM_NAME,team.getName());
        teamDocument.update(teamMap);
    }
    public void deleteTeam(Team team){
        //Antes de borrar el equipo debemos borrar la subcoleccion de members, ya que Firestore no borra subcolecciones automáticamente
        CollectionReference membersRef = db.collection(FirestorePaths.USERS_COLLECTION).document(team.getUserId()).collection(FirestorePaths.TEAMS_COLLECTION).document(String.valueOf(team.getTeamId())).collection(FirestorePaths.MEMBERS_COLLECTION);
        membersRef.get().addOnSuccessListener(querySnapshot -> {
            WriteBatch batch = db.batch();
            for (DocumentSnapshot memberDoc : querySnapshot) {
                batch.delete(memberDoc.getReference());
            }
            batch.commit().addOnSuccessListener(unused -> {
                db.collection(FirestorePaths.USERS_COLLECTION).document(team.getUserId()).collection(FirestorePaths.TEAMS_COLLECTION).document(String.valueOf(team.getTeamId())).delete();
            });
        });
    }
    public void addTeamPokemon(TeamPokemon teamPokemon) {
        DocumentReference teamPokemonDocument = db.collection(FirestorePaths.USERS_COLLECTION).document(teamPokemon.getUserId()).collection(FirestorePaths.TEAMS_COLLECTION).document(String.valueOf(teamPokemon.getTeamId())).collection(FirestorePaths.MEMBERS_COLLECTION).document(String.valueOf(teamPokemon.getId()));
        Map<String, Object> teamPokemonMap = new HashMap<>();
        teamPokemonMap.put(FirestorePaths.TEAM_POKEMON_ID, teamPokemon.getId());
        teamPokemonMap.put(FirestorePaths.TEAM_POKEMON_POKEDEX_NUMBER, teamPokemon.getPokedexNumber());
        teamPokemonMap.put(FirestorePaths.TEAM_POKEMON_CUSTOM_NAME, teamPokemon.getCustomName());
        teamPokemonMap.put(FirestorePaths.TEAM_POKEMON_CUSTOM_SPRITE, teamPokemon.getCustomSprite());
        teamPokemonDocument.set(teamPokemonMap);
    }
    public void updateTeamPokemon(TeamPokemon teamPokemon) {
        DocumentReference teamPokemonDocument = db.collection(FirestorePaths.USERS_COLLECTION).document(teamPokemon.getUserId()).collection(FirestorePaths.TEAMS_COLLECTION).document(String.valueOf(teamPokemon.getTeamId())).collection(FirestorePaths.MEMBERS_COLLECTION).document(String.valueOf(teamPokemon.getId()));
        Map<String, Object> teamPokemonMap = new HashMap<>();
        teamPokemonMap.put(FirestorePaths.TEAM_POKEMON_POKEDEX_NUMBER, teamPokemon.getPokedexNumber());
        teamPokemonMap.put(FirestorePaths.TEAM_POKEMON_CUSTOM_NAME, teamPokemon.getCustomName());
        teamPokemonMap.put(FirestorePaths.TEAM_POKEMON_CUSTOM_SPRITE, teamPokemon.getCustomSprite());
        teamPokemonDocument.update(teamPokemonMap);
    }
    public void deleteTeamPokemon(TeamPokemon teamPokemon){
        DocumentReference teamPokemonDocument = db.collection(FirestorePaths.USERS_COLLECTION).document(teamPokemon.getUserId()).collection(FirestorePaths.TEAMS_COLLECTION).document(String.valueOf(teamPokemon.getTeamId())).collection(FirestorePaths.MEMBERS_COLLECTION).document(String.valueOf(teamPokemon.getId()));
        teamPokemonDocument.delete();
    }
}
