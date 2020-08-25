package sk.spse_po.sa.karnis.filip.rpj;

import android.content.SharedPreferences;
import android.util.Log;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

//Class zodpovedna za odosielanie a prijimanie dat cez internet
public class NetworkUtils {
    static String TAG = "DEBUGTAG123";

    private static String sensor = "https://espgreenhouse.000webhostapp.com/sensor.html";
    private static String settings = "http://espgreenhouse.000webhostapp.com/data.html";
    private static String postSettings = "http://espgreenhouse.000webhostapp.com/apppost.php";
    private static String isGet = "http://espgreenhouse.000webhostapp.com/isget.php";
    static URL SENSOR_URL = createURL(sensor);
    static URL SETTINGS_URL = createURL(settings);
    static URL POST_SETTINGS_URL = createURL(postSettings);
    static URL IS_GET_URL = createURL(isGet);

    //Nacitanie URL z preferencii
    public static void loadURLs(SharedPreferences preferences){
        sensor = preferences.getString("sensor", null);
        settings = preferences.getString("settings", null);
        postSettings = preferences.getString("post", null);

        SENSOR_URL = createURL(sensor);
        SETTINGS_URL = createURL(settings);
        POST_SETTINGS_URL = createURL(postSettings);
    }

    //Vytvorenie URL zo Stringu
    private static URL createURL(String s){
        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Odosle GET Request
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        Log.d(TAG, String.valueOf(url));
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    //Odosle POST Request
    public static boolean postResponseFromHttpUrl (URL url, String data){
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);
            connection.connect();
            writer.flush();
            writer.close();
            out.close();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "NeniOK");
                return false;
            } else Log.d(TAG, "OK");
            return true;
        } catch (IOException e) {
            Log.d(TAG, "Unable to send presence");
            return false;
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }
}
