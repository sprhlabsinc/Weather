package ah.creativecodeapps.tiempo.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDayModel {

	private long timestamp;
	private double minTemperature;
	private double maxTemperature;
	private double dayTemperature;
	private double morningTemperature;
	private double eveningTemperature;
	private double nightTemperature;
	private double pressure;
	private double humidity;
	private int weatherId;
	private String weatherKey;
	private String weatherDescription;
	private double windSpeed;
	private double windDirection;
	private double rainProbability;
	
	public WeatherDayModel(JSONObject json) {
		try {
			setTimestamp(json.getLong("dt"));
			
			JSONObject temperatureJSON = json.getJSONObject("temp");
			setMinTemperature(temperatureJSON.getDouble("min"));
			setMaxTemperature(temperatureJSON.getDouble("max"));
			setDayTemperature(temperatureJSON.getDouble("day"));
			setMorningTemperature(temperatureJSON.getDouble("morn"));
			setEveningTemperature(temperatureJSON.getDouble("eve"));
			setNightTemperature(temperatureJSON.getDouble("night"));
			
			setPressure(json.getDouble("pressure"));
			setHumidity(json.getDouble("humidity"));
			
			JSONArray weatherJSONArray = json.getJSONArray("weather");
			JSONObject weatherJSON = (JSONObject)weatherJSONArray.get(0);
			if(weatherJSON != null) {
				setWeatherId(weatherJSON.getInt("id"));
				setWeatherKey(weatherJSON.getString("main"));
				setWeatherDescription(weatherJSON.getString("description"));
			}
			
			setWindSpeed(json.getDouble("speed"));
			setWindDirection(json.getDouble("deg"));
			setRainProbability(json.getDouble("rain"));
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

	public double getDayTemperature() {
		return dayTemperature;
	}

	public void setDayTemperature(double dayTemperature) {
		this.dayTemperature = dayTemperature;
	}

	public double getMorningTemperature() {
		return morningTemperature;
	}

	public void setMorningTemperature(double morningTemperature) {
		this.morningTemperature = morningTemperature;
	}

	public double getEveningTemperature() {
		return eveningTemperature;
	}

	public void setEveningTemperature(double eveningTemperature) {
		this.eveningTemperature = eveningTemperature;
	}

	public double getNightTemperature() {
		return nightTemperature;
	}

	public void setNightTemperature(double nightTemperature) {
		this.nightTemperature = nightTemperature;
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

	public double getRainProbability() {
		return rainProbability;
	}

	public void setRainProbability(double rainProbability) {
		this.rainProbability = rainProbability;
	}
	
}
