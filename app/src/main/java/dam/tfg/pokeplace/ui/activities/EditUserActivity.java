package dam.tfg.pokeplace.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.adapters.IconAdapter;
import dam.tfg.pokeplace.adapters.PokemonSpinnerAdapter;
import dam.tfg.pokeplace.adapters.TypeSpinnerAdapter;
import dam.tfg.pokeplace.data.Data;
import dam.tfg.pokeplace.data.dao.UserDAO;
import dam.tfg.pokeplace.data.service.TeamService;
import dam.tfg.pokeplace.databinding.ActivityEditUserBinding;
import dam.tfg.pokeplace.interfaces.DialogConfigurator;
import dam.tfg.pokeplace.interfaces.OnUserDataDeletedListener;
import dam.tfg.pokeplace.models.BasePokemon;
import dam.tfg.pokeplace.models.Team;
import dam.tfg.pokeplace.models.Type;
import dam.tfg.pokeplace.models.User;
import dam.tfg.pokeplace.sync.UserSync;
import dam.tfg.pokeplace.utils.DownloadUrlImage;
import dam.tfg.pokeplace.utils.FilesUtils;
import dam.tfg.pokeplace.utils.PermissionManager;

public class EditUserActivity extends BaseActivity {
    private ActivityEditUserBinding binding;
    private User user;
    private UserDAO userDAO;
    private UserSync userSync;
    private TeamService teamService;
    private Uri cameraImageUri;

