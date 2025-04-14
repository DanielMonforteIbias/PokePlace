package dam.tfg.pokeplace.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.models.Pokemon;
import dam.tfg.pokeplace.utils.JSONExtractor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PokeApiPokemonResponse {
    public static void getAllPokemons(PokemonCallback callback, Context context){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://pokeapi.co/api/v2/pokemon?limit=20").get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(context!=null) new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, R.string.error_solicitud_api, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String datos = response.body().string();
                    List<String> pokemonUrlList= JSONExtractor.extractUrls(datos);
                    List<Pokemon> pokemonList = Collections.synchronizedList(new ArrayList<>());
                    AtomicInteger restantes = new AtomicInteger(pokemonUrlList.size());
                    for(String url:pokemonUrlList){
                        getPokemon(url, new PokemonCallback() {
                            @Override
                            public void onPokemonListReceived(List<Pokemon> pokemonList) {

                            }

                            @Override
                            public void onPokemonReceived(Pokemon pokemon) {
                                if (pokemon != null) {
                                    pokemonList.add(pokemon);
                                }
                                if (restantes.decrementAndGet() == 0) {
                                    if(callback != null){
                                        Collections.sort(pokemonList, (p1, p2) -> {return Integer.compare(Integer.parseInt(p1.getPokedexNumber()),Integer.parseInt(p2.getPokedexNumber()));});//Ordenamos la lista por numero de Pokedex, porque viene desordenada al ser asincrono
                                        callback.onPokemonListReceived(pokemonList);
                                    }
                                }
                            }
                        }, context);
                    }
                }
                else {
                    if(context!=null) new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, R.string.error_respuesta_api, Toast.LENGTH_SHORT).show());
                    System.out.println("Error de la API: "+response.message()+" "+response.code()+" "+response.body().toString().toString());
                }
            }
        });
    }
    public static void getPokemon(String pokemonUrl, PokemonCallback callback, Context context){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(pokemonUrl).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(context!=null) new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, R.string.error_solicitud_api, Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String datos = response.body().string();
                    Pokemon pokemon= JSONExtractor.extractPokemon(datos);
                    callback.onPokemonReceived(pokemon);
                }
                else {
                    if(context!=null) new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, R.string.error_respuesta_api, Toast.LENGTH_SHORT).show()); //Mostramos un Toast con informacion del error. Se usa Handler para que se haga en el hilo principal
                    System.out.println("Error de la API: "+response.message()+" "+response.code()+" "+response.body().toString().toString());
                    callback.onPokemonReceived(null);
                }
            }
        });
    }
}
