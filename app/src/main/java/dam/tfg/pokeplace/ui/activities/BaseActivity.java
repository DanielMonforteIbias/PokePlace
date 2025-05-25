package dam.tfg.pokeplace.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.data.Data;
import dam.tfg.pokeplace.interfaces.DialogConfigurator;
import dam.tfg.pokeplace.utils.ToastUtil;

/**
 * Clase de la que heredan todas las actividades de la app para usar varios comportamientos en común sin repetirlos en cada
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected boolean dialogActive =false;
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

    public void showCustomDialog(int layoutResId, boolean cancelable, DialogConfigurator configurator) { //Es public y no protected para poder usarlo tambien desde fragmentos dentro de las actividades
        if(isFinishing() || isDestroyed()) return; //Evitamos errores si la actividad no está en un estado valido
        if(!dialogActive){
            dialogActive =true; //Marcamos que hay dialogo activo para no poder abrir varios. Si queremos abrir un diálogo desde otro, debemos poner dialogActive a false a mano antes de abrir el segundo, o al intentarlo detectará que hay uno y no lo abrirá (aunque se haga el dismiss antes que abrir el segundo)
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(layoutResId, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            configurator.configure(dialog, dialogView);
            dialog.setCancelable(cancelable);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dialogActive =false; //Al cerrar el dialogo ya podemos volver a abrir otro
                }
            });
            dialog.show();
        }
    }
    protected void showToast(String s){
        ToastUtil.showToast(getApplicationContext(),s);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Cargamos los tipos y los Pokémon cada vez que se cree una actividad. Tener la carga en BaseActivity asegura que se pase siempre por ella, si la
        pusiésemos en una actividad, por ejemplo LoginActivity, podría ocurrir que cerremos la app y volvamos a ella tiempo más tarde sin pasar por Login, pero
        los datos se habrían borrado ya de la RAM, por lo que las listas quedarían vacías hasta pasar por Login de nuevo.
        Para que esto funcione es importante que las actividades llamen al super.onCreate
        Las comprobaciones para no llenarlas si ya estan llenas están dentro de los métodos de carga
         */
        Data.getInstance().loadTypes(getApplicationContext());
        Data.getInstance().loadPokemon(getApplicationContext());
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