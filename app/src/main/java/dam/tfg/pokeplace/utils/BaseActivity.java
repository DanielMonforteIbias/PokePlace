package dam.tfg.pokeplace.utils;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.interfaces.DialogConfigurator;

/**
 * Clase de la que heredan todas las actividades de la app para usar varios comportamientos en común sin repetirlos en cada
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected void showCustomDialog(int layoutResId, boolean cancelable, DialogConfigurator configurator) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(layoutResId, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        configurator.configure(dialog, dialogView);
        dialog.setCancelable(cancelable);
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}