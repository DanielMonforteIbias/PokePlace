package dam.tfg.pokeplace;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import dam.tfg.pokeplace.data.dao.UserDAO;
import dam.tfg.pokeplace.databinding.ActivityMainBinding;
import dam.tfg.pokeplace.models.Team;
import dam.tfg.pokeplace.models.User;
import dam.tfg.pokeplace.utils.ToastUtil;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private ImageView imgFoto;
    private TextView txtNombre;
    private TextView txtEmail;
    private Button btnLogout;

    private String providerId="";
    private GoogleSignInClient gClient;
    private GoogleSignInOptions gOptions;

    private UserDAO userDAO;
    public static User user;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_pokedex, R.id.nav_teams, R.id.nav_type_calculator).setOpenableLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        userDAO=new UserDAO(this);

        txtNombre = headerView.findViewById(R.id.txtUser);
        txtEmail = headerView.findViewById(R.id.txtEmailUser);
        btnLogout = headerView.findViewById(R.id.btnLogout);
        imgFoto = headerView.findViewById(R.id.imgUser);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user=userDAO.getUser(firebaseUser.getUid());

        providerId = firebaseUser.getProviderData().get(1).getProviderId();
        if (providerId.equals("google.com")) { //Si el proveedor es Google
            gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            gClient = GoogleSignIn.getClient(this, gOptions);
        }
        updateUserUI();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                switch (providerId) {
                    case "google.com":
                        gClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.i(TAG,"Sesion de Google cerrada");
                            }
                        });
                        break;
                    case "password":
                        break;
                    default:
                        ToastUtil.showToast(getApplicationContext(),getString(R.string.error_logout));
                        break;
                }
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
    public void updateUserUI(){
        txtNombre.setText(user.getName());
        txtEmail.setText(user.getEmail());
        Glide.with(this).load(user.getImage()).placeholder(R.drawable.icon1).into(imgFoto);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_credits) {
            displayCredits();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }
    private void displayCredits(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_credits, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        Button btnClose = dialogView.findViewById(R.id.btnCloseCredits);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}