    private List<String> icons;
    private IconAdapter adapter;

    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<Intent> selectImageActivityLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityEditUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        userDAO=new UserDAO(this);
        userSync=new UserSync(this);
        teamService=new TeamService(getApplicationContext());
        Intent intent=getIntent();
        if(savedInstanceState!=null) user=savedInstanceState.getParcelable("User"); //Si estamos recreando la actividad cogemos el usuario, que puede tener alguna modificacion no guardada
        else{ //Si no, lo cogemos de la bd con el id recibido
            String userId=intent.getStringExtra("userId");
            user=userDAO.getUser(userId);
        }
        loadIcons();
        adapter=new IconAdapter(icons,this);
        updateUserUI();
        binding.txtNameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChangeNameDialog();
            }
        });
        binding.imgUserEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChangeImageDialog();
            }
        });
        binding.btnChangeNameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChangeNameDialog();
            }
        });
        binding.btnChangeImageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChangeImageDialog();
            }
        });
        binding.btnChangeFavTypeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChangeFavTypeDialog();
            }
        });
        binding.imgFavTypeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChangeFavTypeDialog();
            }
        });
        binding.btnChangeFavPokemonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChangeFavPokemonDialog();
            }
        });
        binding.imgFavPokemonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChangeFavPokemonDialog();
            }
        });
        binding.btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDAO.updateUser(user);
                userSync.updateUser(user);
                setResult(RESULT_OK);
                finish();
            }
        });
        binding.btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDeleteUserDialog();
            }
        });
        selectImageActivityLauncher=registerForActivityResult( //Cuando termine la actividad de seleccionar imagen
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Uri imageUri = data.getData();
                                if (imageUri != null) {
                                    String mimeType = getContentResolver().getType(imageUri); //Obtenemos el tipo de imagen
                                    System.out.println(mimeType);
                                    if("image/svg+xml".equalsIgnoreCase(mimeType)) showToast(getString(R.string.not_supported)); //Los svg no se aceptan
                                    /*Los gif de galeria funcionan la primera vez, pero al reabrir la app la Uri pierde permisos y no se muestra, por lo que se ha decidido no permitirlos de galeria,
                                    pero sí de URL
                                    Para que funcionen de galeria habria que usar action open document en vez de action pick y permitir la persistencia de permisos, pero eso hace que se abra
                                    el explorador de archivos en vez de galería, y se ha tomado la decisión de que queda más claro abrir la galería y no merece la pena cambiarlo por un formato
                                    Los gif pueden ser tanto gif como webp animados, ambos darian error. Los webp normales funcionan*/
                                    else if("image/gif".equalsIgnoreCase(mimeType) || ("image/webp".equalsIgnoreCase(mimeType) && FilesUtils.isAnimatedWebp(EditUserActivity.this,imageUri))) showToast(getString(R.string.gif_gallery));
                                    else user.setImage(imageUri.toString());
                                }else{
                                    user.setImage(cameraImageUri.toString());
                                }
                            }
                            updateUserUI();
                        }
                    }
                });
        takePictureLauncher= registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean success) {
                        if (success && cameraImageUri != null) {
                            user.setImage(cameraImageUri.toString());
                            updateUserUI();
                        }
                    }
                }
        );
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("User",user);
    }

    private void displayChangeNameDialog(){
        showCustomDialog(R.layout.dialog_change_name, false, new DialogConfigurator() {
            @Override
            public void configure(AlertDialog dialog, View dialogView) {
                EditText input = dialogView.findViewById(R.id.editTextChangeName);
                input.setText(user.getName());
                Button btnCancel = dialogView.findViewById(R.id.btnCancelChangeName);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button btnAccept=dialogView.findViewById(R.id.btnAcceptChangeName);
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newName = input.getText().toString().trim();
                        if (!newName.isEmpty()) {
                            user.setName(newName);
                            updateUserUI();
                            dialog.dismiss();
                        } else {
                            showToast(getText(R.string.error_empty_name).toString());
                        }
                    }
                });
            }
        });
    }

    private void displayChangeImageDialog(){
        showCustomDialog(R.layout.dialog_change_image, false, new DialogConfigurator() {
            @Override
            public void configure(AlertDialog dialog, View dialogView) {
                Button btnGallery = dialogView.findViewById(R.id.btnGalleryChangeImage);
                btnGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(PermissionManager.checkStoragePermissions(EditUserActivity.this)){
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //El intent abre las fotos ya existentes
                            galleryIntent.setType("image/*");
                            selectImageActivityLauncher.launch(galleryIntent);
                        }
                        else{
                            showToast(getString(R.string.storage_permission_denied));
                            PermissionManager.requestStoragePermissions(EditUserActivity.this);
                        }
                        dialog.dismiss();
                    }
                });
                Button btnCamera = dialogView.findViewById(R.id.btnCameraChangeImage);
                btnCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(PermissionManager.checkCameraPermissions(getApplicationContext())){
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.DISPLAY_NAME, "foto_" + System.currentTimeMillis() + ".jpg");
                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + getString(R.string.app_name));
                            cameraImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            takePictureLauncher.launch(cameraImageUri);
                        }
                        else{
                            showToast(getString(R.string.camera_permission_denied));
                            PermissionManager.requestCameraPermissions(EditUserActivity.this);
                        }
                        dialog.dismiss();
                    }
                });
                Button btnUrl = dialogView.findViewById(R.id.btnUrlChangeImage);
                btnUrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        EditUserActivity.this.dialogActive=false; //Ponemos que no hay dialogo activo para poder abrir otro
                        displayChangeImageUrlDialog();
                    }
                });
                Button btnChooseIcon = dialogView.findViewById(R.id.btnChooseIconChangeImage);
                btnChooseIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        EditUserActivity.this.dialogActive=false; //Ponemos que no hay dialogo activo para poder abrir otro
                        displayChangeIconDialog();
                    }
                });
                Button btnRestoreImage=dialogView.findViewById(R.id.btnRestoreImageChangeImage);
                btnRestoreImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                        if(firebaseUser!=null){
                            String image=(firebaseUser.getPhotoUrl()!=null) ? firebaseUser.getPhotoUrl().toString() : getString(R.string.default_image);
                            user.setImage(image);
                            updateUserUI();
                        }
                        dialog.dismiss();
                    }
                });
                Button btnCancel = dialogView.findViewById(R.id.btnCancelChangeImage);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }
    private void displayChangeIconDialog(){
        showCustomDialog(R.layout.dialog_select_icon, false, new DialogConfigurator() {
            @Override
            public void configure(AlertDialog dialog, View dialogView) {
                Spinner iconSpinner=dialogView.findViewById(R.id.spinnerIcons);
                iconSpinner.setAdapter(adapter);
                int iconIndex = icons.indexOf(user.getImage()); //Obtememos el index de la foto actual
                if (iconIndex!=-1) iconSpinner.setSelection(iconIndex); //Si la foto actual esta en la lista, la seleccionamos. Si no, quiere decir que la foto no es un icono
                Button btnCancel=dialogView.findViewById(R.id.btnCancelChangeIcon);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button btnAccept=dialogView.findViewById(R.id.btnAcceptChangeIcon);
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String icon=adapter.getItem(iconSpinner.getSelectedItemPosition()).toString();
                        user.setImage(icon);
                        updateUserUI();
                        dialog.dismiss();
                    }
                });
            }
        });
    }
    private void displayChangeImageUrlDialog(){
        showCustomDialog(R.layout.dialog_url_image, false, new DialogConfigurator() {
            @Override
            public void configure(AlertDialog dialog, View dialogView) {
                EditText input = dialogView.findViewById(R.id.editTextChangeImageURl);
                Button btnCancel = dialogView.findViewById(R.id.btnCancelChangeImageURl);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button btnAccept=dialogView.findViewById(R.id.btnAcceptChangeImageURl);
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = input.getText().toString().trim();
                        new DownloadUrlImage(EditUserActivity.this, new Consumer<String>() {
                            @Override
                            public void accept(String s) {
                                user.setImage(s);
                                updateUserUI();
                                dialog.dismiss();
                            }
                        }).execute(url);
                    }
                });
            }
        });
    }

    private void displayChangeFavTypeDialog(){
        showCustomDialog(R.layout.dialog_change_fav_type, false, new DialogConfigurator() {
            @Override
            public void configure(AlertDialog dialog, View dialogView) {
                Spinner typesSpinner=dialogView.findViewById(R.id.spinnerFavTypes);
                List<Type>types=Data.getInstance().getTypeList();
                TypeSpinnerAdapter typeAdapter=new TypeSpinnerAdapter(types,getApplicationContext());
                typesSpinner.setAdapter(typeAdapter);
                List<String>typeNames=types.stream().map(Type::getName).collect(Collectors.toList());
                int selectedIndex=(user.getFavType()!=null)?typeNames.indexOf(user.getFavType()):0;
                if(selectedIndex==-1)selectedIndex=0; //Si es -1 porque no se ha encontrado el tipo, lo ponemos a 0
                typesSpinner.setSelection(selectedIndex);
                Button btnCancel = dialogView.findViewById(R.id.btnCancelChangeFavType);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button btnClear=dialogView.findViewById(R.id.btnClearFavType);
                btnClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.setFavType(null);
                        updateUserUI();
                        dialog.dismiss();
                    }
                });
                Button btnAccept=dialogView.findViewById(R.id.btnAcceptChangeFavType);
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Type newType = (Type)typesSpinner.getSelectedItem();
                        if (newType!=null) {
                            user.setFavType(newType.getName());
                            updateUserUI();
                            dialog.dismiss();
                        } else {
                            showToast(getText(R.string.error_setting_fav).toString());
                        }
                    }
                });
            }
        });
    }
    private void displayChangeFavPokemonDialog(){
        showCustomDialog(R.layout.dialog_change_fav_pokemon, false, new DialogConfigurator() {
            @Override
            public void configure(AlertDialog dialog, View dialogView) {
                List<BasePokemon>pokemonList=Data.getInstance().getPokemonList();
                AutoCompleteTextView autoCompleteTextView=dialogView.findViewById(R.id.autocompleteFavPokemon);
                ImageView imgFavoritePokemon=dialogView.findViewById(R.id.imgSelectedFavPokemon);
                if(user.getFavPokemon()!=null){
                    BasePokemon favPokemon=teamService.getBasePokemon(Integer.parseInt(user.getFavPokemon()));
                    if(favPokemon!=null)Glide.with(getApplicationContext()).load(favPokemon.getSprite()).into(imgFavoritePokemon);
                    else Glide.with(getApplicationContext()).load(R.drawable.not_set).into(imgFavoritePokemon);
                }
                else Glide.with(getApplicationContext()).load(R.drawable.not_set).into(imgFavoritePokemon);
                PokemonSpinnerAdapter pokemonAdapter=new PokemonSpinnerAdapter(pokemonList,getApplicationContext());
                autoCompleteTextView.setAdapter(pokemonAdapter);
                autoCompleteTextView.setThreshold(1); //Empieza a sugerir al escribir un caracter
                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        BasePokemon selected = (BasePokemon) parent.getItemAtPosition(position);
                        autoCompleteTextView.setTag(selected); //Guardamos el que esta seleccionado
                        Glide.with(getApplicationContext()).load(selected.getSprite()).into(imgFavoritePokemon);
                    }
                });
                Button btnCancel = dialogView.findViewById(R.id.btnCancelChangeFavPokemon);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button btnClear=dialogView.findViewById(R.id.btnClearFavPokemon);
                btnClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.setFavPokemon(null);
                        updateUserUI();
                        dialog.dismiss();
                    }
                });
                Button btnAccept=dialogView.findViewById(R.id.btnAcceptChangeFavPokemon);
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BasePokemon selectedPokemon=(BasePokemon)autoCompleteTextView.getTag(); //Obtenemos el seleccionado
                        if (selectedPokemon!=null) {
                            user.setFavPokemon(selectedPokemon.getPokedexNumber());
                            updateUserUI();
                            dialog.dismiss();
                        } else {
                            showToast(getText(R.string.error_setting_fav).toString());
                        }
                    }
                });
            }
        });
    }
    private void displayDeleteUserDialog(){
        showCustomDialog(R.layout.dialog_delete, false, new DialogConfigurator() {
            @Override
            public void configure(AlertDialog dialog, View dialogView) {
                Button btnCancel = dialogView.findViewById(R.id.btnCancelDelete);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button btnAccept=dialogView.findViewById(R.id.btnAcceptDelete);
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        EditUserActivity.this.dialogActive=false; //Ponemos que no hay dialogo activo para poder abrir otro
                        showCustomDialog(R.layout.dialog_delete, false, new DialogConfigurator() {
                            @Override
                            public void configure(AlertDialog dialog2, View dialogView2) {
                                TextView message = dialogView2.findViewById(R.id.txtDeleteTitle);
                                message.setText(getString(R.string.delete_user_warning2));
                                Button btnCancel2 = dialogView2.findViewById(R.id.btnCancelDelete);
                                Button btnAccept2 = dialogView2.findViewById(R.id.btnAcceptDelete);
                                btnCancel2.setOnClickListener(v2 -> dialog2.dismiss());
                                btnAccept2.setOnClickListener(v2 -> {
                                    dialog2.dismiss();
                                    deleteUser();
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void updateUserUI(){
        binding.txtNameEdit.setText(user.getName());
        binding.txtEmailEdit.setText(user.getEmail());
        String image=user.getImage();
        if(image!=null){
            if (image.startsWith("http://") || image.startsWith("https://") || image.startsWith("content://") || image.startsWith("file://")){ //Si la imagen es de una URL o una URI, usamos Glide
                Glide.with(this).load(image).into(binding.imgUserEdit);
            }
            else { //Si no, es un recurso de la app, un icono
                int resId=getResources().getIdentifier(image, "drawable", getPackageName());
                if (resId != 0) {
                    binding.imgUserEdit.setImageResource(resId);
                } else {
                    binding.imgUserEdit.setImageResource(R.drawable.icon1); //fallback si no existe
                }
            }
        }
        else Glide.with(this).load(R.drawable.icon1).into(binding.imgUserEdit);
        if(user.getFavType()!=null){
            Type type=Data.getInstance().getTypeByName(user.getFavType());
            if(type!=null) Glide.with(EditUserActivity.this).load(type.getSprite()).into(binding.imgFavTypeEdit);
            else Glide.with(EditUserActivity.this).load(R.drawable.not_set).into(binding.imgFavTypeEdit); //Puede ser nulo trajimos el favorito de Firestore y aun no ha cargado de la API el Pokemon
        }
        else Glide.with(EditUserActivity.this).load(R.drawable.not_set).into(binding.imgFavTypeEdit);

        if(user.getFavPokemon()!=null){
            BasePokemon basePokemon= teamService.getBasePokemon(Integer.parseInt(user.getFavPokemon()));
            if(basePokemon!=null) Glide.with(EditUserActivity.this).load(basePokemon.getSprite()).into(binding.imgFavPokemonEdit); //Puede ser nulo trajimos el favorito de Firestore y aun no ha cargado de la API el Pokemon
            else Glide.with(EditUserActivity.this).load(R.drawable.not_set).into(binding.imgFavPokemonEdit);
        }
        else Glide.with(EditUserActivity.this).load(R.drawable.not_set).into(binding.imgFavPokemonEdit);
    }

    private void loadIcons(){
        int totalIcons=getResources().getInteger(R.integer.total_icons);
        icons = new ArrayList<>();
        for (int i = 1; i <= totalIcons; i++) {
            icons.add("icon"+i); //Todos se llaman icon+numero
        }
    }
    private void deleteUser(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            if (firebaseUser.getProviderData().get(1).getProviderId().equals(EmailAuthProvider.PROVIDER_ID)) { //Si el usuario es de email y password
                displayEnterPasswordDialog(firebaseUser);
            } else if (firebaseUser.getProviderData().get(1).getProviderId().equals(GoogleAuthProvider.PROVIDER_ID)){
                GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);
                if (googleAccount != null) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(googleAccount.getIdToken(), null);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            deleteUser(firebaseUser);
                        } else {
                            showToast(getString(R.string.error_reauth));
                        }
                    });
                }
            }
        } else {
            goToLogin();
        }
    }
    private void displayEnterPasswordDialog(FirebaseUser firebaseUser){
        if(firebaseUser.getEmail()!=null){
            showCustomDialog(R.layout.dialog_enter_password, false, new DialogConfigurator() {
                @Override
                public void configure(AlertDialog dialog, View dialogView) {
                    EditText editTextPassword = dialogView.findViewById(R.id.editTextEnterPassword);
                    Button btnCancel = dialogView.findViewById(R.id.btnCancelEnterPassword);
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    Button btnAccept=dialogView.findViewById(R.id.btnAcceptEnterPassword);
                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String password = editTextPassword.getText().toString().trim();
                            if (!password.isEmpty()) {
                                dialog.dismiss();
                                AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password); //Debemos reautenticar el usuario
                                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        deleteUser(firebaseUser);
                                    } else {
                                        showToast(getString(R.string.error_reauth));
                                    }
                                });
                            } else {
                                showToast(getString(R.string.enter_password));
                            }
                        }
                    });
                }
            });

        }
    }
    private void deleteUser(FirebaseUser firebaseUser){
        //Backup por si falla
        User userBackup = new User(user.getUserId(), user.getEmail(), user.getName(), user.getImage());
        userBackup.setFavType(user.getFavType());
        userBackup.setFavPokemon(user.getFavPokemon());
        deleteUserData(new OnUserDataDeletedListener() {//Borramos primero los datos en local y firestore, pues si borramos primero el usuario de firebase fallará la eliminacion de datos de firestore porque ya no existe
            @Override
            public void onUserDataDeleted() {
                firebaseUser.delete().addOnCompleteListener(task -> { //Por ultimo, eliminamos el usuario de Firebase
                    if (task.isSuccessful()) {
                        goToLogin();
                    } else { //Si falla, restauramos los datos
                        showToast(getString(R.string.error_deleting_user));
                        restoreUserData(userBackup);
                    }
                });
            }
        });
    }

    private void deleteUserData(OnUserDataDeletedListener listener){
        for(Team team:teamService.getAllTeams(user.getUserId())){ //Eliminamos sus equipos
            teamService.removeTeam(team.getTeamId());
        }
        userDAO.deleteUser(user.getUserId()); //Lo eliminamos de la BD
        userSync.deleteUser(user.getUserId(),listener); //De Firestore tambien
    }
    private void restoreUserData(User userBackup) {
        //Restauramos los datos tanto en local como en Firestore, solo si se habían borrado
        if (!userDAO.userExists(userBackup.getUserId())) userDAO.addUser(userBackup);
        userSync.userExists(userBackup.getUserId(), new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) userSync.addUser(userBackup);
                }
            }
        });
    }
    private void goToLogin() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(EditUserActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Aseguramos limpiar el historial de actividade, sin permitir volver hacia atras
        startActivity(intent);
        finish();
    }
}