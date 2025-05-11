package dam.tfg.pokeplace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import dam.tfg.pokeplace.data.dao.UserDAO;
import dam.tfg.pokeplace.data.service.TeamService;
import dam.tfg.pokeplace.databinding.ActivityLoginBinding;
import dam.tfg.pokeplace.interfaces.OnUserFetchedListener;
import dam.tfg.pokeplace.models.Team;
import dam.tfg.pokeplace.models.TeamPokemon;
import dam.tfg.pokeplace.models.User;
import dam.tfg.pokeplace.sync.FirestorePaths;
import dam.tfg.pokeplace.sync.UserSync;
import dam.tfg.pokeplace.utils.BaseActivity;
import dam.tfg.pokeplace.utils.ToastUtil;

public class LoginActivity extends BaseActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;

    private GoogleSignInOptions gOptions;
    private GoogleSignInClient gClient;
    private ActivityLoginBinding binding;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    private UserDAO userDAO;
    private TeamService teamService;
    private UserSync userSync;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        userDAO=new UserDAO(this);
        teamService=new TeamService(getApplicationContext());
        userSync=new UserSync(getApplicationContext());
        auth = FirebaseAuth.getInstance();
        if(checkLoginStatus()){
            binding.main.setVisibility(View.GONE);
            login();
        }

        gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        gClient = GoogleSignIn.getClient(this, gOptions);
        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = gClient.getSignInIntent();
                activityResultLauncher.launch(signInIntent);
            }
        });
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) { //Se ejecutará al obtener resultado del intent
                if (result.getResultCode() == LoginActivity.RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        String idToken = account.getIdToken();
                        if (idToken != null) {
                            // Got an ID token from Google. Use it to authenticate
                            // with Firebase.
                            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                            auth.signInWithCredential(firebaseCredential)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) { //Al completar la tarea
                                            if (task.isSuccessful()) { //Si ha sido exitosa
                                                login();
                                            }
                                        }
                                    });
                        } else
                            Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT).show();
                    } catch (ApiException e) {
                        Toast.makeText(getApplicationContext(), R.string.error_login_api, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=binding.editTextEmail.getText().toString();
                String password=binding.editTextPassword.getText().toString();
                boolean registroValido=true;
                if(email.isEmpty()){
                    registroValido=false;
                    showToast(getString(R.string.error_empty_email));
                }
                else if (!validEmail(email)){
                    registroValido=false;
                    showToast(getString(R.string.error_email));
                }
                if(password.isEmpty()){
                    registroValido=false;
                    showToast(getString(R.string.error_empty_password));
                }
                else if(password.length()<6){
                    registroValido=false;
                    showToast(getString(R.string.error_password_length));
                }
                if(registroValido){
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                showToast(getString(R.string.successful_register));
                            }
                            else{
                                if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) showToast(getString(R.string.error_register_email));
                                else if(task.getException() instanceof FirebaseAuthUserCollisionException) showToast(getString(R.string.error_register_exists));
                                else showToast(getString(R.string.error_register));
                            }
                        }
                    });
                }
            }
        });

        //Login
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=binding.editTextEmail.getText().toString();
                String password=binding.editTextPassword.getText().toString();
                boolean loginValido=true;
                if(email.isEmpty()){
                    loginValido=false;
                    showToast(getString(R.string.error_empty_email));
                }
                else if (!validEmail(email)){
                    loginValido=false;
                    showToast(getString(R.string.error_email));
                }
                if(password.isEmpty()){
                    loginValido=false;
                    showToast(getString(R.string.error_empty_password));
                }
                if(loginValido){
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                login();
                            }
                            else{
                                if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) showToast(getString(R.string.error_login_credentials));
                            }
                        }
                    });
                }
            }
        });

    }

    private boolean checkLoginStatus() {
        FirebaseUser currentUser = auth.getCurrentUser(); //Obtenemos el usuario con sesión iniciada
        return currentUser != null; //Devuelve true si hay cuenta, es decir, si no es null, y false si es null
    }
    private void login() {
        user = auth.getCurrentUser();
        if (user != null) {
            String name = (user.getDisplayName() != null) ? user.getDisplayName() : getString(R.string.default_name);
            String image = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : getString(R.string.default_image);
            User newUser = new User(user.getUid(), user.getEmail(), name, image);
            AtomicInteger pendingTasks = new AtomicInteger(0);
            userSync.userExists(user.getUid(), new OnCompleteListener<DocumentSnapshot>() { //Comprobamos que existe en Firestore
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) { //Si existe en firestore, lo traemos a local
                            pendingTasks.addAndGet(2);// Vamos a lanzar dos tareas: onUserFetched y onTeamsFetched
                            userSync.getUser(document.getString(FirestorePaths.USER_ID), new OnUserFetchedListener() {
                                @Override
                                public void onUserFetched(User user) {
                                    if (!userDAO.userExists(user.getUserId())) userDAO.addUser(user);
                                    else userDAO.updateUser(user); //Si existe, lo actualizamos con los datos remotos, por si hicimos cambios en otro dispositivo mientras ya estábamos en este, para poder verlos aqui tambien
                                    if (pendingTasks.decrementAndGet() == 0) openMainActivity();
                                }
                                @Override
                                public void onTeamsFetched(List<Team> teams, Map<String, List<TeamPokemon>> teamMembersMap) {
                                    //Borramos los equipos que ya no existan en Firestore
                                    Set<String> remoteTeamIds = teams.stream().map(Team::getTeamId).collect(Collectors.toSet());
                                    for (Team localTeam : teamService.getAllTeams(user.getUid())) {
                                        if (!remoteTeamIds.contains(localTeam.getTeamId())) teamService.removeTeam(localTeam.getTeamId());
                                    }
                                    //Insertamos o actualizamos los equipos
                                    for (Team team : teams) {
                                        if (!teamService.teamExists(team.getTeamId())) {
                                            if(teamService.getAllTeams(team.getUserId()).size()<teamService.teamsLimit) teamService.addTeam(team);
                                            else showToast(getString(R.string.error_syncing_teams_size,team.getName()));
                                        }
                                        else teamService.updateTeam(team);
                                        List<TeamPokemon> members = teamMembersMap.get(String.valueOf(team.getTeamId()));
                                        if (members != null) {
                                            //Borramos los que ya no existan en Firestore
                                            Set<String> remoteMemberIds = members.stream().map(TeamPokemon::getId).collect(Collectors.toSet());
                                            for (TeamPokemon localMember : teamService.getTeamWithMembers(team.getTeamId()).getTeamMembers()) {
                                                if (!remoteMemberIds.contains(localMember.getId())) teamService.removeTeamPokemon(localMember.getId());
                                            }

                                            //Insertamos o actualizamos los mimebros
                                            for (TeamPokemon pokemon : members) {
                                                if (!teamService.teamPokemonExists(pokemon.getId())) teamService.addTeamPokemon(pokemon);
                                                else teamService.updateTeamPokemon(pokemon);
                                            }
                                        }
                                    }
                                    if (pendingTasks.decrementAndGet() == 0) openMainActivity();
                                }
                            });
                        } else { //Si no existe, lo añadimos a Firestore y a la bd local si tampoco existe
                            userSync.addUser(newUser);
                            if (!userDAO.userExists(newUser.getUserId())) userDAO.addUser(newUser);
                            openMainActivity();
                        }
                    } else { //Si no se puede completar la tarea, por lo menos lo metemos en la bd local si no estaba
                        if (!userDAO.userExists(newUser.getUserId())) userDAO.addUser(newUser);
                        showToast(getString(R.string.error_syncing_no_connection));
                        openMainActivity();
                    }
                }
            });
        } else showToast(getString(R.string.error_login));
    }

    public void openMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    public boolean validEmail(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches(); //Devolvemos el resultado de si el correo concuerda con el patrón o no
    }

    private void showToast(String s){
        ToastUtil.showToast(getApplicationContext(),s);
    }
}