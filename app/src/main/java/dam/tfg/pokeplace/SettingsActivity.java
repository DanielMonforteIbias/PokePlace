package dam.tfg.pokeplace;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Locale;

import dam.tfg.pokeplace.utils.BaseActivity;

public class SettingsActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private SharedPreferences preferences;
    private boolean settingsChanged=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragment()).commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getString(R.string.settings_title));
        preferences= getSharedPreferences(getString(R.string.preferences),MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        if(key!=null){
            if (key.equals("language")) {
                String selectedLanguage = sharedPreferences.getString("language", "en");
                if (!selectedLanguage.equals(Locale.getDefault().getLanguage())) {
                    settingsChanged=true;
                    recreate();
                    //No hace falta cambiar el idioma de esta pantalla porque con recreate se volvera a ejecutar el metodo  attachBaseContext de la clase padre, que ya establece el idioma y modo con lo que hay en preferencias
                }
            }
            else if(key.equals("theme")){
                boolean darkMode=sharedPreferences.getBoolean(key, false);
                if(darkMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                settingsChanged=true;
            }
        }
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("settingsChanged", settingsChanged);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        settingsChanged = savedInstanceState.getBoolean("settingsChanged", false);
    }
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName(getString(R.string.preferences)); //Importante: Ponemos el mismo archivo de preferencias que usemos en otros lados para observar el correcto y NO el default
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { //La flecha hacia atrás de la barra de arriba
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("settingsChanged", settingsChanged);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}