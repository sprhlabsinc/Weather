package ah.creativecodeapps.tiempo;

import android.app.ProgressDialog;
import android.content.Context;
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
import ah.creativecodeapps.tiempo.model.WeatherDayModel;

public class ForecastDaysActivity extends AppCompatActivity  implements View.OnClickListener  {

    private static final double SECONDS_IN_HOUR = 3600.0;
    private static final double METERS_IN_KM = 1000.0;

    TextView txtCityName;
    ListView mDayList;
    ImageButton btnBack;
    ForecastDayListAdapter mAdapter;
    ArrayList<ForecastDayItem> mItems = new ArrayList<ForecastDayItem>();
    String currentCity;
    private ProgressDialog progressDialog;
    Handler handler;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_days);
        currentCity = getIntent().getStringExtra("CITY");
        txtCityName = (TextView) findViewById(R.id.txtCity);
        txtCityName.setText(currentCity);
        mDayList = (ListView) findViewById(R.id.dayList);
        mAdapter = new ForecastDayListAdapter(this, mItems);
        mDayList.setAdapter(mAdapter);
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

                JSONObject json14Days = RemoteFetch.getJSON(ForecastDaysActivity.this, city, RemoteFetch.OPEN_WEATHER_MAP_API_14DAYS);
                if (json14Days == null) {
                    json14Days = RemoteFetch.getCachedData(ForecastDaysActivity.this, RemoteFetch.DAYS_CACHE14);
                }

                final JSONObject finalJson14Days = json14Days;

                if (json14Days == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(ForecastDaysActivity.this, ForecastDaysActivity.this.getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                ArrayList<WeatherDayModel> weatherList = process14DaysJSON(finalJson14Days);
                                render14DaysWeather(weatherList);
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

    private ArrayList<WeatherDayModel> process14DaysJSON(JSONObject json) throws Exception {
        JSONArray weatherArray = json.getJSONArray("list");
        ArrayList<WeatherDayModel> weatherList = new ArrayList<WeatherDayModel>();

        for (int i = 0; i < weatherArray.length(); i++) {
            JSONObject weatherJSON = (JSONObject) weatherArray.get(i);
            WeatherDayModel model = new WeatherDayModel(weatherJSON);
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

    private void render14DaysWeather(ArrayList<WeatherDayModel> weatherList) {

        mItems.clear();
        for (int i = 0; i < weatherList.size(); i++) {

            WeatherDayModel weatherDayModel = weatherList.get(i);

            int nIcon1 = ImageProvider.getWeatherSmallIconForWeather(weatherDayModel);
            int nIcon2 = ImageProvider.getWeatherSmallIconForWeather(weatherDayModel);

            int minTemperature = (int) weatherDayModel.getMinTemperature();
            int maxTemperature = (int) weatherDayModel.getMaxTemperature();
            String minTemp = minTemperature + RemoteFetch.TEMPERATURE_SYMBOL;
            String maxTemp = maxTemperature + RemoteFetch.TEMPERATURE_SYMBOL;

            String day = DateHelper.getShorDayNameMonthDayNumber(weatherDayModel.getTimestamp());

            double windSpeed = weatherDayModel.getWindSpeed();
            if (RemoteFetch.getUnitsSystem(this) == RemoteFetch.METRIC) {
                windSpeed = weatherDayModel.getWindSpeed() * SECONDS_IN_HOUR / METERS_IN_KM; // Change from m/s to km/h
            }
            String strWind = String.format("%.0f " + RemoteFetch.WIND_SPEED_SYMBOL, windSpeed);
            String strHumity = weatherDayModel.getHumidity() + "%";
            String strPrecipitation = String.format("%.1f", weatherDayModel.getRainProbability()) + " mm";


            mItems.add(new ForecastDayItem(nIcon1, nIcon2, day, maxTemp, minTemp, strWind, strHumity, strPrecipitation));
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

class ForecastDayItem {
    int  mIcon1;
    int  mIcon2;
    String mTemp1;
    String mTemp2;

    String mDay;
    String mWind;
    String mWet;
    String mRain;

    public Boolean mSelected = false;


    public ForecastDayItem(int icon1, int icon2,  String day, String tem1, String tem2, String wind, String wet, String rain) {
        mIcon1 = icon1;
        mIcon2 = icon2;

        mTemp1 = tem1;
        mTemp2 = tem2;
        mWind = wind;
        mWet = wet;
        mRain = rain;
        mDay = day;
    }
}

class  ForecastDayListAdapter extends BaseAdapter{

    Context mContext;
    ArrayList<ForecastDayItem> mItems;

    public ForecastDayListAdapter(Context context, ArrayList<ForecastDayItem> items) {
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
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.forecast_day_item, null);
        }
        else {
            view = convertView;
        }

        TextView txtDay = (TextView) view.findViewById(R.id.day);
        TextView txtTempDay = (TextView) view.findViewById(R.id.temp_day);
        TextView txtTempNight = (TextView) view.findViewById(R.id.temp_night);

        TextView txtWind = (TextView) view.findViewById(R.id.txt_wind);
        TextView txtWet = (TextView) view.findViewById(R.id.txt_humidity);
        TextView txtRain = (TextView) view.findViewById(R.id.txt_precipitation);
        ImageView iconView1 = (ImageView) view.findViewById(R.id.img_weather_day);
        ImageView iconView2 = (ImageView) view.findViewById(R.id.img_weather_night);

        LinearLayout layout_temp = (LinearLayout) view.findViewById(R.id.layout_temperature);
        final LinearLayout layout_detail = (LinearLayout) view.findViewById(R.id.weatherDetailLayout);

        layout_detail.setVisibility(View.GONE);
        if(mItems.get(position).mSelected == true)
        {
            layout_detail.setVisibility(View.VISIBLE);
        }


        layout_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layout_detail.getVisibility() == View.GONE)
                {
                    layout_detail.setVisibility(View.VISIBLE);
                    mItems.get(position).mSelected = true;
                } else {
                    layout_detail.setVisibility(View.GONE);
                    mItems.get(position).mSelected = false;
                }

            }
        });


        txtDay.setText( mItems.get(position).mDay);
        txtTempDay.setText( mItems.get(position).mTemp1);
        txtTempNight.setText( mItems.get(position).mTemp2);
        txtWind.setText( mItems.get(position).mWind);
        txtWet.setText( mItems.get(position).mWet);
        txtRain.setText( mItems.get(position).mRain);

        iconView1.setImageResource(mItems.get(position).mIcon1);
        iconView2.setImageResource(mItems.get(position).mIcon2);
        return view;
    }

}

