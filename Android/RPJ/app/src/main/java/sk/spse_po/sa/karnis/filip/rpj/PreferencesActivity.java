package sk.spse_po.sa.karnis.filip.rpj;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

//Aktivita sluziaca na ukladanie URL
public class PreferencesActivity extends Activity {
    //Elementy
    EditText sensorEditText;
    EditText settingsEditText;
    EditText postEditText;

    Button sensorButton;
    Button settingsButton;
    Button postButton;

    //Hodnoty
    String sensor;
    String settings;
    String post;

    //Vykona sa pri vytvoreni aktivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        //Ziskanie preferencii
        final SharedPreferences preferences = getApplicationContext().getSharedPreferences("URLs", 0);

        //Nacitanie URL
        sensor = preferences.getString("sensor", null);
        settings = preferences.getString("settings", null);
        post = preferences.getString("post", null);

        //Nastavenie URL
        sensorEditText = (EditText) findViewById(R.id.edit_sensor);
        sensorEditText.setText(sensor);
        settingsEditText = (EditText) findViewById(R.id.edit_settings);
        settingsEditText.setText(settings);
        postEditText = (EditText) findViewById(R.id.edit_post);
        postEditText.setText(post);

        //Ukladanie URL
        sensorButton = (Button) findViewById(R.id.send_sensor);
        sensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("sensor", sensorEditText.getText().toString());
                editor.apply();
                NetworkUtils.loadURLs(preferences);
            }
        });
        settingsButton = (Button) findViewById(R.id.send_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("settings", settingsEditText.getText().toString());
                editor.apply();
                NetworkUtils.loadURLs(preferences);
            }
        });
        postButton = (Button) findViewById(R.id.send_post);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("post", postEditText.getText().toString());
                editor.apply();
                NetworkUtils.loadURLs(preferences);
            }
        });
    }
}
