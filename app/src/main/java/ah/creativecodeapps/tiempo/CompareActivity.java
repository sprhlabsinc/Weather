package ah.creativecodeapps.tiempo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class CompareActivity extends AppCompatActivity   implements View.OnClickListener  {

    private static final double SECONDS_IN_HOUR = 3600.0;
    private static final double METERS_IN_KM = 1000.0;

    ListView mCompareList;
    ImageButton btnBack;
    CompareListAdapter mAdapter;
    ArrayList<CompareItem> mItems = new ArrayList<CompareItem>();
    String currentCity1;
    String currentCity2;
    private ProgressDialog progressDialog;
    Handler handler;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        currentCity1 = getIntent().getStringExtra("CITY1");
        currentCity2 = getIntent().getStringExtra("CITY2");
        TextView txtCity1 = (TextView) findViewById(R.id.txtCity1);
        TextView txtCity2 = (TextView) findViewById(R.id.txtCity2);
        txtCity1.setText(currentCity1);
        txtCity2.setText(currentCity2);

        mCompareList = (ListView) findViewById(R.id.compareList);
        mAdapter = new CompareListAdapter(this, mItems);
        mCompareList.setAdapter(mAdapter);
        btnBack = (ImageButton) findViewById (R.id.btnBack);
        btnBack.setOnClickListener(this);



        handler = new Handler();
        updateWeatherData (currentCity1, currentCity2);
        showAd();
    }

    public  void showAd () {
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .build();
        NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.adView);
        adView.loadAd(request);
    }

    private void updateWeatherData(final String city1, final String city2) {

        startUpdatingWeatherData();

        new Thread() {
            public void run() {

                JSONObject jsonCompareData1 = RemoteFetch.getJSON(CompareActivity.this, city1, RemoteFetch.OPEN_WEATHER_MAP_API_14DAYS);
                if (jsonCompareData1 == null) {
                    jsonCompareData1 = RemoteFetch.getCachedData(CompareActivity.this, RemoteFetch.DAYS_CACHE14);
                }

                JSONObject jsonCompareData2 = RemoteFetch.getJSON(CompareActivity.this, city2, RemoteFetch.OPEN_WEATHER_MAP_API_14DAYS);
                if (jsonCompareData2 == null) {
                    jsonCompareData2 = RemoteFetch.getCachedData(CompareActivity.this, RemoteFetch.DAYS_CACHE14);
                }

                final JSONObject finalJsonCompareData1 = jsonCompareData1;
                final JSONObject finalJsonCompareData2 = jsonCompareData2;

                if (jsonCompareData1 == null && jsonCompareData2 == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(CompareActivity.this, CompareActivity.this.getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                ArrayList<WeatherDayModel> weatherList1 = process14DaysJSON(finalJsonCompareData1);
                                ArrayList<WeatherDayModel> weatherList2 = process14DaysJSON(finalJsonCompareData2);
                                render14DaysWeather(weatherList1, weatherList2);
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

    private void render14DaysWeather(ArrayList<WeatherDayModel> weatherList1, ArrayList<WeatherDayModel> weatherList2 ) {

        int nDays = Math.min(weatherList1.size(), weatherList2.size());
        mItems.clear();
        for (int i = 0; i < nDays ; i ++) {

            WeatherDayModel weatherDayModel1 = weatherList1.get(i);
            WeatherDayModel weatherDayModel2 = weatherList2.get(i);

            int nIcon1 = ImageProvider.getWeatherBlackSmallIconForWeather(weatherDayModel1);
            int nIcon2 = ImageProvider.getWeatherBlackSmallIconForWeather(weatherDayModel2);

            int nMaxTemp1 = (int) weatherDayModel1.getMaxTemperature();
            int nMinTemp1 = (int) weatherDayModel1.getMinTemperature();

            int nMaxTemp2 = (int) weatherDayModel2.getMaxTemperature();
            int nMinTemp2 = (int) weatherDayModel2.getMinTemperature();

            String time1 = DateHelper.getShortDay(weatherDayModel1.getTimestamp());
            String time2 = DateHelper.getShortDay(weatherDayModel2.getTimestamp());

            mItems.add(new CompareItem(nIcon1, nIcon2, time1, time2, nMaxTemp1, nMinTemp1, nMaxTemp2, nMinTemp2));
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

class CompareItem {
    int  mIcon1;
    int  mIcon2;
    int mMaxTemp1;
    int mMinTemp1;
    int mMaxTemp2;
    int mMinTemp2;

    String mDay1;
    String mDay2;


    public CompareItem(int icon1, int icon2,  String day1, String day2, int maxTem1, int minTem1, int maxTem2, int minTem2) {
        mIcon1 = icon1;
        mIcon2 = icon2;

        mMaxTemp1 = maxTem1;
        mMinTemp1 = minTem1;
        mMaxTemp2 = maxTem2;
        mMinTemp2 = minTem2;
        mDay1 = day1;
        mDay2 = day2;
    }
}

class  CompareListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<CompareItem> mItems;

    public CompareListAdapter(Context context, ArrayList<CompareItem> items) {
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
            view = inflater.inflate(R.layout.compare_item, null);
        }
        else {
            view = convertView;
        }

        TextView txtDayCity1 = (TextView) view.findViewById(R.id.timeCity1);
        TextView txtDayCity2 = (TextView) view.findViewById(R.id.timeCity2);
        TextView txtTempCity1 = (TextView) view.findViewById(R.id.tempCity1);
        TextView txtTempCity2 = (TextView) view.findViewById(R.id.tempCity2);
        ImageView iconView1 = (ImageView) view.findViewById(R.id.img_weather_city1);
        ImageView iconView2 = (ImageView) view.findViewById(R.id.img_weather_city2);


        String strTemp1 = mItems.get(position).mMaxTemp1 + " / "+ mItems.get(position).mMinTemp1 + RemoteFetch.TEMPERATURE_SYMBOL;
        String strTemp2 = mItems.get(position).mMaxTemp2 + " / "+ mItems.get(position).mMinTemp2 + RemoteFetch.TEMPERATURE_SYMBOL;

        txtDayCity1.setText( mItems.get(position).mDay1);
        txtDayCity2.setText( mItems.get(position).mDay2);

        txtTempCity1.setText( strTemp1);
        txtTempCity2.setText( strTemp2);

        iconView1.setImageResource(mItems.get(position).mIcon1);
        iconView2.setImageResource(mItems.get(position).mIcon2);
        return view;
    }

}



