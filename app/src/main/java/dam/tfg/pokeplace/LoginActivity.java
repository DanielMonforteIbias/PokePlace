package dam.tfg.pokeplace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
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

import dam.tfg.pokeplace.data.dao.UserDAO;
import dam.tfg.pokeplace.databinding.ActivityLoginBinding;
import dam.tfg.pokeplace.models.User;
import dam.tfg.pokeplace.utils.BaseActivity;
import dam.tfg.pokeplace.utils.ToastUtil;

public class LoginActivity extends BaseActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;

    private BeginSignInRequest signInRequest;
    private GoogleSignInOptions gOptions;
    private GoogleSignInClient gClient;
    private ActivityLoginBinding binding;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    private UserDAO userDAO;
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
        auth = FirebaseAuth.getInstance();
        if(checkLoginStatus()){
            iniciarSesion();
        }
        //Sincronizar con Firebase
        signInRequest = BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true).build()).build();
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
                                                iniciarSesion();
                                            }
                                        }
                                    });
                        } else
                            Toast.makeText(getApplicationContext(), R.string.error_inicio_sesion, Toast.LENGTH_SHORT).show();
                    } catch (ApiException e) {
                        Toast.makeText(getApplicationContext(), R.string.error_inicio_sesion_api, Toast.LENGTH_SHORT).show();
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
                if(email.equals("")){
                    registroValido=false;
                    showToast(getString(R.string.error_email_vacio));
                }
                else if (!correoValido(email)){
                    registroValido=false;
                    showToast(getString(R.string.error_email));
                }
                if(password.equals("")){
                    registroValido=false;
                    showToast(getString(R.string.error_contrasena_vacia));
                }
                else if(password.length()<6){
                    registroValido=false;
                    showToast(getString(R.string.error_longitud_contrasena));
                }
                if(registroValido){
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                showToast(getString(R.string.exito_registro));
                            }
                            else{
                                System.out.println(task.getException());
                                if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) showToast(getString(R.string.error_registro_correo));
                                else if(task.getException() instanceof FirebaseAuthUserCollisionException) showToast(getString(R.string.error_registro_ya_existe));
                                else showToast(getString(R.string.error_registro));
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
                if(email.equals("")){
                    loginValido=false;
                    showToast(getString(R.string.error_email_vacio));
                }
                else if (!correoValido(email)){
                    loginValido=false;
                    showToast(getString(R.string.error_email));
                }
                if(password.equals("")){
                    loginValido=false;
                    showToast(getString(R.string.error_contrasena_vacia));
                }
                if(loginValido){
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                iniciarSesion();
                            }
                            else{
                                System.out.println(task.getException());
                                if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) showToast(getString(R.string.error_login_credenciales));
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

    private void iniciarSesion(){
        user = auth.getCurrentUser();
        if(!userDAO.userExists(user.getUid())) { //Si el usuario no existe en la base de datos
            String name= (user.getDisplayName()!=null)?user.getDisplayName():getString(R.string.default_name);
            String image= (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : getString(R.string.default_image);
            userDAO.addUser(new User(user.getUid(),user.getEmail(),name,image));
        }
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    public boolean correoValido(String correo){
        return Patterns.EMAIL_ADDRESS.matcher(correo).matches(); //Devolvemos el resultado de si el correo concuerda con el patrón o no
    }

    private void showToast(String s){
        ToastUtil.showToast(getApplicationContext(),s);
    }
}