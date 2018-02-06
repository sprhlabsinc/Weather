package ah.creativecodeapps.tiempo.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class LocationStatusPreference {

	SharedPreferences prefs;

	public LocationStatusPreference(Context context) {
		prefs = WeatherPreferences.getSharedPreferences(context);
	}
	
	public Boolean getStatus() {
		return prefs.getBoolean("status", true);
	}
	
	public void setStatus(Boolean bStatus) {
		prefs.edit().putBoolean("status", bStatus).commit();
	}
	
}
