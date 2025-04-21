package dam.tfg.pokeplace.utils;

import android.content.Context;
import android.net.Uri;

import java.io.InputStream;

public class FilesUtils {
    public static boolean isAnimatedWebp(Context context, Uri uri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream == null) return false;
            byte[] buffer = new byte[64];
            int bytesRead = inputStream.read(buffer, 0, 64);
            if (bytesRead == -1) return false;
            String header = new String(buffer);
            return header.contains("ANIM");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
