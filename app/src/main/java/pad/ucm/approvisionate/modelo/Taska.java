package pad.ucm.approvisionate.modelo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by root on 19/05/17.
 */

public class Taska extends AsyncTask<String, Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(String... url) {
        URL imageUrl = null;
        HttpURLConnection conn = null;
        Bitmap imagenLoad = null;
        try {
            imageUrl = new URL(url[0]);
            conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
            imagenLoad = BitmapFactory.decodeStream(conn.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imagenLoad;
    }
}
