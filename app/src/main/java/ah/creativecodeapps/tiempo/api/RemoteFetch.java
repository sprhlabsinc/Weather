package ah.creativecodeapps.tiempo.api;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import ah.creativecodeapps.tiempo.R;
import ah.creativecodeapps.tiempo.helpers.WeatherPreferences;

public class RemoteFetch {

	//
	// http://openweathermap.org/api
	//
	public static final String METRIC = "metric";
	public static final String METRIC_SYMBOL = "ºC";
	public static final String METRIC_WIND_SPEED_SYMBOL = "km/h";
	public static final String IMPERIAL = "imperial";
	public static final String IMPERIAL_SYMBOL = "ºF";
	public static final String IMPERIAL_WIND_SPEED_SYMBOL = "miles/h";
	public static String TEMPERATURE_SYMBOL;
	public static String WIND_SPEED_SYMBOL;
	
	public static final String OPEN_WEATHER_MAP_API_TODAYS	= "http://pro.openweathermap.org/data/2.5/weather?q=%s&units=%s&lang=";
	public static final String OPEN_WEATHER_MAP_API_5DAYS  = "http://pro.openweathermap.org/data/2.5/forecast/daily?q=%s&mode=json&units=%s&cnt=5&lang=";
	public static final String OPEN_WEATHER_MAP_API_14DAYS  = "http://pro.openweathermap.org/data/2.5/forecast/daily?q=%s&mode=json&units=%s&cnt=14&lang=";
	public static final String OPEN_WEATHER_MAP_API_10HOURS  = "http://pro.openweathermap.org/data/2.5/forecast/hourly?q=%s&mode=json&units=%s&lang=";

	public static final String TODAYS_CACHE = "todaysCache";
	public static final String DAYS_CACHE5 = "5DaysCache";
	public static final String DAYS_CACHE14 = "14DaysCache";
	public static final String HOURS_CACHE10 = "10HoursCache";
	public static final String UNITS_CACHE = "unitsCache";


	public static JSONObject getJSON(Context context, String city, String apiSuffix) {
		try {
			URL url = null;
//			if(todays) {
//				url = new URL(String.format(OPEN_WEATHER_MAP_API_TODAYS + context.getString(R.string.language_code), URLEncoder.encode(city, "utf-8"), getUnitsSystem(context)));
//			} else {
//				url = new URL(String.format(OPEN_WEATHER_MAP_API_5DAYS + context.getString(R.string.language_code), URLEncoder.encode(city, "utf-8"), getUnitsSystem(context)));
//			}

			url = new URL(String.format(apiSuffix + context.getString(R.string.language_code), URLEncoder.encode(city, "utf-8"), getUnitsSystem(context)));
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			
			connection.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			StringBuffer json = new StringBuffer(1024);
			String tmp = "";
			while((tmp=reader.readLine()) != null) {
				json.append(tmp).append("\n");
			}
			reader.close();
			
			JSONObject data = new JSONObject(json.toString());
			
			if(data.getInt("cod") != 200) {
				return null;
			}
			
			String cacheMode = "";
			if(apiSuffix == OPEN_WEATHER_MAP_API_TODAYS)
				cacheMode = TODAYS_CACHE;
			else if(apiSuffix == OPEN_WEATHER_MAP_API_5DAYS)
				cacheMode = DAYS_CACHE5;
			else if(apiSuffix == OPEN_WEATHER_MAP_API_14DAYS)
				cacheMode = DAYS_CACHE14;
			else if(apiSuffix == OPEN_WEATHER_MAP_API_10HOURS)
				cacheMode = HOURS_CACHE10;
			cacheData(context, json.toString(), cacheMode);
			
			return data;
		} catch(Exception e) {
			return null;
		}
	}

	private static void cacheData(Context context, String dataString, String chacheMode) {
		SharedPreferences prefs = WeatherPreferences.getSharedPreferences(context);
		
//		if(todays) {
//			prefs.edit().putString(TODAYS_CACHE, dataString).commit();
//		} else {
//			prefs.edit().putString(DAYS_CACHE, dataString).commit();
//		}

		prefs.edit().putString(chacheMode, dataString).commit();
	}
	
	public static JSONObject getCachedData(Context context, String chcheMode) {
		SharedPreferences prefs = WeatherPreferences.getSharedPreferences(context);
		
		String dataString = null;
		
//		if(todays) {
//			dataString = prefs.getString(TODAYS_CACHE, null);
//		} else {
//			dataString = prefs.getString(DAYS_CACHE, null);
//		}

		dataString = prefs.getString(chcheMode, null);

		
		if(dataString == null) {
			return null;
		}
		
		JSONObject data = null;
		
		try {
			data = new JSONObject(dataString);
		} catch (JSONException e) {
			data = null;
		}
		
		return data;
	}
	
	public static void toggleUnitsSystem(Context context) {
		if(getUnitsSystem(context).equals(METRIC)) {
			changeUnitsSystem(context, IMPERIAL);
		} else {
			changeUnitsSystem(context, METRIC);
		}
	}
	
	public static void changeUnitsSystem(Context context, String units) {
		SharedPreferences prefs = WeatherPreferences.getSharedPreferences(context);
		prefs.edit().putString(UNITS_CACHE, units).commit();
	}
	
	public static String getUnitsSystem(Context context) {
		SharedPreferences prefs = WeatherPreferences.getSharedPreferences(context);
		String units = prefs.getString(UNITS_CACHE, METRIC);
		
		if(units.equals(METRIC)) {
			TEMPERATURE_SYMBOL = METRIC_SYMBOL;
			WIND_SPEED_SYMBOL = METRIC_WIND_SPEED_SYMBOL;
		} else {
			TEMPERATURE_SYMBOL = IMPERIAL_SYMBOL;
			WIND_SPEED_SYMBOL = IMPERIAL_WIND_SPEED_SYMBOL;
		}
		
		return units;
	}
	
}
