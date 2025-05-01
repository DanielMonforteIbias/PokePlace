package dam.tfg.pokeplace;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import dam.tfg.pokeplace.adapters.IconAdapter;
import dam.tfg.pokeplace.data.dao.UserDAO;
import dam.tfg.pokeplace.databinding.ActivityEditUserBinding;
import dam.tfg.pokeplace.interfaces.DialogConfigurator;
import dam.tfg.pokeplace.models.User;
import dam.tfg.pokeplace.utils.BaseActivity;
import dam.tfg.pokeplace.utils.DownloadUrlImage;
import dam.tfg.pokeplace.utils.FilesUtils;
import dam.tfg.pokeplace.utils.PermissionManager;
import dam.tfg.pokeplace.utils.ToastUtil;

public class EditUserActivity extends BaseActivity {
    private ActivityEditUserBinding binding;
    private User user;
    private UserDAO userDAO;
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
        Intent intent=getIntent();
        user=intent.getParcelableExtra("User");
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
        binding.btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDAO.updateUser(user);
                setResult(RESULT_OK);
                finish();
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
                                    if("image/svg+xml".equalsIgnoreCase(mimeType)) ToastUtil.showToast(EditUserActivity.this,getString(R.string.not_supported)); //Los svg no se aceptan
                                    /*Los gif de galeria funcionan la primera vez, pero al reabrir la app la Uri pierde permisos y no se muestra, por lo que se ha decidido no permitirlos de galeria,
                                    pero sí de URL
                                    Para que funcionen de galeria habria que usar action open document en vez de action pick y permitir la persistencia de permisos, pero eso hace que se abra
                                    el explorador de archivos en vez de galería, y se ha tomado la decisión de que queda más claro abrir la galería y no merece la pena cambiarlo por un formato
                                    Los gif pueden ser tanto gif como webp animados, ambos darian error. Los webp normales funcionan*/
                                    else if("image/gif".equalsIgnoreCase(mimeType) || ("image/webp".equalsIgnoreCase(mimeType) && FilesUtils.isAnimatedWebp(EditUserActivity.this,imageUri))) ToastUtil.showToast(EditUserActivity.this,getString(R.string.gif_gallery));
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
                            ToastUtil.showToast(getApplicationContext(), getText(R.string.error_empty_name).toString());
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
                            ToastUtil.showToast(EditUserActivity.this,getString(R.string.storage_permission_denied));
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
                            ToastUtil.showToast(EditUserActivity.this,getString(R.string.camera_permission_denied));
                            PermissionManager.requestCameraPermissions(EditUserActivity.this);
                        }
                        dialog.dismiss();
                    }
                });
                Button btnUrl = dialogView.findViewById(R.id.btnUrlChangeImage);
                btnUrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayChangeImageUrlDialog();
                        dialog.dismiss();
                    }
                });
                Button btnChooseIcon = dialogView.findViewById(R.id.btnChooseIconChangeImage);
                btnChooseIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayChangeIconDialog();
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
    }

    private void loadIcons(){
        int totalIcons=getResources().getInteger(R.integer.total_icons);
        icons = new ArrayList<>();
        for (int i = 1; i <= totalIcons; i++) {
            icons.add("icon"+i); //Todos se llaman icon+numero
        }
    }
}