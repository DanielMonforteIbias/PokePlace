package dam.tfg.pokeplace.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.models.Move;
import dam.tfg.pokeplace.models.Type;
import dam.tfg.pokeplace.utils.JSONExtractor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PokeApiDetailsResponse {
    public static void getMove(String moveUrl, DetailsCallback callback, Context context){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(moveUrl).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(context!=null) new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, R.string.error_solicitud_api, Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String datos = response.body().string();
                    Move move= JSONExtractor.extractMove(datos);
                    callback.onMoveReceived(move);
                }
                else {
                    if(context!=null) new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, R.string.error_respuesta_api, Toast.LENGTH_SHORT).show()); //Mostramos un Toast con informacion del error. Se usa Handler para que se haga en el hilo principal
                    System.out.println("Error de la API: "+response.message()+" "+response.code()+" "+response.body().toString().toString());
                    callback.onMoveReceived(null);
                }
            }
        });
    }
}
