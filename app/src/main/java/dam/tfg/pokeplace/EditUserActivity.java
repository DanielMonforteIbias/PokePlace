package dam.tfg.pokeplace;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import dam.tfg.pokeplace.databinding.ActivityEditUserBinding;
import dam.tfg.pokeplace.interfaces.DialogConfigurator;
import dam.tfg.pokeplace.models.Team;
import dam.tfg.pokeplace.models.User;
import dam.tfg.pokeplace.utils.BaseActivity;
import dam.tfg.pokeplace.utils.ToastUtil;

public class EditUserActivity extends BaseActivity {
    private ActivityEditUserBinding binding;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityEditUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent=getIntent();
        user=intent.getParcelableExtra("User");
        updateUserUI();
        binding.txtNameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChangeNameDialog();
            }
        });
        binding.imgUserEdit.setOnClickListener((v)->displayChangeImageDialog());
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
                setResult(RESULT_OK);
                finish();
            }
        });
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
                            updateUserUI();
                            dialog.dismiss();
                        } else {
                            ToastUtil.showToast(getApplicationContext(), getText(R.string.nombre_vacio).toString());
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
                        dialog.dismiss();
                    }
                });
                Button btnCamera = dialogView.findViewById(R.id.btnGalleryChangeImage);
                btnCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button btnUrl = dialogView.findViewById(R.id.btnUrlChangeImage);
                btnUrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button btnChooseIcon = dialogView.findViewById(R.id.btnChooseIconChangeImage);
                btnChooseIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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

    private void updateUserUI(){
        binding.txtNameEdit.setText(user.getName());
        binding.txtEmailEdit.setText(user.getEmail());
        String image=user.getImage();
        if (image.startsWith("http://") || image.startsWith("https://") || image.startsWith("content://") || image.startsWith("file://")){ //Si la imagen es de una URL o una URI, usamos Glide
            Glide.with(this).load(image).placeholder(R.drawable.icon1).into(binding.imgUserEdit);
        }
        else {//Si no, es un String en Base64 (viene de la camara), asi que haremos un bitmap y despues usaremos Glide
            byte[] decodedString = Base64.decode(user.getImage(), Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Glide.with(this).load(bitmap).placeholder(R.drawable.icon1).into(binding.imgUserEdit);
        }
    }
}