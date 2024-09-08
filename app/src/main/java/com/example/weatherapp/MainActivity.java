package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class MainActivity extends AppCompatActivity {

    Button getForecast;
    EditText zipcode;
    TextView currentTemp;
    TextView currentTime;
    TextView catchPhrase;

    TextView dayOneTime;
    TextView dayOneHighTemp;
    TextView dayOneLowTemp;

    TextView dayTwoTime;
    TextView dayTwoHighTemp;
    TextView dayTwoLowTemp;

    TextView dayThreeTime;
    TextView dayThreeHighTemp;
    TextView dayThreeLowTemp;

    TextView dayFourTime;
    TextView dayFourHighTemp;
    TextView dayFourLowTemp;

    TextView dayFiveTime;
    TextView dayFiveHighTemp;
    TextView dayFiveLowTemp;

    ImageView currentImage;
    ImageView dayOneImage;
    ImageView dayTwoImage;
    ImageView dayThreeImage;
    ImageView dayFourImage;
    ImageView dayFiveImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getForecast = findViewById(R.id.id_forecast);
        zipcode = findViewById(R.id.id_editTextZipCode);
        currentTemp = findViewById(R.id.id_currentTemp);
        currentTime = findViewById(R.id.id_currentTime);
        catchPhrase = findViewById(R.id.id_catchPhrase);

        dayOneTime = findViewById(R.id.id_dayOneTime);
        dayOneHighTemp = findViewById(R.id.id_dayOneHighTemp);
        dayOneLowTemp = findViewById(R.id.id_dayOneLowTemp);

        dayTwoTime = findViewById(R.id.id_dayTwoTime);
        dayTwoHighTemp = findViewById(R.id.id_dayTwoHighTemp);
        dayTwoLowTemp = findViewById(R.id.id_dayTwoLowTemp);

        dayThreeTime = findViewById(R.id.id_dayThreeTime);
        dayThreeHighTemp = findViewById(R.id.id_dayThreeHighTemp);
        dayThreeLowTemp = findViewById(R.id.id_dayThreeLowTemp);

        dayFourTime = findViewById(R.id.id_dayFourTime);
        dayFourHighTemp = findViewById(R.id.id_dayFourHighTemp);
        dayFourLowTemp = findViewById(R.id.id_dayFourLowTemp);

        dayFiveTime = findViewById(R.id.id_dayFiveTime);
        dayFiveHighTemp = findViewById(R.id.id_dayFiveHighTemp);
        dayFiveLowTemp = findViewById(R.id.id_dayFiveLowTemp);

        currentImage = findViewById(R.id.id_imageCurrent);
        dayOneImage = findViewById(R.id.id_dayOneImage);
        dayTwoImage = findViewById(R.id.id_dayTwoImage);
        dayThreeImage = findViewById(R.id.id_dayThreeImage);
        dayFourImage = findViewById(R.id.id_dayFourImage);
        dayFiveImage = findViewById(R.id.id_dayFiveImage);

        zipcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zipcode.setText("");
            }
        });

        getForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(zipcode.getWindowToken(), 0);

                if(zipcode.getText().toString() != null){
                    DownloadDataAsync downloadData = new DownloadDataAsync();
                    downloadData.execute(zipcode.getText().toString());
                }
            }
        });
    }

    // Inner Class
    private class DownloadDataAsync extends AsyncTask<String, Void, String> {
        String reader = "";
        protected String doInBackground(String... params) {
            String zipCode = params[0];
            String urlSpec = "http://api.openweathermap.org/data/2.5/forecast?zip=" + zipCode +",US&units=imperial&cnt=5&appid=542dd011885e8b290483d27482397b94";
            try {
                URL url = new URL(urlSpec);
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String read;
                while((read = bufferedReader.readLine()) != null){
                    reader += read;
                }
            } catch(FileNotFoundException e){
                return "404";
            } catch (IOException e) {
                e.printStackTrace();
            }

            return reader;
        }

        protected void onPostExecute(String result) {
            if (result.compareTo("404") == 0) {
                Log.d("TAG", "Invalid Zipcode");
                Toast.makeText(MainActivity.this, "Invalid Zipcode", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject jsonData = new JSONObject(result);

                String responseCode = jsonData.getString("cod");

                JSONObject city = jsonData.getJSONObject("city");
                String cityName = city.getString("name");
                String country = city.getString("country");
                Log.d("TAG1", cityName + ',' + country);

                JSONArray jsonList = jsonData.getJSONArray("list");



                JSONObject day1 = jsonList.getJSONObject(0);
                JSONObject main1 = day1.getJSONObject("main");
                double tempCurr = main1.getDouble("temp");
                currentTemp.setText((int)(Math.round(tempCurr)) + "℉");

                JSONArray weatherList1 = day1.getJSONArray("weather");
                JSONObject weatherObject1 = weatherList1.getJSONObject(0);
                String weatherDescription1 = weatherObject1.getString("description");
                String weathercode = weatherObject1.getString("id");
                int weatherCodei = Integer.parseInt(weathercode);
                String weatherIcon1 = weatherObject1.getString("icon");
                dayOneImage.setImageResource(getImageResourceId(weatherIcon1));
                currentImage.setImageResource(getImageResourceId(weatherIcon1));
                catchPhrase.setText(catchPhrase(weatherCodei));

                double tempMin1 = main1.getDouble("temp_min");
                double tempMax1 = main1.getDouble("temp_max");
                dayOneHighTemp.setText("High: " + (int)(Math.round(tempMax1)) +"℉");
                dayOneLowTemp.setText("Low: " + (int)(Math.round(tempMin1)) + "℉");

                //Log.d("TAG", currentTemp.getText().toString());

                long dt = day1.getLong("dt");
                LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(dt, 0, ZoneOffset.UTC);
                String localDt = localDateTime.toString();
                Log.d("TAG", localDt);
                currentTime.setText("" + localDt);

                JSONObject day2 = jsonList.getJSONObject(1);
                JSONObject main2 = day2.getJSONObject("main");

                double tempMin2 = main2.getDouble("temp_min");
                double tempMax2 = main2.getDouble("temp_max");
                dayTwoHighTemp.setText("High: " + (int)(Math.round(tempMax2)) +"℉");
                dayTwoLowTemp.setText("Low: " + (int)(Math.round(tempMin2)) + "℉");

                JSONArray weatherList2 = day2.getJSONArray("weather");

                // String weatherDescription2 = weatherList2.getJSONObject(0).getString("description");
                JSONObject weatherObject2 = weatherList2.getJSONObject(0);
                String weatherDescription2 = weatherObject2.getString("description");
                String weatherIcon2 = weatherObject2.getString("icon");
                dayTwoImage.setImageResource(getImageResourceId(weatherIcon2));

                JSONObject day3 = jsonList.getJSONObject(2);
                JSONObject main3 = day3.getJSONObject("main");

                double tempMin3 = main3.getDouble("temp_min");
                double tempMax3 = main3.getDouble("temp_max");
                dayThreeHighTemp.setText("High: " + (int)(Math.round(tempMax3)) +"℉");
                dayThreeLowTemp.setText("Low: " + (int)(Math.round(tempMin3)) + "℉");

                JSONArray weatherList3 = day3.getJSONArray("weather");
                // String weatherDescription3 = weatherList3.getJSONObject(0).getString("description");
                JSONObject weatherObject3 = weatherList3.getJSONObject(0);
                String weatherDescription3 = weatherObject3.getString("description");
                String weatherIcon3 = weatherObject3.getString("icon");
                dayThreeImage.setImageResource(getImageResourceId(weatherIcon3));

                JSONObject day4 = jsonList.getJSONObject(3);
                JSONObject main4 = day4.getJSONObject("main");

                double tempMin4 = main4.getDouble("temp_min");
                double tempMax4 = main4.getDouble("temp_max");
                dayFourHighTemp.setText("High: " + (int)(Math.round(tempMax4)) +"℉");
                dayFourLowTemp.setText("Low: " + (int)(Math.round(tempMin4)) + "℉");

                JSONArray weatherList4 = day4.getJSONArray("weather");
                // String weatherDescription4 = weatherList4.getJSONObject(0).getString("description");
                JSONObject weatherObject4 = weatherList4.getJSONObject(0);
                String weatherDescription4 = weatherObject4.getString("description");
                String weatherIcon4 = weatherObject4.getString("icon");
                dayFourImage.setImageResource(getImageResourceId(weatherIcon4));

                JSONObject day5 = jsonList.getJSONObject(4);
                JSONObject main5 = day5.getJSONObject("main");

                double tempMin5 = main5.getDouble("temp_min");
                double tempMax5 = main5.getDouble("temp_max");
                dayFiveHighTemp.setText("High: " + (int)(Math.round(tempMax5)) +"℉");
                dayFiveLowTemp.setText("Low: " + (int)(Math.round(tempMin5)) + "℉");

                JSONArray weatherList5 = day5.getJSONArray("weather");
                // String weatherDescription5 = weatherList5.getJSONObject(0).getString("description");
                JSONObject weatherObject5 = weatherList5.getJSONObject(0);
                String weatherDescription5 = weatherObject5.getString("description");
                String weatherIcon5 = weatherObject5.getString("icon");
                dayFiveImage.setImageResource(getImageResourceId(weatherIcon5));
                Log.d("brooooo", weatherDescription5);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        public int getImageResourceId(String weatherIcon){
            switch (weatherIcon) {
                case "01d":
                    return R.drawable.ic_01d;
                case "01n":
                    return R.drawable.ic_01n;
                case "02d":
                    return R.drawable.ic_02d;
                case "02n":
                    return R.drawable.ic_02n;
                case "03d":
                    return R.drawable.ic_03d;
                case "03n":
                    return R.drawable.ic_03n;
                case "04d":
                    return R.drawable.ic_04d;
                case "04n":
                    return R.drawable.ic_04n;
                case "09d":
                    return R.drawable.ic_09d;
                case "09n":
                    return R.drawable.ic_09n;
                case "10d":
                    return R.drawable.ic_10d;
                case "10n":
                    return R.drawable.ic_10n;
                case "11d":
                    return R.drawable.ic_11d;
                case "11n":
                    return R.drawable.ic_11n;
                case "13d":
                    return R.drawable.ic_13d;
                case "13n":
                    return R.drawable.ic_13n;
                case "50d":
                    return R.drawable.ic_50d;
                case "50n":
                    return R.drawable.ic_50n;
            }
            return 0;
        }

        public String catchPhrase(int code){

            if(code >= 200 && code <= 232){
                return "Thunderstorm";
            }


            return "hihi";




        }



    }

    }