package ah.creativecodeapps.tiempo.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class CityPreference {
	SharedPreferences prefs;
	
	public CityPreference(Context context) {
		prefs = WeatherPreferences.getSharedPreferences(context);
	}
	
	public String getCity() {
		return prefs.getString("city", "mar del plata, ar");
	}
	
	public void setCity(String city) {
		prefs.edit().putString("city", city).commit();
	}
	
}
