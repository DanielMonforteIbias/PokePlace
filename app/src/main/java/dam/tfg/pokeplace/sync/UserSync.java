package dam.tfg.pokeplace.sync;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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
    public void deleteUser(String userId){
        DocumentReference userRef = db.collection(FirestorePaths.USERS_COLLECTION).document(userId); //Obtenemos el documento del usuario
        userRef.delete();
    }
    public void userExists(String userId, OnCompleteListener<DocumentSnapshot> listener) {
        DocumentReference userDocument = db.collection(FirestorePaths.USERS_COLLECTION).document(userId);
        userDocument.get().addOnCompleteListener(listener);
    }
}
