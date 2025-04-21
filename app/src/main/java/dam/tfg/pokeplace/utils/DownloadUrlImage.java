package dam.tfg.pokeplace.utils;

import android.content.Context;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

import dam.tfg.pokeplace.R;

public class DownloadUrlImage extends AsyncTaskExecutorService<String, Void, Boolean> {
    private final Context context;
    private final Consumer<String> callback;
    private String url;
    public DownloadUrlImage(Context context, Consumer<String> callback) {
        this.context = context;
        this.callback = callback;
    }
    @Override
    protected Boolean doInBackground(String s) {
        try {
            url=s;
            URL imageUrl = new URL(s);
            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            String contentType = connection.getContentType();
            if (url.endsWith(".svg")) return false; //Formato no soportado
            return contentType != null && contentType.startsWith("image/");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean validUrl) {
        if (validUrl) {
            callback.accept(url);
        } else {
            ToastUtil.showToast(context,context.getString(R.string.url_not_valid));
        }
    }
}