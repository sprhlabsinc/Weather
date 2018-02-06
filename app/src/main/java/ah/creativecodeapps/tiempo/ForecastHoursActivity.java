package ah.creativecodeapps.tiempo;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ah.creativecodeapps.tiempo.api.RemoteFetch;
import ah.creativecodeapps.tiempo.helpers.DateHelper;
import ah.creativecodeapps.tiempo.helpers.ImageProvider;
import ah.creativecodeapps.tiempo.model.WeatherHourModel;

public class ForecastHoursActivity extends AppCompatActivity implements View.OnClickListener {

    private static final double SECONDS_IN_HOUR = 3600.0;
    private static final double METERS_IN_KM = 1000.0;

    TextView txtCityName;
    ListView mHoursList;
    ImageButton btnBack;
    ForecastHoursListAdapter mAdapter;
    ArrayList<ForecastHoursItem> mItems = new ArrayList<ForecastHoursItem>();
    String currentCity;
    private ProgressDialog progressDialog;
    Handler handler;
    private int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_hours);

        currentCity = getIntent().getStringExtra("CITY");
        txtCityName = (TextView) findViewById(R.id.txtCity);
        txtCityName.setText(currentCity);
        mHoursList = (ListView) findViewById(R.id.hoursList);
        mAdapter = new ForecastHoursListAdapter(this, mItems);
        mHoursList.setAdapter(mAdapter);
        btnBack = (ImageButton) findViewById (R.id.btnBack);
        btnBack.setOnClickListener(this);

        handler = new Handler();
        updateWeatherData (currentCity);
        showAd();
    }

    public  void showAd () {
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .build();
        NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.adView);
        adView.loadAd(request);
    }

    private void updateWeatherData(final String city) {

        startUpdatingWeatherData();

        new Thread() {
            public void run() {

                JSONObject json10Hours = RemoteFetch.getJSON(ForecastHoursActivity.this, city, RemoteFetch.OPEN_WEATHER_MAP_API_10HOURS);
                if (json10Hours == null) {
                    json10Hours = RemoteFetch.getCachedData(ForecastHoursActivity.this, RemoteFetch.HOURS_CACHE10);
                }

                final JSONObject finalJson10Hours = json10Hours;

                if (json10Hours == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(ForecastHoursActivity.this, ForecastHoursActivity.this.getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                ArrayList<WeatherHourModel> weatherList = process10HoursJSON(finalJson10Hours);
                                render10HoursWeather(weatherList);
                            } catch (Exception exception) {
                                // do nothing
                            }
                        }

                    });
                }
                updateWeatherDataDidFinish();
            }
        }.start();
    }

    private ArrayList<WeatherHourModel> process10HoursJSON(JSONObject json) throws Exception {
        JSONArray weatherArray = json.getJSONArray("list");
        ArrayList<WeatherHourModel> weatherList = new ArrayList<WeatherHourModel>();

        for (int i = 0; i < weatherArray.length()  ; i++) {
            JSONObject weatherJSON = (JSONObject) weatherArray.get(i);
            WeatherHourModel model = new WeatherHourModel(weatherJSON);
            weatherList.add(model);
        }
        return weatherList;
    }

    public void startUpdatingWeatherData() {
        showLoadingDialog();
    }

    public synchronized void showLoadingDialog() {

        progressDialog = ProgressDialog.show(this, "", getString(R.string.searching), true);
    }

    public synchronized void dismissLoadingDialog() {

        if (progressDialog == null) {
            return;
        }
        progressDialog.dismiss();
    }

    public void updateWeatherDataDidFinish() {
        dismissLoadingDialog();
        //updateWidget();
    }

    private void render10HoursWeather(ArrayList<WeatherHourModel> weatherList) {

        mItems.clear();
        for (int i = 0; i < weatherList.size() - 1 ; i += 2) {

            WeatherHourModel weatherHourModel1 = weatherList.get(i);
            WeatherHourModel weatherHourModel2 = weatherList.get(i + 1);

            int nIcon1 = ImageProvider.getWeatherBlackSmallIconForWeather(weatherHourModel1);
            int nIcon2 = ImageProvider.getWeatherBlackSmallIconForWeather(weatherHourModel2);

            int nTemp1 = (int) weatherHourModel1.getTemperature();
            int nTemp2 = (int) weatherHourModel2.getTemperature();

            String strTemp1 = nTemp1 + RemoteFetch.TEMPERATURE_SYMBOL;
            String strTemp2 = nTemp2 + RemoteFetch.TEMPERATURE_SYMBOL;

            String time1 = DateHelper.getHourDetail(weatherHourModel1.getTimestamp());
            String time2 = DateHelper.getHourDetail(weatherHourModel2.getTimestamp());


            double windSpeed1 = weatherHourModel1.getWindSpeed();
            double windSpeed2 = weatherHourModel2.getWindSpeed();
            if (RemoteFetch.getUnitsSystem(this) == RemoteFetch.METRIC) {
                windSpeed1 = weatherHourModel1.getWindSpeed() * SECONDS_IN_HOUR / METERS_IN_KM; // Change from m/s to km/h
                windSpeed2 = weatherHourModel2.getWindSpeed() * SECONDS_IN_HOUR / METERS_IN_KM; // Change from m/s to km/h
            }
            String strWind1 = String.format("%.0f " + RemoteFetch.WIND_SPEED_SYMBOL, windSpeed1);
            String strWind2 = String.format("%.0f " + RemoteFetch.WIND_SPEED_SYMBOL, windSpeed2);

            String strHumity1 = weatherHourModel1.getHumidity() + "%";
            String strHumity2 = weatherHourModel2.getHumidity() + "%";

            String strPrecipitation1 =  "none";
            String strPrecipitation2 =  "none";


            mItems.add(new ForecastHoursItem(time1, time2, nIcon1, nIcon2, strTemp1, strTemp2, strWind1, strWind2, strHumity1, strHumity2, strPrecipitation1, strPrecipitation2));
        }

        mAdapter.notifyDataSetChanged();


    }

    @Override
    public void onClick(View v) {
        int nId = v.getId();
        switch (nId) {
            case R.id.btnBack:
                finish();
                break;
            default:
                break;
        }
    }
}

