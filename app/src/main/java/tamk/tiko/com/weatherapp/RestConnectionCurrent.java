package tamk.tiko.com.weatherapp;

import android.content.Context;
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
 * Rest connection for getting todays weather.
 *
 * @author Juho Lahtinen
 * @version 1.0
 * @since 1.7
 */
public class RestConnectionCurrent extends AsyncTask <String, Void, String>{

    /**
     * Context of host. Used to get the appid.
     */
    private Context host;

    /**
     * Constructor gets the host context as a Parameter.
     *
     * @param host used to get the appid.
     */
    public RestConnectionCurrent(Context host) {
        this.host = host;
    }

    /**
     * Seperate thread that gets the weather data
     * from openweathermap.org. And converts it to string
     * using the method convertStreamToString().
     *
     * @param params Gets latitude and longitude as strings.
     * @return  Returns String weather information.
     */
    @Override
    protected String doInBackground(String... params) {
        String lat = params[0];
        String lon = params[1];
        String appId = host.getResources().getString(R.string.appid);
        URI uri = null;
        final DefaultHttpClient httpClient = new DefaultHttpClient();

        try {
            uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("api.openweathermap.org")
                    .setPath("/data/2.5/weather")
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

    /**
     * Converts InputStream to String and returns it.
     *
     * @param is InputStream that gets converted to String
     * @return Returns String that is converted from InputStream.
     */
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
