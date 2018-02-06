package ah.creativecodeapps.tiempo.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class WeatherPreferences {

	public static SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences("ah.creativecodeapps.tiempo", Context.MODE_PRIVATE);
	}
	
}
