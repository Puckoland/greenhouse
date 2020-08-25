package sk.spse_po.sa.karnis.filip.rpj;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//Aktivita sluziaca na zobrazenie hodnot v skleniku
public class ShowActivity extends AppCompatActivity {
    //Elementy v aktivite
    private TextView mTemperatureOutsideTextView;
    private TextView mHumidityOutsideTextView;
    private TextView mTemperatureTextView;
    private TextView mHumidityInsideTextView;
    private TextView mSoilMoistureTextView;
    private TextView mSunTitleTextView;
    private TextView mTimeTextView;
    private TextView mDateTextView;
    private TextView mWaterTextView;
    private TextView mWindowTextView;

    int sunCount = 8;
    private ImageView[] mSunImageViews = new ImageView[sunCount];

    private ConstraintLayout mConstraintLayout;
    private LinearLayout mSunLayout;

    //Hodnoty
    private Spanned temperatureOutside = null;
    private Spanned humidityOutside = null;
    private Spanned soilMoisture = null;
    private Spanned temperature = null;
    private Spanned humidityInside = null;
    private Spanned time = null;
    private Spanned date = null;
    private String water = null;
    private String window = null;
    private String sunTitle;
    private boolean sunOn = false;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private JSONObject data;

    //Vykona sa pri vytvoreni aktivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        mTemperatureOutsideTextView = (TextView) findViewById(R.id.text_temperature_outside);
        mHumidityOutsideTextView = (TextView) findViewById(R.id.text_humidity_outside);
        mTemperatureTextView = (TextView) findViewById(R.id.text_temperature);
        mHumidityInsideTextView = (TextView) findViewById(R.id.text_humidity_inside);
        mSoilMoistureTextView = (TextView) findViewById(R.id.text_soil_moisture);
        mSunTitleTextView = (TextView) findViewById(R.id.text_sun);
        mTimeTextView = (TextView) findViewById(R.id.text_time);
        mDateTextView = (TextView) findViewById(R.id.text_date);
        mWaterTextView = (TextView) findViewById(R.id.text_water);
        mWindowTextView = (TextView) findViewById(R.id.text_window);

        mSunLayout = (LinearLayout) findViewById(R.id.sun);
        mConstraintLayout = (ConstraintLayout) findViewById(R.id.constraint_layout);

        for (int i = 1; i < sunCount+1; i++){
            String s = "image_sun"+i;
            int id = getResources().getIdentifier(s, "id", getPackageName());
            mSunImageViews[i-1] = (ImageView) findViewById(id);
        }

        loadData();
    }

    //Nacitanie dat v JSON formate z internetu
    private void loadData() {
        showDataView();

        new FetchGETRequestTask().execute();
    }

    //Nacitanie dat z JSON formatu
    private void loadFromJSON(String json){
        try {
            data = new JSONObject(json);
            temperatureOutside = Html.fromHtml("<b>Outside Temperature: </b>" + data.getInt("temperatureout") + " °C");
            humidityOutside = Html.fromHtml("<b>Humidity of the outside air: </b>" + data.getInt("humidityout") + "%");
            temperature = Html.fromHtml("<b>Inside Temperature: </b>" + data.getInt("temperaturein") + " °C");
            humidityInside = Html.fromHtml("<b>Humidity of the inside air: </b>" + data.getInt("humidityin") + "%");
            soilMoisture = Html.fromHtml("<b>Soil Moisture: </b>" + data.getInt("moisture") + "%");
            time = Html.fromHtml("<b>Time set in greenhouse: </b>" + data.getString("time").substring(0,5));
            date = Html.fromHtml("<b>Date set in greenhouse: </b>" + data.getString("date"));
            water = "Enough water in tank";
            window = "Windows are closed";

            if (data.getBoolean("needwater")) {
                water = "No water in tank!!!";
                mWaterTextView.setTextColor(Color.RED);
                mWaterTextView.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.tank_empty), null, null, null);
            } else {
                mWaterTextView.setTextColor(Color.BLACK);
                mWaterTextView.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.tank_full), null, null, null);
            }
            if (data.getBoolean("window")) window = "Windows are opened";

            sunTitle = "Man-Made sun is OFF";
            JSONObject sun = data.getJSONObject("sun");
            sunOn = sun.getBoolean("on");
            if (sunOn){
                JSONArray suns = sun.getJSONArray("suns");
                for (int i = 0; i < suns.length(); i++) {
                    if (suns.getInt(i) == 1)
                        mSunImageViews[i].setImageResource(R.drawable.sun1);
                    else
                        mSunImageViews[i].setImageResource(R.drawable.sun0);
                }
                sunTitle = "Man-Made sun is ON";
            }
            mTemperatureOutsideTextView.setText(temperatureOutside);
            mHumidityOutsideTextView.setText(humidityOutside);
            mTemperatureTextView.setText(temperature);
            mHumidityInsideTextView.setText(humidityInside);
            mTimeTextView.setText(time);
            mDateTextView.setText(date);
            mSoilMoistureTextView.setText(soilMoisture);
            mSunTitleTextView.setText(sunTitle);
            mWaterTextView.setText(water);
            mWindowTextView.setText(window);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Zobrazenie dat + skrytie chybovej spravy
    private void showDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);

        mConstraintLayout.setVisibility(View.VISIBLE);
        if (sunOn)
            mSunLayout.setVisibility(View.VISIBLE);
    }

    //Zobrazenie chybovej spravy + skrytie dat
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mConstraintLayout.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    //Posle GET Request na server -> ziska data
    //Sluzi na "pozastavenie" aktivit
    //Aby pouzivatel videl ze sa data nacitavaju z internetu (co moze chvilu trvat)
    public class FetchGETRequestTask extends AsyncTask<String, Void, String> {
        //Pred odoslanim
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mConstraintLayout.setVisibility(View.INVISIBLE);
            mSunLayout.setVisibility(View.INVISIBLE);

            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        //Odosielanie
        @Override
        protected String doInBackground(String... params) {
            try {
                return NetworkUtils.getResponseFromHttpUrl(NetworkUtils.SENSOR_URL);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        //Po odoslani
        @Override
        protected void onPostExecute(String weatherData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (weatherData != null) {
                loadFromJSON(weatherData);
                showDataView();
            } else {
                showErrorMessage();
            }
        }
    }

    //Zobrazi menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rpj, menu);
        return true;
    }

    //Urcuje co sa ma stat po vybrani prvku z menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            loadData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
