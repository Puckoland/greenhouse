package sk.spse_po.sa.karnis.filip.rpj;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

//Aktivita sluziaca na zobrazenie a zmenu nastaveni sklenika
public class SetActivity extends AppCompatActivity
        implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{
    String TAG = "DEBUGTAG123";

    //Elementy v aktivite
    private Switch mSunSwitch;
//    private Switch mWindowSwitch;
//    private Switch mWateringSwitch;
    private TextView mTemperatureMaxTextView;
    private TextView mTemperatureMinTextView;
    private TextView mHumidityTextView;
    private TextView mTimeTextView;
    private TextView mDateTextView;
    private TextView mErrorMessage;

    private FloatingActionButton fab;

    private ProgressBar mProgressBar;
    private ConstraintLayout mConstraintLayout;

    //Hodnoty
    private int temperatureMin;
    private int temperatureMax;
    private int humidity;
    private boolean sun;
    private boolean window;
    private boolean watering;
    private int hour;
    private int minute;
    private int year;
    private int month;
    private int day;

    //Vykona sa pri vytvoreni aktivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mConstraintLayout = (ConstraintLayout) findViewById(R.id.constraint_layout);
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message_display);

        fab = (FloatingActionButton) findViewById(R.id.fab_post);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FetchPOSTRequestTask().execute();
            }
        });

        mSunSwitch = (Switch) findViewById(R.id.switch_sun);
        mSunSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d(TAG, String.valueOf(b));
                sun = b;
            }
        });

