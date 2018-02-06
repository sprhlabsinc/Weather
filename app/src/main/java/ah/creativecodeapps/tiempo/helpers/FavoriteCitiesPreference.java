package ah.creativecodeapps.tiempo.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class FavoriteCitiesPreference {

	SharedPreferences prefs;
	public static final String FAVORITES = "Favorites";

	public FavoriteCitiesPreference(Context context) {
		prefs = WeatherPreferences.getSharedPreferences(context);
	}
	
	public void storeFavorites(ArrayList favorites) {
		Gson gson = new Gson();
		String jsonFavorites = gson .toJson(favorites);
		prefs.edit().putString(FAVORITES, jsonFavorites).commit();
	}
	public ArrayList loadFavorites (){
		ArrayList favorites;
		if (prefs.contains(FAVORITES)) {
			String jsonFavorites = prefs.getString(FAVORITES, null);
			Gson gson = new Gson();
			String[] favoriteItems = gson .fromJson(jsonFavorites, String[].class);
			favorites = new ArrayList(Arrays.asList(favoriteItems));
			return favorites;
		} else
			return null;
	}

	public void addFavorite(String str) {
		ArrayList favorites = loadFavorites();
		if (favorites == null)
			favorites = new ArrayList();
		favorites.add(str);
		storeFavorites(favorites);
	}
	public void removeFavorite(String str) {
		ArrayList favorites = loadFavorites();
		if (favorites != null) {
			favorites.remove(str);
			storeFavorites(favorites);
		}
	}
	public  void removeAllFavorites() {
		ArrayList favorites = loadFavorites();
		if (favorites != null) {
			favorites.clear();
			storeFavorites(favorites);
		}
	}
}
