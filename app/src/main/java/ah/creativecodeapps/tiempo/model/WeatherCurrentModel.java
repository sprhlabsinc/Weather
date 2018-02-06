package ah.creativecodeapps.tiempo.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherCurrentModel {

	private String country;
	private String city;
	private long timestamp;
	private long sunrise;
	private long sunset;
	private double temperature;
	private double pressure;
	private double humidity;
	private int weatherId;
	private String weatherKey;
	private String weatherDescription;
	private double windSpeed;
	private double windDirection;
	private double rain;

	public WeatherCurrentModel(JSONObject json) {
		try {
			setTimestamp(json.getLong("dt"));
			setCity(json.getString("name"));

			JSONObject sysObject = json.getJSONObject("sys");
			setCountry(sysObject.getString("country"));
			setSunrise(sysObject.getLong("sunrise"));
			setSunset(sysObject.getLong("sunset"));

			JSONArray weatherArray = json.getJSONArray("weather");
			JSONObject weatherObject = (JSONObject) weatherArray.get(0);
			if (weatherObject != null) {
				setWeatherId(weatherObject.getInt("id"));
				setWeatherKey(weatherObject.getString("main"));
				setWeatherDescription(weatherObject.getString("description"));
			}

			JSONObject mainObject = json.getJSONObject("main");
			setTemperature(mainObject.getDouble("temp"));
			setPressure(mainObject.getDouble("pressure"));
			setHumidity(mainObject.getDouble("humidity"));

			JSONObject windObject = json.getJSONObject("wind");
			setWindSpeed(windObject.getDouble("speed"));
			setWindDirection(windObject.getDouble("deg"));

			JSONObject rain = json.optJSONObject("rain");

			if (rain != null) {
				try {
					setRain(rain.getDouble("3h"));
				} catch (JSONException e1) {
					try {
						setRain(rain.getDouble("2h"));
					} catch (JSONException e2) {
						setRain(rain.optDouble("1h", 0.0));
					}
				}
			}

		} catch (JSONException exception) {
			// do nothing
		}
	}

	private void setRain(double rain) {
		this.rain = rain;
	}

	/**
	 * 
	 * @return Precipitation, mm
	 */
	public double getRain() {
		return rain;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getSunrise() {
		return sunrise;
	}

	public void setSunrise(long sunrise) {
		this.sunrise = sunrise;
	}

	public long getSunset() {
		return sunset;
	}

	public void setSunset(long sunset) {
		this.sunset = sunset;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getPressure() {
		return pressure;
	}

	public void setPressure(double pressure) {
		this.pressure = pressure;
	}

	public double getHumidity() {
		return humidity;
	}

	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}

	public int getWeatherId() {
		return weatherId;
	}

	public void setWeatherId(int weatherId) {
		this.weatherId = weatherId;
	}

	public String getWeatherKey() {
		return weatherKey;
	}

	public void setWeatherKey(String weatherKey) {
		this.weatherKey = weatherKey;
	}

	public String getWeatherDescription() {
		return weatherDescription;
	}

	public void setWeatherDescription(String weatherDescription) {
		this.weatherDescription = weatherDescription;
	}

	public double getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
	}

	public double getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(double windDirection) {
		this.windDirection = windDirection;
	}

}