class ForecastHoursItem {
    int  mIcon1;
    String mTime1;
    String mTemp1;
    String mWind1;
    String mWet1;
    String mRain1;

    int  mIcon2;
    String mTime2;
    String mTemp2;
    String mWind2;
    String mWet2;
    String mRain2;


    public Boolean mSelected1 = false;
    public Boolean mSelected2 = false;





    public ForecastHoursItem(String time1,String time2, int icon1, int icon2, String temp1, String temp2, String wind1, String wind2, String wet1, String wet2, String rain1, String rain2) {
        mIcon1 = icon1;
        mTime1 = time1;
        mTemp1 = temp1;
        mWind1 = wind1;
        mWet1 = wet1;
        mRain1 = rain1;

        mIcon2 = icon2;
        mTime2 = time2;
        mTemp2 = temp2;
        mWind2 = wind2;
        mWet2 = wet2;
        mRain2 = rain2;


    }
}

class  ForecastHoursListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<ForecastHoursItem> mItems;

    private static String colorTime = "#fb4f68";


    public ForecastHoursListAdapter(Context context, ArrayList<ForecastHoursItem> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.forecast_hour_item, null);
        }
        else {
            view = convertView;
        }


        ImageView iconView1 = (ImageView) view.findViewById(R.id.img_time1);
        ImageView iconView2 = (ImageView) view.findViewById(R.id.img_time2);
        final TextView txtTime1 = (TextView) view.findViewById(R.id.time1);
        final TextView txtTime2 = (TextView) view.findViewById(R.id.time2);
        TextView txtTemp1 = (TextView) view.findViewById(R.id.temp_day);
        TextView txtTemp2 = (TextView) view.findViewById(R.id.temp_night);

        final TextView txtWind1 = (TextView) view.findViewById(R.id.txt_wind1);
        final TextView txtWet1 = (TextView) view.findViewById(R.id.txt_humidity1);
        final TextView txtRain1 = (TextView) view.findViewById(R.id.txt_precipitation1);

        final TextView txtWind2 = (TextView) view.findViewById(R.id.txt_wind2);
        final TextView txtWet2 = (TextView) view.findViewById(R.id.txt_humidity2);
        final TextView txtRain2 = (TextView) view.findViewById(R.id.txt_precipitation2);


        final RelativeLayout layout_temp1 = (RelativeLayout) view.findViewById(R.id.layout_temp1);
        final LinearLayout layout_detail1 = (LinearLayout) view.findViewById(R.id.weatherDetailLayout1);

        final RelativeLayout layout_temp2 = (RelativeLayout) view.findViewById(R.id.layout_temp2);
        final LinearLayout layout_detail2 = (LinearLayout) view.findViewById(R.id.weatherDetailLayout2);

        layout_detail1.setVisibility(View.GONE);
        layout_temp1.setBackgroundColor(Color.TRANSPARENT);
        txtTime1.setTextColor(Color.parseColor(colorTime));

        layout_detail2.setVisibility(View.GONE);
        layout_temp2.setBackgroundColor(Color.TRANSPARENT);
        txtTime2.setTextColor(Color.parseColor(colorTime));

        if(mItems.get(position).mSelected1 == true )
        {
            layout_detail1.setVisibility(View.VISIBLE);
            layout_temp1.setBackgroundColor(Color.parseColor(colorTime));
            txtTime1.setTextColor(Color.WHITE);
        }
        if(mItems.get(position).mSelected2 == true )
        {
            layout_detail2.setVisibility(View.VISIBLE);
            layout_temp2.setBackgroundColor(Color.parseColor(colorTime));
            txtTime2.setTextColor(Color.WHITE);
        }









        layout_temp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_temp2.setBackgroundColor(Color.TRANSPARENT);
                layout_detail2.setVisibility(View.GONE);
                txtTime2.setTextColor(Color.parseColor(colorTime));
                if(layout_detail1.getVisibility() == View.GONE) {
                    layout_detail1.setVisibility(View.VISIBLE);
                    layout_temp1.setBackgroundColor(Color.parseColor(colorTime));
                    txtTime1.setTextColor(Color.WHITE);
                    mItems.get(position).mSelected1 = true;
                    mItems.get(position).mSelected2 = false;
                }
                else {
                    layout_detail1.setVisibility(View.GONE);
                    layout_temp1.setBackgroundColor(Color.TRANSPARENT);
                    txtTime1.setTextColor(Color.parseColor(colorTime));
                    mItems.get(position).mSelected1 = false;
                }

            }
        });

        layout_temp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_temp1.setBackgroundColor(Color.TRANSPARENT);
                layout_detail1.setVisibility(View.GONE);
                txtTime1.setTextColor(Color.parseColor(colorTime));
                if(layout_detail2.getVisibility() == View.GONE)
                {
                    layout_detail2.setVisibility(View.VISIBLE);
                    layout_temp2.setBackgroundColor(Color.parseColor(colorTime));
                    txtTime2.setTextColor(Color.WHITE);
                    mItems.get(position).mSelected2 = true;
                    mItems.get(position).mSelected1 = false;
                }
                else {
                    layout_detail2.setVisibility(View.GONE);
                    layout_temp2.setBackgroundColor(Color.TRANSPARENT);
                    txtTime2.setTextColor(Color.parseColor(colorTime));
                    mItems.get(position).mSelected2 = false;
                }

            }
        });


        txtTime1.setText( mItems.get(position).mTime1);
        txtTemp1.setText( mItems.get(position).mTemp1);
        iconView1.setImageResource(mItems.get(position).mIcon1);
        txtWind1.setText( mItems.get(position).mWind1);
        txtWet1.setText( mItems.get(position).mWet1);
        txtRain1.setText( mItems.get(position).mRain1);

        txtTime2.setText( mItems.get(position).mTime2);
        txtTemp2.setText( mItems.get(position).mTemp2);
        iconView2.setImageResource(mItems.get(position).mIcon2);
        txtWind2.setText( mItems.get(position).mWind2);
        txtWet2.setText( mItems.get(position).mWet2);
        txtRain2.setText( mItems.get(position).mRain2);


        return view;
    }

}