//        mWindowSwitch = (Switch) findViewById(R.id.switch_window);
//        mWindowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Log.d("SWITCH_WINDOW", String.valueOf(b));
//                window = b;
//            }
//        });
//
//        mWateringSwitch = (Switch) findViewById(R.id.switch_watering);
//        mWateringSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Log.d("SWITCH_WATERING", String.valueOf(b));
//                watering = b;
//            }
//        });

        mTemperatureMaxTextView = (TextView) findViewById(R.id.tv_temperature_max);
        mTemperatureMaxTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberPickerDialog(2);
            }
        });

        mTemperatureMinTextView = (TextView) findViewById(R.id.tv_temperature_min);
        mTemperatureMinTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberPickerDialog(1);
            }
        });

        mHumidityTextView = (TextView) findViewById(R.id.tv_humidity);
        mHumidityTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberPickerDialog(3);
            }
        });

        mTimeTextView = (TextView) findViewById(R.id.tv_time);
        mTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        mDateTextView = (TextView) findViewById(R.id.tv_date);
        mDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        loadData();
    }

    //Nacitanie dat v JSON formate z internetu
    public void loadData(){
        showDataView();
        new FetchGETRequestTask().execute();
    }

    //Nacitanie dat z JSON formatu
    private void loadFromJSON(String json){
        try {
            JSONObject data = new JSONObject(json);
            temperatureMax = data.getInt("temperaturemax");
            temperatureMin = data.getInt("temperaturemin");
            humidity = data.getInt("humidity");
            sun = data.getBoolean("sun");
            window = data.getBoolean("window");
            watering = data.getBoolean("watering");
            hour = data.getInt("hour");
            minute = data.getInt("minute");
            year = data.getInt("year");
            month = data.getInt("month");
            day = data.getInt("day");
        } catch (JSONException e){
            e.printStackTrace();
        }
        mTemperatureMaxTextView.setText(getString(R.string.temperature_holder, temperatureMax));
        mTemperatureMinTextView.setText(getString(R.string.temperature_holder, temperatureMin));
        mHumidityTextView.setText(getString(R.string.humidity_holder, humidity));
        mSunSwitch.setChecked(sun);
//        mWindowSwitch.setChecked(window);
//        mWateringSwitch.setChecked(watering);
        mTimeTextView.setText(getString(R.string.time_holder, hour, minute));
        mDateTextView.setText(getString(R.string.date_holder, day, month, year));
    }

    //Zobrazenie dat + skrytie chybovej spravy
    public void showDataView() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mConstraintLayout.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
    }

    //Zobrazenie chybovej spravy + skrytie dat
    public void showErrorMessage(){
        mProgressBar.setVisibility(View.INVISIBLE);
        mConstraintLayout.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
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
            mProgressBar.setVisibility(View.VISIBLE);
        }

        //Odosielanie
        @Override
        protected String doInBackground(String... params) {
            try {
                return NetworkUtils.getResponseFromHttpUrl(NetworkUtils.SETTINGS_URL);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        //Po odosielani
        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                Log.d(TAG, json);
                loadFromJSON(json);
                showDataView();
            }
            else showErrorMessage();
        }
    }

    //Spusti sa po nastaveni casu v TimePickerFragmente
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        Log.d(TAG, String.valueOf(hour) + ":" + String.valueOf(minute));
        this.hour = hour;
        this.minute = minute;
        mTimeTextView.setText(getString(R.string.time_holder, this.hour, this.minute));
    }

    //Spusti sa po nastaveni datumu v DatePickerFragmente
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Log.d(TAG, String.valueOf(day) + "." + String.valueOf(month) +
                "." + String.valueOf(year));
        this.year = year;
        this.month = month+1;
        this.day = day;
        mDateTextView.setText(getString(R.string.date_holder, this.day, this.month, this.year));
    }

    //Posle POST Request na server -> posiela data
    //Sluzi na "pozastavenie" aktivit
    //Aby pouzivatel videl ze sa data posielaju na internet (co moze chvilu trvat)
    public class FetchPOSTRequestTask extends AsyncTask<String, String , Boolean> {
        //Pred odoslanim
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mConstraintLayout.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        //Odosielanie
        @Override
        protected Boolean doInBackground(String... params) {
            String data = getData();
            Log.d(TAG, data);
            try {
                NetworkUtils.postResponseFromHttpUrl(NetworkUtils.IS_GET_URL, "isget=true");
                return NetworkUtils.postResponseFromHttpUrl(NetworkUtils.POST_SETTINGS_URL, data);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        //Po odoslani
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                Toast.makeText(getApplicationContext(), "Posted", Toast.LENGTH_LONG).show();
                showDataView();
                super.onPostExecute(aBoolean);
            }
            else {
                showErrorMessage();
                Toast.makeText(getApplicationContext(),
                        "Post Failed!", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Sformatuje data, ktore posielame
    private String getData(){
        return "temperaturemin=" + temperatureMin + "&humidity=" + humidity +
                "&temperaturemax=" + temperatureMax + "&hour=" + hour +
                "&minute=" + minute + "&year=" + year + "&month=" + month + "&day=" + day +
                "&sun=" + sun + "&window=" + window + "&watering=" + watering;
    }

    //Zobrazi dialog na vyber cisla
    private void numberPickerDialog(final int type){
        final NumberPicker numberPicker = new NumberPicker(this);
        if (type == 1){
            numberPicker.setMinValue(10);
            numberPicker.setMaxValue(25);
        }
        else if (type == 2){
            numberPicker.setMinValue(25);
            numberPicker.setMaxValue(50);
        }
        else if (type == 3){
            numberPicker.setMinValue(20);
            numberPicker.setMaxValue(60);
        }

        NumberPicker.OnValueChangeListener valueChangeListener =
                new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {

            }
        };
        numberPicker.setOnValueChangedListener(valueChangeListener);

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(numberPicker);
        builder.setTitle("Change Value").setIcon(R.drawable.thermometer);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, String.valueOf(numberPicker.getValue()));
                if (type == 1){
                    temperatureMin = numberPicker.getValue();
                    mTemperatureMinTextView.setText(getString(R.string.temperature_holder, temperatureMin));
                }
                else if (type == 2) {
                    temperatureMax = numberPicker.getValue();
                    mTemperatureMaxTextView.setText(getString(R.string.temperature_holder, temperatureMax));
                }
                else if (type == 3){
                    humidity = numberPicker.getValue();
                    mHumidityTextView.setText(getString(R.string.humidity_holder, humidity));
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
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
