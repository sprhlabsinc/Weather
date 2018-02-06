package ah.creativecodeapps.tiempo;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ah.creativecodeapps.tiempo.api.RemoteFetch;
import ah.creativecodeapps.tiempo.api.ReverseLocation;
import ah.creativecodeapps.tiempo.helpers.CityPreference;
import ah.creativecodeapps.tiempo.helpers.DateHelper;
import ah.creativecodeapps.tiempo.helpers.FavoriteCitiesPreference;
import ah.creativecodeapps.tiempo.helpers.ImageProvider;
import ah.creativecodeapps.tiempo.helpers.LocationStatusPreference;
import ah.creativecodeapps.tiempo.model.WeatherCurrentModel;
import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    private static final double SECONDS_IN_HOUR = 3600.0;
    private static final double METERS_IN_KM = 1000.0;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE1 = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE2 = 2;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE3 = 3;



    int FORECAST_HOURS_REQUEST_CODE = 4;
    int FORECAST_DAYS_REQUEST_CODE = 5;
    int COMPARE_CITIES_REQUEST_CODE = 6;


    private static String TAG = MainActivity.class.getSimpleName();

    ListView mDrawerList;
    DrawerListAdapter mDrawerListAdapter;
    RelativeLayout mDrawerPane;
    //    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

    private SimpleLocation location;
    private Timer locationTimer;
    private Handler handler;

    private ProgressDialog progressDialog;
    private int counter = 0;

    TextView windDetail;
    TextView humidityDetail;
    TextView precipitation;
    Button btnCurrentTemperature;
    Button btnForecastDays;
    ImageButton btnToggle;
    ImageButton btnSearch;
    ImageButton btnFavorite;
    Button btnCompare;
    TextView txtCityName;
    TextView txtCompareCityName1;
    TextView txtCompareCityName2;
    Button btnForecastHours;
    ImageView weatherIcon;
    TextView dateField;
    Switch switchLocation;

    RelativeLayout todayDetailLayout;
    ScrollView mainScrollView;
    Boolean mSlideStatus = false;
    String currentCity = "";
    String compareCity1 = "";
    String compareCity2 = "";
    Boolean isFovorite = false;

    List favoriteList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        loadFavorites();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerListAdapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(mDrawerListAdapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });
        initUIVariables();
        location = new SimpleLocation(MainActivity.this);
        handler = new Handler();
        LocationStatusPreference locationPref = new LocationStatusPreference(MainActivity.this);
        switchLocation.setChecked(locationPref.getStatus());
    }


    public void loadFavorites ()
    {
        mNavItems.clear();
        favoriteList = new FavoriteCitiesPreference(MainActivity.this).loadFavorites();
        if(favoriteList != null)
        {
            for(int nIdx = 0; nIdx < favoriteList.size(); nIdx ++)
            {
                String strCity = favoriteList.get(nIdx).toString();
                mNavItems.add(new NavItem(strCity, R.drawable.ic_favorite));
            }
        }
    }

    public  void initUIVariables ()
    {
        windDetail = (TextView) findViewById(R.id.wind);
        humidityDetail = (TextView) findViewById(R.id.humidity);
        precipitation = (TextView) findViewById(R.id.precipitation);
        btnCurrentTemperature = (Button) findViewById(R.id.current_temperature);
        weatherIcon = (ImageView) findViewById(R.id.weather_icon);
        dateField = (TextView) findViewById(R.id.date_field);
        todayDetailLayout = (RelativeLayout)findViewById(R.id.layout_today_detail);
        mainScrollView = (ScrollView)findViewById(R.id.sv);
        btnForecastHours = (Button) findViewById(R.id.btn_forecast_hours);
        btnForecastDays = (Button) findViewById(R.id.btn_forecast_days);
        btnToggle = (ImageButton) findViewById(R.id.btnDrawerMenu);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        btnFavorite = (ImageButton) findViewById(R.id.btnFavorite);
        txtCityName = (TextView) findViewById(R.id.txtCity);
        txtCompareCityName1 = (TextView) findViewById(R.id.txtCity1);
        txtCompareCityName2 = (TextView) findViewById(R.id.txtCity2);
        btnCompare = (Button) findViewById(R.id.btnCompare);
        switchLocation = (Switch) findViewById(R.id.switchLocation);

        btnCurrentTemperature.setOnClickListener(this);
        btnForecastDays.setOnClickListener(this);
        btnForecastHours.setOnClickListener(this);
        btnToggle.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnFavorite.setOnClickListener(this);
        txtCompareCityName1.setOnClickListener(this);
        txtCompareCityName2.setOnClickListener(this);
        btnCompare.setOnClickListener(this);
        switchLocation.setOnCheckedChangeListener(this);

        showAd();

    }

    public  void showAd () {
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .build();
        NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.adView);
        adView.loadAd(request);
    }

    private void selectItemFromDrawer(int position) {
        currentCity = mNavItems.get(position).mTitle;
        updateWeatherData(currentCity);
        Fragment fragment = new PreferencesFragement();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, fragment)
                .commit();

        mDrawerList.setItemChecked(position, true);
        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
        mSlideStatus = false;
    }

    private void autodetectCity()
    {
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        } else {
            location.beginUpdates();
            locationTimer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    doAutoDetectCity();
                }
            };
            locationTimer.scheduleAtFixedRate(timerTask, 0, 1000);
        }
    }

    private void doAutoDetectCity() {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        if (latitude != 0 || longitude != 0) {
            if (locationTimer != null) {
                locationTimer.cancel();
                locationTimer = null;
            }

            final String locationName = ReverseLocation.getLocationNameFromLatitudeAndLongitude(MainActivity.this, location.getLatitude(),
                    location.getLongitude());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (locationName != null) {
                        changeCity(locationName);
                    }
                }
            });

            location.endUpdates();
        }
    }

    public void manualSelectCity (double dLat, double dLong)
    {
        final String locationName = ReverseLocation.getLocationNameFromLatitudeAndLongitude(MainActivity.this, dLat, dLong);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (locationName != null) {
                    changeCity(locationName);
                }
            }
        });
    }

    public void changeCity(String city) {
        updateWeatherData(city);
        currentCity = city;
        new CityPreference(MainActivity.this).setCity(city);
    }


    public void setCityName (String city)
    {
        txtCityName.setText(city);
    }
    public void setCompareCityName1 (String city)
    {
        txtCompareCityName1.setText(city);
    }
    public void setCompareCityName2 (String city)
    {
        txtCompareCityName2.setText(city);
    }

    private void updateWeatherData(final String city) {

        startUpdatingWeatherData();

        new Thread() {
            public void run() {
                JSONObject jsonTodays = RemoteFetch.getJSON(MainActivity.this, city, RemoteFetch.OPEN_WEATHER_MAP_API_TODAYS);
                if (jsonTodays == null) {
                    jsonTodays = RemoteFetch.getCachedData(MainActivity.this, RemoteFetch.TODAYS_CACHE);
                }
                final JSONObject finalJsonTodays = jsonTodays;

                if (jsonTodays == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                WeatherCurrentModel todaysWeather = processTodaysJSON(finalJsonTodays);
                                renderTodaysWeather(todaysWeather);
                            } catch (Exception exception) {
                                // do nothing
                            }
                        }

                    });
                }
                updateWeatherDataDidFinish();
                mainScrollView.scrollTo(0,0);
            }
        }.start();
    }

    @SuppressLint("DefaultLocale")
    private void renderTodaysWeather(WeatherCurrentModel todaysWeather) {
        setCityName(currentCity);
        isFovorite = isFavorite(currentCity);
        if(isFovorite)
            btnFavorite.setBackgroundResource(R.drawable.ic_favorite);
        else
            btnFavorite.setBackgroundResource(R.drawable.ic_unfavorite);
        humidityDetail.setText(todaysWeather.getHumidity() + "%");

        double windSpeed = todaysWeather.getWindSpeed();
        if (RemoteFetch.getUnitsSystem(this) == RemoteFetch.METRIC) {
            windSpeed = todaysWeather.getWindSpeed() * SECONDS_IN_HOUR / METERS_IN_KM; // Change from m/s to km/h
        }
        windDetail.setText(String.format("%.0f " + RemoteFetch.WIND_SPEED_SYMBOL, windSpeed));
        precipitation.setText(String.format("%.1f", todaysWeather.getRain()) + " mm");
        dateField.setText(DateHelper.getDayNameMonthDayNumber(todaysWeather.getTimestamp()));

        btnCurrentTemperature.setText(String.format("%.0f", todaysWeather.getTemperature()) + RemoteFetch.TEMPERATURE_SYMBOL);

//        String date = DateHelper.getDayNameMonthDayNumberHour(todaysWeather.getTimestamp());
//        updatedField.setText(getActivity().getString(R.string.last_update) + ": " + date);

        weatherIcon.setImageResource(ImageProvider.getWeatherIconForWeather(todaysWeather));
        //todayDetailLayout.setBackgroundResource(ImageProvider.getBackgroundForWeather(todaysWeather));
        //dismissLoadingDialog();
    }
    Boolean isFavorite (String strCity) {
        if(favoriteList != null)
        {
            for(int nIdx = 0; nIdx < favoriteList.size(); nIdx ++)
            {
                String cityName = favoriteList.get(nIdx).toString();
                if(cityName.equals(strCity))
                    return true;
            }
        }
        return false;
    }

    public void updateWeatherDataDidFinish() {
        dismissLoadingDialog();
        //updateWidget();
    }

    private WeatherCurrentModel processTodaysJSON(JSONObject json) throws Exception {
        WeatherCurrentModel todaysWeather = new WeatherCurrentModel(json);
        return todaysWeather;
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

    @Override
    public void onClick(View v) {
        int nId = v.getId();
        switch (nId) {
            case R.id.current_temperature:
                RemoteFetch.toggleUnitsSystem(MainActivity.this);
                updateWeatherData(currentCity);
                break;
            case R.id.btn_forecast_hours:
                Intent intent = new Intent(MainActivity.this, ForecastHoursActivity.class);
                intent.putExtra("CITY", currentCity);
                startActivity(intent);
                break;
            case R.id.btn_forecast_days:
                intent = new Intent(MainActivity.this, ForecastDaysActivity.class);
                intent.putExtra("CITY", currentCity);
                startActivity(intent);
                break;
            case R.id.btnDrawerMenu:
                onDrawerMenu();
                break;
            case R.id.btnSearch:
                onSearch();
                break;
            case R.id.txtCity1:
                onSearchCity1();
                break;
            case R.id.txtCity2:
                onSearchCity2();
                break;
            case R.id.btnCompare:
                onCompare();
                break;
            case R.id.btnFavorite:
                onFavorite();
                break;
            default:
                break;
        }
    }

    public void onDrawerMenu()
    {
        if(mSlideStatus){
            mDrawerLayout.closeDrawer(mDrawerPane);
        }else{
            mDrawerLayout.openDrawer(mDrawerPane);
        }
    }
    public void onSearchCity1()
    {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(typeFilter)
                    .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE2);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }
    public void onSearchCity2()
    {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(typeFilter)
                    .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE3);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }
    public  void  onCompare () {
        if(compareCity1.equals("") ) {
            Toast.makeText(MainActivity.this, R.string.warning_enter_cityname1,
                    Toast.LENGTH_LONG).show();
            return;
        }
        if(compareCity2.equals("")) {
            Toast.makeText(MainActivity.this, R.string.warning_enter_cityname2,
                    Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(MainActivity.this, CompareActivity.class);
        intent.putExtra("CITY1", compareCity1);
        intent.putExtra("CITY2", compareCity2);
        startActivity(intent);

    }

    public void onSearch()
    {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(typeFilter)
                    .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE1);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    public void onFavorite()
    {
        if(currentCity.equals(""))
            return;

        isFovorite = !isFovorite;
        if(isFovorite) {
            btnFavorite.setBackgroundResource(R.drawable.ic_favorite);
            new FavoriteCitiesPreference(MainActivity.this).addFavorite(currentCity);
        } else {
            btnFavorite.setBackgroundResource(R.drawable.ic_unfavorite);
            new FavoriteCitiesPreference(MainActivity.this).removeFavorite(currentCity);
        }
        loadFavorites();
        mDrawerListAdapter.notifyDataSetChanged();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                String  strAddress = place.getAddress().toString();
                String[] aryAddress = strAddress.split(",");
                String strCountry = aryAddress[aryAddress.length - 1];
                String cityName = place.getName().toString() + "," + strCountry;
                updateWeatherData(cityName);
                currentCity = cityName;
                setCityName(cityName);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE2) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                String  strAddress = place.getAddress().toString();
                String[] aryAddress = strAddress.split(",");
                String strCountry = aryAddress[aryAddress.length - 1];
                String cityName = place.getName().toString() + "," + strCountry;
                compareCity1 = cityName;
                setCompareCityName1(cityName);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE3) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                String  strAddress = place.getAddress().toString();
                String[] aryAddress = strAddress.split(",");
                String strCountry = aryAddress[aryAddress.length - 1];
                String cityName = place.getName().toString() + "," + strCountry;
                compareCity2 = cityName;
                setCompareCityName2(cityName);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LocationStatusPreference pref = new LocationStatusPreference(MainActivity.this);
        if(buttonView.getId() == R.id.switchLocation)
        {
            pref.setStatus(isChecked);
            CityPreference cityPref = new CityPreference(MainActivity.this);
            if(isChecked) {
                autodetectCity();
            } else
                changeCity(cityPref.getCity());
        }
    }
}



class NavItem {
    String mTitle;
    int mIcon;

    public NavItem(String title, int icon) {
        mTitle = title;
        mIcon = icon;
    }
}

class DrawerListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<NavItem> mNavItems;

    public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
        mContext = context;
        mNavItems = navItems;
    }

    @Override
    public int getCount() {
        return mNavItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_item, null);
        }
        else {
            view = convertView;
        }



        String strCityName = mNavItems.get(position).mTitle;
        String[] aryAddress = strCityName.split(",");
        String strCountry = aryAddress[aryAddress.length - 1];
        String strCity = aryAddress[0];



        TextView titleView = (TextView) view.findViewById(R.id.cityName);
        TextView subtitleView = (TextView) view.findViewById(R.id.countryName);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon_city);

        titleView.setText( strCity );
        subtitleView.setText( strCountry );
        iconView.setImageResource(mNavItems.get(position).mIcon);
        return view;
    }
}

