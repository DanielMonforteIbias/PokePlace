package dam.tfg.pokeplace.api;

import android.app.Activity;
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
    private static int offset=0;
    private static int limit=60; //Cargaremos de 60 en 60 para tener todos pero no esperar a todos para que el usuario no espere mucho
    private static int totalPokemon=1025; //Aunque haya 1304 resultados en la API, hay 1025 Pokemon
    public static void getAllPokemons(PokemonCallback callback, Context context, int currentPokemonNumber){
        if(currentPokemonNumber>=totalPokemon) return; //Si ya  estan todos, no hacemos nada
        offset=currentPokemonNumber; //El offset es donde empezaremos. De normal sera 0, pero puede que se llame este metodo al retomar la carga si se dejo a medias, asi que empezamos por donde se quedo
        if(offset+limit>totalPokemon)limit=totalPokemon-offset; //si quedan menos del limite, cogemos solo los que quedan hasta llegar al limite, no lo siguiente
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://pokeapi.co/api/v2/pokemon?limit="+limit+"&offset="+offset).get().build();
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
                                        try{
                                            System.out.println("Añadidos "+limit+" pokemon");
                                            Collections.sort(pokemonList, (p1, p2) -> {return Integer.compare(Integer.parseInt(p1.getPokedexNumber()),Integer.parseInt(p2.getPokedexNumber()));});//Ordenamos la lista por numero de Pokedex, porque viene desordenada al ser asincrono
                                            callback.onPokemonListReceived(pokemonList);
                                            offset+=60;
                                            if(offset<totalPokemon)getAllPokemons(callback,context,offset); //Llamamos al metodo de nuevo para cargar los 60 siguientes solo si aun quedan
                                        }catch(NullPointerException e){ //Si se cierra la actividad durante la carga da este error
                                            System.out.println("Error. Fragment nulo");
                                        }
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
