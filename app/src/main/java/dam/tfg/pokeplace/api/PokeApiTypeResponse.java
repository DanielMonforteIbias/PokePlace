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
import dam.tfg.pokeplace.models.Type;
import dam.tfg.pokeplace.utils.JSONExtractor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PokeApiTypeResponse {
    public static void getAllTypes(TypeCallback callback, Context context){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://pokeapi.co/api/v2/type").get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(context!=null) new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, R.string.error_solicitud_api, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String datos = response.body().string();
                    List<String> typeUrlList= JSONExtractor.extractUrls(datos);
                    List<Type> typeList = Collections.synchronizedList(new ArrayList<>());
                    AtomicInteger restantes = new AtomicInteger(typeUrlList.size());
                    for(String url:typeUrlList){
                        getType(url, new TypeCallback() {
                            @Override
                            public void onTypeListReceived(List<Type> typeList) {

                            }

                            @Override
                            public void onTypeReceived(Type type) {
                                if (type != null) {
                                    typeList.add(type);
                                }
                                if (restantes.decrementAndGet() == 0) {
                                    if(callback != null){
                                        callback.onTypeListReceived(typeList);
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
    public static void getType(String typeUrl, TypeCallback callback, Context context){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(typeUrl).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(context!=null) new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, R.string.error_solicitud_api, Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String datos = response.body().string();
                    Type type= JSONExtractor.extractType(datos);
                    callback.onTypeReceived(type);
                }
                else {
                    if(context!=null) new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, R.string.error_respuesta_api, Toast.LENGTH_SHORT).show()); //Mostramos un Toast con informacion del error. Se usa Handler para que se haga en el hilo principal
                    System.out.println("Error de la API: "+response.message()+" "+response.code()+" "+response.body().toString().toString());
                    callback.onTypeReceived(null);
                }
            }
        });
    }
}
