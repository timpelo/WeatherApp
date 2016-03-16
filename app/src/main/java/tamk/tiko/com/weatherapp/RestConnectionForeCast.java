package tamk.tiko.com.weatherapp;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by juhis5 on 18.2.2016.
 */
public class RestConnectionForeCast extends AsyncTask <String, Void, String>{

    @Override
    protected String doInBackground(String... params) {
        String lat = params[0];
        String lon = params[1];
        String appId = "44db6a862fba0b067b1930da0d769e98";
        URI uri = null;
        final DefaultHttpClient httpClient = new DefaultHttpClient();

        try {
            uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("api.openweathermap.org")
                    .setPath("/data/2.5/forecast")
                    .setParameter("lat", lat)
                    .setParameter("lon", lon)
                    .setParameter("appid", appId)
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpGet httpGet = new HttpGet(uri);
        InputStream stream = null;
        try {
            HttpResponse response = httpClient.execute(httpGet);
            stream = response.getEntity().getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = convertStreamToString(stream);
        return result;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
