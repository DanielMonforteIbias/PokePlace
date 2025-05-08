package dam.tfg.pokeplace.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import java.io.IOException;
import java.util.List;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.models.Move;
import dam.tfg.pokeplace.models.Pokemon;
import dam.tfg.pokeplace.utils.JSONExtractor;
import dam.tfg.pokeplace.utils.ToastUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PokeApiDetailsResponse {
    public static void getPokemon(String pokemonUrl, PokemonCallback callback, Context context){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(pokemonUrl).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(context!=null) new Handler(Looper.getMainLooper()).post(() -> ToastUtil.showToast(context,context.getString(R.string.error_api_request)));
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String datos = response.body().string();
                    Pokemon pokemon= JSONExtractor.extractPokemon(datos);
                    callback.onPokemonReceived(pokemon);
                }
                else {
                    if(context!=null) new Handler(Looper.getMainLooper()).post(() -> ToastUtil.showToast(context,context.getString(R.string.error_api_response))); //Mostramos un Toast con informacion del error. Se usa Handler para que se haga en el hilo principal
                    System.out.println("Error de la API: "+response.message()+" "+response.code()+" "+response.body().toString().toString());
                    callback.onPokemonReceived(null);
                }
            }
        });
    }
    public static void getMove(String moveUrl, PokemonCallback callback, Context context){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(moveUrl).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(context!=null) new Handler(Looper.getMainLooper()).post(() -> ToastUtil.showToast(context,context.getString(R.string.error_api_request)));
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String datos = response.body().string();
                    Move move= JSONExtractor.extractMove(datos);
                    callback.onMoveReceived(move);
                }
                else {
                    if(context!=null) new Handler(Looper.getMainLooper()).post(() -> ToastUtil.showToast(context,context.getString(R.string.error_api_response))); //Mostramos un Toast con informacion del error. Se usa Handler para que se haga en el hilo principal
                    System.out.println("Error de la API: "+response.message()+" "+response.code()+" "+response.body().toString().toString());
                    callback.onMoveReceived(null);
                }
            }
        });
    }
    public static void getPokemonDetails(String speciesUrl, PokemonSpeciesCallback callback, Context context){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(speciesUrl).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(context!=null) new Handler(Looper.getMainLooper()).post(() -> ToastUtil.showToast(context,context.getString(R.string.error_api_request)));
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String datos = response.body().string();
                    List<Pair<String,String>> descriptions= JSONExtractor.extractDescriptions(datos);
                    callback.onDetailsReceived(descriptions);
                }
                else {
                    if(context!=null) new Handler(Looper.getMainLooper()).post(() -> ToastUtil.showToast(context,context.getString(R.string.error_api_response))); //Mostramos un Toast con informacion del error. Se usa Handler para que se haga en el hilo principal
                    System.out.println("Error de la API: "+response.message()+" "+response.code()+" "+response.body().toString().toString());
                    callback.onDetailsReceived(null);
                }
            }
        });
    }
}
