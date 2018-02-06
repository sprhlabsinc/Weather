package ah.creativecodeapps.tiempo.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherHourModel {

	private long timestamp;
	private double minTemperature;
	private double maxTemperature;
	private double Temperature;
	private double pressure;
	private double humidity;
	private int weatherId;
	private String weatherKey;
	private String weatherDescription;
	private double windSpeed;
	private double windDirection;

	public WeatherHourModel(JSONObject json) {
		try {
			setTimestamp(json.getLong("dt"));
			
			JSONObject mainJSON = json.getJSONObject("main");
			setMinTemperature(mainJSON.getDouble("temp_min"));
			setMaxTemperature(mainJSON.getDouble("temp_max"));
			setTemperature(mainJSON.getDouble("temp"));


			setPressure(mainJSON.getDouble("pressure"));
			setHumidity(mainJSON.getDouble("humidity"));
			
			JSONArray weatherJSONArray = json.getJSONArray("weather");
			JSONObject weatherJSON = (JSONObject)weatherJSONArray.get(0);
			if(weatherJSON != null) {
				setWeatherId(weatherJSON.getInt("id"));
				setWeatherKey(weatherJSON.getString("main"));
				setWeatherDescription(weatherJSON.getString("description"));
			}

			JSONObject windJSON = json.getJSONObject("wind");
			setWindSpeed(windJSON.getDouble("speed"));
			setWindDirection(windJSON.getDouble("deg"));

		} catch(JSONException exception) {
			// do nothing
		}
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public double getMinTemperature() {
		return minTemperature;
	}

	public void setMinTemperature(double minTemperature) {
		this.minTemperature = minTemperature;
	}

	public double getMaxTemperature() {
		return maxTemperature;
	}

	public void setMaxTemperature(double maxTemperature) {
		this.maxTemperature = maxTemperature;
	}

	public double getTemperature() {
		return Temperature;
	}

	public void setTemperature(double maxTemperature) { this.Temperature = maxTemperature;	}

	public double getPressure() {
		return pressure;
	}

	public void setPressure(double pressure) {
		this.pressure = pressure;
	}

	public double getHumidity() { return humidity; }

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

	public void setWeatherDescription(String weatherDescription) { this.weatherDescription = weatherDescription; }

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
