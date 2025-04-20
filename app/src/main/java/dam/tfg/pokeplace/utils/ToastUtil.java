package dam.tfg.pokeplace.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    private static Toast currentToast;
    public static void showToast(Context context, String message) {
        if (currentToast != null) {
            currentToast.cancel(); //Para mostrar solo uno y no llenar de Toast
        }
        currentToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        currentToast.show();
    }
}