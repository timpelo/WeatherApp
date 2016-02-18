package tamk.tiko.com.weatherapp;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by juhis5 on 18.2.2016.
 */
public class RestConnection extends AsyncTask <Void, Void, String>{


    @Override
    protected String doInBackground(Void... params) {
        //String locId = params[0];
        String locId = "2172797";
        String appId = "44db6a862fba0b067b1930da0d769e98";
        URI uri = null;
        final DefaultHttpClient httpClient = new DefaultHttpClient();


        try {
            uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("api.openweathermap.org")
                    .setPath("/data/2.5/weather")
                    .setParameter("id", locId)
                    .setParameter("appid", appId)
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpGet httpGet = new HttpGet(uri);
        String result = "NOT WORKING!!!!";
        try {
            HttpResponse response = httpClient.execute(httpGet);
            result = response.getEntity().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
