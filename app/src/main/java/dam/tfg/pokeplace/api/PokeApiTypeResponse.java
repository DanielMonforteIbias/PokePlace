package dam.tfg.pokeplace.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.models.Type;
import dam.tfg.pokeplace.utils.JSONExtractor;
import dam.tfg.pokeplace.utils.ToastUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PokeApiTypeResponse {
    /*private static int limit=18; //Aunque haya 20 tipos en la pokeapi, el unknown y el estelar no los contamos
    public static void getAllTypes(TypeCallback callback, Context context){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://pokeapi.co/api/v2/type?limit="+limit).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(context!=null) new Handler(Looper.getMainLooper()).post(() -> ToastUtil.showToast(context,context.getString(R.string.error_api_request)));
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
                                    System.out.println("TIPO  RECIBIDO: "+type.getName());
                                }
                                if (restantes.decrementAndGet() == 0) {
                                    if(callback != null){
                                        Collections.sort(typeList,(t1, t2) -> t1.getName().compareTo(t2.getName()));
                                        callback.onTypeListReceived(typeList);
                                    }
                                }
                            }
                        }, context);
                    }
                }
                else {
                    if(context!=null) new Handler(Looper.getMainLooper()).post(() -> ToastUtil.showToast(context,context.getString(R.string.error_api_response)));
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
                if(context!=null) new Handler(Looper.getMainLooper()).post(() -> ToastUtil.showToast(context,context.getString(R.string.error_api_request)));
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String datos = response.body().string();
                    Type type= JSONExtractor.extractTypeAPI(datos);
                    callback.onTypeReceived(type);
                }
                else {
                    if(context!=null) new Handler(Looper.getMainLooper()).post(() -> ToastUtil.showToast(context,context.getString(R.string.error_api_response))); //Mostramos un Toast con informacion del error. Se usa Handler para que se haga en el hilo principal
                    System.out.println("Error de la API: "+response.message()+" "+response.code()+" "+response.body().toString().toString());
                    callback.onTypeReceived(null);
                }
            }
        });
    }*/
}
