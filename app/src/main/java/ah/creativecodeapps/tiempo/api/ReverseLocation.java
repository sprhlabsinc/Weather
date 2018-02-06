package ah.creativecodeapps.tiempo.api;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;*/

public class ReverseLocation {

	public static String getLocationNameFromLatitudeAndLongitude(Context ctx, double latitude, double longitude) {
//		JSONObject locationInfo = getLocationInfo(latitude, longitude);
//		String locality = null;
//		String country = null;
//		try {
//			JSONArray results = locationInfo.getJSONArray("results");
//			JSONObject firstResult = (JSONObject)results.get(0);
//			JSONArray addressComponents = firstResult.getJSONArray("address_components");
//			for(int i = 0; i < addressComponents.length(); i++) {
//				JSONObject json = addressComponents.getJSONObject(i);
//				JSONArray types = json.getJSONArray("types");
//				List<String> typesList = getTypesFromJSON(types);
//				if(typesList.contains("locality")) {
//					locality = json.getString("long_name");
//				} else if(typesList.contains("country")) {
//					country = json.getString("long_name");
//				}
//
//			}
//		} catch(Exception ex) {
//			return null;
//		}
//
//		if(locality == null || country == null) {
//			return null;
//		}
//
//		return locality + "," + country;

		Geocoder gcd = new Geocoder(ctx, Locale.getDefault());
		List<Address> addresses = null;
		try {
			addresses = gcd.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		if (addresses.size() > 0)
		{
			System.out.println(addresses.get(0).getLocality());
		}
		else
		{
			// do your staff
			return  null;
		}

		String strCity = addresses.get(0).getLocality();
//		String strCountry = addresses.get(0).getCountryCode();
		String strCountry = addresses.get(0).getCountryName();


		return strCity + "," + strCountry;

	}
	
	private static JSONObject getLocationInfo(double latitude, double longitude) {
        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&sensor=false");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch(Exception e) {
        	return null;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
        	return null;
        }
        
        return jsonObject;
    }
	
	private static List<String> getTypesFromJSON(JSONArray typesArray) {
		ArrayList<String> list = new ArrayList<String>();
		
		try {
			for(int i = 0; i < typesArray.length(); i++) {
				list.add(typesArray.getString(i));
			}
		} catch(JSONException ex) {
			// do nothing
		}

		return list;
	}
	
}
