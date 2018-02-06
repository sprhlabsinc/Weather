package ah.creativecodeapps.tiempo.helpers;

import java.util.Date;

import ah.creativecodeapps.tiempo.R;
import ah.creativecodeapps.tiempo.model.WeatherCurrentModel;
import ah.creativecodeapps.tiempo.model.WeatherDayModel;
import ah.creativecodeapps.tiempo.model.WeatherHourModel;

public class ImageProvider {

	public static int getWeatherIconForWeather(WeatherCurrentModel todaysWeather) {

		int actualId = todaysWeather.getWeatherId();

		long sunrise = todaysWeather.getSunrise() * 1000;
		long sunset = todaysWeather.getSunset() * 1000;

		int id = actualId / 100;

		int icon = 0;

		if (actualId == 800) {
			long currentTime = new Date().getTime();
			if (currentTime >= sunrise && currentTime < sunset) {
				icon = R.drawable.weather_sunny;
			} else {
				icon = R.drawable.weather_clear_night;
			}
		} else {
			switch (id) {
			case 2:
				icon = R.drawable.weather_thunder;
				break;
			case 3:
				icon = R.drawable.weather_drizzle;
				break;
			case 7:
				icon = R.drawable.weather_foggy;
				break;
			case 8:
				icon = R.drawable.weather_cloudy;
				break;
			case 6:
				icon = R.drawable.weather_snowy;
				break;
			case 5:
				icon = R.drawable.weather_rainy;
				break;
			default:
				icon = R.drawable.weather_sunny;
				break;
			}
		}

		return icon;
	}
	
	

	public static int getWeatherSmallIconForWeather(WeatherDayModel weatherDayModel) {

		int id = weatherDayModel.getWeatherId() / 100;
		int icon = 0;

		switch (id) {
		case 2:
			icon = R.drawable.small_thunder_icon;
			break;
		case 3:
			icon = R.drawable.small_drizzle_day_icon;
			break;
		case 7:
			icon = R.drawable.small_twister_icon;
			break;
		case 8:
			icon = R.drawable.small_cloudy_day_icon;
			break;
		case 6:
			icon = R.drawable.small_snowy_icon;
			break;
		case 5:
			icon = R.drawable.small_rainy_day_icon;
			break;
		default:
			icon = R.drawable.small_sunny_icon;
			break;
		}

		return icon;
	}
	public static int getWeatherSmallIconForWeather(WeatherHourModel weatherDayModel) {

		int id = weatherDayModel.getWeatherId() / 100;
		int icon = 0;

		switch (id) {
			case 2:
				icon = R.drawable.small_thunder_icon;
				break;
			case 3:
				icon = R.drawable.small_drizzle_day_icon;
				break;
			case 7:
				icon = R.drawable.small_twister_icon;
				break;
			case 8:
				icon = R.drawable.small_cloudy_day_icon;
				break;
			case 6:
				icon = R.drawable.small_snowy_icon;
				break;
			case 5:
				icon = R.drawable.small_rainy_day_icon;
				break;
			default:
				icon = R.drawable.small_sunny_icon;
				break;
		}

		return icon;
	}

	public static int getWeatherBlackSmallIconForWeather(WeatherHourModel hourModel) {

		int id = hourModel.getWeatherId() / 100;
		int icon = 0;

		switch (id) {
			case 2:
				icon = R.drawable.small_thunder_icon_black;
				break;
			case 3:
				icon = R.drawable.small_drizzle_day_icon_black;
				break;
			case 7:
				icon = R.drawable.small_twister_icon_black;
				break;
			case 8:
				icon = R.drawable.small_cloudy_day_icon_black;
				break;
			case 6:
				icon = R.drawable.small_snowy_icon_black;
				break;
			case 5:
				icon = R.drawable.small_rainy_day_icon_black;
				break;
			default:
				icon = R.drawable.small_sunny_icon_black;
				break;
		}

		return icon;
	}

	public static int getWeatherBlackSmallIconForWeather(WeatherDayModel dayModel) {

		int id = dayModel.getWeatherId() / 100;
		int icon = 0;

		switch (id) {
			case 2:
				icon = R.drawable.small_thunder_icon_black;
				break;
			case 3:
				icon = R.drawable.small_drizzle_day_icon_black;
				break;
			case 7:
				icon = R.drawable.small_twister_icon_black;
				break;
			case 8:
				icon = R.drawable.small_cloudy_day_icon_black;
				break;
			case 6:
				icon = R.drawable.small_snowy_icon_black;
				break;
			case 5:
				icon = R.drawable.small_rainy_day_icon_black;
				break;
			default:
				icon = R.drawable.small_sunny_icon_black;
				break;
		}

		return icon;
	}



	public static int getBackgroundForWeather(WeatherCurrentModel todaysWeather) {
		
		int actualId = todaysWeather.getWeatherId();

		long sunrise = todaysWeather.getSunrise() * 1000;
		long sunset = todaysWeather.getSunset() * 1000;

		int id = actualId / 100;

		int icon = 0;

		if (actualId == 800) {
			long currentTime = new Date().getTime();
			if (currentTime >= sunrise && currentTime < sunset) {
				icon = R.drawable.background_sunny;
			} else {
				icon = R.drawable.background_clear_night;
			}
		} else {
			switch (id) {
			case 2:
				icon = R.drawable.background_thunder;
				break;
			case 3:
				icon = R.drawable.background_drizzle;
				break;
			case 7:
				icon = R.drawable.background_foggy;
				break;
			case 8:
				icon = R.drawable.background_cloudy;
				break;
			case 6:
				icon = R.drawable.background_snowy;
				break;
			case 5:
				icon = R.drawable.background_rainy;
				break;
			default:
				icon = R.drawable.background_sunny;
				break;
			}
		}

		return icon;
	}
}
