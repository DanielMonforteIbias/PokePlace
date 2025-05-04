package dam.tfg.pokeplace.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.interfaces.DialogConfigurator;

/**
 * Clase de la que heredan todas las actividades de la app para usar varios comportamientos en común sin repetirlos en cada
 */
public abstract class BaseActivity extends AppCompatActivity {
    /**
     * Configuramos el idioma y modo del contexto antes de crear la actividad
     * @param newBase El nuevo contexto
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences(newBase.getString(R.string.preferences), MODE_PRIVATE);
        String language = preferences.getString("language", "en");
        Configuration config = new Configuration();
        setLocale(config,language);
        Context context = newBase.createConfigurationContext(config);
        boolean darkMode = preferences.getBoolean("theme", false);
        if (darkMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.attachBaseContext(context);
    }
    protected void setLocale(Configuration config,String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        config.setLocale(locale);
    }
    protected void showCustomDialog(int layoutResId, boolean cancelable, DialogConfigurator configurator) {
        if(isFinishing() || isDestroyed()) return; //Evitamos errores si la actividad no está en un estado valido
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