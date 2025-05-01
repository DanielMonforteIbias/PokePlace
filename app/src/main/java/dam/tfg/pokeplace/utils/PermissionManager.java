package dam.tfg.pokeplace.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

public class PermissionManager {
    public static final int PERMISO_CAMARA=1;
    public static final int PERMISO_ALMACENAMIENTO=2;

    public static boolean checkCameraPermissions(Context c) {
        return ActivityCompat.checkSelfPermission(c, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestCameraPermissions(Activity a) {
        ActivityCompat.requestPermissions(a, new String[]{android.Manifest.permission.CAMERA}, PERMISO_CAMARA);
    }


    public static boolean checkStoragePermissions(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return ActivityCompat.checkSelfPermission(c, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        else return ActivityCompat.checkSelfPermission(c, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestStoragePermissions(Activity a) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(a, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISO_ALMACENAMIENTO);
        } else {
            ActivityCompat.requestPermissions(a, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISO_ALMACENAMIENTO);
        }
    }
}
