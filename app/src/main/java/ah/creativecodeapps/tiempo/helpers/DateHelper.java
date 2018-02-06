package ah.creativecodeapps.tiempo.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateHelper {

	public static String getDayNameMonthDayNumberHour(long time) {
		
		int gmtOffset = TimeZone.getDefault().getRawOffset();

		DateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd - HH:mm", Locale.getDefault());
		Date date = new Date(time * 1000 + gmtOffset);

		return dateFormat.format(date);
	}

	public static String getDayNameMonthDayNumber(long time) {
		
		int gmtOffset = TimeZone.getDefault().getRawOffset();

		DateFormat dateFormat = new SimpleDateFormat("EE, MMMM dd", Locale.getDefault());
		Date date = new Date(time * 1000 + gmtOffset);

		return dateFormat.format(date);
	}

	public static String getShorDayNameMonthDayNumber(long time) {

		int gmtOffset = TimeZone.getDefault().getRawOffset();

		DateFormat dateFormat = new SimpleDateFormat("EE, dd/MM", Locale.getDefault());
		Date date = new Date(time * 1000 + gmtOffset);

		return dateFormat.format(date);
	}
	
	public static String getShortDay(long time) {
		
		int gmtOffset = TimeZone.getDefault().getRawOffset();
		
		DateFormat dateFormat = new SimpleDateFormat("EE", Locale.getDefault());
		Date date = new Date(time * 1000 + gmtOffset);

		return dateFormat.format(date);
	}

	public static String getHourDetail(long time) {

		int gmtOffset = TimeZone.getDefault().getRawOffset();

		DateFormat dateFormat = new SimpleDateFormat("EE, hh:mm a", Locale.getDefault());
		Date date = new Date(time * 1000 + gmtOffset);

		return dateFormat.format(date);
	}
}
