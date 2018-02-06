package ah.creativecodeapps.tiempo.helpers;

import android.content.Context;
import android.graphics.Typeface;

public class FontsHelper {
	
	private static Typeface font;
	
	public static void loadFonts(Context context) {
		font = Typeface.createFromAsset(context.getAssets(), "fonts/weather.ttf");
	}
	
	public static Typeface getDefaultFont() {
		return font;
	}
}
